package io.zrz.hai.runtime.engine.steps;

import io.zrz.hai.haiscript.IndentPrintWriter;
import io.zrz.hai.symbolic.type.HConnectionType;
import lombok.Getter;

/**
 * scans the edges in a connection, applying the specified expressions to each
 * edge.
 *
 * each edgescan source is unique within a context, and must not be shared.
 *
 */

public class EEdgeScanStep implements EStep {

  @Getter
  private final EStep source;

  public EEdgeScanStep(EStep source, HConnectionType conn, String prefix) {
    this.source = source;
  }

  @Override
  public EStepKind getStepKind() {
    return EStepKind.EDGESCAN;
  }

  @Override
  public void dump(IndentPrintWriter w) {
    w.print("EDGESCAN ");
    w.print(Integer.toHexString(this.hashCode()));
    w.print(" ON ");
    w.print(this.source);
  }

  @Override
  public String toString() {
    return "EDGESCAN(" + Integer.toHexString(this.hashCode()) + ")";
  }

  @Override
  public <R> R accept(EStepVisitor<R> visitor) {
    return visitor.visitEdgeScan(this);
  }

}
