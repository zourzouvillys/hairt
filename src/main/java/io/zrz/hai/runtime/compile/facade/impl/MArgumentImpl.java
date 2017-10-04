package io.zrz.hai.runtime.compile.facade.impl;

import io.zrz.hai.runtime.compile.facade.MArgument;
import io.zrz.hai.runtime.compile.facade.MInputType;

public class MArgumentImpl implements MArgument {

  private final String name;
  private final MInputType type;
  private final boolean optional;

  public MArgumentImpl(String pname, MInputType ptype, boolean optional) {
    this.name = pname;
    this.type = ptype;
    this.optional = optional;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public MInputType getType() {
    return this.type;
  }

  @Override
  public String toString() {
    return String.format("%s%s: %s", this.name, this.isMandatory() ? "" : "?", this.type);
  }

  @Override
  public MArgument withName(String name) {
    return new MArgumentImpl(name, this.type, !this.isMandatory());
  }

  @Override
  public boolean isMandatory() {
    return !this.optional;
  }

}
