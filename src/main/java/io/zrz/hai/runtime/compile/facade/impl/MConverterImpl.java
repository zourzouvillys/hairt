package io.zrz.hai.runtime.compile.facade.impl;

import io.zrz.hai.runtime.compile.facade.MArgument;
import io.zrz.hai.runtime.compile.facade.MConverter;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MType;
import io.zrz.hai.symbolic.HMethod;
import io.zrz.hai.symbolic.type.HType;
import lombok.Getter;

public class MConverterImpl implements MConverter {

  private final MViewContextImpl ctx;
  private final MDeclTypeImpl type;
  private final HMethod method;

  @Getter
  private final MArgument inputType;

  @Getter
  private final MOutputType outputType;

  /**
   *
   */

  public MConverterImpl(MViewContextImpl ctx, MDeclTypeImpl type, HMethod method) {
    this.ctx = ctx;
    this.type = type;
    this.method = method;

    final String pname = method.getExecutable().getParameters().get(0).getName();
    final HType ptype = method.getExecutable().getParameters().get(0).getType();

    this.inputType = ctx.arg(pname, ctx.input(ptype), true);
    this.outputType = ctx.output(method.getReturnType());
  }

  public boolean canSupply(MType type) {
    return this.outputType.equals(type);
  }

}
