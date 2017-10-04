package io.zrz.hai.runtime.engine.steps;

import io.zrz.hai.haiscript.IndentPrintWriter;
import io.zrz.hai.symbolic.type.HDeclType;
import lombok.Getter;

public class ENewNodeStep implements EStep {

  @Getter
  private final HDeclType type;

  public ENewNodeStep(HDeclType type) {
    this.type = type;
  }

  @Override
  public EStepKind getStepKind() {
    return EStepKind.NEWNODE;
  }

  @Override
  public void dump(IndentPrintWriter w) {
    w.print("NEW-NODE");
  }

  @Override
  public <R> R accept(EStepVisitor<R> visitor) {
    return visitor.visitNewNode(this);
  }

  @Override
  public String toString() {
    return "NEW-NODE:" + this.type;
  }

}
