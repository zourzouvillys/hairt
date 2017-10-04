package io.zrz.hai.runtime.compile.facade.exec;

import io.zrz.hai.runtime.compile.facade.impl.MMethodImpl;
import io.zrz.hai.symbolic.expr.HExpr;
import io.zrz.hai.symbolic.expr.HExprUtils;

/**
 * provides an expression for a field access.
 */

public class MFieldExpression {

  private final MMethodImpl field;

  public MFieldExpression(MMethodImpl field) {
    this.field = field;
  }

  public void generate() {

    final HExpr expr = this.field.getMember().getExecutable().getExpression();

    HExprUtils.dump(System.err, expr);

    // System.err.println(expr);

    expr.accept(new InlineExpression(this.field.getMember().getExecutable()));

  }

}
