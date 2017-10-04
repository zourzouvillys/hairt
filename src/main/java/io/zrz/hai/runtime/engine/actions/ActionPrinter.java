package io.zrz.hai.runtime.engine.actions;

import io.zrz.hai.syntax.IndentPrintWriter;

public class ActionPrinter implements EActionVisitor<Void> {

  private final IndentPrintWriter w;

  public ActionPrinter(IndentPrintWriter w) {
    this.w = w;
  }

  @Override
  public Void visitAddToConnection(AddToConnectionAction action) {

    this.w.print("ADD TO CONNECTION ");
    this.w.print(action.getStep());
    this.w.print(" += ");
    this.w.print(action.getTarget());

    return null;

  }

  @Override
  public Void visitSetState(SetStateAction action) {

    this.w.print("SET (");
    this.w.print(action.getStep());
    this.w.print(").");
    this.w.print(action.getState().getName());
    this.w.print(" = ");
    this.w.print(action.getValue());

    return null;

  }

  @Override
  public Void visitSetLink(SetLinkAction action) {

    this.w.print("SET  LINK (");
    this.w.print(action.getStep());
    this.w.print(").");
    this.w.print(action.getLink().getName());
    this.w.print(" = ");
    this.w.print(action.getValue());

    return null;
  }

}
