package io.zrz.hai.runtime;

import javax.json.Json;
import javax.json.JsonValue;

import io.zrz.hai.symbolic.expr.HConstExpr;
import io.zrz.hai.symbolic.type.HType;
import lombok.Getter;

/**
 * a scalar value, or a tuple.
 *
 * String, Int, Bool, Blob, Instant, JSON.
 *
 * and the types array & range.
 *
 */

public class ZValue implements ZAny {

  @Getter
  private final HType type;

  @Getter
  private final Object value;

  public ZValue(HType type, Object value) {
    this.type = type;
    this.value = value;
  }

  public static ZValue from(HConstExpr expr) {
    return new ZValue(expr.getType(), expr.getValue());
  }

  public static ZValue from(String value) {
    throw new IllegalArgumentException();
  }

  public static ZValue from(boolean value) {
    throw new IllegalArgumentException();
  }

  public static ZValue from(long value) {
    throw new IllegalArgumentException();
  }

  public static ZValue from(double value) {
    throw new IllegalArgumentException();
  }

  @Override
  public String toString() {
    return String.format("%s: %s", this.type, this.value);
  }

  public JsonValue toJson() {
    switch (this.type.getTypeKind()) {
      case STRING:
        return Json.createValue((String) this.value);
      case BOOLEAN:
        return ((boolean) this.value) ? JsonValue.TRUE : JsonValue.FALSE;
      case INT:
        return Json.createValue((long) this.value);
      case DOUBLE:
        return Json.createValue((double) this.value);
    }
    throw new IllegalArgumentException(this.type.getTypeKind().toString());
  }

  @Override
  public ZKind getZKind() {
    switch (this.type.getTypeKind()) {
      case STRING:
        return ZKind.STRING;
      case BOOLEAN:
        return ZKind.BOOLEAN;
      case INT:
        return ZKind.LONG;
      case DOUBLE:
        return ZKind.DOUBLE;
    }
    throw new IllegalArgumentException(this.type.getTypeKind().toString());
  }

}
