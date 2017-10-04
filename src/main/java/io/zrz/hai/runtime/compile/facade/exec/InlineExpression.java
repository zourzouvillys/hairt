package io.zrz.hai.runtime.compile.facade.exec;

import io.zrz.hai.expr.AbstractHExprVisitor;
import io.zrz.hai.expr.HBinaryExpr;
import io.zrz.hai.expr.HConstExpr;
import io.zrz.hai.expr.HExpr;
import io.zrz.hai.expr.HIndexAccessExpr;
import io.zrz.hai.expr.HMemberExpr;
import io.zrz.hai.type.HExecutable;
import io.zrz.hai.type.HMember;
import io.zrz.hai.type.HMethod;

public class InlineExpression extends AbstractHExprVisitor<HExpr> {

  // private final HExecutable exec;

  public InlineExpression(HExecutable exec) {
    // this.exec = exec;
  }

  @Override
  public HExpr visitDefault(HExpr expr) {
    return expr;
  }

  /**
   * an invocation is replaced with the content of the target executable.
   */

  private HExpr visitInvoke(HBinaryExpr expr) {

    switch (expr.getLeft().getExprKind()) {
      case MEMBER_ACCESS: {
        final HMemberExpr memberAccess = (HMemberExpr) expr.getLeft();
        System.err.println(memberAccess.getMember());
        break;
      }
      default:
        break;
    }

    return null;
  }

  @Override
  public HExpr visitMemberAccess(HMemberExpr memberAccess) {
    final HMember member = memberAccess.getMember();
    switch (member.getMemberKind()) {
      case METHOD:
        final HMethod method = (HMethod) member;
        System.err.println("METHOD MEMBER: " + method.getExecutable().getExpression());
        break;
      default:
        return memberAccess;
    }
    return memberAccess;
  }

  @Override
  public HExpr visitIndexAccess(HIndexAccessExpr expr) {

    return expr;
  }

  @Override
  public HExpr visitBinary(HBinaryExpr expr) {
    switch (expr.getExprKind()) {
      case INVOKE:
        return this.visitInvoke(expr);
      default:
        break;
    }
    return expr;
  }

  @Override
  public HExpr visitConst(HConstExpr expr) {
    return expr;
  }

}
