package io.zrz.hai.runtime.engine.eval;

import io.zrz.hai.symbolic.HConnection;
import io.zrz.hai.symbolic.HLink;
import io.zrz.hai.symbolic.type.HDeclType;

/**
 * common node interface, regardless of being backed by disk or memory.
 */

public interface INode extends IPropertyContainer {

  /**
   * the model decltype this node represents.
   */

  HDeclType getType();

  /**
   * fetch handle for the given named connection.
   */

  IConnection connection(HConnection connection);

  /**
   * fetch link
   */

  IEdge getLink(HLink link);

  /**
   * set the given link to the specified target node.
   */

  IEdge setLink(HLink link, INode target);

}
