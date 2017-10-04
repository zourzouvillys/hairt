package io.zrz.hai.runtime.compile.facade.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import io.zrz.hai.runtime.HRuntimeUtils;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MOutputKind;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.type.HConnection;
import io.zrz.hai.type.HDeclKind;
import io.zrz.hai.type.HDeclType;
import io.zrz.hai.type.HLink;
import io.zrz.hai.type.HMemberKind;
import io.zrz.hai.type.HMethod;
import io.zrz.hai.type.HNodeType;
import io.zrz.hai.type.HState;
import io.zrz.hai.type.HTypeUtils;
import io.zrz.hai.type.HViewType;

public class MDeclTypeImpl extends AbstractMOutputTypeImpl implements MOutputType {

  protected final MViewContextImpl ctx;
  protected final HDeclType type;
  private final Supplier<List<? extends MField>> declFields;

  public MDeclTypeImpl(MViewContextImpl ctx, HDeclType type) {
    this.ctx = ctx;
    this.type = type;
    this.declFields = Suppliers.memoize(() -> this._declFields());
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
    if (this.type.getDeclKind() == HDeclKind.VIEW) {
      return HRuntimeUtils.typeName((HViewType) this.type, this.ctx.viewKind);
    }
    return this.type.getQualifiedName();
  }

  @Override
  public Stream<? extends MField> fields() {
    return this.declFields.get().stream();
  }

  public Stream<? extends MField> typeInfoFields() {
    return Stream.of(new MDynamicFieldImpl(
        this.ctx,
        this,
        "__typename",
        this.ctx.output(this.ctx.registry().getModule().findType("Type")),
        MDynamicFieldKind.COUNT));
  }

  public Stream<? extends MField> schemaFields() {
    return Stream.of(new MDynamicFieldImpl(
        this.ctx,
        this,
        "__schema",
        this.ctx.output(this.ctx.registry().getModule().findType("Type")),
        MDynamicFieldKind.COUNT));
  }

  public List<? extends MField> _declFields() {
    return HTypeUtils.getMembers(this.type)
        .filter(m -> this.ctx.isExported(m))
        .map(member -> {

          switch (member.getMemberKind()) {

            case METHOD:
              return new MMethodImpl(this.ctx, this, (HMethod) member);

            case STATE:
              return new MStateFieldImpl(this.ctx, this, (HState) member);

            case AMBIENT:
            case LINK:
              return new MLinkFieldImpl(this.ctx, this, (HLink) member);

            case CONNECTION: {

              final HConnection field = (HConnection) member;
              final HNodeType componentType = field.getConnectionType().getNodeType();

              if (componentType.getDeclKind() == HDeclKind.TYPE) {
                return new MDynamicArrayFieldImpl(this.ctx, this, field.getName(), this.ctx.output(componentType));
              }

              return new MConnectionFieldImpl(this.ctx, this, (HConnection) member);

            }

            case PERMISSION:
            case SELECTION:
              break;
          }
          throw new IllegalArgumentException(member.getMemberKind().toString());
        })
        .filter(m -> this.ctx.isVisible(m))
        .collect(Collectors.toList());
  }

  public Stream<MConverterImpl> converters() {
    return HTypeUtils.getMembers(this.type)
        .filter(member -> member.getMemberKind() == HMemberKind.METHOD)
        .filter(f -> f.getModifiers().isConst())
        .filter(m -> m.getModifiers().isAuto())
        .map(HMethod.class::cast)
        .map(m -> new MConverterImpl(this.ctx, this, m));
  }

  @Override
  public boolean equals(Object type) {
    if (type instanceof MDeclTypeImpl) {
      return ((MDeclTypeImpl) type).type == this.type;
    }
    return false;
  }

  @Override
  public MOutputKind outputKind() {
    return MOutputKind.OBJECT;
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
    if (this.type.getDeclKind() == HDeclKind.VIEW) {
      return HRuntimeUtils.typeName((HViewType) this.type, this.ctx.viewKind);
    }
    return this.type.getQualifiedName();
  }

}
