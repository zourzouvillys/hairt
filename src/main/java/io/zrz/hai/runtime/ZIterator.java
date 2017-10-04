package io.zrz.hai.runtime;

/**
 * an instance of a single traversal of a path.
 */

public interface ZIterator extends ZRef {

  /**
   * Provides a cursor that can be used to continue iterating from the current
   * position.
   */

  ZValue getCursor();

  /**
   * advances to the next edge, returning true if more were found, otherwise
   * false.
   */

  boolean next();

  /**
   * true if there are more.
   */

  boolean hasNext();

  /**
   * the edge at the current iteration position.
   */

  ZEdge edge();

  /**
   * the node at hte current iteration position.
   */

  ZNode node();

}
