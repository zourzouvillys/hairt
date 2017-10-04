package io.zrz.hai.runtime.compile.facade;

import java.util.stream.Stream;

import javax.annotation.Nullable;

public interface MInputType extends MType {

  /**
   * the fields in this input type.
   */

  Stream<? extends MArgument> arguments();

  /**
   * find a specific field by name.
   */

  @Nullable
  default MArgument findField(String symbol) {
    return arguments().filter(s -> s.getName().equals(symbol)).findAny().orElse(null);
  }

  MInputType toArray();

}
