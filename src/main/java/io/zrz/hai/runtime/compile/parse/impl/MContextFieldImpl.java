package io.zrz.hai.runtime.compile.parse.impl;

import io.zrz.hai.runtime.compile.facade.MFeature;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MFieldKind;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.runtime.compile.facade.MMutability;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MShape;
import io.zrz.hai.runtime.compile.facade.MViewContext;
import io.zrz.hai.symbolic.HMember;

public class MContextFieldImpl implements MField {

  @Override
  public boolean supports(MFeature feature) {
    return false;
  }

  @Override
  public String getSimpleName() {
    return null;
  }

  @Override
  public MMutability getMutability() {
    return MMutability.CONST;
  }

  @Override
  public MInputType getInputType() {
    throw new IllegalAccessError();
  }

  @Override
  public MOutputType getOutputType() {
    throw new IllegalAccessError();
  }

  @Override
  public MShape getOutputShape() {
    throw new IllegalAccessError();
  }

  @Override
  public MOutputType getDeclaringType() {
    throw new IllegalAccessError();
  }

  @Override
  public MOutputType getReflectedType() {
    throw new IllegalAccessError();
  }

  @Override
  public MViewContext getContext() {
    throw new IllegalAccessError();
  }

  @Override
  public HMember getMember() {
    throw new IllegalAccessError();
  }

  @Override
  public MFieldKind getFieldKind() {
    return null;
  }

}
