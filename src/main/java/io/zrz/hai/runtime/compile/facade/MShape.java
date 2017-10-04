package io.zrz.hai.runtime.compile.facade;

public enum MShape {

  /**
   * may be a single value, or none.
   */

  MAYBE,

  /**
   * a single, result is guaranteed.
   */

  SINGLE,

  /**
   * a list of items.
   */

  LIST

}
