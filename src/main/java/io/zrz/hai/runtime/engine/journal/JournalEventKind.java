package io.zrz.hai.runtime.engine.journal;

public enum JournalEventKind {

  /**
   * create a new node.
   */

  NODE_CREATE,

  /**
   * delete a node. all edges must first be removed.
   */

  NODE_DELETE,

  /**
   * set a link from one node to another.
   */

  LINK_SET,

  /**
   * clear a link
   */

  LINK_CLEAR,

  /**
   * add a node to a connection.
   */

  CONNECTION_ADD,

  /**
   * remove a node from a connection.
   */

  CONNECTION_REMOVE,

  /**
   * set a property value.
   */

  PROPERTY_SET,

  /**
   * remove (unset) a property value.
   */

  PROPERTY_REMOVE,

}
