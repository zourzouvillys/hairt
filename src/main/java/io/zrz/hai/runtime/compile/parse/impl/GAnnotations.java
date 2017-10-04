package io.zrz.hai.runtime.compile.parse.impl;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import io.joss.graphql.core.doc.GQLArgument;
import io.joss.graphql.core.doc.GQLDirective;
import io.joss.graphql.core.value.GQLValueConverters;
import io.zrz.hai.runtime.compile.parse.GAnnotation;
import io.zrz.hai.runtime.compile.parse.GAnnotationKind;
import io.zrz.hai.symbolic.expr.HExpr;

public class GAnnotations {

  public static GAnnotation parse(GQLDirective d) {
    switch (d.name()) {
      case "live":
      case "defer":
      case "stream":
        return parse(d.name(), d.args());
      case "export":
        return parseExport(d.name(), d.args());
      case "skip":
      case "include":
        return parseConditional(d.name(), d.args());
      default:
        throw new IllegalArgumentException(d.toString());
    }
  }

  public static interface GExportAnnotation extends GAnnotation {
    String as();
  }

  public static interface GConditionalAnnotation extends GAnnotation {
    HExpr condition();
  }

  private static GAnnotation parse(String name, List<GQLArgument> args) {

    final GAnnotationKind kind = GAnnotationKind.valueOf(name.toUpperCase());

    return new GAnnotation() {

      @Override
      public String getName() {
        return name;
      }

      @Override
      public Map<String, HExpr> getProperties() {
        return Maps.newHashMap();
      }

      @Override
      public GAnnotationKind getAnnotationKind() {
        return kind;
      }

      @Override
      public String toString() {
        return String.format("@%s", this.getName());
      }

    };

  }

  private static GConditionalAnnotation parseConditional(String name, List<GQLArgument> args) {

    final GAnnotationKind kind = GAnnotationKind.valueOf(name.toUpperCase());

    return new GConditionalAnnotation() {

      @Override
      public String getName() {
        return name;
      }

      @Override
      public Map<String, HExpr> getProperties() {
        return Maps.newHashMap();
      }

      @Override
      public GAnnotationKind getAnnotationKind() {
        return kind;
      }

      @Override
      public HExpr condition() {
        throw new IllegalArgumentException();
      }

      @Override
      public String toString() {
        switch (kind) {
          case SKIP:
          case INCLUDE:
            return String.format("@%s(if: %s)", this.getName(), this.condition());
        }
        throw new IllegalArgumentException();
      }

    };

  }

  private static GExportAnnotation parseExport(String name, List<GQLArgument> args) {

    final GAnnotationKind kind = GAnnotationKind.valueOf(name.toUpperCase());

    return new GExportAnnotation() {

      @Override
      public String getName() {
        return name;
      }

      @Override
      public Map<String, HExpr> getProperties() {
        return Maps.newHashMap();
      }

      @Override
      public GAnnotationKind getAnnotationKind() {
        return kind;
      }

      @Override
      public String as() {
        return args.stream()
            .filter(x -> x.name().equals("as"))
            .findAny()
            .map(x -> x.value().apply(GQLValueConverters.stringConverter()))
            .orElse(null);
      }

      @Override
      public String toString() {
        switch (kind) {
          case EXPORT:
            return String.format("@%s(as: \"%s\")", this.getName(), this.as());
        }
        throw new IllegalArgumentException();
      }

    };

  }

}
