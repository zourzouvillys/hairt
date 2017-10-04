package io.zrz.hai.runtime;

/**
 * base interface for all values at runtime.
 *
 * this can be a ZValue (any scalar), or a ZRef (any decl, array, or tuple).
 * nodes, edges, and connections are represented using ZNode, ZEdge, and
 * ZConnection.
 *
 */

public interface ZAny {

  ZKind getZKind();

}
