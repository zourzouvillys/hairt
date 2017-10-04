package io.zrz.hai.runtime.engine.execution;

import io.zrz.hai.expr.AbstractHExprVisitor;
import io.zrz.hai.expr.HExpr;
import io.zrz.hai.expr.HMemberExpr;
import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.type.HMember;

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
