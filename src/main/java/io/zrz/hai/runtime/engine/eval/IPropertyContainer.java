package io.zrz.hai.runtime.engine.eval;

import io.zrz.hai.runtime.ZValue;

/**
 * common interface for accessing properties on nodes and edges.
 */

public interface IPropertyContainer {

  boolean hasProperty(String name);

  ZValue getProperty(String name);

  void setProperty(String name, ZValue value);

}
