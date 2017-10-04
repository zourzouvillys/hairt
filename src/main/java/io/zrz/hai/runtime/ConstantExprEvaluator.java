package io.zrz.hai.runtime;

import io.zrz.hai.symbolic.expr.AbstractHExprVisitor;
import io.zrz.hai.symbolic.expr.HConstExpr;
import io.zrz.hai.symbolic.expr.HExpr;

public class ConstantExprEvaluator extends AbstractHExprVisitor<String> {

  @Override
  public String visitDefault(HExpr expr) {
    throw new IllegalArgumentException(expr.getClass().toGenericString());
  }

  @Override
  public String visitConst(HConstExpr expr) {
    return (String) expr.getValue();
  }

}
