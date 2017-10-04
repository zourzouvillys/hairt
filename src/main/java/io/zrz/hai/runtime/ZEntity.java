package io.zrz.hai.runtime;

import io.zrz.hai.symbolic.type.HDeclType;

/**
 * an edge or node, that can be stored.
 */

public interface ZEntity extends ZAny, ZObject {

  HDeclType getType();

}
