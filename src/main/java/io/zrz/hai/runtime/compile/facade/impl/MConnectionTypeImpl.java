package io.zrz.hai.runtime.compile.facade.impl;

import java.util.stream.Stream;

import io.zrz.hai.runtime.compile.facade.MFeature;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MOutputKind;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MShape;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.type.HConnectionType;
import io.zrz.hai.type.HDeclType;
import io.zrz.hai.type.HTypeToken;

public class MConnectionTypeImpl extends AbstractMOutputTypeImpl implements MOutputType {

  private final MViewContextImpl ctx;
  private final HConnectionType type;

  public MConnectionTypeImpl(MViewContextImpl ctx, HConnectionType type) {
    this.ctx = ctx;
    this.type = type;
  }

  @Override
  public MOutputType withViewKind(MViewKind viewKind) {
    if (viewKind != this.ctx.viewKind) {
      return this.ctx.withViewKind(viewKind).output(this.type);
    }
    return this;
  }

  @Override
  public String toString() {
    return this.type.getQualifiedName();
  }

  @Override
  public Stream<? extends MField> fields() {
    return Stream.of(this.edgesField(), this.nodesField(), this.pageInfoField(), this.countField());
  }

  protected MOutputType pageInfoType() {
    final MOutputStructType type = new MOutputStructType(this.ctx, "PageInfo");
    type.add(new MDynamicFieldImpl(this.ctx, this, "hasNextPage", this.ctx.output(this.ctx.getTypeLoader().fromToken(HTypeToken.BOOLEAN)),
        MDynamicFieldKind.PAGEINFO));
    type.add(new MDynamicFieldImpl(this.ctx, this, "hasPreviousPage", this.ctx.output(this.ctx.getTypeLoader().fromToken(HTypeToken.BOOLEAN)),
        MDynamicFieldKind.PAGEINFO));
    return type;
  }

  private MOutputType getEdgeType() {
    return new MEdgeTypeImpl(this.ctx, this.type.getEdgeType());
  }

  private MOutputType getNodeType() {
    return this.ctx.output(this.type.getNodeType());
  }

  private MField countField() {

    return new MDynamicFieldImpl(this.ctx, this, "totalCount", this.ctx.output(this.ctx.getTypeLoader().fromToken(HTypeToken.INT)), MDynamicFieldKind.COUNT) {

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
      public MShape getOutputShape() {
        return MShape.SINGLE;
      }

    };

  }

  private MField edgesField() {
    return new MDynamicFieldImpl(this.ctx, this, "edges", this.getEdgeType(), MDynamicFieldKind.EDGES) {

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
      public MShape getOutputShape() {
        return MShape.LIST;
      }

    };
  }

  private MField nodesField() {
    return new MDynamicFieldImpl(this.ctx, this, "nodes", this.getNodeType(), MDynamicFieldKind.NODES) {

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
      public MShape getOutputShape() {
        return MShape.LIST;
      }

    };
  }

  private MField pageInfoField() {
    return new MDynamicFieldImpl(this.ctx, this, "pageInfo", this.pageInfoType(), MDynamicFieldKind.PAGEINFO) {

      @Override
      public MShape getOutputShape() {
        return MShape.SINGLE;
      }

    };
  }

  @Override
  public boolean equals(Object type) {
    if (type instanceof MConnectionTypeImpl) {
      return ((MConnectionTypeImpl) type).type == this.type;
    }
    return false;
  }

  @Override
  public MOutputKind outputKind() {
    return MOutputKind.CONNECTION;
  }

  @Override
  public MViewContextImpl getView() {
    return this.ctx;
  }

  @Override
  public HDeclType getType() {
    return this.type;
  }

  @Override
  public String getSimpleName() {
    return this.type.getQualifiedName();
  }

}
