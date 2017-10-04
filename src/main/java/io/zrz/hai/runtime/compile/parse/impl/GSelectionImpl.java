package io.zrz.hai.runtime.compile.parse.impl;

import java.util.Map;

import io.zrz.hai.runtime.compile.parse.GBodyImpl;
import io.zrz.hai.runtime.compile.parse.GSelection;
import io.zrz.hai.symbolic.HLoader;
import io.zrz.hai.symbolic.type.HType;

public interface GSelectionImpl extends GSelection {

  void resolve(GDocumentImpl doc, GBodyImpl exec);

  void mergeOutputType(HLoader loader, Map<String, HType> fields);

}
