package io.zrz.hai.runtime.engine.actions;

import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.runtime.engine.steps.EStep;
import io.zrz.hai.type.HState;
import lombok.Value;

@Value(staticConstructor = "of")
public class SetStateAction implements EAction {

  EStep step;
  HState state;
  ZValue value;

  @Override
  public EActionKind getActionKind() {
    return EActionKind.SET_STATE;
  }

  @Override
  public <T> T apply(EActionVisitor<T> visitor) {
    return visitor.visitSetState(this);
  }

}
