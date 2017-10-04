package io.zrz.hai.runtime.compile.facade.impl;

import io.zrz.hai.runtime.compile.facade.MFeature;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MFieldKind;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.runtime.compile.facade.MMutability;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MShape;
import io.zrz.hai.runtime.compile.facade.MViewContext;
import io.zrz.hai.type.HConnection;
import io.zrz.hai.type.HMember;
import io.zrz.hai.type.HTypeToken;

public class MConnectionFieldImpl extends AbstractMField implements MField {

  private final MViewContextImpl ctx;
  private final MDeclTypeImpl decltype;
  private final HConnection field;

  public MConnectionFieldImpl(MViewContextImpl ctx, MDeclTypeImpl decltype, HConnection field) {
    this.ctx = ctx;
    this.decltype = decltype;
    this.field = field;
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
  public String getSimpleName() {
    return this.field.getName();
  }

  @Override
  public MMutability getMutability() {
    return MMutability.CONST;
  }

  @Override
  public MInputType getInputType() {

    // JitTupleInitExprBuilder b = new JitTupleInitExprBuilder();

    final MInputStructType x = new MInputStructType(this.ctx);

    x.add(new MArgumentImpl("first", this.ctx.input(this.ctx.getTypeLoader().fromToken(HTypeToken.INT)), true));
    x.add(new MArgumentImpl("last", this.ctx.input(this.ctx.getTypeLoader().fromToken(HTypeToken.INT)), true));
    x.add(new MArgumentImpl("before", this.ctx.input(this.ctx.getTypeLoader().fromToken(HTypeToken.STRING)), true));
    x.add(new MArgumentImpl("after", this.ctx.input(this.ctx.getTypeLoader().fromToken(HTypeToken.STRING)), true));

    return x;

    // return this.ctx.input(JitTupleType.emptyTuple());

  }

  @Override
  public MOutputType getOutputType() {
    return this.ctx.output(this.field.getConnectionType());
  }

  @Override
  public MShape getOutputShape() {
    return MShape.SINGLE;
  }

  @Override
  public MOutputType getDeclaringType() {
    return this.ctx.output(this.field.getDeclaringType());
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
    return this.field;
  }

  @Override
  public MFieldKind getFieldKind() {
    return MFieldKind.CONNECTION;
  }

}
