package io.zrz.hai.runtime.engine.execution;

import io.zrz.hai.expr.AbstractHExprVisitor;
import io.zrz.hai.expr.HExpr;
import io.zrz.hai.expr.HMemberExpr;
import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.type.HMember;
import io.zrz.hai.type.HMethod;

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
