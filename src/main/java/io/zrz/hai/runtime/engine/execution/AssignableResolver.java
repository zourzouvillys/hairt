package io.zrz.hai.runtime.engine.execution;

import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.symbolic.HConnection;
import io.zrz.hai.symbolic.HLink;
import io.zrz.hai.symbolic.HMember;
import io.zrz.hai.symbolic.HState;
import io.zrz.hai.symbolic.expr.AbstractHExprVisitor;
import io.zrz.hai.symbolic.expr.HExpr;
import io.zrz.hai.symbolic.expr.HMemberExpr;
import io.zrz.hai.symbolic.expr.HVarExpr;

/**
 * generates an EIndexTarget from an expression.
 */

public class AssignableResolver extends AbstractHExprVisitor<EAssignableTarget> {

  private final EFrameContext ctx;

  public AssignableResolver(EFrameContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public EAssignableTarget visitDefault(HExpr expr) {
    throw new IllegalArgumentException(expr.getClass().toString());
  }

  @Override
  public EAssignableTarget visitMemberAccess(HMemberExpr expr) {
    final ZNode instance = expr.getExpression().accept(this.ctx.nodeResolver);
    final HMember member = expr.getMember();
    switch (member.getMemberKind()) {
      case STATE:
        return EAssignableTarget.member(instance, (HState) member);
      case CONNECTION:
        return EAssignableTarget.member(instance, (HConnection) member);
      case LINK:
        return EAssignableTarget.member(instance, (HLink) member);
    }
    throw new IllegalArgumentException(member.getMemberKind().toString());
  }

  @Override
  public EAssignableTarget visitVar(HVarExpr var) {
    return EAssignableTarget.var(var);
  }

}
