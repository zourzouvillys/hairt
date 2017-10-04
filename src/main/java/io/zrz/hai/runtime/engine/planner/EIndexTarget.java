package io.zrz.hai.runtime.engine.planner;

import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.type.HMember;
import lombok.Value;

@Value(staticConstructor = "of")
public class EIndexTarget {

  ZNode instance;
  HMember member;

}
