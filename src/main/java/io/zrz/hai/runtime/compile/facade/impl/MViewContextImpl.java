package io.zrz.hai.runtime.compile.facade.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.zrz.hai.runtime.compile.facade.MArgument;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.runtime.compile.facade.MMutability;
import io.zrz.hai.runtime.compile.facade.MOutputKind;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MViewContext;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.runtime.compile.facade.exec.MFieldExpression;
import io.zrz.hai.symbolic.HLoader;
import io.zrz.hai.symbolic.HMember;
import io.zrz.hai.symbolic.HModule;
import io.zrz.hai.symbolic.HTypeToken;
import io.zrz.hai.symbolic.HTypeUtils;
import io.zrz.hai.symbolic.type.HConnectionType;
import io.zrz.hai.symbolic.type.HDeclKind;
import io.zrz.hai.symbolic.type.HDeclType;
import io.zrz.hai.symbolic.type.HEventType;
import io.zrz.hai.symbolic.type.HTupleType;
import io.zrz.hai.symbolic.type.HType;
import io.zrz.hai.symbolic.type.HViewType;
import lombok.SneakyThrows;

public class MViewContextImpl implements MViewContext {

  private final MRegistryImpl registry;
  private final HViewType entryPoint;
  final MViewKind viewKind;
  private MDeclTypeImpl root;
  private List<MConverterImpl> converters;
  private final MViewImpl view;

  // cache for types
  private final Cache<HType, MOutputType> outputs = CacheBuilder.newBuilder().build();
  private final Cache<HType, MInputType> inputs = CacheBuilder.newBuilder().build();

  public MViewContextImpl(MViewImpl view, MRegistryImpl registry, HViewType entryPoint, MViewKind viewKind) {
    this.view = view;
    this.registry = registry;
    this.entryPoint = entryPoint;
    this.viewKind = viewKind;
  }

  public MRegistryImpl registry() {
    return this.registry;
  }

  @Override
  public MDeclTypeImpl root() {
    return this.root;
  }

  /**
   * find a method which returns the requested type, and is marked as auto.
   */

  public MArgument converter(HDeclType type) {
    for (final MConverterImpl c : this.converters) {
      if (c.canSupply(this.output(type))) {
        return c.getInputType();
      }
    }
    throw new IllegalArgumentException("nothing to supply " + type + " as input");
  }

  /**
   * provide the output type for the given JitType.
   */

  @SneakyThrows
  public MOutputType output(HType type) {
    return this.outputs.get(type, () -> this._output(type));
  }

  @Override
  @SneakyThrows
  public MInputType input(HType type) {
    return this.inputs.get(type, () -> this._input(type));
  }

  private MOutputType _decloutput(HDeclType type) {
    switch (type.getDeclKind()) {
      case NODE:
      case VIEW:
      case EDGE:
      case TYPE:
      case INTERFACE:
        return new MDeclTypeImpl(this, type);
      case CONNECTION:
        return new MConnectionTypeImpl(this, (HConnectionType) type);
      case EVENT:
        return new MEventTypeImpl(this, (HEventType) type);
      case ENUM:
      case STRUCT:
        break;
    }
    throw new IllegalArgumentException(type.getDeclKind().toString());
  }

  public MOutputType output(HType type, MViewKind viewKind) {
    if (viewKind == this.viewKind) {
      return this.output(type);
    }
    return this.view.context(viewKind).output(type);
  }

  public MViewContextImpl withViewKind(MViewKind viewKind) {
    if (viewKind == this.viewKind) {
      return this;
    }
    return this.view.context(viewKind);
  }

  @Override
  public MInputType inputType(String name) {
    final HType type = this.registry.getModule().findType(name);
    return this.input(type);
  }

  /**
   *
   */

  public MOutputType _output(HType type) {

    switch (type.getTypeKind()) {
      case STRING:
      case INT:
      case BOOLEAN:
      case DOUBLE:
        return new MPrimitiveType(this, type);
      case VOID:
        return new MVoidType(this);
      case ARRAY:
        return new MArrayType(this, this.output(HTypeUtils.componentType(type)));
      case DECL:
        return this._decloutput((HDeclType) this);
      case INTERSECTION:
      case LAMBDA:
      case NEVER:
      case TUPLE:
      case UNION:
      case WILDCARD:
        break;
    }

    throw new IllegalArgumentException(type.getTypeKind().toString());

  }

  private MInputType _input(HType type) {

    switch (type.getTypeKind()) {

      case STRING:
      case INT:
      case BOOLEAN:
      case DOUBLE:
        return new MPrimitiveType(this, type);

      case VOID:
        return new MInputTupleType(this, HTypeUtils.emptyTuple());

      case TUPLE:
        return new MInputTupleType(this, (HTupleType) type);

      case ARRAY:
        return new MPrimitiveType(this, type);

      default:
        throw new IllegalArgumentException(type.getTypeKind().toString());

    }

  }

  public boolean isVisible(MField m) {

    switch (this.viewKind) {
      case MUTATION:
        return m.getMutability() == MMutability.MUTABLE;
      case QUERY:
        return m.getMutability() == MMutability.CONST && m.getOutputType().outputKind() != MOutputKind.EVENT;
      case SUBSCRIPTION:
        return m.getOutputType().outputKind() == MOutputKind.EVENT;
    }

    throw new IllegalArgumentException(this.viewKind.toString());
  }

  /**
   *
   */

  public MArgument arg(String pname, MInputType ptype, boolean optional) {
    return new MArgumentImpl(pname, ptype, optional);
  }

  @Override
  public String toString() {
    return String.format("%s@%s", this.viewKind, this.entryPoint);
  }

  public boolean isExported(HMember m) {
    if (m.getModifiers().isPrivate()) {
      return false;
    } else if (m.getModifiers().isProtected()) {
      return false;
    }
    if (m.getModifiers().isInternal()) {
      return false;
    }
    if (!this.isExported(HTypeUtils.type(m))) {
      return false;
    }
    if (m.getModifiers().isStatic()) {
      return false;
    }
    return (m.getDeclaringType().getDeclKind() == HDeclKind.VIEW)
        || (m.getDeclaringType().getDeclKind() == HDeclKind.TYPE)
        || m.getDeclaringType().getModifiers().isExport()
        || m.getModifiers().isExport();

  }

  private boolean isExported(HType type) {
    switch (type.getTypeKind()) {
      case DECL: {
        final HDeclType decl = (HDeclType) type;
        if (decl.getModifiers().isInternal()) {
          return false;
        }
        return true;
      }
      default:
        return true;
    }
  }

  @Override
  public MInputType createInput(List<MArgument> args) {
    return new MInputStructType(this, args);
  }

  @Override
  public MViewKind viewKind() {
    return this.viewKind;
  }

  @Override
  public MOutputType outputType(String name) {
    return this.output(this.registry.getModule().getType(name));
  }

  @Override
  public HModule getModule() {
    return this.registry.getModule();
  }

  /**
   * Initialise this view.
   */

  void init() {

    this.root = (MDeclTypeImpl) this.output(this.entryPoint);
    this.converters = this.root.converters().collect(Collectors.toList());

    final Scanner scanner = new Scanner();

    scanner.scan(this.root());
    // now we have each field that is accssiable through this context, generate the
    // fields inline.

    for (final MField field : scanner.scanned) {
      if (field instanceof MDynamicArrayFieldImpl || field instanceof MDynamicFieldImpl) {
        continue;
      }
      if (field.getMember() == null) {
        // for MEdge
        continue;
      }
      switch (field.getMember().getMemberKind()) {
        case METHOD:
          this.inline((MMethodImpl) field);
          break;
        case CONNECTION:
          // nothing to do here.
          break;
        case AMBIENT:
        case LINK:
        case STATE:
          // these require no work, as they are already roots.
          break;
        case PERMISSION:
        case SELECTION:
        default:
          throw new IllegalArgumentException();
      }
    }

  }

  private void inline(MMethodImpl field) {

    // final HMethod method = field.getMember();

    new MFieldExpression(field).generate();

  }

  private class Scanner {

    Set<MField> scanned = new HashSet<>();
    Set<MInputType> inputs = new HashSet<>();
    Set<MOutputType> outputs = new HashSet<>();

    public void scan(MDeclTypeImpl type) {
      type.fields().forEach(this::scan);
    }

    private void scan(MField field) {
      if (field.getContext() != MViewContextImpl.this) {
        return;
      }
      if (!this.scanned.add(field)) {
        return;
      }
      this.scan(field.getInputType());
      this.scan(field.getOutputType());
    }

    private void scan(MOutputType outputType) {
      if (outputType.getView() != MViewContextImpl.this) {
        return;
      }
      if (!this.outputs.add(outputType)) {
        return;
      }
      outputType.fields().forEach(this::scan);
    }

    private void scan(MInputType inputType) {
      if (!this.inputs.add(inputType)) {
        return;
      }
      inputType.arguments().map(MArgument::getType).forEach(this::scan);
    }

  }

  @Override
  public HLoader getTypeLoader() {
    return this.getModule().getTypeLoader();
  }

  @Override
  public MInputType input(HTypeToken token) {
    return this.input(this.getTypeLoader().fromToken(token));
  }

}
