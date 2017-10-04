package io.zrz.hai.runtime.engine.planner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.zrz.hai.runtime.ZAny;
import io.zrz.hai.runtime.ZEntity;
import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.symbolic.expr.HVar;
import io.zrz.hai.symbolic.type.HNodeType;
import io.zrz.hai.symbolic.type.HTupleType;
import lombok.Getter;

public class EFrameContext {

  ZEntity self;
  HTupleType params;
  List<? extends ZAny> args;
  private Map<HVar, ZAny> vars;

  @Getter
  private final PlannerFrame frame;

  public EFrameContext(PlannerFrame frame, ZEntity context, HTupleType params, List<? extends ZAny> args) {
    this.frame = frame;
    this.self = Objects.requireNonNull(context);
    this.params = params;
    this.args = args;
  }

  /**
   * create an instance of the specified node.
   */

  public ZNode createNode(HNodeType type) {
    return this.frame.createNode(type);
  }

  public void set(HVar variable, ZAny value) {
    if (this.vars == null) {
      this.vars = new HashMap<>();
    }
    this.vars.put(variable, Objects.requireNonNull(value));
  }

  public Optional<ZAny> var(HVar variable) {
    if (this.vars == null) {
      return Optional.empty();
    }
    return this.vars.entrySet()
        .stream()
        .filter(f -> f.getKey().getName().equals(variable.getName()))
        .map(f -> f.getValue())
        .findFirst();
  }

  /**
   *
   */

  final AnyResolver anyResolver = new AnyResolver(this);
  final ArgResolver argResolver = new ArgResolver(this);
  final AssignableResolver assignableResolver = new AssignableResolver(this);
  final ConnectionResolver connectionResolver = new ConnectionResolver(this);
  final IndexableResolver indexableResolver = new IndexableResolver(this);
  final InvokeResolver invokeResolver = new InvokeResolver(this);
  final NodeResolver nodeResolver = new NodeResolver(this);
  final VoidVisitor voidResolver = new VoidVisitor(this);
  final ScalarResolver scalarResolver = new ScalarResolver(this);

}
