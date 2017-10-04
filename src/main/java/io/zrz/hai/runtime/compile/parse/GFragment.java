package io.zrz.hai.runtime.compile.parse;

import java.util.List;

import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.symbolic.type.HTupleType;

public interface GFragment extends GBodyImpl {

  String getName();

  MOutputType getType();

  List<? extends GSelection> getSelections();

  HTupleType getOutputType();

}
