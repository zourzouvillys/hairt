package io.zrz.hai.runtime.engine.stack;

import io.zrz.hai.symbolic.expr.AbstractHExprKindVoidVisitor;
import io.zrz.hai.symbolic.expr.DelegatingHExprVoidVisitor;
import io.zrz.hai.symbolic.expr.HBinaryExpr;
import io.zrz.hai.symbolic.expr.HExpr;
import io.zrz.hai.symbolic.expr.HExprKindVisitor;

public class StackVisitor extends AbstractHExprKindVoidVisitor {

  private StackVisitor() {
  }

  @Override
  public void visitInvoke(HBinaryExpr expr) {
  }

  @Override
  public void visitDefault(HExpr expr) {
    throw new IllegalArgumentException(String.format("%s(%s)", expr.getExprKind(), expr.getClass().getSimpleName()));
  }

  public static HExprKindVisitor<Void> createInstance() {
    return DelegatingHExprVoidVisitor.wrap(new StackVisitor());
  }

}
