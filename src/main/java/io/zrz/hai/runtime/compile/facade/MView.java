package io.zrz.hai.runtime.compile.facade;

import io.zrz.hai.symbolic.HModule;
import io.zrz.hai.symbolic.type.HViewType;

/**
 *
 */

public interface MView {

  HViewType getViewType();

  /**
   * fetch a context for a selection.
   */

  MViewContext context(MViewKind viewKind);

  MArgument createArgument(String name, MInputType input, boolean optional);

  HModule getModule();

}
