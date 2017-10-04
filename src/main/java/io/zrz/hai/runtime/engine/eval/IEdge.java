package io.zrz.hai.runtime.engine.eval;

/**
 * common edge interface regardless of being backed by a neo4j disk based node
 * or memory.
 */

public interface IEdge extends IPropertyContainer {

  /**
   * the start node of this edge.
   */

  INode getStartNode();

  /**
   * the end node of this edge.
   */

  INode getEndNode();

  /**
   * remove this edge - either from the connection or the link - whichever it is
   * part of.
   */

  void delete();

}
