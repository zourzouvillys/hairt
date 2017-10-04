package io.zrz.hai.runtime.compile.facade;

import io.zrz.hai.symbolic.type.HType;

/**
 * a type logically maps to an object or interface. it exposes the backing types
 * as fields with a single cohrent view.
 */

public interface MType {

  HType getType();

}
