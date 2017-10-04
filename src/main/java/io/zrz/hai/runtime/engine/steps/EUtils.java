package io.zrz.hai.runtime.engine.steps;

public class EUtils {

  public static String toString(EStep source) {
    if (source instanceof EEdgeTraverseStep) {
      return ((EEdgeTraverseStep) source).getSource() + "->";
    }
    return source.toString() + ".";
  }

}
