package io.zrz.hai.runtime.engine.execution;

import io.zrz.hai.runtime.ZAny;
import io.zrz.hai.runtime.ZConnection;
import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.symbolic.HConnection;
import io.zrz.hai.symbolic.HLink;
import io.zrz.hai.symbolic.HState;
import io.zrz.hai.symbolic.expr.HVarExpr;

public interface EAssignableTarget {

  void assign(EFrameContext ctx, ZAny value);

  static EAssignableTarget var(HVarExpr var) {
    return (EFrameContext ctx, ZAny val) -> {
      ctx.set(var.getVariable(), val);
    };
  }

  static EAssignableTarget member(final ZNode instance, final HState state) {
    return (EFrameContext ctx, ZAny val) -> {
      instance.setState(state, (ZValue) val);
    };
  }

  static EAssignableTarget member(final ZNode instance, final HConnection connection) {
    return (EFrameContext ctx, ZAny val) -> {
      final ZConnection conn = instance.getConnection(connection);
      conn.addNode((ZNode) val);
    };
  }

  static EAssignableTarget member(final ZNode instance, final HLink link) {
    return (EFrameContext ctx, ZAny val) -> {
      instance.createLinkTo((ZNode) val, link);
    };
  }

}
