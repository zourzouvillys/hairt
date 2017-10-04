package io.zrz.hai.runtime.engine.planner;

import java.util.List;
import java.util.stream.Collectors;

import io.zrz.hai.runtime.ZAny;
import io.zrz.hai.runtime.ZEntity;
import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.runtime.engine.EPlannerContext;
import io.zrz.hai.symbolic.HExecutable;
import io.zrz.hai.symbolic.HTypeUtils;
import io.zrz.hai.symbolic.expr.AbstractHExprVisitor;
import io.zrz.hai.symbolic.expr.HExpr;
import io.zrz.hai.symbolic.type.HNodeType;
import io.zrz.hai.symbolic.type.HType;
import lombok.Getter;

public class PlannerFrame {

  private final HExecutable method;

  @Getter
  private final EPlannerContext planner;

  @Getter
  private final FrameRecorder recorder;

  public PlannerFrame(HExecutable exec, EPlannerContext planner) {
    this.method = exec;
    this.planner = planner;
    this.recorder = new FrameRecorder();
  }

  public PlannerFrame(HExecutable exec, EPlannerContext planner, FrameRecorder recorder) {
    this.method = exec;
    this.planner = planner;
    this.recorder = recorder;
  }

  public ZNode createNode(HNodeType type) {
    return this.planner.createNode(this, type);
  }

  /**
   * start execution of this frame.
   *
   * @param hType
   *
   * @param args
   *          HConstExpr values which are fully resolved.
   */

  public static List<ZAny> convert(List<? extends HExpr> args) {
    return args
        .stream()
        .map(index -> index.accept(new ArgResolver(null)))
        .collect(Collectors.toList());
  }

  /**
   *
   */

  public ZAny run(HType expected, ZEntity ctx, List<? extends ZAny> args) {
    final EFrameContext fctx = new EFrameContext(this, ctx, this.method.getInputType(), args);
    return this.method.getExpression().accept(this.visitorFor(expected, fctx));
  }

  /**
   *
   */

  <T> AbstractHExprVisitor<? extends ZAny> visitorFor(HType type, EFrameContext fctx) {
    switch (type.getTypeKind()) {
      case DECL:
        switch (HTypeUtils.declKind(type)) {
          case NODE:
          case TYPE:
            return fctx.nodeResolver;
          case CONNECTION:
            return fctx.connectionResolver;
          case EDGE:
          case ENUM:
          case EVENT:
          case INTERFACE:
          case STRUCT:
          case VIEW:
          default:
            throw new IllegalArgumentException(HTypeUtils.declKind(type).toString());
        }
      case STRING:
      case DOUBLE:
      case INT:
      case BOOLEAN:
        return fctx.scalarResolver;
      case ARRAY:
      case INTERSECTION:
      case LAMBDA:
      case NEVER:
      case TUPLE:
      case UNION:
      case VOID:
      case WILDCARD:
      default:
        throw new IllegalArgumentException(type.getTypeKind().toString());
    }
  }

}
