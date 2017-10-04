package io.zrz.hai.runtime.engine.steps;

import io.zrz.hai.haiscript.IndentPrintWriter;

/**
 * common interface implemented by all steps.
 *
 * a step has a set of dependent inputs, and can emit output key/values.
 *
 * an output can be a single field name, or a sub.path. this allows us to avoid
 * inserting extra nodes in the plan when it is not needed. So users { edges {
 * node } } can be a single node with an output of 'users.edges.node'.
 *
 */

public interface EStep {

  /**
   *
   */

  EStepKind getStepKind();

  /**
   *
   */

  void dump(IndentPrintWriter w);

  /**
   *
   */

  <R> R accept(EStepVisitor<R> visitor);

}
