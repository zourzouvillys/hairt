package io.zrz.hai.runtime.engine.steps;

import io.zrz.hai.syntax.IndentPrintWriter;
import lombok.Getter;

/**
 * a step which counts the number of items in the connection.
 */

public class EConnectionCountExpr implements EStep, EExpr {

  @Getter
  private final EConnectionStep source;

  public EConnectionCountExpr(EConnectionStep source) {
    this.source = source;
  }

  @Override
  public EStepKind getStepKind() {
    return EStepKind.COUNT;
  }

  @Override
  public void dump(IndentPrintWriter w) {
    w.print("COUNT ON ");
    w.print(this.source);
  }

  @Override
  public <R> R accept(EStepVisitor<R> visitor) {
    return visitor.visitCount(this);
  }

  @Override
  public String toString() {
    return "COUNT(" + this.source + ")";
  }

}
