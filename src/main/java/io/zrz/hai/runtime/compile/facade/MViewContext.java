package io.zrz.hai.runtime.compile.facade;

import java.util.List;

import io.zrz.hai.type.HLoader;
import io.zrz.hai.type.HModule;
import io.zrz.hai.type.HType;
import io.zrz.hai.type.HTypeToken;

public interface MViewContext {

  /**
   * The root output type for selections.
   */

  MOutputType root();

  /**
   *
   */

  MInputType input(HType type);

  /**
   *
   * @param name
   * @return
   */

  MInputType inputType(String name);

  /**
   *
   * @param margs
   * @return
   */

  MInputType createInput(List<MArgument> margs);

  MViewKind viewKind();

  MOutputType outputType(String name);

  HModule getModule();

  HLoader getTypeLoader();

  MInputType input(HTypeToken b);

}
