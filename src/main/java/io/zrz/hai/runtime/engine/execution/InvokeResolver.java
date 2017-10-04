package io.zrz.hai.runtime.engine.execution;

import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.symbolic.HMember;
import io.zrz.hai.symbolic.HMethod;
import io.zrz.hai.symbolic.expr.AbstractHExprVisitor;
import io.zrz.hai.symbolic.expr.HExpr;
import io.zrz.hai.symbolic.expr.HMemberExpr;

/**
 * generates an EIndexTarget from an expression.
 */

public class InvokeResolver extends AbstractHExprVisitor<EInvokeTarget> {

  private final EFrameContext ctx;

  public InvokeResolver(EFrameContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public EInvokeTarget visitDefault(HExpr expr) {
    throw new IllegalArgumentException(expr.getClass().toString());
  }

  @Override
  public EInvokeTarget visitMemberAccess(HMemberExpr expr) {

    final ZNode instance = expr.getExpression().accept(this.ctx.nodeResolver);

    if (instance == null) {
      return null;
    }

    final HMember member = expr.getMember();

    return EInvokeTarget.of(instance, (HMethod) member);

  }

}
