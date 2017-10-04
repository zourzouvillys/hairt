package io.zrz.hai.runtime.engine.steps;

public interface EStepVisitor<R> {

  R visitViewer(EViewerStage step);

  R visitLink(ELinkStep step);

  R visitEdgeTraverse(EEdgeTraverseStep step);

  R visitField(EStateExpr step);

  R visitConnection(EConnectionStep step);

  R visitIterator(EConnectionIteratorStep step);

  R visitConnectionIndex(EConnectionIndexStep step);

  R visitEdgeScan(EEdgeScanStep step);

  R visitCount(EConnectionCountExpr step);

  R visitNewNode(ENewNodeStep step);

  R visitInvoke(EInvokeStep step);

  R visitScalar(EScalarStep step);

}
