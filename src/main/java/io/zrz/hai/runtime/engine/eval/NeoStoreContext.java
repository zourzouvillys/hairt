package io.zrz.hai.runtime.engine.eval;

import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.runtime.engine.steps.EConnectionIteratorStep;
import io.zrz.hai.symbolic.HConnection;
import io.zrz.hai.symbolic.HLink;

/**
 * all access to/from the store is handed to this interface, to support
 * dependency tracking.
 *
 * as we read, it builds up a set of version dependencies. because commits are
 * not serialised if they don't conflict, we keep track of each commit tree that
 * we depend on. as they are folded in (after they no longer are needed to be
 * independent), they are given a unique sequence number that is serialised.
 *
 */

public interface NeoStoreContext {

  // read dependencies

  void count(INode start, HConnection connection);

  void read(INode node, String key);

  void readlink(IEdge rel, HLink link);

  void readlink(INode node, HLink link, IEdge rel);

  void traverse(IEdge rel);

  void hit(INode start, HConnection connection, ZValue key);

  void miss(INode start, HConnection connection, ZValue key);

  void scan(EConnectionIteratorStep it, INode start, HConnection connection);

  void next(EConnectionIteratorStep it, INode start, HConnection connection, IEdge e);

  // write log

  void setlink(INode node, HLink link, IEdge rel);

  void write(INode node, String key, String value);

  void append(INode node, HConnection connection, INode end);

  void create(INode node);

  void hasprop(INode node, String key);

}
