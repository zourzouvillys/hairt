package io.zrz.hai.runtime.engine.execution;

import io.zrz.hai.expr.AbstractHExprVisitor;
import io.zrz.hai.expr.HBinaryExpr;
import io.zrz.hai.expr.HExpr;
import io.zrz.hai.runtime.ZAny;

/**
 * A visitor which handles expressions in a void context.
 */

public class VoidVisitor extends AbstractHExprVisitor<Void> {

  private final EFrameContext ctx;

  public VoidVisitor(EFrameContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public Void visitDefault(HExpr expr) {
    throw new IllegalArgumentException(expr.getClass().toString());
  }

  @Override
  public Void visitBinary(HBinaryExpr expr) {

    switch (expr.getExprKind()) {

      case ASSIGN: {
        final EAssignableTarget target = expr.getLeft().accept(this.ctx.assignableResolver);
        // TODO: use resolver specific to expected value type.
        final ZAny value = expr.getRight().accept(this.ctx.anyResolver);
        target.assign(this.ctx, value);
        break;
      }

      case INVOKE:
      default:
        throw new IllegalArgumentException(expr.getExprKind().toString());

    }

    return null;
  }

}
