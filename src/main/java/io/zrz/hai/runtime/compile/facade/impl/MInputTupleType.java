package io.zrz.hai.runtime.compile.facade.impl;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.zrz.hai.runtime.compile.facade.MArgument;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.symbolic.type.HTupleType;
import io.zrz.hai.symbolic.type.HType;

public class MInputTupleType implements MInputType {

  private final MViewContextImpl ctx;
  private final HTupleType type;

  public MInputTupleType(MViewContextImpl ctx, HTupleType type) {
    this.ctx = ctx;
    this.type = type;
  }

  @Override
  public Stream<? extends MArgument> arguments() {
    return this.type.getFields().stream().map(x -> new MArgumentImpl(x.getArgumentName(), this.ctx.input((HType) x.getDataType()), x.isOptional()));
  }

  @Override
  public String toString() {
    return this.type.getFields().stream()
        .map(x -> String.format("%s: %s",
            x.getArgumentName(),
            this.ctx.input((HType) x.getDataType())))
        .collect(Collectors.joining(", "));
  }

  @Override
  public MInputType toArray() {
    return new MArrayType(this.ctx, this);
  }

  @Override
  public HType getType() {
    throw new IllegalArgumentException();
  }

}
