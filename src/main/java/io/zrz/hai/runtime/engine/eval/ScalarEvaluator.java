package io.zrz.hai.runtime.engine.eval;

import javax.json.Json;
import javax.json.JsonValue;

import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.runtime.engine.steps.EConnectionCountExpr;
import io.zrz.hai.runtime.engine.steps.EStateExpr;

public class ScalarEvaluator extends AbstractStepVisitor<JsonValue> {

  private final NeoEvaluationContext ctx;

  public ScalarEvaluator(NeoEvaluationContext ctx) {
    this.ctx = ctx;
  }

  /**
   * a full count of the connection.
   */

  @Override
  public JsonValue visitCount(EConnectionCountExpr step) {
    final INode start = this.ctx.getCache().get(step.getSource(), () -> step.getSource().accept(new NodeEvaluator(this.ctx)));
    if (start == null) {
      return JsonValue.NULL;
    }
    this.ctx.getContext().getStoreTracker().count(start, step.getSource().getConnection());
    return Json.createValue(start.connection(step.getSource().getConnection()).count());
  }

  /**
   * fetch a specific field value.
   */

  @Override
  public JsonValue visitField(EStateExpr step) {

    final INode node = this.ctx.getCache().get(step.getSource(), () -> step.getSource().accept(new NodeEvaluator(this.ctx)));

    if (node == null) {
      return null;
    }

    if (step.getField().getName().equals("__typename")) {
      return Json.createValue(node.getType().getQualifiedName());
    }

    final String propName = ENeoUtils.propertyFor(step.getField());

    if (!node.hasProperty(propName)) {
      // missing property.
      return null;
    }

    final ZValue prop = node.getProperty(propName);

    if (prop == null) {
      return JsonValue.NULL;
    }

    return prop.toJson();

  }

}
