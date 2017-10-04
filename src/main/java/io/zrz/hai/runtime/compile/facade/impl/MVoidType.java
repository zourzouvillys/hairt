package io.zrz.hai.runtime.compile.facade.impl;

import java.util.stream.Stream;

import io.zrz.hai.runtime.compile.facade.MArgument;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.runtime.compile.facade.MOutputKind;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.type.HType;
import io.zrz.hai.type.HTypeToken;

public class MVoidType implements MInputType, MOutputType {

  private final MViewContextImpl ctx;

  public MVoidType(MViewContextImpl ctx) {
    this.ctx = ctx;
  }

  @Override
  public MOutputType withViewKind(MViewKind viewKind) {
    return this;
  }

  @Override
  public String toString() {
    return "VOID";
  }

  @Override
  public Stream<MField> fields() {
    return Stream.empty();
  }

  @Override
  public Stream<? extends MArgument> arguments() {
    return Stream.empty();
  }

  @Override
  public MOutputKind outputKind() {
    return MOutputKind.VOID;
  }

  @Override
  public MViewContextImpl getView() {
    return this.ctx;
  }

  @Override
  public MInputType toArray() {
    return new MArrayType(this.ctx, this);
  }

  @Override
  public HType getType() {
    return this.ctx.getTypeLoader().fromToken(HTypeToken.VOID);
  }

  @Override
  public String getSimpleName() {
    return "VOID";
  }

}
