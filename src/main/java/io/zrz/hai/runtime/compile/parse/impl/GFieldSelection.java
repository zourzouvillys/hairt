package io.zrz.hai.runtime.compile.parse.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.zrz.hai.haiscript.IndentPrintWriter;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MMutability;
import io.zrz.hai.runtime.compile.facade.MShape;
import io.zrz.hai.runtime.compile.parse.GAnnotation;
import io.zrz.hai.runtime.compile.parse.GAnnotationKind;
import io.zrz.hai.runtime.compile.parse.GBodyImpl;
import io.zrz.hai.runtime.compile.parse.GSelection;
import io.zrz.hai.symbolic.HLoader;
import io.zrz.hai.symbolic.HTypeUtils;
import io.zrz.hai.symbolic.expr.HTupleInitExpr;
import io.zrz.hai.symbolic.type.HDeclKind;
import io.zrz.hai.symbolic.type.HType;
import lombok.Getter;

public class GFieldSelection implements GSelection, GSelectionImpl {

  @Getter
  String outputName;

  @Getter
  MField field;

  @Getter
  HTupleInitExpr arguments;

  @Getter
  List<GSelectionImpl> selections = new LinkedList<>();

  @Getter
  List<GAnnotation> annotations = new LinkedList<>();

  GFieldSelection(String outputName, MField field, HTupleInitExpr args) {
    this.outputName = outputName;
    this.field = Objects.requireNonNull(field);
    this.arguments = args;
  }

  public static GFieldSelection of(String outputName, MField field, HTupleInitExpr args) {
    return new GFieldSelection(outputName, field, args);
  }

  public void add(GSelectionImpl select) {
    this.selections.add(Objects.requireNonNull(select));
  }

  public void add(GAnnotation a) {
    this.annotations.add(Objects.requireNonNull(a));
  }

  @Override
  public HType getOutputType() {

    if (this.selections.isEmpty()) {

      switch (this.field.getOutputShape()) {
        case LIST:
          return this.field.getOutputType().getType();
        case MAYBE:
        case SINGLE:
          return this.field.getOutputType().getType();
        default:
          throw new IllegalArgumentException();
      }

    }

    throw new IllegalArgumentException();
  }

  public String getName() {
    return this.outputName;
  }

  @Override
  public void resolve(GDocumentImpl doc, GBodyImpl exec) {
    this.selections.forEach(sel -> sel.resolve(doc, exec));
  }

  @Override
  public GSelectionKind getSelectionKind() {
    switch (this.field.getOutputShape()) {
      case LIST:
        return GSelectionKind.LIST;
      case MAYBE:
      case SINGLE:
        if (this.selections.isEmpty()) {
          return this.kindOf(this.field.getOutputType().getType());
        }
        if (HTypeUtils.isKind(this.field.getOutputType().getType(), HDeclKind.CONNECTION)) {
          return GSelectionKind.CONNECTION;
        }
        return GSelectionKind.OBJECT;
    }
    throw new IllegalArgumentException();
  }

  private GSelectionKind kindOf(HType type) {
    switch (type.getTypeKind()) {
      case ARRAY:
        return this.kindOf(HTypeUtils.componentType(type));
      case BOOLEAN:
      case DOUBLE:
      case INT:
      case STRING:
        return GSelectionKind.SCALAR;
      case DECL:
      case INTERSECTION:
      case LAMBDA:
      case NEVER:
      case TUPLE:
      case UNION:
      case VOID:
      case WILDCARD:
      default:
        return GSelectionKind.OBJECT;
    }
  }

  @Override
  public void mergeOutputType(HLoader module, Map<String, HType> fields) {

    if (this.selections.isEmpty()) {

      fields.put(this.getOutputName(), this.getOutputType());

    } else {

      final Map<String, HType> res = new HashMap<>();

      this.selections.forEach(sel -> sel.mergeOutputType(module, res));

      switch (this.field.getOutputShape()) {
        case LIST:
          fields.put(this.getOutputName(), module.getArrayFor(HTypeUtils.createTuple(res)));
          break;
        case MAYBE:
        case SINGLE:
          fields.put(this.getOutputName(), HTypeUtils.createTuple(res));
          break;
        default:
          throw new IllegalArgumentException();
      }

    }
  }

  @Override
  public void dump(IndentPrintWriter w) {

    if (!this.outputName.equals(this.field.getSimpleName())) {

      w.print(this.outputName);
      w.print(": ");

    }

    if (this.field.getOutputShape() == MShape.LIST) {
      w.print("[");
    }

    w.print(this.field.getDeclaringType());

    if (this.field.getMutability() == MMutability.MUTABLE) {
      w.print("->");
    } else {
      w.print(".");
    }

    w.print(this.field.getSimpleName());

    if (this.field.getOutputShape() == MShape.LIST) {
      w.print("]");
    }

    if (this.field.getInputType().arguments().count() > 0) {
      w.print(this.field.getInputType());
    }

    for (final GAnnotation a : this.annotations) {
      w.print(" ");
      w.print(a);
    }

    if (!this.selections.isEmpty()) {

      w.println(" {");
      w.inc();
      this.selections.forEach(sel -> {

        sel.dump(w);
        w.forceLine();
      });
      w.dec();
      w.println("}");

    } else {

      w.print(": ");
      w.println(this.field.getOutputType());

    }

    w.forceLine();

  }

  @Override
  public HType getReceiverType() {
    return this.field.getOutputType().getType();
  }

  @Override
  public Optional<GAnnotation> getAnnotation(GAnnotationKind kind) {
    return this.annotations.stream().filter(a -> a.getAnnotationKind() == kind).findFirst();
  }

}
