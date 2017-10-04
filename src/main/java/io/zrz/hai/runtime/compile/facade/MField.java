package io.zrz.hai.runtime.compile.facade;

import io.zrz.hai.symbolic.HMember;

/**
 * A single field, which could be backed as state, a method, alias, or property.
 * The implementation details aren't relevant to the consumers of this API.
 */

public interface MField {

  /**
   * test the field for a specific feature.
   */

  boolean supports(MFeature feature);

  /**
   * the simple name of this field.
   */

  String getSimpleName();

  /**
   * a field which will change state when invoked is mutable. otherwise, it is
   * immutable (const).
   */

  MMutability getMutability();

  /**
   * the input type (which is a set of arguments) for this field.
   */

  MInputType getInputType();

  /**
   * the resulting type.
   */

  MOutputType getOutputType();

  /**
   * The shape of the output.
   */

  MShape getOutputShape();

  /**
   * The type which declared this field.
   */

  MOutputType getDeclaringType();

  /**
   * The type which this field was reflected from, which is different from the
   * declaring type if it is inherited from a base class or interface, and not
   * overridden by the type being accessed.
   */

  MOutputType getReflectedType();

  /**
   *
   * @return
   */

  MViewContext getContext();

  /**
   *
   * @return
   */

  HMember getMember();

  /**
   * The provider kind of this field - method, link, connection, dynamic, etc.
   */

  MFieldKind getFieldKind();

}
