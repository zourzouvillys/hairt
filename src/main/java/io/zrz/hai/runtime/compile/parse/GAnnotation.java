package io.zrz.hai.runtime.compile.parse;

import java.util.Map;

import io.zrz.hai.symbolic.expr.HExpr;

public interface GAnnotation {

  GAnnotationKind getAnnotationKind();

  String getName();

  Map<String, HExpr> getProperties();

}
