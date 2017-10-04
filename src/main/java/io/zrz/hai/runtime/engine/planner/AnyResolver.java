package io.zrz.hai.runtime.engine.planner;

import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import io.zrz.hai.expr.AbstractHExprVisitor;
import io.zrz.hai.expr.HBinaryExpr;
import io.zrz.hai.expr.HConstExpr;
import io.zrz.hai.expr.HExpr;
import io.zrz.hai.expr.HIndexAccessExpr;
import io.zrz.hai.expr.HMemberExpr;
import io.zrz.hai.expr.HThisExpr;
import io.zrz.hai.expr.HTupleInitExpr;
import io.zrz.hai.expr.HTypeBinaryExpr;
import io.zrz.hai.expr.HVarExpr;
import io.zrz.hai.runtime.ZAny;
import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.type.HConnection;
import io.zrz.hai.type.HLink;
import io.zrz.hai.type.HMember;
import io.zrz.hai.type.HNodeType;

/**
 * resolves to absolutely anything. used when we don't know or care what the
 * result is, e.g the CLI executing a statement.
 */

public class AnyResolver extends AbstractHExprVisitor<ZAny> {

  private final EFrameContext ctx;

  public AnyResolver(EFrameContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public ZNode visitDefault(HExpr expr) {
    throw new IllegalArgumentException(expr.getClass().toString());
  }

  @Override
  public ZAny visitThis(HThisExpr expr) {
    return this.ctx.self;
  }

  @Override
  public ZAny visitConst(HConstExpr expr) {
    return ZValue.from(expr);
  }

  /**
   * a variable can potentially resolve to a node. lets find out.
   */

  @Override
  public ZAny visitVar(HVarExpr expr) {
    final String name = expr.getVariable().getName();
    final OptionalInt idx = this.ctx.params.index(name);
    if (!idx.isPresent()) {
      throw new IllegalArgumentException(name);
    }
    return this.ctx.args.get(idx.getAsInt());
  }

  /**
   * a variable can potentially resolve to a node. lets find out.
   */

  @Override
  public ZAny visitBinary(HBinaryExpr expr) {
    // final ZAny left = expr.getLeft().accept(this);
    // final ZAny right = expr.getRight().accept(this);
    // return left;
    throw new IllegalArgumentException();
  }

  @Override
  public ZAny visitTypeBinary(HTypeBinaryExpr expr) {

    switch (expr.getExprKind()) {
      case NEW:
        // final ZAny left = expr.getLeft().accept(this);
        // final ZAny right = expr.getExpression().accept(this);
        return this.ctx.createNode((HNodeType) expr.getType());
      default:
        throw new IllegalArgumentException(expr.getExprKind().toString());
    }

  }

  @Override
  public ZAny visitTupleInit(HTupleInitExpr expr) {
    return null;
  }

  /**
   *
   */

  @Override
  public ZAny visitMemberAccess(HMemberExpr expr) {

    // fetch the base.
    final ZNode base = expr.getExpression().accept(this.ctx.nodeResolver);

    // now, find the member type.
    final HMember member = expr.getMember();

    switch (member.getMemberKind()) {
      case AMBIENT:
        return Objects.requireNonNull(base.getEdge((HLink) member).getEndNode());
      case CONNECTION:
        return base.getConnection((HConnection) member);
      case LINK:
        return Objects.requireNonNull(base.getEdge((HLink) member).getEndNode());
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
  public ZNode visitIndexAccess(HIndexAccessExpr expr) {

    final EIndexTarget base = expr.getExpression().accept(this.ctx.indexableResolver);

    final List<ZAny> args = expr.getIndexers().stream().map(index -> index.accept(this.ctx.argResolver)).collect(Collectors.toList());

    final HMember member = base.getMember();

    switch (member.getMemberKind()) {
      case CONNECTION:
        return base.getInstance().getConnection((HConnection) member).getIndexedNode(args.get(0)).getEndNode();
      case AMBIENT:
      case LINK:
      case METHOD:
      case PERMISSION:
      case SELECTION:
      case STATE:
        // none of these are indexable at the moment.
        break;
    }

    throw new IllegalArgumentException(member.getMemberKind().toString());

  }

}
