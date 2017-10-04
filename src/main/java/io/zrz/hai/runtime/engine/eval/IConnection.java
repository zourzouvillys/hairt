package io.zrz.hai.runtime.engine.eval;

import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.symbolic.HConnection;

public interface IConnection extends Iterable<IEdge> {

  /**
   * the model connection that this {@link IConnection} represents.
   */

  HConnection getMember();

  /**
   * lookup an edge by a unique target node property value.
   */

  IEdge lookup(String key, ZValue value);

  /**
   * add the given target node to the connection.
   */

  IEdge add(INode target);

  int count();

}
