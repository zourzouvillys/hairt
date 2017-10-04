package io.zrz.hai.runtime.compile.parse.impl;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.zrz.hai.haiscript.IndentPrintWriter;
import io.zrz.hai.runtime.compile.facade.MView;
import io.zrz.hai.runtime.compile.parse.GDocument;
import io.zrz.hai.runtime.compile.parse.GExecutable;
import io.zrz.hai.symbolic.HModule;
import lombok.Getter;

/**
 * A parsed and resolved GQL document, with each operation available as a HExpr
 * with input and output parameters.
 *
 * The document is cached based on the root view context and query.
 *
 */

public class GDocumentImpl implements GDocument {

  /**
   *
   */

  @Getter
  List<GExecutableImpl> executables = new LinkedList<>();

  /**
   *
   */

  @Getter
  List<GFragmentImpl> fragments = new LinkedList<>();

  @Getter
  private final MView view;

  @Getter
  private GExecutableImpl defaultExecutable;

  public GDocumentImpl(MView view) {
    this.view = view;
  }

  public void add(GExecutableImpl convert) {
    if (convert.getName() == null) {
      Preconditions.checkState(this.defaultExecutable == null, "already a default executable");
      this.defaultExecutable = convert;
    } else {
      this.executables.add(convert);
    }
  }

  public void add(GFragmentImpl convert) {
    this.fragments.add(convert);
  }

  @Override
  public String toString() {
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    final IndentPrintWriter w = new IndentPrintWriter(os);

    w.println("document {");

    w.println();

    if (this.defaultExecutable != null) {
      w.inc();
      this.defaultExecutable.dump(w);
      w.println();
      w.dec();
    }

    this.executables.forEach(exec -> {
      w.inc();
      exec.dump(w);
      w.println();
      w.dec();
    });

    this.fragments.forEach(exec -> {
      w.inc();
      exec.dump(w);
      w.println();
      w.dec();
    });

    w.println("}");

    w.flush();
    return os.toString();
  }

  @Override
  public GFragmentImpl fragment(String name) {
    return this.fragments.stream().filter(f -> StringUtils.equals(f.name(), name)).findAny().orElse(null);
  }

  public void resolve() {

    if (this.defaultExecutable != null) {
      this.defaultExecutable.resolve(this);
    }
    this.executables.forEach(exec -> exec.resolve(this));
    this.fragments.forEach(frag -> frag.resolve(this));

    // ---

    if (this.defaultExecutable != null) {
      this.defaultExecutable.seal();
    }

    this.executables.forEach(exec -> exec.seal());
    this.fragments.forEach(frag -> frag.seal());

  }

  public HModule getModule() {
    return this.view.getModule();
  }

  @Override
  public GExecutable getExecutable(String name) {
    return this.executables.stream().filter(f -> StringUtils.equals(f.getName(), name)).findAny().orElse(null);
  }

}
