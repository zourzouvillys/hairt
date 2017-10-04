package io.zrz.hai.runtime.compile.parse;

import java.util.List;

import io.zrz.hai.runtime.compile.facade.MView;

public interface GDocument {

  GExecutable getDefaultExecutable();

  GFragment fragment(String name);

  List<? extends GFragment> getFragments();

  List<? extends GExecutable> getExecutables();

  MView getView();

  GExecutable getExecutable(String operationName);

}
