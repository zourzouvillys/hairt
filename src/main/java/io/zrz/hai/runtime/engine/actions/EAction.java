package io.zrz.hai.runtime.engine.actions;

public interface EAction {

  EActionKind getActionKind();

  <T> T apply(EActionVisitor<T> visitor);

}
