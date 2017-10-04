package io.zrz.hai.runtime.engine.eval;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.zrz.hai.runtime.engine.steps.EStep;

/**
 * caches parts of the graph that are refeenced during execution.
 */

public class NeoResultCache {

  private final Map<EStep, INode> nodes = new HashMap<>();
  private final NeoResultCache parent;

  public NeoResultCache() {
    this.parent = null;
  }

  public NeoResultCache(NeoResultCache parent) {
    this.parent = parent;
  }

  public INode get(EStep step, Supplier<INode> supplier) {
    if (this.nodes.containsKey(step)) {
      return this.nodes.get(step);
    } else if (this.parent != null && this.parent.contains(step)) {
      return this.parent.get(step);
    }
    final INode val = supplier.get();
    this.nodes.put(step, val);
    return val;
  }

  private INode get(EStep step) {
    if (this.nodes.containsKey(step)) {
      return this.nodes.get(step);
    } else if (this.parent != null) {
      return this.parent.get(step);
    }
    return null;
  }

  private boolean contains(EStep step) {
    if (this.nodes.containsKey(step)) {
      return true;
    } else if (this.parent != null && this.parent.contains(step)) {
      return true;
    }
    return false;
  }

}
