package io.zrz.hai.runtime;

import io.zrz.hai.type.HEdgeType;

public interface ZEdge extends ZPropertyContainer, ZEntity, ZObject {

  /**
   * The edge type.
   */

  @Override
  HEdgeType getType();

  /**
   * the start node
   */

  ZNode getStartNode();

  /**
   * the end node
   */

  ZNode getEndNode();

}
