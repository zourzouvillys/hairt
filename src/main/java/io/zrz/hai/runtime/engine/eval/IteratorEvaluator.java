package io.zrz.hai.runtime.engine.eval;

import com.google.common.collect.Iterables;

import io.zrz.hai.runtime.engine.steps.EConnectionIteratorStep;
import io.zrz.hai.type.HConnection;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides an inte
 */

@Slf4j
public class IteratorEvaluator extends AbstractStepVisitor<Iterable<IEdge>> {

  private final NeoEvaluationContext ctx;

  public IteratorEvaluator(NeoEvaluationContext ctx) {
    this.ctx = ctx;
  }

  /**
   * generates an iterator to traverse a connection.
   */

  @Override
  public Iterable<IEdge> visitIterator(EConnectionIteratorStep step) {

    final INode start = step.getSource().accept(new NodeEvaluator(this.ctx));
    // this.ctx.getCache().get(step.getSource(), () -> step.getSource().accept(new
    // NodeEvaluator(this.ctx)));

    if (start == null) {
      log.debug("missing iterable source {}", step.getSource());
      return null;
    }

    final Iterable<IEdge> rel = start.connection(step.getSource().getConnection());

    final HConnection conn = step.getSource().getConnection();

    this.ctx.getContext().getStoreTracker().scan(step, start, conn);

    return Iterables.transform(rel, e -> {
      this.ctx.getContext().getStoreTracker().next(step, start, conn, e);
      return e;
    });

  }

}
