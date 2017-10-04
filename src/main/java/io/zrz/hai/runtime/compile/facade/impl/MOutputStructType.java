package io.zrz.hai.runtime.compile.facade.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MOutputKind;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.type.HDeclType;

public class MOutputStructType implements MOutputType {

  private final MViewContextImpl ctx;
  private final List<MField> entries = new LinkedList<>();
  private final String name;

  public MOutputStructType(MViewContextImpl ctx, String name) {
    this.ctx = ctx;
    this.name = name;
  }

  @Override
  public Stream<? extends MField> fields() {
    return this.entries.stream();
  }

  public void add(MField arg) {
    this.entries.add(arg);
  }

  @Override
  public String toString() {
    // this.fields().map(Object::toString).collect(Collectors.joining(", "))
    return this.name;
  }

  @Override
  public MOutputType withViewKind(MViewKind query) {
    throw new IllegalArgumentException();
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
    throw new IllegalArgumentException();
  }

  @Override
  public String getSimpleName() {
    return this.name;
  }

}
