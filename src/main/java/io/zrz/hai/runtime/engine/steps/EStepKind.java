package io.zrz.hai.runtime.engine.steps;

public enum EStepKind {

  /**
   * the active view for the operation.
   */

  VIEWER,

  /**
   * a connection selection.
   */

  CONNECTION,

  /**
   *
   */

  ITERATOR,

  /**
   * a scan on a set of edges.
   */

  EDGESCAN,

  /**
   * a link selection
   */

  LINK,

  /**
   * lookup an edge in a connection by an index value.
   */

  CONNECTION_INDEX,

  /**
   * count of the number of edges in the connection.
   */

  COUNT,

  /**
   * traverse the edge
   */

  EDGETRAVERSE,

  /**
   * a new node instance.
   */

  NEWNODE,

  /**
   * a field
   */

  FIELD,

  /**
   * perform an invocation
   */

  INVOKE,

  /**
   * a scalar value
   */

  SCALAR,

}
