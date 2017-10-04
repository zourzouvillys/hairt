package io.zrz.hai.runtime.engine.actions;

public interface EActionVisitor<T> {

  T visitAddToConnection(AddToConnectionAction action);

  T visitSetState(SetStateAction action);

  T visitSetLink(SetLinkAction action);

}
