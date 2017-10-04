package io.zrz.hai.runtime.compile.parse;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import io.zrz.graphql.core.doc.GQLArgument;
import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.graphql.core.doc.GQLFragmentDefinition;
import io.zrz.graphql.core.doc.GQLFragmentSpreadSelection;
import io.zrz.graphql.core.doc.GQLInlineFragmentSelection;
import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.graphql.core.doc.GQLSelection;
import io.zrz.graphql.core.doc.GQLSelectionVisitor;
import io.zrz.graphql.core.types.GQLNonNullType;
import io.zrz.graphql.core.value.GQLBooleanValue;
import io.zrz.graphql.core.value.GQLEnumValueRef;
import io.zrz.graphql.core.value.GQLFloatValue;
import io.zrz.graphql.core.value.GQLIntValue;
import io.zrz.graphql.core.value.GQLListValue;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLStringValue;
import io.zrz.graphql.core.value.GQLValueVisitor;
import io.zrz.graphql.core.value.GQLVariableRef;
import io.zrz.hai.runtime.compile.facade.MArgument;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MInputType;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MView;
import io.zrz.hai.runtime.compile.facade.MViewContext;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.runtime.compile.facade.impl.MInputStructType;
import io.zrz.hai.runtime.compile.parse.impl.GAnnotations;
import io.zrz.hai.runtime.compile.parse.impl.GDocumentImpl;
import io.zrz.hai.runtime.compile.parse.impl.GExecutableImpl;
import io.zrz.hai.runtime.compile.parse.impl.GFieldSelection;
import io.zrz.hai.runtime.compile.parse.impl.GFragmentImpl;
import io.zrz.hai.runtime.compile.parse.impl.GFragmentSpreadSelection;
import io.zrz.hai.runtime.compile.parse.impl.GInlineSpreadSelection;
import io.zrz.hai.runtime.compile.parse.impl.GSelectionImpl;
import io.zrz.hai.runtime.compile.parse.impl.GSelectionKind;
import io.zrz.hai.runtime.compile.parse.impl.TypeExtractor;
import io.zrz.hai.symbolic.HTypeToken;
import io.zrz.hai.symbolic.expr.HExpr;
import io.zrz.hai.symbolic.expr.HExprFactory;
import io.zrz.hai.symbolic.expr.HTupleInitExpr;
import io.zrz.hai.symbolic.expr.utils.TupleInitExprBuilder;
import lombok.Getter;

/**
 * Takes a GraphQL parsed document, and converts to a GDocument.
 */

public class GDocumentBuilder {

  private final MView view;

  private final GQLDocument doc;

  private GDocumentImpl result;

  @Getter
  private final GDiagnostics diagnostics;

  /**
   *
   * @param module
   * @param q
   */

  public GDocumentBuilder(MView view, GQLDocument doc) {
    this.view = view;
    this.doc = doc;
    this.diagnostics = new GDiagnostics(doc, view);
  }

  /**
   * convert to a document.
   */

  public GDocumentImpl build() {
    Preconditions.checkState(this.result == null);
    this.result = new GDocumentImpl(this.view);
    this.doc.fragments().forEach(op -> this.result.add(this.convert(op)));
    this.doc.operations().forEach(op -> this.result.add(this.convert(op)));
    this.result.resolve();
    return this.result;
  }

  /**
   * each operation and declared fragment has its own GExecutable.
   */

  private GExecutableImpl convert(GQLOperationDefinition op) {

    final MViewKind type = from(op.type());

    final MViewContext ctx = this.view.context(type);

    final List<MArgument> margs = op.vars().stream()
        .map(var -> {
          final MInputType ptype = var.type().apply(new TypeExtractor(ctx));
          // TODO: fix nullable stuffs
          return this.view.createArgument(var.name(), ptype, !(var.type() instanceof GQLNonNullType));
        })
        .collect(Collectors.toList());

    final MInputType input = ctx.createInput(margs);

    final GExecutableImpl exec = new GExecutableImpl(this.result, op.name(), type, input, ctx);

    // log.debug("{} {}", type, op.name());

    //
    for (final GQLSelection sel : op.selections()) {
      final GSelectionImpl child = this.select(exec, ctx.root(), sel);
      if (child != null) {
        exec.add(child);
      }
    }

    return exec;

  }

  private HTupleInitExpr toInputType(GBodyImpl exec, MViewContext ctx, MInputStructType in, List<GQLArgument> args) {

    final TupleInitExprBuilder b = new TupleInitExprBuilder();

    for (final GQLArgument arg : args) {

      final String name = arg.name();

      final HExpr value = arg.value().apply(new GQLValueVisitor<HExpr>() {

        @Override
        public HExpr visitVarValue(GQLVariableRef value) {

          final MArgument var = exec.var(value.name()).orElse(null);

          if (var == null) {
            GDocumentBuilder.this.diagnostics.error(GDiagnosticsKind.MISSING_PARAMETER,
                exec,
                value,
                "variable ${} not found in operation declaration",
                value.name());

            return null;
          }

          in.add(var.withName(arg.name()));

          return HExprFactory.var(var.getName(), var.getType().getType());

        }

        @Override
        public HExpr visitObjectValue(GQLObjectValue value) {
          return null;
        }

        @Override
        public HExpr visitListValue(GQLListValue value) {
          return null;
        }

        @Override
        public HExpr visitBooleanValue(GQLBooleanValue value) {
          in.add(GDocumentBuilder.this.view.createArgument(arg.name(), ctx.input(HTypeToken.BOOLEAN), false));
          return HExprFactory.value(value.value());
        }

        @Override
        public HExpr visitIntValue(GQLIntValue value) {
          in.add(GDocumentBuilder.this.view.createArgument(arg.name(), ctx.input(HTypeToken.INT), false));
          return HExprFactory.value(value.value());
        }

        @Override
        public HExpr visitStringValue(GQLStringValue value) {
          in.add(GDocumentBuilder.this.view.createArgument(arg.name(), ctx.input(HTypeToken.STRING), false));
          return HExprFactory.value(value.value());
        }

        @Override
        public HExpr visitFloatValue(GQLFloatValue value) {
          in.add(GDocumentBuilder.this.view.createArgument(arg.name(), ctx.input(HTypeToken.DOUBLE), false));
          return HExprFactory.value(value.value());
        }

        @Override
        public HExpr visitEnumValueRef(GQLEnumValueRef value) {
          GDocumentBuilder.this.diagnostics.error(GDiagnosticsKind.UNSUPPORTED_FEATURE, exec, value, "unknown enum value '{}'", value.value());
          return null;
        }

      });

      if (value != null) {

        b.add(name, value);

      }

    }

    return b.build(this.view.getModule().getTypeLoader());
  }

  private GSelectionImpl select(GBodyImpl exec, MOutputType type, GQLSelection sel) {

    return sel.apply(new GQLSelectionVisitor<GSelectionImpl>() {

      @Override
      public GSelectionImpl visitFieldSelection(GQLFieldSelection selection) {

        final MField field = type.field(selection.name());

        if (field == null) {
          GDocumentBuilder.this.diagnostics.warn(GDiagnosticsKind.UNKNOWN_FIELD, exec, selection, type,
              "unknown field {} in {}",
              selection.name(),
              type.getSimpleName());
          return null;
        }

        // fetch the type and the values in the document.
        final MInputStructType pin = new MInputStructType(type.getView());
        final HTupleInitExpr pvals = GDocumentBuilder.this.toInputType(exec, type.getView(), pin, selection.args());

        // and the defined parameters for this field.
        final MInputType params = field.getInputType();

        // bind the parameters, and throw if we can't handle them.
        final HTupleInitExpr args = this.bind(selection, field, params, pin, pvals);

        final MOutputType childType = field.getOutputType();

        final GFieldSelection psel = GFieldSelection.of(selection.outputName(), field, args);

        if (!selection.selections().isEmpty()) {
          selection.selections().forEach(child -> {
            final GSelectionImpl sub = GDocumentBuilder.this.select(exec, childType, child);
            if (sub != null) {
              psel.add(sub);
            }
          });
        }

        if (!selection.directives().isEmpty()) {
          for (final GQLDirective d : selection.directives()) {
            this.apply(d, psel);
          }
        }

        if (psel.getSelections().isEmpty() && psel.getSelectionKind() != GSelectionKind.SCALAR) {
          GDocumentBuilder.this.diagnostics.warn(GDiagnosticsKind.EMPTY_SELECTION, exec, selection, "empty selection");
          return null;
        }

        return psel;
      }

      /**
       * bind the provided args to the parameters, failing if we don't have the right
       * ones, or extra are provided that we don't know about.
       *
       * @param selection
       *
       * @param field
       *
       * @param params
       * @param pin
       * @param pvals
       */

      private HTupleInitExpr bind(GQLFieldSelection selection, MField field, MInputType params, MInputStructType pin, HTupleInitExpr pvals) {

        final Set<String> required = params.arguments().filter(f -> f.isMandatory()).map(f -> f.getName()).collect(Collectors.toSet());
        final Set<String> provided = pin.arguments().filter(f -> f.isMandatory()).map(f -> f.getName()).collect(Collectors.toSet());

        final SetView<String> missing = Sets.difference(required, provided);

        if (!missing.isEmpty()) {
          GDocumentBuilder.this.diagnostics.error(
              GDiagnosticsKind.MISSING_ARGUMENT,
              exec,
              selection,
              field,
              "missing mandatory argument{}: {}",
              missing.size() > 1 ? "s" : "",
              missing.stream().collect(Collectors.joining(", ")));

          // throw new IllegalArgumentException(String.format("selection on field %s.%s is
          // missing mandatory argument%s: %s",
          // field.getReflectedType().getSimpleName(),
          // field.getSimpleName(),
          // missing.size() > 1 ? "s" : "",
          // missing.stream().collect(Collectors.joining(", "))));
        }

        final Set<String> available = params.arguments().map(f -> f.getName()).collect(Collectors.toSet());
        final Set<String> all = pin.arguments().map(f -> f.getName()).collect(Collectors.toSet());

        final SetView<String> unknown = Sets.difference(all, available);

        if (!unknown.isEmpty()) {

          GDocumentBuilder.this.diagnostics.error(
              GDiagnosticsKind.UNKNOWN_ARGUMENT,
              exec,
              selection,
              field,
              "no such parameter{}: {} (available: {})",
              unknown.size() > 1 ? "s" : "",
              unknown.stream().collect(Collectors.joining(", ")),
              available);

          // throw new IllegalArgumentException(String.format("selection on %s.%s called
          // with unknown argument%s: %s",
          // field.getReflectedType().getSimpleName(),
          // field.getSimpleName(),
          // unknown.size() > 1 ? "s" : "",
          // unknown.stream().collect(Collectors.joining(", "))));
        }

        // now check the types are correct, or at least can be coerced.

        final TupleInitExprBuilder b = new TupleInitExprBuilder();

        params.arguments().forEachOrdered(arg -> {

          if (pvals.getType().get(arg.getName()) != null) {

            b.add(arg.getName(), pvals.expr(arg.getName()));

          }

        });

        return b.build(GDocumentBuilder.this.view.getModule().getTypeLoader());

      }

      private void apply(GQLDirective d, GFieldSelection psel) {
        final GAnnotation a = GAnnotations.parse(d);
        if (a != null) {
          psel.add(a);
        }
      }

      @Override
      public GSelectionImpl visitFragmentSelection(GQLFragmentSpreadSelection selection) {
        return GFragmentSpreadSelection.of(GDocumentBuilder.this.result, selection.name(), type);
      }

      @Override
      public GSelectionImpl visitInlineFragment(GQLInlineFragmentSelection frag) {

        final GFragmentImpl fragexec = new GFragmentImpl(type);

        //
        final List<GAnnotation> directives = frag.directives().stream()
            .map(d -> GAnnotations.parse(d))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        for (final GQLSelection sel : frag.selections()) {
          final GSelectionImpl child = GDocumentBuilder.this.select(exec, type, sel);
          if (child != null) {
            fragexec.add(child);
          }
        }

        if (!frag.selections().isEmpty()) {
          if (fragexec.getSelections().isEmpty()) {
            return null;
          }
        }

        return GInlineSpreadSelection.of(fragexec, type, directives);

      }

    });
  }

  static MViewKind from(GQLOpType optype) {
    switch (optype) {
      case Query:
        return MViewKind.QUERY;
      case Mutation:
        return MViewKind.MUTATION;
      case Subscription:
        return MViewKind.SUBSCRIPTION;
      default:
        throw new IllegalArgumentException(optype.toString());
    }
  }

  /**
   * convert a fragment into an executable.
   *
   * fragments are never applied on mutations (which is only ever root fields).
   *
   * @return
   */

  private GFragmentImpl convert(GQLFragmentDefinition frag) {

    final MViewContext ctx = this.view.context(MViewKind.QUERY);

    final MOutputType root = ctx.outputType(frag.namedType().name());

    final GFragmentImpl exec = new GFragmentImpl(this.result, frag.name(), root);

    //

    for (final GQLSelection sel : frag.selections()) {
      final GSelectionImpl child = this.select(exec, root, sel);
      if (child != null) {
        exec.add(child);
      }
    }

    return exec;

  }

}
