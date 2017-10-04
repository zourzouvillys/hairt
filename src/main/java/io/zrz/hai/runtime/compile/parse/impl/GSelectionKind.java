package io.zrz.hai.runtime.compile.parse.impl;

public enum GSelectionKind {

  /**
   * the selection is a single scalar field.
   */

  SCALAR,

  /**
   * the selection is a fragment spread on the current context.
   */

  SPREAD,

  /**
   * the selection is a list of items. the sub-selections should be made on each
   * item provided by the field in the selection.
   */

  LIST,

  /**
   * The selection produces an object, the child selections will be on the object.
   */

  OBJECT,

  /**
   * the selection produces a connection
   */

  CONNECTION

}
