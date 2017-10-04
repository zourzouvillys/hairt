package io.zrz.hai.runtime.compile.parse;

import java.util.Collection;

/**
 * calculates an estimated rate limit size from a query.
 */

public class GCostEstimator {

  public static int calculate(GExecutable exec) {
    return sum(exec.getSelections());
  }

  private static int sum(Collection<? extends GSelection> collection) {
    return collection.stream().mapToInt(sel -> sum(sel)).sum();
  }

  private static int sum(GSelection sel) {
    switch (sel.getSelectionKind()) {
      case LIST:
        return sum(sel.getSelections()) * 100;
      case OBJECT:
        return sum(sel.getSelections());
      case SCALAR:
        return 1;
      case SPREAD:
        return sum(sel.getSelections());
      case CONNECTION:
        return sum(sel.getSelections());
    }
    throw new IllegalArgumentException();
  }

}
