package io.zrz.hai.runtime.compile.parse.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.zrz.hai.haiscript.IndentPrintWriter;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.parse.GAnnotation;
import io.zrz.hai.runtime.compile.parse.GAnnotationKind;
import io.zrz.hai.runtime.compile.parse.GBodyImpl;
import io.zrz.hai.runtime.compile.parse.GSelection;
import io.zrz.hai.symbolic.HLoader;
import io.zrz.hai.symbolic.type.HDeclType;
import io.zrz.hai.symbolic.type.HType;

public class GInlineSpreadSelection implements GSelectionImpl, GFragmentSelection {

  private final GFragmentImpl fragment;

  private final MOutputType type;

  private final List<GAnnotation> directives;

  public GInlineSpreadSelection(GFragmentImpl fragment, MOutputType type, List<GAnnotation> directives) {
    this.fragment = fragment;
    this.type = type;
    this.directives = directives;
  }

  @Override
  public void dump(IndentPrintWriter w) {
    w.print("... ");
    if (!this.directives.isEmpty()) {
      w.print(this.directives.stream().map(d -> d.toString()).collect(Collectors.joining(" ")));
      w.print(" ");
    }
    w.print("on ");
    w.print(this.type);
    this.fragment.dump(w);
  }

  public static GInlineSpreadSelection of(GFragmentImpl fragment, MOutputType type, List<GAnnotation> directives) {
    return new GInlineSpreadSelection(fragment, type, directives);
  }

  @Override
  public HType getOutputType() {
    throw new IllegalArgumentException();
  }

  @Override
  public void resolve(GDocumentImpl doc, GBodyImpl exec) {
    this.fragment.resolve(doc);
  }

  @Override
  public GSelectionKind getSelectionKind() {
    return GSelectionKind.SPREAD;
  }

  @Override
  public void mergeOutputType(HLoader module, Map<String, HType> fields) {
    this.fragment.mergeOutputType(module, fields);
  }

  @Override
  public Collection<? extends GSelection> getSelections() {
    return this.fragment.getSelections();
  }

  @Override
  public MField getField() {
    return new MContextFieldImpl();
  }

  @Override
  public HType getReceiverType() {
    return this.type.getType();
  }

  @Override
  public String getOutputName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<GAnnotation> getAnnotation(GAnnotationKind kind) {
    throw new IllegalArgumentException();
  }

  @Override
  public HDeclType getSpreadType() {
    return (HDeclType) this.type.getType();
  }

}
