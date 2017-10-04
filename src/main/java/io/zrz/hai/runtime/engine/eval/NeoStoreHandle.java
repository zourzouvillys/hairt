package io.zrz.hai.runtime.engine.eval;

import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.factory.primitive.LongObjectMaps;
import org.neo4j.graphdb.Node;

import io.zrz.hai.runtime.engine.concurrency.SnapshotHandle;
import io.zrz.hai.type.HDeclType;

/**
 * abstract MVCC access to the store.
 *
 * Each node has a reference node with (ReferenceVersion, VersionedNode) label.
 * Then, for each additional version, there is a (VersionedNode) which has a
 * relationship to the referenced node with ->PARENT_VERSION->.
 *
 * Two independent changesets may modify the same commit without conflicting, in
 * which case there will be a VersionedNode with multiple incoming
 * :PARENT_VERSION relationships. These can't always be merged at commit time,
 * as the changesets may still be needed to be independent to allow visibility
 * in a snapshot. A background process merges these as they roll out of the
 * snapshot window.
 *
 * The ReferenceVersion has relationships to the LATEST_VERSION, which is the
 * VersionedNode instances which are the current head version for that logical
 * node. again, there may be multiple when they need merging.
 *
 * The ReferenceVersion is the "current" merged version. It contains the full
 * state. Later and previous versions contain diffs of this version.
 *
 * A connection is handled as an independent entity in the store, as it contains
 * metadata related to the connection itself. Each connection that has any
 * connection has a node, labelled (ReferenceVersion, VersionedConnection)
 * attached to it by way of a CONNECTION_OF relationship.
 *
 *
 *
 *
 */

public class NeoStoreHandle {

  /**
   * the underlying store.
   */

  // private final NeoStore store;

  /**
   * the version we are reading.
   *
   * we firstly check the changesets in the snapshot, then the node store if no
   * match was found, before returning the result.
   *
   */

  // private final SnapshotHandle readver;

  /**
   *
   */

  private final MutableLongObjectMap<NeoNode> mappings = LongObjectMaps.mutable.empty();

  /**
   * create a new handle for reading from a specific minimum version.
   */

  public NeoStoreHandle(NeoStore store, SnapshotHandle readver) {
    // this.store = store;
    // this.readver = readver;
  }

  /**
   * fetch a node that represents the backing version of this store node.
   */

  public NeoNode getNode(Node node) {
    // check that it is visibile in this version, and not been deleted or replaced.
    return this.mappings.getIfAbsentPut(node.getId(), () -> new NeoNode(this, node));
  }

  /**
   * create a new node of the given type.
   */

  public INode createNode(HDeclType type) {
    return new NeoNode(this, type);
  }

}
