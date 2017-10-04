package io.zrz.hai.runtime.engine.actions;

import io.zrz.hai.runtime.engine.steps.EStep;
import io.zrz.hai.symbolic.HConnection;
import lombok.Value;

@Value(staticConstructor = "of")
public class AddToConnectionAction implements EAction {

  EStep step;
  HConnection connection;
  EStep target;

  @Override
  public EActionKind getActionKind() {
    return EActionKind.ADD_TO_CONNECTION;
  }

  @Override
  public <T> T apply(EActionVisitor<T> visitor) {
    return visitor.visitAddToConnection(this);
  }

}
