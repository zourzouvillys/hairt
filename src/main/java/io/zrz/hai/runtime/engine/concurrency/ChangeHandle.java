package io.zrz.hai.runtime.engine.concurrency;

import java.util.stream.Collectors;
import java.util.stream.LongStream;

import com.google.common.collect.ImmutableSet;

public class ChangeHandle {

  /**
   * the state of this changeset
   */

  ChangeState state = ChangeState.ACTIVE;

  /**
   * the base global sequence number this changeset it based on.
   */

  final long base;

  /**
   * any direct parents which are included in this changeset.
   *
   * when a changeset is instantiated, this includes the head changeset of all
   * branches which have been committed but not folded.
   *
   * if there are no parents in a changeset, then it is directly on the base
   * commit.
   *
   */

  private final ImmutableSet<ChangeHandle> parents;

  /**
   * the changeset ID. allocated and incremented every time a new changeset is
   * opened. This can not be used to order changes, as the order a changeset is
   * started is not the order they are commited.
   */

  final long cid;

  /**
   * the commit sequence number of this changeset, once it has been committed to
   * the store.
   */

  public long bid;

  /**
   *
   */

  ChangeHandle(long cid, long base, ImmutableSet<ChangeHandle> parents) {
    this.cid = cid;
    this.base = base;
    this.parents = parents;
  }

  /**
   *
   */

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("CHANGESET(state=");
    sb.append(this.state).append(", ");
    if (this.bid > 0) {
      sb.append("bid=").append(this.bid).append(", ");
    }
    sb.append("cid=").append(this.cid).append(", ");
    sb.append("base=").append(this.base);
    if (!this.parents.isEmpty()) {

      sb.append(", parents=[");
      // sb.append(this.parents.stream().flatMapToLong(p ->
      // p.commits()).distinct().mapToObj(cid ->
      // Long.toString(cid)).collect(Collectors.joining(", ")));
      sb.append(this.parents.stream().map(x -> Long.toString(x.cid)).collect(Collectors.joining(", ")));
      sb.append("]");
    }
    sb.append(")");
    return sb.toString();
  }

  public LongStream commits() {
    return LongStream.concat(
        LongStream.of(this.cid),
        this.parents.stream().filter(p -> p.bid == 0).flatMapToLong(p -> p.commits()));
  }

  /**
   * a snapshot handle which includes the base commit plus all changesets which
   * are visible.
   */

  public SnapshotToken snapshot() {
    if (this.bid != 0) {
      return new SnapshotToken(this.bid);
    }
    return new SnapshotToken(this.base); // , LongSets.immutable.withAll(this.commits()));
  }

}
