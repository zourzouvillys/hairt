package io.zrz.hai.runtime.engine.planner;

import java.util.List;
import java.util.Objects;

import io.zrz.hai.runtime.ZAny;
import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.symbolic.HMethod;

public class EInvokeTarget {

  private final ZNode instance;
  private final HMethod method;

  public EInvokeTarget(ZNode instance, HMethod method) {
    this.instance = Objects.requireNonNull(instance);
    this.method = Objects.requireNonNull(method);
  }

  public static EInvokeTarget of(ZNode instance, HMethod method) {
    return new EInvokeTarget(instance, method);
  }

  public ZAny invoke(EFrameContext ctx, List<ZAny> values) {
    final PlannerFrame frame = new PlannerFrame(this.method.getExecutable(), ctx.getFrame().getPlanner(), ctx.getFrame().getRecorder());
    return frame.run(this.method.getReturnType(), this.instance, values);
  }

}
