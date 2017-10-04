package io.zrz.hai.runtime.compile.facade.impl;

import io.zrz.hai.runtime.compile.facade.MFeature;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MShape;

public abstract class AbstractMField implements MField {

  /**
   *
   */

  @Override
  public boolean supports(MFeature feature) {
    switch (feature) {
      case LIVE:
        return false;
      default:
        throw new IllegalArgumentException(feature.toString());
    }
  }

  @Override
  public String toString() {

    final StringBuilder sb = new StringBuilder();

    sb.append(String.format("%20s", this.getClass().getSimpleName()));
    sb.append(" ");
    sb.append(String.format("%10s", this.getMutability()));
    sb.append(" ");
    sb.append(String.format("%10s", this.getReflectedType().getView()));
    sb.append(" ");
    sb.append(this.getDeclaringType());
    sb.append(".");
    sb.append(this.getSimpleName());
    sb.append(this.getInputType());
    sb.append(": ");
    if (this.getOutputShape() == MShape.LIST) {
      sb.append("[").append(this.getOutputType()).append("]");
    } else {
      sb.append(this.getOutputType());
    }
    // sb.append(" -> ");
    // sb.appendln(this.toString());

    return sb.toString();

  }

}
