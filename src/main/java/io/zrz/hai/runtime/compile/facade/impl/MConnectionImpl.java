package io.zrz.hai.runtime.compile.facade.impl;

import io.zrz.hai.symbolic.type.HConnectionType;
import io.zrz.hai.symbolic.type.HTupleType;

public class MConnectionImpl {

  private final HConnectionType connType;

  public MConnectionImpl(HConnectionType connType) {
    this.connType = connType;
  }

  /**
   *
   * @return
   */

  public HTupleType getInputType() {

    this.connType.getFilters()
        .forEach(d -> System.err.println("FILTER " + d.getMemberKind() + " " + " " + d.getName() + "" + d.getExecutable().getParameters()));
    this.connType.getSorters().forEach(d -> System.err.println("SORTS " + d.getMemberKind() + " " + " " + d.getName()));
    this.connType.getIndexes().forEach(d -> System.err.println("INDEX " + d));
    this.connType.getUniqueConstraints().forEach(d -> System.err.println("UNIQUES " + d));

    System.err.println();

    // new JitTupleType(module, fields)

    return null;

  }

}
