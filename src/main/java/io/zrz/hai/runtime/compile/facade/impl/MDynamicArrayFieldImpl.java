package io.zrz.hai.runtime.compile.facade.impl;

import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MFieldKind;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.runtime.compile.facade.MMutability;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MShape;
import io.zrz.hai.runtime.compile.facade.MViewContext;
import io.zrz.hai.type.HMember;
import io.zrz.hai.type.HTypeToken;

public class MDynamicArrayFieldImpl extends AbstractMField implements MField {

  private final String name;
  private final MOutputType type;
  private final MViewContextImpl ctx;
  private final MOutputType decltype;

  public MDynamicArrayFieldImpl(MViewContextImpl ctx, MOutputType decltype, String name, MOutputType type) {
    this.ctx = ctx;
    this.decltype = decltype;
    this.name = name;
    this.type = type;
  }

  @Override
  public String getSimpleName() {
    return this.name;
  }

  @Override
  public MMutability getMutability() {
    return MMutability.CONST;
  }

  @Override
  public MInputType getInputType() {
    return this.ctx.input(this.ctx.getTypeLoader().fromToken(HTypeToken.VOID));
  }

  @Override
  public MOutputType getOutputType() {
    return this.type;
  }

  @Override
  public MShape getOutputShape() {
    return MShape.LIST;
  }

  @Override
  public MOutputType getDeclaringType() {
    return this.decltype;
  }

  @Override
  public MOutputType getReflectedType() {
    return this.decltype;
  }

  @Override
  public MViewContext getContext() {
    return this.ctx;
  }

  @Override
  public HMember getMember() {
    throw new IllegalArgumentException();
  }

  @Override
  public MFieldKind getFieldKind() {
    return MFieldKind.DYNAMIC;
  }

}
