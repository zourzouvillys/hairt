package io.zrz.hai.runtime.engine.actions;

import io.zrz.hai.runtime.engine.steps.EStep;
import io.zrz.hai.type.HLink;
import lombok.Value;

@Value(staticConstructor = "of")
public class SetLinkAction implements EAction {

  EStep step;
  HLink link;
  EStep value;

  @Override
  public EActionKind getActionKind() {
    return EActionKind.SET_LINK;
  }

  @Override
  public <T> T apply(EActionVisitor<T> visitor) {
    return visitor.visitSetLink(this);
  }

}
