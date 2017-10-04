package io.zrz.hai.runtime.compile.parse;

import java.util.Collection;
import java.util.Optional;

import io.zrz.hai.haiscript.IndentPrintWriter;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.parse.impl.GSelectionKind;
import io.zrz.hai.symbolic.type.HType;

public interface GSelection {

  Optional<GAnnotation> getAnnotation(GAnnotationKind kind);

  String getOutputName();

  GSelectionKind getSelectionKind();

  /**
   * The field associated with this selection if there is one, otherwise null (if
   * it is a spread).
   */

  MField getField();

  /**
   * The receiver type for this selection.
   *
   * If this is a fragment spread, then it is the spreaded type, otherwise, it is
   * the receiver type for the field.
   *
   */

  HType getReceiverType();

  /**
   * the resulting output type, based on the query.
   */

  HType getOutputType();

  /**
   * the selections associated with this selection.
   *
   * If the type is a fragment, then this is the selections to merge. Otherwise,
   * it is the fields to select.
   *
   */

  Collection<? extends GSelection> getSelections();

  /**
   * dump the selection to a stream.
   */

  void dump(IndentPrintWriter w);

}
