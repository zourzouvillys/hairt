package io.zrz.hai.runtime;

import io.zrz.hai.symbolic.HTypeToken;
import io.zrz.hai.symbolic.type.HType;

/**
 * each "partition" contains a single runtime host, which shares types, modules,
 * expressions, and anything else which can be cached between security contexts.
 */

public interface ZRuntimeHost {

  /**
   * load a type with the given token.
   */

  <T extends HType> T fromTypeToken(HTypeToken token, Class<T> klass);

  /**
   * load a type with the given token.
   */

  default HType fromTypeToken(HTypeToken token) {
    return fromTypeToken(token, HType.class);
  }

  /**
   * the primitive built-in types available in every runtime instance.
   */

  default HType stringType() {
    return fromTypeToken(HTypeToken.STRING, HType.class);
  }

  default HType intType() {
    return fromTypeToken(HTypeToken.INT, HType.class);
  }

  default HType boolType() {
    return fromTypeToken(HTypeToken.BOOLEAN, HType.class);
  }

  default HType doubleType() {
    return fromTypeToken(HTypeToken.DOUBLE, HType.class);
  }

}
