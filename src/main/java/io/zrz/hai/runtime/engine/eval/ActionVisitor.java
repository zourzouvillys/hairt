package io.zrz.hai.runtime.engine.eval;

import javax.json.JsonString;

import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.runtime.engine.actions.AddToConnectionAction;
import io.zrz.hai.runtime.engine.actions.EActionVisitor;
import io.zrz.hai.runtime.engine.actions.SetLinkAction;
import io.zrz.hai.runtime.engine.actions.SetStateAction;
import io.zrz.hai.runtime.engine.planner.SelectionArgResolver.VarValue;

public class ActionVisitor implements EActionVisitor<Void> {

  private final NeoEvaluationContext ctx;

  public ActionVisitor(NeoEvaluationContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public Void visitAddToConnection(AddToConnectionAction action) {

    final INode start = action.getStep().accept(new NodeEvaluator(this.ctx));

    if (start == null) {
      throw new IllegalArgumentException("start");
    }

    final INode end = action.getTarget().accept(new NodeEvaluator(this.ctx));

    if (end == null) {
      throw new IllegalArgumentException("end");
    }

    start.connection(action.getConnection()).add(end);

    return null;

  }

  @Override
  public Void visitSetState(SetStateAction action) {

    final INode node = action.getStep().accept(new NodeEvaluator(this.ctx));

    if (node == null) {
      throw new IllegalArgumentException("missing expected node");
    }

    final ZValue value = action.getValue();

    if (value instanceof VarValue) {

      final VarValue var = (VarValue) value;
      final JsonString val = (JsonString) this.ctx.vars.asJsonObject().get(var.getName());
      node.setProperty(action.getState().getName(), ZValue.from(val.getString()));

    } else {

      switch (value.getType().getTypeKind()) {
        case STRING:
          node.setProperty(action.getState().getName(), ZValue.from((String) value.getValue()));
          return null;
      }

      throw new IllegalArgumentException("unable to calculate value for " + value.getClass());

    }

    return null;

  }

  @Override
  public Void visitSetLink(SetLinkAction action) {
    final INode node = action.getStep().accept(new NodeEvaluator(this.ctx));
    final INode target = action.getValue().accept(new NodeEvaluator(this.ctx));
    node.setLink(action.getLink(), target);
    return null;
  }

}
