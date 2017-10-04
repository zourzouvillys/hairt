package io.zrz.hai.runtime.compile.parse;

import java.util.Optional;

import io.zrz.hai.runtime.compile.facade.MArgument;

public interface GBodyImpl {

  default Optional<MArgument> var(String name) {
    return Optional.empty();
  }

}
