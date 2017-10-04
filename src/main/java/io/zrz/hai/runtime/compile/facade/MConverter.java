package io.zrz.hai.runtime.compile.facade;

/**
 * a conversion function that can convert from an input type to an output type.
 */

public interface MConverter {

  MArgument getInputType();

  MOutputType getOutputType();

}
