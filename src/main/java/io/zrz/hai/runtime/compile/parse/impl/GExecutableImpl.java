package io.zrz.hai.runtime.compile.parse.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.zrz.hai.runtime.compile.facade.MArgument;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.runtime.compile.facade.MViewContext;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.runtime.compile.parse.GCostEstimator;
import io.zrz.hai.runtime.compile.parse.GDocument;
import io.zrz.hai.runtime.compile.parse.GExecutable;
import io.zrz.hai.runtime.engine.EExecutionPlan;
import io.zrz.hai.runtime.engine.EPlannerContext;
import io.zrz.hai.syntax.IndentPrintWriter;
import io.zrz.hai.type.HTupleType;
import io.zrz.hai.type.HType;
import io.zrz.hai.type.HTypeUtils;
import lombok.Getter;

public class GExecutableImpl implements GExecutable {

  @Getter
  private final String name;

  @Getter
  private final MViewKind kind;

  @Getter
  private final HashMap<String, MInputType> vars = new HashMap<>();

  @Getter
  private final MInputType inputType;

  @Getter
  private final List<GSelectionImpl> selections = new LinkedList<>();

  @Getter
  private final MViewContext viewContext;

  private final GDocument doc;

  @Getter
  private int estimatedCost;

  @Getter
  private EExecutionPlan executionPlan;

  /**
   *
   */

  public GExecutableImpl(GDocument doc, String name, MViewKind viewKnd, MInputType input, MViewContext ctx) {
    this.doc = doc;
    this.name = name;
    this.kind = viewKnd;
    this.inputType = input;
    this.viewContext = ctx;
  }

  @Override
  public Optional<MArgument> var(String name) {
    return Optional.ofNullable(this.inputType.findField(name));
  }

  /**
   *
   */

  public void param(String name, MInputType input) {
    if (this.vars.containsKey(name)) {
      throw new IllegalArgumentException(String.format("parameter '%s' is already declared", name));
    }
    this.vars.put(name, input);
  }

  /**
   *
   */

  public void add(GSelectionImpl select) {
    this.selections.add(Objects.requireNonNull(select));
  }

  /**
   * the output type is built by merging the fields that are being select, and any
   * fragments that are spread.
   */

  @Override
  public HTupleType getOutputType() {

    final Map<String, HType> fields = new HashMap<>();

    for (final GSelectionImpl sel : this.selections) {
      sel.mergeOutputType(this.viewContext.getTypeLoader(), fields);
    }

    return HTypeUtils.createTuple(fields);

  }

  public void dump(IndentPrintWriter w) {

    w.print(this.kind);
    w.print(" ");
    w.print(this.name);

    w.print(this.inputType);

    w.println(" {");

    w.inc();
    this.selections.forEach(s -> s.dump(w));
    w.dec();

    w.println("}");

  }

  public void resolve(GDocumentImpl doc) {
    this.selections.forEach(sel -> sel.resolve(doc, this));
  }

  @Override
  public GDocument getDocument() {
    return this.doc;
  }

  public void seal() {
    this.estimatedCost = GCostEstimator.calculate(this);
    this.executionPlan = new EPlannerContext(this).plan();
  }

}
