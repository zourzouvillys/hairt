package io.zrz.hai.runtime.compile.facade.impl;

import io.zrz.hai.runtime.compile.facade.MFeature;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MFieldKind;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.runtime.compile.facade.MMutability;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MShape;
import io.zrz.hai.runtime.compile.facade.MViewContext;
import io.zrz.hai.symbolic.HMember;
import io.zrz.hai.symbolic.HState;
import io.zrz.hai.symbolic.HTypeToken;
import io.zrz.hai.symbolic.HTypeUtils;

public class MStateFieldImpl extends AbstractMField implements MField {

  private final MViewContextImpl ctx;
  private final MDeclTypeImpl type;
  private final HState member;

  public MStateFieldImpl(MViewContextImpl ctx, MDeclTypeImpl owner, HState member) {
    this.ctx = ctx;
    this.type = owner;
    this.member = member;
  }

  @Override
  public boolean supports(MFeature feature) {
    switch (feature) {
      case LIVE:
        return true;
      default:
        return super.supports(feature);
    }
  }

  @Override
  public MInputType getInputType() {
    return this.ctx.input(this.ctx.getTypeLoader().fromToken(HTypeToken.VOID));
  }

  @Override
  public MOutputType getOutputType() {
    return this.ctx.output(HTypeUtils.type(this.member));
  }

  @Override
  public String getSimpleName() {
    return this.member.getName();
  }

  @Override
  public MOutputType getDeclaringType() {
    return this.ctx.output(this.member.getDeclaringType());
  }

  @Override
  public MOutputType getReflectedType() {
    return this.type;
  }

  @Override
  public MMutability getMutability() {
    return MMutability.CONST;
  }

  @Override
  public MShape getOutputShape() {
    return MShape.SINGLE;
  }

  @Override
  public MViewContext getContext() {
    return this.ctx;
  }

  @Override
  public HMember getMember() {
    return this.member;
  }

  @Override
  public MFieldKind getFieldKind() {
    return MFieldKind.STATE;
  }

}
