package io.zrz.hai.runtime.engine.eval;

import javax.json.JsonString;
import javax.json.JsonValue;

import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.runtime.engine.planner.SelectionArgResolver.VarValue;
import io.zrz.hai.runtime.engine.steps.EConnectionIndexStep;
import io.zrz.hai.runtime.engine.steps.EEdgeScanStep;
import io.zrz.hai.runtime.engine.steps.EEdgeTraverseStep;
import io.zrz.hai.runtime.engine.steps.ELinkStep;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */

@Slf4j
public class EdgeEvaluator extends AbstractStepVisitor<IEdge> {

  private final NeoEvaluationContext ctx;

  public EdgeEvaluator(NeoEvaluationContext ctx2) {
    this.ctx = ctx2;
  }

  /**
   *
   */

  @Override
  public IEdge visitEdgeScan(EEdgeScanStep step) {
    return this.ctx.edge;
  }

  /**
   * traverse a link.
   */

  @Override
  public IEdge visitLink(ELinkStep step) {

    final INode start = this.ctx.getCache().get(step.getSource(), () -> {
      final INode node = step.getSource().accept(new NodeEvaluator(this.ctx));
      return node;
    });

    if (start == null) {
      log.debug("null link: {}", step.getLink());
      return null;
    }

    final IEdge rel = start.getLink(step.getLink());

    if (rel == null) {
      // no such link.
      log.debug("missing link {} in {}", step.getLink().getName(), start);
      return null;
    }

    this.ctx.getContext().getStoreTracker().readlink(start, step.getLink(), rel);

    return rel;

  }

  /**
   *
   */

  @Override
  public IEdge visitEdgeTraverse(EEdgeTraverseStep step) {

    final IEdge rel = step.getSource().accept(this);

    if (rel == null) {
      log.debug("Null edge traversal of {}", step.getSource());
      return null;
    }

    this.ctx.getContext().getStoreTracker().traverse(rel);

    return rel;
  }

  /**
   * fetches a single edge based on a unique constraint lookup.
   */

  @Override
  public IEdge visitConnectionIndex(EConnectionIndexStep step) {

    final INode start = this.ctx.getCache().get(step.getSource(), () -> step.getSource().accept(new NodeEvaluator(this.ctx)));

    if (start == null) {
      log.debug("Null edge of connection index");
      return null;
    }

    ZValue key = null;

    if (step.getKey() instanceof VarValue) {

      final VarValue var = (VarValue) step.getKey();
      final JsonValue val = this.ctx.vars.get(var.getName());
      key = ZValue.from(((JsonString) val).getString());

    } else {

      key = ZValue.from((String) ((ZValue) step.getKey()).getValue());

    }

    // this is the slow path. we scan each node. give feedback to the statistics
    // engine to trigger indexing if needed.

    for (final IEdge rel : start.connection(step.getConnection())) {
      if (rel.getEndNode().getProperty("username").equals(key)) {
        this.ctx.getContext().getStoreTracker().hit(start, step.getConnection(), key);
        return rel;
      }
    }

    this.ctx.getContext().getStoreTracker().miss(start, step.getConnection(), key);

    return null;

  }

}
