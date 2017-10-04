package io.zrz.hai.runtime.engine.steps;

import java.util.Objects;

import io.zrz.hai.haiscript.IndentPrintWriter;
import io.zrz.hai.runtime.ZAny;
import io.zrz.hai.symbolic.HConnection;
import lombok.Getter;

public class EConnectionIndexStep implements EStep {

  @Getter
  private final HConnection connection;

  @Getter
  private final EStep source;

  @Getter
  private final ZAny key;

  public EConnectionIndexStep(EStep context, HConnection member, ZAny key) {
    this.source = context;
    this.connection = member;
    this.key = Objects.requireNonNull(key);
  }

  @Override
  public EStepKind getStepKind() {
    return EStepKind.CONNECTION_INDEX;
  }

  @Override
  public void dump(IndentPrintWriter w) {
    w.print(this.connection);
  }

  @Override
  public String toString() {
    return this.source + "[" + this.key + "]";
  }

  @Override
  public <R> R accept(EStepVisitor<R> visitor) {
    return visitor.visitConnectionIndex(this);
  }

}
