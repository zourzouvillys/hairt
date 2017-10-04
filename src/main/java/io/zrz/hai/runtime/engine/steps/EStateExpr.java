package io.zrz.hai.runtime.engine.steps;

import java.util.Objects;

import io.zrz.hai.haiscript.IndentPrintWriter;
import io.zrz.hai.symbolic.HState;
import lombok.Getter;

public class EStateExpr implements EExpr, EStep {

  @Getter
  private final EStep source;

  @Getter
  private final HState field;

  public EStateExpr(EStep source, HState field) {
    this.source = Objects.requireNonNull(source);
    this.field = field;
  }

  @Override
  public void dump(IndentPrintWriter w) {
    w.print("SCALAR ");
    w.print(EUtils.toString(this.source));
    w.append(this.field.getName());
  }

  @Override
  public String toString() {
    final StringBuilder w = new StringBuilder();
    w.append(EUtils.toString(this.source));
    // w.append(this.field.getDeclaringType().getQualifiedName());
    // w.append(".");
    w.append(this.field.getName());
    return w.toString();
  }

  @Override
  public EStepKind getStepKind() {
    return EStepKind.FIELD;
  }

  @Override
  public <R> R accept(EStepVisitor<R> visitor) {
    return visitor.visitField(this);
  }

}
