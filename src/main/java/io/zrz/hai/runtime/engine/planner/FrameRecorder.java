package io.zrz.hai.runtime.engine.planner;

import java.util.LinkedList;
import java.util.List;

import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.runtime.engine.actions.AddToConnectionAction;
import io.zrz.hai.runtime.engine.actions.EAction;
import io.zrz.hai.runtime.engine.actions.SetLinkAction;
import io.zrz.hai.runtime.engine.actions.SetStateAction;
import io.zrz.hai.runtime.engine.steps.EConnectionStep;
import io.zrz.hai.runtime.engine.steps.EStep;
import io.zrz.hai.symbolic.HConnection;
import io.zrz.hai.symbolic.HLink;
import io.zrz.hai.symbolic.HState;
import io.zrz.hai.symbolic.type.HNodeType;
import lombok.Getter;

public class FrameRecorder {

  @Getter
  private final List<EAction> actions = new LinkedList<>();

  public void set(EStep node, HState state, ZValue value) {
    this.actions.add(SetStateAction.of(node, state, value));
  }

  public void add(EConnectionStep step, HConnection connection, EStep target) {
    this.actions.add(AddToConnectionAction.of(step, connection, target));
  }

  public void link(EStep step, HLink link, EStep target) {
    this.actions.add(SetLinkAction.of(step, link, target));
  }

  public void create(HNodeType type) {

  }

}
