package io.zrz.hai.runtime.engine.execution;

import java.util.List;
import java.util.stream.Collectors;

import io.zrz.hai.expr.AbstractHExprVisitor;
import io.zrz.hai.expr.HExpr;
import io.zrz.hai.runtime.ZAny;
import io.zrz.hai.runtime.ZEntity;
import io.zrz.hai.type.HExecutable;
import io.zrz.hai.type.HType;
import io.zrz.hai.type.HTypeUtils;

public class JitFrame {

  private final HExecutable method;

  public JitFrame(HExecutable exec) {
    this.method = exec;
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
    final EFrameContext fctx = new EFrameContext(ctx, this.method.getInputType(), args);
    // this.method.getExpression().accept(StackVisitor.createInstance());
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
            return fctx.nodeResolver;
          case CONNECTION:
            return fctx.connectionResolver;
          case EDGE:
          case ENUM:
          case EVENT:
          case INTERFACE:
          case STRUCT:
          case TYPE:
          case VIEW:
          default:
            throw new IllegalArgumentException(HTypeUtils.declKind(type).toString());
        }
      case ARRAY:
      case BOOLEAN:
      case DOUBLE:
      case INT:
      case INTERSECTION:
      case LAMBDA:
      case NEVER:
      case STRING:
      case TUPLE:
      case UNION:
      case VOID:
      case WILDCARD:
      default:
        throw new IllegalArgumentException(type.getTypeKind().toString());
    }
  }

}
