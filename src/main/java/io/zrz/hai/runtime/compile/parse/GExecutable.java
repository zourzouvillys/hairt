package io.zrz.hai.runtime.compile.parse;

import java.util.List;

import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.runtime.compile.facade.MViewContext;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.runtime.engine.EExecutionPlan;
import io.zrz.hai.type.HTupleType;

/**
 * An executable within a document.
 */

public interface GExecutable extends GBodyImpl {

  /**
   *
   */

  GDocument getDocument();

  /**
   * The view context this executable was bound to.
   */

  MViewContext getViewContext();

  /**
   * The name of the query, or null if it has no name.
   */

  String getName();

  /**
   * the kind of executable
   */

  MViewKind getKind();

  /**
   * The input parameters for this executable.
   */

  MInputType getInputType();

  /**
   * The output type this executable will return.
   */

  HTupleType getOutputType();

  /**
   * The selections made in this query.
   */

  List<? extends GSelection> getSelections();

  /**
   *
   */

  EExecutionPlan getExecutionPlan();

  /**
   *
   */

  int getEstimatedCost();

}
