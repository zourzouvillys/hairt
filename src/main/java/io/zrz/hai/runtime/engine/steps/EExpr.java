package io.zrz.hai.runtime.engine.steps;

import io.zrz.hai.haiscript.IndentPrintWriter;

/**
 * an expression that can be applied to a single node to provide output, e.g a
 * scalar state field, node ID, or node type.
 */

public interface EExpr {

  void dump(IndentPrintWriter w);

}
