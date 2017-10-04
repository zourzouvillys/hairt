package io.zrz.hai.runtime.compile.facade;

import java.util.stream.Stream;

import com.google.common.base.Splitter;

import io.zrz.hai.runtime.compile.facade.impl.MViewContextImpl;
import io.zrz.hai.symbolic.HTypeUtils;
import io.zrz.hai.symbolic.type.HTupleType;
import io.zrz.hai.symbolic.type.HType;

/**
 *
 */

public interface MOutputType extends MType {

  /**
   * returns this type within a different view kind.
   */

  MOutputType withViewKind(MViewKind query);

  /**
   * the kind of output from this type.
   */

  MOutputKind outputKind();

  /**
   * the view this field is in
   */

  MViewContextImpl getView();

  /**
   * all of the fields in this output type.
   */

  Stream<? extends MField> fields();

  /**
   * find a field with specified name, given the specified input arguments.
   */

  default MField findField(String symbol, HTupleType args) {
    return this.fields()
        .filter(f -> f.getSimpleName().equals(symbol))
        .findAny()
        .orElse(null);
  }

  /**
   * return the field with the specified symbol.
   */

  default MField field(String symbol) {
    return this.findField(symbol, HTypeUtils.emptyTuple());
  }

  /**
   * select a field with a specific path.
   */

  default MField path(String path) {
    MOutputType type = this;
    MField field = null;
    for (final String symbol : Splitter.on(".").splitToList(path)) {
      field = type.field(symbol);
      if (field == null) {
        throw new IllegalArgumentException(String.format("Can't find '%s' in '%s'", symbol, type));
      }
      type = field.getOutputType();
    }
    return field;
  }

  @Override
  HType getType();

  String getSimpleName();

}
