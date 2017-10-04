package io.zrz.hai.runtime.engine.results;

import javax.json.JsonValue;

public interface EResultCollector {

  /**
   * put a scalar value
   */

  void putValue(String key, JsonValue value);

  /**
   * create a new nested result set. the result set must be closed once all values
   * have been provided to it.
   */

  EResultCollector createNestedInstance(String key);

  /**
   * close this result set instance. no more scalar values may be provided to it.
   *
   * if any child instances are open, they will continue to be writable.
   *
   */

  void close();

}
