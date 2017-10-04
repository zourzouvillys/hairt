package io.zrz.hai.runtime.engine.steps;

import io.zrz.hai.haiscript.IndentPrintWriter;
import io.zrz.hai.runtime.ZValue;
import lombok.Getter;

public class EScalarStep implements EStep, EExpr {

  @Getter
  private final ZValue value;

  public EScalarStep(ZValue value) {
    this.value = value;
  }

  @Override
  public EStepKind getStepKind() {
    return EStepKind.SCALAR;
  }

  @Override
  public void dump(IndentPrintWriter w) {
    w.print("SCALAR");
  }

  @Override
  public <R> R accept(EStepVisitor<R> visitor) {
    return visitor.visitScalar(this);
  }

  @Override
  public String toString() {
    return "SCALAR:" + this.value;
  }

}
