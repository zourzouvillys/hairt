package io.zrz.hai.runtime.compile.facade.impl;

import java.util.HashMap;
import java.util.Map;

import io.zrz.hai.runtime.compile.facade.MRegistry;
import io.zrz.hai.symbolic.HModule;
import io.zrz.hai.symbolic.HTypeUtils;
import io.zrz.hai.symbolic.type.HDeclKind;
import io.zrz.hai.symbolic.type.HViewType;
import lombok.Getter;

/**
 *
 *
 */

public class MRegistryImpl implements MRegistry {

  @Getter
  private final HModule module;

  private final Map<HViewType, MViewImpl> views = new HashMap<>();

  private MRegistryImpl(HModule module) {
    this.module = module;
  }

  public static MRegistryImpl init(HModule module) {
    final MRegistryImpl reg = new MRegistryImpl(module);
    reg.init();
    return reg;
  }

  /**
   *
   */

  @Override
  public MViewImpl view(HViewType entryPoint) {
    return this.views.get(entryPoint);
  }

  /**
   * initializes the registry, performing upfront work to generate the views and
   * inline expressions.
   */

  public void init() {
    this.module.getTypes().stream()
        .filter(t -> HTypeUtils.isKind(t, HDeclKind.VIEW))
        .map(HViewType.class::cast)
        .forEach(view -> this.views.put(view, new MViewImpl(this, view)));
  }

  @Override
  public MViewImpl view(String viewName) {
    return this.view(this.module.getType(viewName, HViewType.class));
  }

}
