package io.zrz.hai.runtime.compile.parse.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;

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
import lombok.Getter;

public class GFragmentSpreadSelection implements GSelectionImpl, GFragmentSelection {

  @Getter
  private final String name;

  private final MOutputType type;

  private GFragmentImpl fragment;

  private final GDocumentImpl doc;

  public GFragmentSpreadSelection(GDocumentImpl doc, String name, MOutputType type) {
    this.doc = doc;
    this.name = name;
    this.type = type;
  }

  @Override
  public void dump(IndentPrintWriter w) {
    w.print("...");
    w.print(this.name);
    w.print("(on ");
    w.print(this.type);
    w.print(")");
  }

  @Override
  public HType getOutputType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void resolve(GDocumentImpl doc, GBodyImpl exec) {
    this.fragment = doc.fragment(this.name);
    if (this.fragment == null) {
      throw new IllegalArgumentException("unknown fragment: " + this.name);
    }
  }

  public static GSelectionImpl of(GDocumentImpl doc, String name, MOutputType type) {
    return new GFragmentSpreadSelection(doc, name, type);
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
    if (this.fragment == null) {
      this.fragment = this.doc.fragment(this.name);
    }
    Preconditions.checkState(this.fragment != null, "fragment not resolved", this.name);
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
