package io.zrz.hai.runtime.engine.eval;

import io.zrz.hai.runtime.engine.steps.EConnectionCountExpr;
import io.zrz.hai.runtime.engine.steps.EConnectionIndexStep;
import io.zrz.hai.runtime.engine.steps.EConnectionIteratorStep;
import io.zrz.hai.runtime.engine.steps.EConnectionStep;
import io.zrz.hai.runtime.engine.steps.EEdgeScanStep;
import io.zrz.hai.runtime.engine.steps.EEdgeTraverseStep;
import io.zrz.hai.runtime.engine.steps.EInvokeStep;
import io.zrz.hai.runtime.engine.steps.ELinkStep;
import io.zrz.hai.runtime.engine.steps.ENewNodeStep;
import io.zrz.hai.runtime.engine.steps.EScalarStep;
import io.zrz.hai.runtime.engine.steps.EStateExpr;
import io.zrz.hai.runtime.engine.steps.EStep;
import io.zrz.hai.runtime.engine.steps.EStepVisitor;
import io.zrz.hai.runtime.engine.steps.EViewerStage;

public class AbstractStepVisitor<T> implements EStepVisitor<T> {

  private T visitDefault(EStep step) {
    throw new IllegalArgumentException(this.getClass().toString() + ": " + step.getStepKind().toString());
  }

  @Override
  public T visitViewer(EViewerStage step) {
    return this.visitDefault(step);
  }

  @Override
  public T visitLink(ELinkStep step) {
    return this.visitDefault(step);
  }

  @Override
  public T visitEdgeTraverse(EEdgeTraverseStep step) {
    return this.visitDefault(step);
  }

  @Override
  public T visitField(EStateExpr step) {
    return this.visitDefault(step);
  }

  @Override
  public T visitConnection(EConnectionStep step) {
    return this.visitDefault(step);
  }

  @Override
  public T visitIterator(EConnectionIteratorStep step) {
    return this.visitDefault(step);
  }

  @Override
  public T visitConnectionIndex(EConnectionIndexStep step) {
    return this.visitDefault(step);
  }

  @Override
  public T visitEdgeScan(EEdgeScanStep step) {
    return this.visitDefault(step);
  }

  @Override
  public T visitCount(EConnectionCountExpr step) {
    return this.visitDefault(step);
  }

  @Override
  public T visitNewNode(ENewNodeStep step) {
    return this.visitDefault(step);
  }

  @Override
  public T visitInvoke(EInvokeStep step) {
    return this.visitDefault(step);
  }

  @Override
  public T visitScalar(EScalarStep step) {
    return this.visitDefault(step);
  }

}
