package io.zrz.hai.runtime.engine.steps;

import java.util.List;

import io.zrz.hai.haiscript.IndentPrintWriter;
import io.zrz.hai.runtime.engine.actions.ActionPrinter;
import io.zrz.hai.runtime.engine.actions.EAction;
import lombok.Getter;

public class EInvokeStep implements EStep {

  @Getter
  private final EStep result;

  @Getter
  private final List<EAction> actions;

  public EInvokeStep(List<EAction> actions, EStep result) {
    this.result = result;
    this.actions = actions;
  }

  @Override
  public EStepKind getStepKind() {
    return EStepKind.INVOKE;
  }

  @Override
  public void dump(IndentPrintWriter w) {
    w.println("INVOKE");
    w.inc();
    this.actions.forEach(s -> {
      s.apply(new ActionPrinter(w));
      w.forceLine();
    });
    w.dec();
  }

  @Override
  public <R> R accept(EStepVisitor<R> visitor) {
    return visitor.visitInvoke(this);
  }

  @Override
  public String toString() {
    return "INVOKE";
  }

}
