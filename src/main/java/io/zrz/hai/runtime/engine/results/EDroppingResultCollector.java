package io.zrz.hai.runtime.engine.results;

import javax.json.JsonValue;

public class EDroppingResultCollector implements EResultCollector {

  private static final EDroppingResultCollector INSTANCE = new EDroppingResultCollector();

  @Override
  public void putValue(String key, JsonValue value) {
  }

  /**
   *
   */

  @Override
  public EResultCollector createNestedInstance(String key) {
    return INSTANCE;
  }

  @Override
  public void close() {
  }

  public static final EDroppingResultCollector getInstance() {
    return INSTANCE;
  }

}
