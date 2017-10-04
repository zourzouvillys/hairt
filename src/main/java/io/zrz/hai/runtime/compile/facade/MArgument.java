package io.zrz.hai.runtime.compile.facade;

public interface MArgument {

  /**
   * the name of the argument.
   */

  String getName();

  /**
   * the type of the argument.
   */

  MInputType getType();

  /**
   * if this argument is mandatory or not.
   */

  boolean isMandatory();

  /**
   * provides a new instance of this argument, with a different name.
   */

  MArgument withName(String string);

}
