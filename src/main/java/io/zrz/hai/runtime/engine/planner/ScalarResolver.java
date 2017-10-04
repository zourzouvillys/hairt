package io.zrz.hai.runtime.engine.planner;

import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.symbolic.expr.AbstractHExprVisitor;
import io.zrz.hai.symbolic.expr.HConstExpr;
import io.zrz.hai.symbolic.expr.HExpr;

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
