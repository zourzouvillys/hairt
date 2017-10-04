package io.zrz.hai.runtime.engine.steps;

import io.zrz.hai.syntax.IndentPrintWriter;
import io.zrz.hai.type.HLink;
import lombok.Getter;

public class ELinkStep implements EStep {

  @Getter
  private final EStep source;

  @Getter
  private final HLink link;

  public ELinkStep(EStep context, HLink member) {
    this.source = context;
    this.link = member;
  }

  @Override
  public EStepKind getStepKind() {
    return EStepKind.LINK;
  }

  @Override
  public void dump(IndentPrintWriter w) {
    // TODO Auto-generated method stub

  }

  @Override
  public String toString() {
    return EUtils.toString(this.source) + this.link.getName();
  }

  @Override
  public <R> R accept(EStepVisitor<R> visitor) {
    return visitor.visitLink(this);
  }

}
