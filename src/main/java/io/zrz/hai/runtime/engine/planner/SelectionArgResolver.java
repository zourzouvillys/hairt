package io.zrz.hai.runtime.engine.planner;

import java.util.OptionalInt;

import io.zrz.hai.expr.AbstractHExprVisitor;
import io.zrz.hai.expr.HConstExpr;
import io.zrz.hai.expr.HExpr;
import io.zrz.hai.expr.HVarExpr;
import io.zrz.hai.runtime.ZAny;
import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.runtime.compile.parse.impl.GFieldSelection;
import io.zrz.hai.runtime.engine.steps.EExpr;
import io.zrz.hai.syntax.IndentPrintWriter;
import io.zrz.hai.type.HType;
import lombok.Getter;

public class SelectionArgResolver extends AbstractHExprVisitor<ZAny> {

  private final GFieldSelection sel;

  public SelectionArgResolver(GFieldSelection sel) {
    this.sel = sel;
  }

  @Override
  public ZAny visitDefault(HExpr expr) {
    throw new IllegalArgumentException(expr.getClass().toString());
  }

  public static class VarValue extends ZValue implements EExpr {

    @Getter
    private final String name;

    public VarValue(HType type, String name, HExpr val) {
      super(type, null);
      this.name = name;
    }

    @Override
    public void dump(IndentPrintWriter w) {
      w.print("VAR $" + this.name);
    }

  }

  @Override
  public ZAny visitVar(HVarExpr expr) {

    final String name = expr.getVariable().getName();

    final OptionalInt idx = this.sel.getArguments().getType().index(name);

    if (!idx.isPresent()) {
      throw new IllegalArgumentException(name);
    }

    final HExpr val = this.sel.getArguments().getInitializers().get(idx.getAsInt());

    // we actually have a variable, so will need to resolve later.

    return new VarValue(val.getType(), name, val);
  }

  @Override
  public ZAny visitConst(HConstExpr expr) {
    return ZValue.from(expr);
  }

}
