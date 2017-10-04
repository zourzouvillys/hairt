package io.zrz.hai.runtime.compile.parse.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.parse.GFragment;
import io.zrz.hai.syntax.IndentPrintWriter;
import io.zrz.hai.type.HLoader;
import io.zrz.hai.type.HTupleType;
import io.zrz.hai.type.HType;
import io.zrz.hai.type.HTypeUtils;
import lombok.Getter;

public class GFragmentImpl implements GFragment {

  @Getter
  private final String name;

  @Getter
  private final MOutputType type;

  @Getter
  private final List<GSelectionImpl> selections = new LinkedList<>();

  private GDocumentImpl doc;

  public GFragmentImpl(GDocumentImpl doc, String name, MOutputType root) {
    this.doc = doc;
    this.name = name;
    this.type = root;
  }

  /**
   * create anonymous (inline) fragment.
   */

  public GFragmentImpl(MOutputType type) {
    this.type = type;
    this.name = null;
  }

  public void add(GSelectionImpl child) {
    this.selections.add(child);
  }

  public void dump(IndentPrintWriter w) {

    if (this.name == null) {
      // an inline fragment
    } else {

      w.print("FRAGMENT");
      w.print(" ");
      w.print(this.name);
      w.print(" on ");
      w.print(this.type);
    }

    w.println(" {");

    w.inc();
    this.selections.forEach(s -> s.dump(w));
    w.dec();

    w.println("}");

  }

  public String name() {
    return this.name;
  }

  @Override
  public HTupleType getOutputType() {
    final Map<String, HType> fields = new HashMap<>();
    for (final GSelectionImpl sel : this.selections) {
      sel.mergeOutputType(this.doc.getModule().getTypeLoader(), fields);
    }
    return HTypeUtils.createTuple(fields);
  }

  public void resolve(GDocumentImpl doc) {
    this.selections.forEach(sel -> sel.resolve(doc, this));
  }

  public void mergeOutputType(HLoader loader, Map<String, HType> fields) {
    this.selections.forEach(sel -> sel.mergeOutputType(loader, fields));
  }

  public void seal() {
  }

}
