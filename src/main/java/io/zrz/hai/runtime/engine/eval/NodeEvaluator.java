package io.zrz.hai.runtime.engine.eval;

import io.zrz.hai.runtime.engine.steps.EConnectionStep;
import io.zrz.hai.runtime.engine.steps.EEdgeTraverseStep;
import io.zrz.hai.runtime.engine.steps.EInvokeStep;
import io.zrz.hai.runtime.engine.steps.ELinkStep;
import io.zrz.hai.runtime.engine.steps.ENewNodeStep;
import io.zrz.hai.runtime.engine.steps.EStep;
import io.zrz.hai.runtime.engine.steps.EViewerStage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NodeEvaluator extends AbstractStepVisitor<INode> {

  private final NeoEvaluationContext ctx;

  public NodeEvaluator(NeoEvaluationContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public INode visitViewer(EViewerStage step) {
    return this.ctx.viewNode;
  }

  @Override
  public INode visitLink(ELinkStep step) {

    return this.ctx.getCache().get(step.getSource(), () -> {

      final EStep start = step.getSource();

      final IEdge rel = start.accept(new EdgeEvaluator(this.ctx));

      if (rel == null) {
        log.debug("Null edge on link {} {}", start, step.getLink().getName());
        return null;
      }

      this.ctx.getContext().getStoreTracker().readlink(rel, step.getLink());

      return rel.getEndNode();

    });

  }

  @Override
  public INode visitConnection(EConnectionStep step) {
    return this.ctx.getCache().get(step.getSource(), () -> step.getSource().accept(this));
  }

  @Override
  public INode visitNewNode(ENewNodeStep step) {
    return this.ctx.getCache().get(step, () -> this.ctx.createNode(step.getType()));
  }

  @Override
  public INode visitEdgeTraverse(EEdgeTraverseStep step) {

    return this.ctx.getCache().get(step.getSource(), () -> {

      final IEdge rel = step.getSource().accept(new EdgeEvaluator(this.ctx));

      if (rel == null) {
        log.debug("Null edge on traversal of {} {}", step.getSource(), step.getMember());
        return null;
      }

      this.ctx.getContext().getStoreTracker().traverse(rel);

      return rel.getEndNode();

    });

  }

  @Override
  public INode visitInvoke(EInvokeStep step) {
    return this.ctx.getCache().get(step, () -> {
      step.getActions().forEach(action -> action.apply(new ActionVisitor(this.ctx)));
      return step.getResult().accept(this);
    });

  }

}
