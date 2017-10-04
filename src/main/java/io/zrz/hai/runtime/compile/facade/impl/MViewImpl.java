package io.zrz.hai.runtime.compile.facade.impl;

import io.zrz.hai.runtime.compile.facade.MArgument;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.runtime.compile.facade.MView;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.type.HModule;
import io.zrz.hai.type.HViewType;
import lombok.Getter;

public class MViewImpl implements MView {

  @Getter
  private MViewContextImpl query;

  @Getter
  private MViewContextImpl mutation;

  @Getter
  private MViewContextImpl subscription;

  @Getter
  private final HViewType viewType;

  private final MRegistryImpl registry;

  public MViewImpl(MRegistryImpl registry, HViewType view) {
    this.viewType = view;
    this.registry = registry;
  }

  @Override
  public MViewContextImpl context(MViewKind viewKind) {
    switch (viewKind) {
      case QUERY:
        if (this.query == null) {
          this.query = new MViewContextImpl(this, this.registry, this.viewType, MViewKind.QUERY);
          this.query.init();
        }
        return this.query;
      case MUTATION:
        if (this.mutation == null) {
          this.mutation = new MViewContextImpl(this, this.registry, this.viewType, MViewKind.MUTATION);
          this.mutation.init();
        }
        return this.mutation;
      case SUBSCRIPTION:
        if (this.subscription == null) {
          this.subscription = new MViewContextImpl(this, this.registry, this.viewType, MViewKind.SUBSCRIPTION);
          this.subscription.init();
        }
        return this.subscription;
    }
    throw new IllegalArgumentException(viewKind.toString());
  }

  @Override
  public MArgument createArgument(String name, MInputType input, boolean optional) {
    return new MArgumentImpl(name, input, optional);
  }

  @Override
  public HModule getModule() {
    return this.viewType.getModule();
  }

}
