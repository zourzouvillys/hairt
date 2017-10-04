package io.zrz.hai.runtime.compile.facade.impl;

import io.zrz.hai.runtime.compile.facade.MFeature;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MFieldKind;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.runtime.compile.facade.MMutability;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MShape;
import io.zrz.hai.runtime.compile.facade.MViewContext;
import io.zrz.hai.type.HLink;
import io.zrz.hai.type.HMember;
import io.zrz.hai.type.HType;
import io.zrz.hai.type.HTypeToken;
import io.zrz.hai.type.HTypeUtils;

public class MLinkFieldImpl extends AbstractMField implements MField {

  private final MViewContextImpl ctx;
  private final HLink member;
  private final MDeclTypeImpl type;
  private final HType resultType;

  public MLinkFieldImpl(MViewContextImpl ctx, MDeclTypeImpl type, HLink member) {
    this.member = member;
    this.type = type;
    this.ctx = ctx;
    this.resultType = HTypeUtils.type(member);
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
    return this.ctx.output(this.resultType);
  }

  @Override
  public MShape getOutputShape() {
    return MShape.SINGLE;
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
  public MViewContext getContext() {
    return this.ctx;
  }

  @Override
  public HMember getMember() {
    return this.member;
  }

  @Override
  public MFieldKind getFieldKind() {
    return MFieldKind.LINK;
  }

}
