package io.zrz.hai.runtime.engine.steps;

import io.zrz.hai.syntax.IndentPrintWriter;
import io.zrz.hai.type.HConnection;
import io.zrz.hai.type.HDeclType;
import io.zrz.hai.type.HLink;
import io.zrz.hai.type.HMember;
import lombok.Getter;

/**
 * step which traverses an edge (which is the context) to the end node.
 */

public class EEdgeTraverseStep implements EStep {

  @Getter
  private final EStep source;
  private final HDeclType type;

  @Getter
  private HMember member;

  public EEdgeTraverseStep(EStep context, HConnection member) {
    this.source = context;
    this.member = member;
    this.type = member.getConnectionType().getNodeType();
  }

  public EEdgeTraverseStep(EStep context, HLink link) {
    this.source = context;
    this.member = link;
    this.type = link.getType();
  }

  public EEdgeTraverseStep(EStep context, HDeclType type) {
    this.source = context;
    this.type = type;
  }

  @Override
  public EStepKind getStepKind() {
    return EStepKind.EDGETRAVERSE;
  }

  @Override
  public void dump(IndentPrintWriter w) {
    w.print("EDGE TRAVERSAL ON ");
    w.print(this.source);
  }

  @Override
  public String toString() {
    return this.source + "->{endNode:" + this.type.getQualifiedName() + "}";
  }

  @Override
  public <R> R accept(EStepVisitor<R> visitor) {
    return visitor.visitEdgeTraverse(this);
  }

}
