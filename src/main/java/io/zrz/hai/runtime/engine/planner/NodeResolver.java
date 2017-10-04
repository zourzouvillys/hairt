package io.zrz.hai.runtime.engine.planner;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import io.zrz.hai.expr.AbstractHExprVisitor;
import io.zrz.hai.expr.HBinaryExpr;
import io.zrz.hai.expr.HBlockExpr;
import io.zrz.hai.expr.HExpr;
import io.zrz.hai.expr.HGotoExpr;
import io.zrz.hai.expr.HIndexAccessExpr;
import io.zrz.hai.expr.HMemberExpr;
import io.zrz.hai.expr.HThisExpr;
import io.zrz.hai.expr.HTupleInitExpr;
import io.zrz.hai.expr.HTypeBinaryExpr;
import io.zrz.hai.expr.HVarExpr;
import io.zrz.hai.runtime.ZAny;
import io.zrz.hai.runtime.ZEdge;
import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.type.HConnection;
import io.zrz.hai.type.HLink;
import io.zrz.hai.type.HMember;
import io.zrz.hai.type.HNodeType;

public class NodeResolver extends AbstractHExprVisitor<ZNode> {

  private final EFrameContext ctx;

  public NodeResolver(EFrameContext ctx) {
    this.ctx = Objects.requireNonNull(ctx);
  }

  @Override
  public ZNode visitDefault(HExpr expr) {
    throw new IllegalArgumentException(expr.getClass().toString());
  }

  @Override
  public ZNode visitThis(HThisExpr expr) {
    return (ZNode) this.ctx.self;
  }

  @Override
  public ZNode visitGoto(HGotoExpr expr) {
    if (expr.getValue() == null) {
      return null;
    }
    return expr.getValue().accept(this);
  }

  /**
   * a variable can potentially resolve to a node. lets find out.
   */

  @Override
  public ZNode visitVar(HVarExpr expr) {

    final String name = expr.getVariable().getName();

    final OptionalInt idx = this.ctx.params.index(name);

    if (!idx.isPresent()) {

      final Optional<ZAny> res = this.ctx.var(expr.getVariable());

      if (res.isPresent()) {
        return res.map(ZNode.class::cast).get();
      }

      throw new IllegalArgumentException(name);
    }

    return (ZNode) this.ctx.args.get(idx.getAsInt());

  }

  /**
   *
   */

  @Override
  public ZNode visitMemberAccess(HMemberExpr expr) {

    // fetch the base.
    final ZNode base = expr.getExpression().accept(this);

    // now, find the member type.
    final HMember member = expr.getMember();

    switch (member.getMemberKind()) {
      case AMBIENT:
      case LINK: {
        final ZNode link = base.getEdge((HLink) member).getEndNode();
        return link;
      }
      case CONNECTION:
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
  public ZNode visitIndexAccess(HIndexAccessExpr expr) {

    final EIndexTarget base = expr.getExpression().accept(this.ctx.indexableResolver);

    final List<ZAny> args = expr.getIndexers().stream().map(index -> index.accept(this.ctx.argResolver)).collect(Collectors.toList());

    final HMember member = base.getMember();

    switch (member.getMemberKind()) {
      case CONNECTION: {
        final ZEdge edge = base.getInstance().getConnection((HConnection) member).getIndexedNode(args.get(0));
        return Objects.requireNonNull(edge).getEndNode();
      }
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

  @Override
  public ZNode visitBinary(HBinaryExpr expr) {

    switch (expr.getExprKind()) {

      case INVOKE:
        return this.invoke(expr, expr.getLeft(), (HTupleInitExpr) expr.getRight());

      case ASSIGN:
      case INCR_ASSIGN: {
        final EAssignableTarget target = expr.getLeft().accept(this.ctx.assignableResolver);
        final ZNode value = expr.getRight().accept(this);
        target.assign(this.ctx, value);
        return value;
      }

      default:
        throw new IllegalArgumentException(expr.getExprKind().toString());

    }

  }

  /**
   *
   */

  @Override
  public ZNode visitTypeBinary(HTypeBinaryExpr expr) {
    switch (expr.getExprKind()) {
      case CAST:
      case AS:
        return expr.getExpression().accept(this);
      case NEW:
        // final ZAny left = expr.getLeft().accept(this);
        // final ZAny right = expr.getExpression().accept(this);
        return this.ctx.createNode((HNodeType) expr.getType());
      default:
        throw new IllegalArgumentException(expr.getExprKind().toString());
    }
  }

  /**
   *
   */

  @Override
  public ZNode visitBlock(HBlockExpr expr) {
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

  private ZNode invoke(HBinaryExpr expr, HExpr left, HTupleInitExpr args) {

    final EInvokeTarget base = expr.getLeft().accept(this.ctx.invokeResolver);

    if (base == null) {
      throw new IllegalArgumentException();
    }

    final List<ZAny> values = args.getInitializers()
        .stream()
        .map(index -> index.accept(this.ctx.argResolver))
        .collect(Collectors.toList());

    return (ZNode) base.invoke(this.ctx, values);

  }

}
