package io.zrz.hai.runtime.engine.execution;

import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.symbolic.HMember;
import io.zrz.hai.symbolic.expr.AbstractHExprVisitor;
import io.zrz.hai.symbolic.expr.HExpr;
import io.zrz.hai.symbolic.expr.HMemberExpr;

/**
 * generates an EIndexTarget from an expression.
 */

public class IndexableResolver extends AbstractHExprVisitor<EIndexTarget> {

  private final EFrameContext ctx;

  public IndexableResolver(EFrameContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public EIndexTarget visitDefault(HExpr expr) {
    throw new IllegalArgumentException(expr.getClass().toString());
  }

  @Override
  public EIndexTarget visitMemberAccess(HMemberExpr expr) {

    final ZNode instance = expr.getExpression().accept(this.ctx.nodeResolver);

    final HMember member = expr.getMember();

    return EIndexTarget.of(instance, member);

  }

}
