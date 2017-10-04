package io.zrz.hai.runtime;

import java.util.function.Consumer;

import io.zrz.hai.expr.HLambdaExpr;
import io.zrz.hai.runtime.engine.PagingParams;

/**
 * A ZConnection is a logical set of edges.
 *
 * Although initially fetched from a node, can be refined by applying filters.
 * Each application results in a new instance being returned that filters out
 * the items.
 *
 */

public interface ZConnection extends ZRef {

  /**
   * add a node into this connection.
   */

  ZEdge addNode(ZNode target, ZPropertyContainer edge);

  /**
   * add node to this connection.
   */

  default ZEdge addNode(ZNode target) {
    return addNode(target, null);
  }

  /**
   * remove the given node from this connection, returning true if it was found.
   */

  boolean remove(ZNode remove);

  /**
   * remove the given edge from this connection, returning true if it was found.
   */

  boolean remove(ZEdge edge);

  /**
   * find a single unique edge by an index value.
   */

  ZEdge getEdge(HLambdaExpr unique);

  /**
   * find a single unique node by index value.
   */

  ZEdge getNode(HLambdaExpr unique);

  /**
   * apply a filter to this connection. Only edges matching the given filter will
   * be returned in an iteration.
   */

  ZConnection filter(HLambdaExpr filter);

  /**
   * apply a reducer to all entries.
   */

  ZConnection reduce(HLambdaExpr filter);

  /**
   * provide a count of the number of edges in this connection with the current
   * filters.
   */

  int count();

  /**
   * fetch an iterator for traversing the edges in this connection, starting at
   * the beginning.
   */

  ZIterator iterator();

  /**
   * find a node indexed by the given value.
   */

  ZEdge getIndexedNode(ZAny value);

  void forEach(Consumer<ZEdge> consumer);

  ZConnection params(PagingParams params);

  @Override
  default ZKind getZKind() {
    return ZKind.CONNECTION;
  }

}
