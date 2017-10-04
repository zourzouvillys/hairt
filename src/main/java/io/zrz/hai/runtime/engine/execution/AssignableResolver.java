package io.zrz.hai.runtime.engine.execution;

import io.zrz.hai.expr.AbstractHExprVisitor;
import io.zrz.hai.expr.HExpr;
import io.zrz.hai.expr.HMemberExpr;
import io.zrz.hai.expr.HVarExpr;
import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.type.HConnection;
import io.zrz.hai.type.HLink;
import io.zrz.hai.type.HMember;
import io.zrz.hai.type.HState;

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
      case AMBIENT:
      case LINK:
        return EAssignableTarget.member(instance, (HLink) member);
      case METHOD:
      case PERMISSION:
      case SELECTION:
        break;
    }
    throw new IllegalArgumentException(member.getMemberKind().toString());
  }

  @Override
  public EAssignableTarget visitVar(HVarExpr var) {
    return EAssignableTarget.var(var);
  }

}
