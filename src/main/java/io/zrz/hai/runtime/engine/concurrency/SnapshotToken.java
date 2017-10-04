package io.zrz.hai.runtime.engine.concurrency;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;

import org.eclipse.collections.api.map.primitive.MutableLongLongMap;
import org.eclipse.collections.impl.factory.primitive.LongLongMaps;

import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * includes a global store sequence, local node sequences, and specific
 * changesets.
 */

public class SnapshotToken {

  /**
   *
   */

  @Setter
  @Getter
  private long globalSequence;

  /**
   * each node sequence which is included.
   */

  @Getter
  private final MutableLongLongMap nodeSequences;

  /**
   * the node changesets which are included.
   */

  @Getter
  private final MutableLongLongMap changesets;

  public SnapshotToken(long globalSequence) {
    this.globalSequence = globalSequence;
    this.nodeSequences = LongLongMaps.mutable.empty();
    this.changesets = LongLongMaps.mutable.empty();
  }

  public SnapshotToken() {
    this.nodeSequences = LongLongMaps.mutable.empty();
    this.changesets = LongLongMaps.mutable.empty();
  }

  public SnapshotToken(long globalVersion, MutableLongLongMap nodeSequences2, MutableLongLongMap changesets2) {
    this.globalSequence = globalVersion;
    this.nodeSequences = nodeSequences2;
    this.changesets = changesets2;
  }

  /**
   *
   */

  @SneakyThrows
  public String toToken() {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      try (DataOutputStream os = new DataOutputStream(baos)) {
        // version/encoding identifier
        os.writeByte(1);

        writeUnsignedVarLong(this.globalSequence, os);

        writeUnsignedVarLong(this.nodeSequences.size(), os);
        this.nodeSequences.forEachKeyValue((nodeId, seqId) -> {
          writeUnsignedVarLong(nodeId, os);
          writeUnsignedVarLong(seqId, os);
        });

        writeUnsignedVarLong(this.changesets.size(), os);
        this.changesets.forEachKeyValue((nodeId, seqId) -> {
          writeUnsignedVarLong(nodeId, os);
          writeUnsignedVarLong(seqId, os);
        });

      }
      baos.flush();

      return BaseEncoding.base64Url().omitPadding().encode(baos.toByteArray());

    }

  }

  @SneakyThrows
  public static SnapshotToken fromToken(String token) {

    try (ByteArrayInputStream baos = new ByteArrayInputStream(BaseEncoding.base64Url().omitPadding().decode(token))) {

      try (DataInputStream in = new DataInputStream(baos)) {

        // version/encoding identifier

        final int version = in.readByte();

        if (version != 1) {
          throw new IllegalArgumentException("invalid token version: " + version);
        }

        final long globalVersion = readUnsignedVarLong(in);

        //

        final int numNodeSequences = Ints.checkedCast(readUnsignedVarLong(in));

        final MutableLongLongMap nodeSequences = LongLongMaps.mutable.empty();

        for (int i = 0; i < numNodeSequences; ++i) {
          final long nodeId = readUnsignedVarLong(in);
          final long seqId = readUnsignedVarLong(in);
          nodeSequences.put(nodeId, seqId);
        }

        //

        final int numChangesets = Ints.checkedCast(readUnsignedVarLong(in));

        final MutableLongLongMap changesets = LongLongMaps.mutable.empty();

        for (int i = 0; i < numChangesets; ++i) {
          final long nodeId = readUnsignedVarLong(in);
          final long seqId = readUnsignedVarLong(in);
          changesets.put(nodeId, seqId);
        }

        return new SnapshotToken(globalVersion, nodeSequences, changesets);

      }

    }

  }

  @SneakyThrows
  public static void writeUnsignedVarLong(long value, DataOutput out) {
    while ((value & 0xFFFFFFFFFFFFFF80L) != 0L) {
      out.writeByte(((int) value & 0x7F) | 0x80);
      value >>>= 7;
    }
    out.writeByte((int) value & 0x7F);
  }

  @SneakyThrows
  public static long readUnsignedVarLong(DataInput in) {
    long value = 0L;
    int i = 0;
    long b;
    while (((b = in.readByte()) & 0x80L) != 0) {
      value |= (b & 0x7F) << i;
      i += 7;
      if (i > 63) {
        throw new IllegalArgumentException("overflow");
      }
    }
    return value | (b << i);
  }

  @Override
  public String toString() {
    return String.format("global=%d, nodes=%s, changeset=%s", this.globalSequence, this.nodeSequences, this.changesets);
  }

}
