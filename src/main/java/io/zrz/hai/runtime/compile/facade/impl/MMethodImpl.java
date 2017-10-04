package io.zrz.hai.runtime.compile.facade.impl;

import io.zrz.hai.runtime.compile.facade.MArgument;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MFieldKind;
import io.zrz.hai.runtime.compile.facade.MMutability;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MShape;
import io.zrz.hai.runtime.compile.facade.MViewContext;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.type.HConnectionType;
import io.zrz.hai.type.HDeclKind;
import io.zrz.hai.type.HMethod;
import io.zrz.hai.type.HNodeType;
import io.zrz.hai.type.HParameter;
import io.zrz.hai.type.HType;
import io.zrz.hai.type.HTypeKind;
import io.zrz.hai.type.HTypeToken;
import io.zrz.hai.type.HTypeUtils;

public class MMethodImpl extends AbstractMField implements MField {

  private final HMethod method;
  private final MDeclTypeImpl type;
  private final HType resultType;
  private final MViewContextImpl ctx;

  public MMethodImpl(MViewContextImpl ctx, MDeclTypeImpl type, HMethod method) {
    this.ctx = ctx;
    this.method = method;
    this.type = type;
    this.resultType = HTypeUtils.type(method);
  }

  /**
   * adapt any input parameters as needed. if a param is not an input type, then
   * we need to convert it using an implicit conversion in the current context, or
   * the view.
   */

  @Override
  public MInputStructType getInputType() {

    final MInputStructType input = new MInputStructType(this.ctx);

    if (this.method.getExecutable() != null) {
      for (final HParameter p : this.method.getExecutable().getParameters()) {
        final MArgument ptype = this.convert(p.getName(), p.getType(), p.isOptional());
        input.add(ptype);
      }
    }

    //

    if (HTypeUtils.isKind(this.resultType, HDeclKind.CONNECTION)) {

      final HConnectionType conn = ((HConnectionType) this.resultType);

      if (this.ctx.viewKind != MViewKind.SUBSCRIPTION) {

        input.add(new MArgumentImpl("first", this.ctx.input(this.ctx.getTypeLoader().fromToken(HTypeToken.INT)), true));
        input.add(new MArgumentImpl("last", this.ctx.input(this.ctx.getTypeLoader().fromToken(HTypeToken.INT)), true));
        input.add(new MArgumentImpl("before", this.ctx.input(this.ctx.getTypeLoader().fromToken(HTypeToken.STRING)), true));
        input.add(new MArgumentImpl("after", this.ctx.input(this.ctx.getTypeLoader().fromToken(HTypeToken.STRING)), true));

        final MInputStructType filter = new MInputStructType(this.ctx);

        for (final HMethod f : conn.getFilters()) {
          final HType filterInputType = f.getExecutable().getParameters().get(0).getType();
          filter.add(new MArgumentImpl(f.getName(), this.ctx.input(filterInputType), true));
        }

        input.add(new MArgumentImpl("filter", filter, true));

        input.add(new MArgumentImpl("sort", this.ctx.input(this.resultType.getTypeLoader().getArrayFor(this.ctx.getTypeLoader().fromToken(HTypeToken.STRING))),
            true));

      } else {

        for (final HMethod f : conn.getFilters()) {
          final HType filterInputType = f.getExecutable().getParameters().get(0).getType();
          input.add(new MArgumentImpl(f.getName(), this.ctx.input(filterInputType), true));
        }

      }

    }

    //

    //
    return input;

  }

  /**
   * if the method returns void, then we return the parent.
   */

  @Override
  public MOutputType getOutputType() {

    if (this.ctx.viewKind == MViewKind.SUBSCRIPTION && HTypeUtils.isKind(this.resultType, HDeclKind.CONNECTION)) {
      final HConnectionType conn = (HConnectionType) this.resultType;
      return this.ctx.output(conn.getNodeType(), MViewKind.SUBSCRIPTION);
    }

    final MViewKind targetViewKind = (this.getMutability() == MMutability.MUTABLE || this.ctx.viewKind == MViewKind.SUBSCRIPTION)
        ? MViewKind.QUERY
        : this.ctx.viewKind;

    //
    if (this.resultType.getTypeKind() == HTypeKind.VOID) {
      // a VOID return results in the object it was called on being returned.
      return this.getReflectedType().withViewKind(targetViewKind);
    }

    return this.ctx.output(this.resultType, targetViewKind);

  }

  /**
   * attach a conversion to load the node.
   */

  private MArgument coerce(String name, HNodeType type) {
    return this.ctx.converter(type);
  }

  /**
   *
   */

  private MArgument convert(String name, HType type, boolean optional) {

    switch (type.getTypeKind()) {
      case STRING:
      case INT:
      case BOOLEAN:
      case DOUBLE:
        return this.ctx.arg(name, this.ctx.input(type), optional);
      case ARRAY:
      case TUPLE:
      case DECL:
        switch (HTypeUtils.declKind(type)) {
          case TYPE:
          case NODE:
            // a node needs to be converted
            return this.coerce(name, (HNodeType) type);
          case CONNECTION:
          case EDGE:
          case EVENT:
          case ENUM:
          case INTERFACE:
          case STRUCT:
          case VIEW:
            break;
        }
        throw new IllegalArgumentException(type.toString());
      case UNION:
      case INTERSECTION:
      case WILDCARD:
        break;
      case LAMBDA:
      case VOID:
      case NEVER:
      default:
        break;
    }
    throw new IllegalArgumentException(type.toString());

  }

  @Override
  public String getSimpleName() {
    return this.method.getName();
  }

  @Override
  public MOutputType getDeclaringType() {
    return this.ctx.output(this.method.getDeclaringType());
  }

  @Override
  public MOutputType getReflectedType() {
    return this.type;
  }

  @Override
  public MMutability getMutability() {
    return this.method.getModifiers().isConst() ? MMutability.CONST : MMutability.MUTABLE;
  }

  @Override
  public MShape getOutputShape() {
    return MShape.SINGLE;
  }

  @Override
  public MViewContext getContext() {
    return this.ctx;
  }

  @Override
  public HMethod getMember() {
    return this.method;
  }

  @Override
  public MFieldKind getFieldKind() {
    return MFieldKind.METHOD;
  }

}
