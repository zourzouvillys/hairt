package io.zrz.hai.runtime.compile.facade.impl;

import java.util.stream.Stream;

import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MOutputKind;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.type.HDeclType;
import io.zrz.hai.type.HEdgeType;
import io.zrz.hai.type.HTypeToken;

public class MEdgeTypeImpl extends AbstractMOutputTypeImpl implements MOutputType {

  private final MViewContextImpl ctx;
  private final HEdgeType edgeType;

  public MEdgeTypeImpl(MViewContextImpl ctx, HEdgeType edgeType) {
    this.ctx = ctx;
    this.edgeType = edgeType;
  }

  @Override
  public MOutputType withViewKind(MViewKind viewKind) {
    if (viewKind != this.ctx.viewKind) {
      return this.ctx.withViewKind(viewKind).output(this.edgeType);
    }
    return this;
  }

  @Override
  public Stream<? extends MField> fields() {
    return Stream.of(this.nodeField(), this.cursorField());
  }

  protected MOutputType getNodeType() {
    return this.ctx.output(this.edgeType.getNodeType());
  }

  private MField nodeField() {
    return new MDynamicFieldImpl(this.ctx, this, "node", this.getNodeType(), MDynamicFieldKind.NODE);
  }

  private MField cursorField() {
    return new MDynamicFieldImpl(this.ctx, this, "cursor", this.ctx.output(this.ctx.getTypeLoader().fromToken(HTypeToken.STRING)), MDynamicFieldKind.NODE);
  }

  @Override
  public String toString() {
    return this.edgeType.getQualifiedName();
  }

  @Override
  public MOutputKind outputKind() {
    return MOutputKind.EDGE;
  }

  @Override
  public MViewContextImpl getView() {
    return this.ctx;
  }

  @Override
  public HDeclType getType() {
    return this.edgeType;
  }

  @Override
  public String getSimpleName() {
    return this.edgeType.getQualifiedName();
  }

}
