package io.zrz.hai.runtime.engine.planner;

import java.util.OptionalInt;

import io.zrz.hai.runtime.ZAny;
import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.symbolic.expr.AbstractHExprVisitor;
import io.zrz.hai.symbolic.expr.HConstExpr;
import io.zrz.hai.symbolic.expr.HExpr;
import io.zrz.hai.symbolic.expr.HVarExpr;

public class ArgResolver extends AbstractHExprVisitor<ZAny> {

  private final EFrameContext ctx;

  public ArgResolver(EFrameContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public ZAny visitDefault(HExpr expr) {
    throw new IllegalArgumentException(expr.getClass().toString());
  }

  @Override
  public ZAny visitVar(HVarExpr expr) {
    final String name = expr.getVariable().getName();
    final OptionalInt idx = this.ctx.params.index(name);
    if (!idx.isPresent()) {
      throw new IllegalArgumentException(name);
    }
    return this.ctx.args.get(idx.getAsInt());
  }

  @Override
  public ZAny visitConst(HConstExpr expr) {
    return ZValue.from(expr);
  }

}
