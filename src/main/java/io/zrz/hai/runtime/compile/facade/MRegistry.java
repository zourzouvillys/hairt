package io.zrz.hai.runtime.compile.facade;

import io.zrz.hai.runtime.compile.facade.impl.MViewImpl;
import io.zrz.hai.symbolic.type.HViewType;

public interface MRegistry {

  /**
   * provides an MType instance to query based on the specified entry point type
   * and view kind.
   */

  MViewImpl view(HViewType entryPoint);

  MViewImpl view(String string);

}
