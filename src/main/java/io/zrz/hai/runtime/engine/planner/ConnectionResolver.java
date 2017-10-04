package io.zrz.hai.runtime.engine.planner;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import io.zrz.hai.expr.AbstractHExprVisitor;
import io.zrz.hai.expr.HBinaryExpr;
import io.zrz.hai.expr.HBlockExpr;
import io.zrz.hai.expr.HExpr;
import io.zrz.hai.expr.HGotoExpr;
import io.zrz.hai.expr.HMemberExpr;
import io.zrz.hai.expr.HTupleInitExpr;
import io.zrz.hai.expr.HVarExpr;
import io.zrz.hai.runtime.ZAny;
import io.zrz.hai.runtime.ZConnection;
import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.type.HConnection;
import io.zrz.hai.type.HMember;

public class ConnectionResolver extends AbstractHExprVisitor<ZConnection> {

  private final EFrameContext ctx;

  public ConnectionResolver(EFrameContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public ZConnection visitDefault(HExpr expr) {
    throw new IllegalArgumentException(expr.getClass().toString());
  }

  @Override
  public ZConnection visitGoto(HGotoExpr expr) {
    return expr.getValue().accept(this);
  }

  /**
   * a variable can potentially resolve to a node. lets find out.
   */

  @Override
  public ZConnection visitVar(HVarExpr expr) {

    final String name = expr.getVariable().getName();

    final OptionalInt idx = this.ctx.params.index(name);

    if (!idx.isPresent()) {

      final Optional<ZAny> res = this.ctx.var(expr.getVariable());

      if (res.isPresent()) {
        return res.map(ZConnection.class::cast).get();
      }

      throw new IllegalArgumentException(name);
    }

    return (ZConnection) this.ctx.args.get(idx.getAsInt());

  }

  /**
   *
   */

  @Override
  public ZConnection visitMemberAccess(HMemberExpr expr) {

    // fetch the base.
    final ZNode base = expr.getExpression().accept(this.ctx.nodeResolver);

    // now, find the member type.
    final HMember member = expr.getMember();

    switch (member.getMemberKind()) {
      case CONNECTION:
        return base.getConnection((HConnection) member);
      case AMBIENT:
        break;
      case LINK:
        break;
      case METHOD:
        break;
      case PERMISSION:
        break;
      case SELECTION:
        break;
      case STATE:
        break;
      default:
        break;
    }

    throw new IllegalArgumentException(member.getMemberKind().toString());

  }

  @Override
  public ZConnection visitBinary(HBinaryExpr expr) {

    switch (expr.getExprKind()) {

      case INVOKE:
        return this.invoke(expr, expr.getLeft(), (HTupleInitExpr) expr.getRight());

      default:
        throw new IllegalArgumentException(expr.getExprKind().toString());

    }

  }

  /**
   *
   */

  @Override
  public ZConnection visitBlock(HBlockExpr expr) {
    for (int i = 0; i < expr.getExpressions().size(); ++i) {
      final HExpr e = expr.getExpressions().get(i);
      if (i != (expr.getExpressions().size() - 1)) {
        e.accept(this.ctx.voidResolver);
      } else {
        return e.accept(this);
      }
    }
    throw new IllegalArgumentException("empty block");
  }

  private ZConnection invoke(HBinaryExpr expr, HExpr left, HTupleInitExpr args) {

    final EInvokeTarget base = expr.getLeft().accept(this.ctx.invokeResolver);

    final List<ZAny> values = args.getInitializers()
        .stream()
        .map(index -> index.accept(this.ctx.argResolver))
        .collect(Collectors.toList());

    return (ZConnection) base.invoke(this.ctx, values);

  }

}
