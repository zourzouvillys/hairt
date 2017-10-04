package io.zrz.hai.runtime.engine.steps;

import io.zrz.hai.expr.HExpr;
import io.zrz.hai.expr.HTupleInitExpr;
import io.zrz.hai.syntax.IndentPrintWriter;
import lombok.Getter;

public class EConnectionIteratorStep implements EStep {

  @Getter
  private final EConnectionStep source;

  private HExpr first;
  private HExpr last;

  public EConnectionIteratorStep(EStep context, HTupleInitExpr args) {

    this.source = (EConnectionStep) context;

    if (args.getType().contains("first")) {
      this.first = args.expr("first");
    }

    if (args.getType().contains("last")) {
      this.last = args.expr("last");
    }

  }

  @Override
  public EStepKind getStepKind() {
    return EStepKind.ITERATOR;
  }

  @Override
  public void dump(IndentPrintWriter w) {
    w.print(this.toString());
    w.print(" ON ");
    w.print(this.source);
    if (this.first != null) {
      w.print(" FIRST ");
      w.print(this.first);
    }
    if (this.last != null) {
      w.print(" LAST ");
      w.print(this.last);
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("ITER:" + Integer.toHexString(this.hashCode()));
    return sb.toString();
  }

  @Override
  public <R> R accept(EStepVisitor<R> visitor) {
    return visitor.visitIterator(this);
  }
}
