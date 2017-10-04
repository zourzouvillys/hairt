package io.zrz.hai.runtime.engine.journal;

public class JournalEvents {

  /**
   * create a new node.
   */

  public static class NodeCreate {

  }

  /**
   * delete a node. all edges must first be removed.
   */

  public static class NodeDelete {

  }

  /**
   * set a link from one node to another.
   */

  public static class LinkSet {

  }

  /**
   * clear a link
   */

  public static class LinkClear {

  }

  /**
   * add a node to a connection.
   */

  public static class ConnectionAdd {

  }

  /**
   * remove a node from a connection.
   */

  public static class ConnectionRemove {

  }

  /**
   * set a property value.
   */

  public static class PropertySet {

  }

  /**
   * remove (unset) a property value.
   */

  public static class PropertyRemove {

  }

}
