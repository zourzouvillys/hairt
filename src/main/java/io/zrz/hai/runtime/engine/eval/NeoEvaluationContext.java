package io.zrz.hai.runtime.engine.eval;

import javax.json.JsonObject;

import io.zrz.hai.type.HDeclType;
import io.zrz.hai.type.HModule;
import lombok.Getter;

/**
 * a context for evaluating processing.
 *
 * a new instance of this is created for each edge scan level, with the parent
 * as the previous one.
 *
 */

public class NeoEvaluationContext {

  /**
   * the operation execution context.
   */

  @Getter
  private final ENeoExecutionContext context;

  /**
   * the parent context, if any.
   */

  @Getter
  private NeoEvaluationContext parent;

  /**
   * the view node.
   */

  @Getter
  public INode viewNode;

  /**
   * variables.
   */

  public JsonObject vars;

  /**
   * the cache for this context.
   */

  @Getter
  private final NeoResultCache cache;

  /**
   * the current edge which is being scanned.
   */

  @Getter
  IEdge edge;

  /**
   *
   */

  public NeoEvaluationContext(ENeoExecutionContext exec) {
    this.cache = new NeoResultCache();
    this.viewNode = exec.viewNode;
    this.context = exec;
    this.vars = exec.vars;
  }

  /**
   *
   */

  public NeoEvaluationContext(NeoEvaluationContext ctx, IEdge rel) {
    this.cache = new NeoResultCache(ctx.cache);
    this.viewNode = ctx.viewNode;
    this.context = ctx.context;
    this.parent = ctx;
    this.edge = rel;
    this.vars = ctx.vars;
  }

  /**
   *
   */

  public HModule getModule() {
    return this.context.getModule();
  }

  /**
   *
   */

  public INode createNode(HDeclType type) {
    return this.context.createNode(type);
  }

}
