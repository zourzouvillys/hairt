package io.zrz.hai.runtime.compile.facade.impl;

import java.util.stream.Stream;

import io.zrz.hai.runtime.compile.facade.MArgument;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.runtime.compile.facade.MOutputKind;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.type.HTupleType;
import io.zrz.hai.type.HType;

public class MPrimitiveType implements MOutputType, MInputType {

  private final MViewContextImpl ctx;
  private final HType type;

  public MPrimitiveType(MViewContextImpl ctx, HType type) {
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
  public MField findField(String fieldName, HTupleType args) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String toString() {
    return this.type.toString();
  }

  @Override
  public Stream<? extends MField> fields() {
    return Stream.empty();
  }

  @Override
  public Stream<? extends MArgument> arguments() {
    return Stream.empty();
  }

  @Override
  public MOutputKind outputKind() {
    return MOutputKind.SCALAR;
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
    return this.type;
  }

  @Override
  public String getSimpleName() {
    return this.type.toString();
  }

}
