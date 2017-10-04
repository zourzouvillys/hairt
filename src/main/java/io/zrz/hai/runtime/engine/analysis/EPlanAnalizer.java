package io.zrz.hai.runtime.engine.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.zrz.hai.haiscript.IndentPrintWriter;
import io.zrz.hai.runtime.engine.EExecutionPlan;
import io.zrz.hai.runtime.engine.steps.EConnectionCountExpr;
import io.zrz.hai.runtime.engine.steps.EConnectionIndexStep;
import io.zrz.hai.runtime.engine.steps.EConnectionIteratorStep;
import io.zrz.hai.runtime.engine.steps.EConnectionStep;
import io.zrz.hai.runtime.engine.steps.EEdgeScanStep;
import io.zrz.hai.runtime.engine.steps.EEdgeTraverseStep;
import io.zrz.hai.runtime.engine.steps.EExpr;
import io.zrz.hai.runtime.engine.steps.EInvokeStep;
import io.zrz.hai.runtime.engine.steps.ELinkStep;
import io.zrz.hai.runtime.engine.steps.ENewNodeStep;
import io.zrz.hai.runtime.engine.steps.EResultIntent;
import io.zrz.hai.runtime.engine.steps.EResultIntent.Nested;
import io.zrz.hai.runtime.engine.steps.EResultIntent.Value;
import io.zrz.hai.runtime.engine.steps.EScalarStep;
import io.zrz.hai.runtime.engine.steps.EStateExpr;
import io.zrz.hai.runtime.engine.steps.EStep;
import io.zrz.hai.runtime.engine.steps.EStepVisitor;
import io.zrz.hai.runtime.engine.steps.EViewerStage;
import lombok.Getter;

/**
 * performs binding time analysis.
 *
 * at this point, we don't have any input variables. a second pass
 * (RuntimeAnalysis) is used to perform last-minute optimisations based on the
 * actual input values.
 *
 * the result of the analysis is an execution plan and result shape, with
 * ordered execution steps with dependencies that can be handled directly by the
 * execution engine.
 *
 */

public class EPlanAnalizer implements EStepVisitor<EStep> {

  private final EExecutionPlan plan;

  /**
   * any steps which are identical are folded into a single instance.
   */

  private final Map<EStep, ExprHolder> steps = new HashMap<>();

  /**
   * each expression has a holder, which provides the list of dependencies and
   * dependent expressions, as well as the value output or edge scans is it used
   * for.
   */

  public static class ExprHolder {

    @Getter
    final EStep step;

    // expressions which depend on this
    @Getter
    private final Set<ExprHolder> dependencies = new HashSet<>();

    // expressions which this value depends on.
    @Getter
    private final Set<ExprHolder> dependsOn = new HashSet<>();

    // outputs that this value contributes to
    @Getter
    private final Set<Value> outputs = new HashSet<>();

    // edges that this expression provides
    @Getter
    private final Set<Nested> nested = new HashSet<>();

    ExprHolder(EStep step) {
      this.step = step;
    }

    public static ExprHolder of(EStep step) {
      return new ExprHolder(step);
    }

    void dependency(ExprHolder dep) {
      this.dependencies.add(dep);
      dep.dependsOn.add(this);
    }

    @Override
    public String toString() {
      return "DEPS";
    }

    public void output(Value v) {
      this.outputs.add(v);
    }

    public void output(Nested nested) {
      this.nested.add(nested);
    }

  }

  /**
   * @param rootIntent
   *
   */

  public EPlanAnalizer(EExecutionPlan plan) {
    this.plan = plan;
  }

  /**
   *
   */

  public void analize() {

    final IndentPrintWriter w = new IndentPrintWriter(System.err);

    w.println("========");

    this.plan.getRootIntent().dump(w);
    this.analize(this.plan.getRootIntent());
    this.plan.setExpressions(this.steps.values());

    for (final ExprHolder expr : this.plan.getExpressions()) {
      expr.getStep().dump(w);
      w.forceLine();
    }

    w.println("========");

    w.println("========");

    // find any without dependencies, and scan.
    this.steps.values()
        .stream()
        .filter(f -> f.dependsOn.isEmpty())
        .forEach(holder -> {

          w.print(holder.step + ": ");
          holder.step.dump(w);
          w.forceLine();

          w.inc();
          this.deps(w, holder, new HashSet<>());
          w.dec();

        });

    w.println("========");

    w.flush();

  }

  private void deps(IndentPrintWriter w, ExprHolder holder, Set<ExprHolder> visited) {

    if (!visited.add(holder)) {
      throw new IllegalArgumentException("recursive dependency");
    }

    holder.dependencies.forEach(dep -> {

      if (!dep.outputs.isEmpty()) {

        w.print("[");
        w.print(dep.outputs.stream().map(x -> x.getPath()).collect(Collectors.joining(", ")));
        w.print("]: ");

      } else if (!dep.nested.isEmpty()) {

        w.print("[");
        w.print(dep.nested.stream().map(x -> x.getPath()).collect(Collectors.joining(", ")));
        w.print("]: ");

      } else {

        w.println(dep.step);
        w.inc();
        this.deps(w, dep, visited);
        w.forceLine();
        w.dec();
        return;

      }

      dep.step.dump(w);

      w.inc();
      this.deps(w, dep, visited);
      w.forceLine();
      w.dec();

    });

  }

  private void analize(EResultIntent intent) {
    intent.getValues().values().forEach(v -> this.analize(v));
    intent.getValues().values().forEach(v -> this.analize(v.getType()));
    intent.getNested().values().forEach(v -> this.analize(v.getScan()));
    intent.getNested().values().forEach(v -> this.analize(v.getIntent()));
    intent.getNested().values().forEach(v -> this.analize(intent, v));
  }

  private void analize(Value v) {
    this.get((EStep) v.getType()).output(v);
  }

  private void analize(EResultIntent parent, Nested nested) {
    this.get(nested.getScan()).output(nested);
  }

  private void analize(EExpr type) {
    final EStep step = ((EStep) type);
    this.analize(step);
  }

  /**
   *
   */

  private void analize(EStep step) {
    step.accept(this);
  }

  private ExprHolder get(EStep step) {

    final boolean added = !this.steps.containsKey(step);

    final ExprHolder res = this.steps.computeIfAbsent(step, s -> ExprHolder.of(step));

    if (added) {
      this.analize(step);
    }

    return res;

  }

  // --------------------------------------------------------------------------------
  //
  // --------------------------------------------------------------------------------

  @Override
  public EStep visitViewer(EViewerStage step) {
    return step;
  }

  @Override
  public EStep visitNewNode(ENewNodeStep step) {
    return step;
  }

  @Override
  public EStep visitEdgeScan(EEdgeScanStep step) {
    this.get(step.getSource()).dependency(this.get(step));
    return step;
  }

  @Override
  public EStep visitConnection(EConnectionStep step) {
    this.get(step.getSource()).dependency(this.get(step));
    return step;
  }

  @Override
  public EStep visitCount(EConnectionCountExpr step) {
    this.get(step.getSource()).dependency(this.get(step));
    return step;
  }

  @Override
  public EStep visitLink(ELinkStep step) {
    this.get(step.getSource()).dependency(this.get(step));
    return step;
  }

  @Override
  public EStep visitConnectionIndex(EConnectionIndexStep step) {
    this.get(step.getSource()).dependency(this.get(step));
    return step;
  }

  @Override
  public EStep visitEdgeTraverse(EEdgeTraverseStep step) {
    this.get(step.getSource()).dependency(this.get(step));
    return step;
  }

  @Override
  public EStep visitField(EStateExpr step) {
    this.get(step.getSource()).dependency(this.get(step));
    return null;
  }

  @Override
  public EStep visitIterator(EConnectionIteratorStep step) {
    this.get(step.getSource()).dependency(this.get(step));
    return null;
  }

  @Override
  public EStep visitInvoke(EInvokeStep step) {
    this.get(step.getResult()).dependency(this.get(step));
    return null;
  }

  @Override
  public EStep visitScalar(EScalarStep step) {
    return null;
  }

}
