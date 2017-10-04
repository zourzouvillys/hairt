package io.zrz.hai.runtime.engine.eval;

import java.util.Map.Entry;

import javax.json.JsonObject;
import javax.json.JsonValue;

import io.zrz.hai.runtime.compile.facade.MViewContext;
import io.zrz.hai.runtime.engine.EExecutionPlan;
import io.zrz.hai.runtime.engine.concurrency.ChangeHandle;
import io.zrz.hai.runtime.engine.concurrency.SnapshotHandle;
import io.zrz.hai.runtime.engine.results.EResultCollector;
import io.zrz.hai.runtime.engine.steps.EResultIntent;
import io.zrz.hai.runtime.engine.steps.EResultIntent.Nested;
import io.zrz.hai.runtime.engine.steps.EResultIntent.Value;
import io.zrz.hai.type.HDeclType;
import io.zrz.hai.type.HModule;
import io.zrz.hai.runtime.engine.steps.EStep;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * performs execution using neo4j at the backing store.
 */

@Slf4j
public class ENeoExecutionContext {

  final INode viewNode;
  final JsonObject vars;
  final EResultCollector results;
  final EExecutionPlan plan;

  /**
   * the handle for reading which provides a consistent and stable view of the
   * store.
   */

  @Getter
  private SnapshotHandle readHandle;

  /**
   * The write handle which is the open changeset, and provides conflict
   * detection. If this is a read only context, then it will be null.
   */

  @Getter
  private ChangeHandle writeHandle;

  @Getter
  private final NeoStoreContext storeTracker;

  @Getter
  private final HModule module;

  @Getter
  private final MViewContext view;
  private final NeoStoreHandle store;

  public ENeoExecutionContext(EExecutionPlan plan, NeoStoreHandle store, INode viewNode, JsonObject vars, EResultCollector results) {
    this.module = plan.getExec().getViewContext().getModule();
    this.view = plan.getExec().getViewContext();
    this.plan = plan;
    this.store = store;
    this.viewNode = viewNode;
    this.vars = vars;
    this.storeTracker = new NeoWorkContext(this);
    this.results = results;
  }

  /**
   * executes this plan instance, providing the results to the collector.
   */

  public void execute() {
    this.single(this.results, this.plan.getRootIntent(), new NeoEvaluationContext(this));
    this.results.close();
  }

  /**
   * handle a single result intent level.
   */

  void single(EResultCollector results, EResultIntent intent, NeoEvaluationContext ctx) {

    // process each scalar value in this result intent

    for (final Entry<String, Value> e : intent.getValues().entrySet()) {
      final Value v = e.getValue();
      final EStep step = ((EStep) v.getType());
      final JsonValue res = step.accept(new ScalarEvaluator(ctx));
      results.putValue(e.getKey(), res);
    }

    // iterate through each nested result intent

    for (final Entry<String, Nested> e : intent.getNested().entrySet()) {

      final Nested nested = e.getValue();

      final EStep source = nested.getScan().getSource();

      final Iterable<IEdge> rels = source.accept(new IteratorEvaluator(ctx));

      if (rels == null) {
        log.debug("skipping nested {}, no iterator", source);
        continue;
      }

      for (final IEdge rel : rels) {
        final EResultCollector sub = results.createNestedInstance(e.getValue().getPath());
        this.single(sub, e.getValue().getIntent(), new NeoEvaluationContext(ctx, rel));
        sub.close();
      }

    }

  }

  /**
   *
   */

  public INode createNode(HDeclType type) {
    final INode node = this.store.createNode(type);
    this.getStoreTracker().create(node);
    return node;
  }

}
