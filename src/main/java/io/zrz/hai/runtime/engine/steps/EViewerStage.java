package io.zrz.hai.runtime.engine.steps;

import io.zrz.hai.syntax.IndentPrintWriter;

/**
 */

public class EViewerStage implements EStep {

  public EViewerStage() {
  }

  @Override
  public EStepKind getStepKind() {
    return EStepKind.VIEWER;
  }

  @Override
  public void dump(IndentPrintWriter w) {
    w.print("VIEWER");
  }

  @Override
  public String toString() {
    return "VIEWER";
  }

  @Override
  public <R> R accept(EStepVisitor<R> visitor) {
    return visitor.visitViewer(this);
  }

}
