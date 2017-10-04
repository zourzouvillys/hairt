package io.zrz.hai.runtime;

import io.zrz.hai.symbolic.HState;
import io.zrz.hai.symbolic.HTypeUtils;
import io.zrz.hai.symbolic.type.HDeclType;

public interface ZPropertyContainer extends ZRef {

  /**
   * the type of this entity.
   */

  HDeclType getType();

  /**
   * retrieve a state value, or null if it is not set.
   */

  ZValue getState(HState state);

  /**
   * set a state value.
   */

  void setState(HState state, ZValue value);

  /**
   * overloads for retrieving values by field name.
   */

  default ZValue getState(String fieldName) {
    return getState(HTypeUtils.getMember(getType(), fieldName).map(HState.class::cast).get());
  }

  /**
   * overloads for setting values.
   */

  default void setState(String fieldName, String value) {
    setState(fieldName, ZValue.from(value));
  }

  default void setState(String fieldName, long value) {
    setState(fieldName, ZValue.from(value));
  }

  default void setState(String fieldName, boolean value) {
    setState(fieldName, ZValue.from(value));
  }

  default void setState(String fieldName, double value) {
    setState(fieldName, ZValue.from(value));
  }

  /**
   *
   */

  default void setState(String fieldName, ZValue value) {
    setState(HTypeUtils.getMember(getType(), fieldName).map(HState.class::cast).get(), value);
  }

}
