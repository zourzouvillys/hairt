package io.zrz.hai.runtime.compile.facade.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.zrz.hai.runtime.compile.facade.MArgument;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.symbolic.type.HType;

public class MInputStructType implements MInputType {

  private final MViewContextImpl ctx;
  private final List<MArgument> entries;

  public MInputStructType(MViewContextImpl ctx) {
    this.ctx = ctx;
    this.entries = new LinkedList<>();
  }

  public MInputStructType(MViewContextImpl ctx, List<MArgument> args) {
    this.ctx = ctx;
    this.entries = args;
  }

  @Override
  public Stream<? extends MArgument> arguments() {
    return this.entries.stream();
  }

  public void add(MArgument arg) {
    this.entries.add(arg);
  }

  @Override
  public String toString() {
    if (this.entries.isEmpty()) {
      return "";
    }
    return '(' + this.arguments().map(Object::toString).collect(Collectors.joining(", ")) + ')';
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
