package io.zrz.hai.runtime.compile.facade;

import io.zrz.hai.type.HModule;
import io.zrz.hai.type.HViewType;

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
