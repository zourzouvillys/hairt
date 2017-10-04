package io.zrz.hai.runtime;

import io.zrz.hai.expr.AbstractHExprVisitor;
import io.zrz.hai.expr.HConstExpr;
import io.zrz.hai.expr.HExpr;

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
