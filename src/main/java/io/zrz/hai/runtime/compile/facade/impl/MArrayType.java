package io.zrz.hai.runtime.compile.facade.impl;

import java.util.stream.Stream;

import io.zrz.hai.runtime.compile.facade.MArgument;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.runtime.compile.facade.MOutputKind;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MType;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.symbolic.HTypeUtils;
import io.zrz.hai.symbolic.type.HTupleType;
import io.zrz.hai.symbolic.type.HType;
import lombok.Getter;

public class MArrayType implements MOutputType, MInputType {

  private final MViewContextImpl ctx;

  @Getter
  private final MType componentType;

  public MArrayType(MViewContextImpl ctx, MType type) {
    this.ctx = ctx;
    this.componentType = type;
  }

  @Override
  public Stream<? extends MField> fields() {
    return Stream.empty();
  }

  @Override
  public MField findField(String fieldName, HTupleType args) {
    return null;
  }

  @Override
  public String toString() {
    return "[" + this.componentType + "]";
  }

  @Override
  public MOutputType withViewKind(MViewKind query) {
    throw new IllegalArgumentException();
  }

  @Override
  public MOutputKind outputKind() {
    return MOutputKind.ARRAY;
  }

  @Override
  public MViewContextImpl getView() {
    return this.ctx;
  }

  @Override
  public Stream<? extends MArgument> arguments() {
    return Stream.of();
  }

  @Override
  public MInputType toArray() {
    return new MArrayType(this.ctx, this);
  }

  @Override
  public HType getType() {
    return HTypeUtils.makeArrayType(this.componentType.getType());
  }

  @Override
  public String getSimpleName() {
    return "ARRAY";
  }

}
