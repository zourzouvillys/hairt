package io.zrz.hai.runtime.engine.results;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonValue;

import com.google.common.collect.LinkedHashMultimap;

import lombok.Getter;

public class EBufferedResultCollector implements EResultCollector {

  @Getter
  private final Map<String, JsonValue> scalars = new HashMap<>();

  @Getter
  private final LinkedHashMultimap<String, EBufferedResultCollector> nested = LinkedHashMultimap.create();

  /**
   *
   */

  @Override
  public void putValue(String key, JsonValue value) {
    this.scalars.put(key, value);
  }

  /**
   *
   */

  @Override
  public EResultCollector createNestedInstance(String key) {
    final EBufferedResultCollector nested = new EBufferedResultCollector();
    this.nested.put(key, nested);
    return nested;
  }

  @Override
  public void close() {
    //
  }

}
