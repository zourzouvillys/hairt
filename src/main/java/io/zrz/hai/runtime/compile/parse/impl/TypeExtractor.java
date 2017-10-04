package io.zrz.hai.runtime.compile.parse.impl;

import io.joss.graphql.core.lang.GQLTypeVisitor;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.runtime.compile.facade.MViewContext;

public class TypeExtractor implements GQLTypeVisitor<MInputType> {

  private final MViewContext view;

  public TypeExtractor(MViewContext view) {
    this.view = view;
  }

  @Override
  public MInputType visitNonNull(GQLNonNullType type) {
    return type.type().apply(this);
  }

  @Override
  public MInputType visitList(GQLListType type) {
    return type.type().apply(this).toArray();
  }

  @Override
  public MInputType visitDeclarationRef(GQLDeclarationRef type) {
    return this.view.inputType(type.name());
  }

}
