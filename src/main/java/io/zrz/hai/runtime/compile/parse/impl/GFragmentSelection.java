package io.zrz.hai.runtime.compile.parse.impl;

import io.zrz.hai.runtime.compile.parse.GSelection;
import io.zrz.hai.symbolic.type.HDeclType;

public interface GFragmentSelection extends GSelection {

  /**
   * the type that this fragment will be applied to.
   */

  HDeclType getSpreadType();

}
