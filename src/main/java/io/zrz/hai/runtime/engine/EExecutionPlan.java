package io.zrz.hai.runtime.engine;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import io.zrz.hai.haiscript.IndentPrintWriter;
import io.zrz.hai.runtime.compile.parse.GExecutable;
import io.zrz.hai.runtime.engine.analysis.EPlanAnalizer.ExprHolder;
import io.zrz.hai.runtime.engine.steps.EResultIntent;
import io.zrz.hai.runtime.engine.steps.EStep;
import lombok.Getter;

/**
 * a single packaged execution plan, ready to be run.
 */

public class EExecutionPlan {

  @Getter
  private final GExecutable exec;

  @Getter
  private final EResultIntent rootIntent;

  @Getter
  private ImmutableList<ExprHolder> expressions;

  public EExecutionPlan(GExecutable exec, EStep step, EResultIntent rootIntent) {
    this.exec = exec;
    this.rootIntent = rootIntent;
  }

  public void dump(IndentPrintWriter w) {
    w.println("PLAN");
  }

  public void setExpressions(Collection<ExprHolder> values) {
    this.expressions = ImmutableList.copyOf(values);
  }

}
