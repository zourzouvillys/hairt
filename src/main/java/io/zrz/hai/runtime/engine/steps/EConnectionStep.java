package io.zrz.hai.runtime.engine.steps;

import io.zrz.hai.haiscript.IndentPrintWriter;
import io.zrz.hai.symbolic.HConnection;
import lombok.Getter;

public class EConnectionStep implements EStep {

  @Getter
  private final HConnection connection;
  @Getter
  private final EStep source;

  public EConnectionStep(EStep context, HConnection member) {
    this.source = context;
    this.connection = member;
  }

  @Override
  public EStepKind getStepKind() {
    return EStepKind.CONNECTION;
  }

  @Override
  public void dump(IndentPrintWriter w) {
    w.print(this.connection);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(EUtils.toString(this.source));
    sb.append(this.connection.getName());
    return sb.toString();
  }

  @Override
  public <R> R accept(EStepVisitor<R> visitor) {
    return visitor.visitConnection(this);
  }

}
