package io.zrz.hai.runtime.engine.planner;

import io.zrz.hai.expr.AbstractHExprVisitor;
import io.zrz.hai.expr.HConstExpr;
import io.zrz.hai.expr.HExpr;
import io.zrz.hai.runtime.ZValue;

/**
 * generates an EIndexTarget from an expression.
 */

public class ScalarResolver extends AbstractHExprVisitor<ZValue> {

  public ScalarResolver(EFrameContext ctx) {
  }

  @Override
  public ZValue visitDefault(HExpr expr) {
    throw new IllegalArgumentException(expr.getClass().toString());
  }

  @Override
  public ZValue visitConst(HConstExpr expr) {
    return ZValue.from(expr);
  }

}
