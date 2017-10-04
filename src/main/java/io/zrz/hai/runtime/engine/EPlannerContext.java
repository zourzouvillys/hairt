package io.zrz.hai.runtime.engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.zrz.hai.runtime.PathToken;
import io.zrz.hai.runtime.ZAny;
import io.zrz.hai.runtime.ZConnection;
import io.zrz.hai.runtime.ZEdge;
import io.zrz.hai.runtime.ZIterator;
import io.zrz.hai.runtime.ZKind;
import io.zrz.hai.runtime.ZNode;
import io.zrz.hai.runtime.ZPropertyContainer;
import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.runtime.compile.facade.impl.MDynamicFieldImpl;
import io.zrz.hai.runtime.compile.parse.GExecutable;
import io.zrz.hai.runtime.compile.parse.GSelection;
import io.zrz.hai.runtime.compile.parse.impl.GExecutableImpl;
import io.zrz.hai.runtime.compile.parse.impl.GFieldSelection;
import io.zrz.hai.runtime.compile.parse.impl.GSelectionKind;
import io.zrz.hai.runtime.engine.actions.EAction;
import io.zrz.hai.runtime.engine.analysis.EPlanAnalizer;
import io.zrz.hai.runtime.engine.planner.PlannerFrame;
import io.zrz.hai.runtime.engine.planner.SelectionArgResolver;
import io.zrz.hai.runtime.engine.steps.EConnectionCountExpr;
import io.zrz.hai.runtime.engine.steps.EConnectionIndexStep;
import io.zrz.hai.runtime.engine.steps.EConnectionIteratorStep;
import io.zrz.hai.runtime.engine.steps.EConnectionStep;
import io.zrz.hai.runtime.engine.steps.EEdgeScanStep;
import io.zrz.hai.runtime.engine.steps.EEdgeTraverseStep;
import io.zrz.hai.runtime.engine.steps.EExpr;
import io.zrz.hai.runtime.engine.steps.EInvokeStep;
import io.zrz.hai.runtime.engine.steps.ELinkStep;
import io.zrz.hai.runtime.engine.steps.ENewNodeStep;
import io.zrz.hai.runtime.engine.steps.EResultIntent;
import io.zrz.hai.runtime.engine.steps.EScalarStep;
import io.zrz.hai.runtime.engine.steps.EStateExpr;
import io.zrz.hai.runtime.engine.steps.EStep;
import io.zrz.hai.runtime.engine.steps.EViewerStage;
import io.zrz.hai.symbolic.HConnection;
import io.zrz.hai.symbolic.HLink;
import io.zrz.hai.symbolic.HMethod;
import io.zrz.hai.symbolic.HState;
import io.zrz.hai.symbolic.expr.HLambdaExpr;
import io.zrz.hai.symbolic.type.HConnectionType;
import io.zrz.hai.symbolic.type.HDeclType;
import io.zrz.hai.symbolic.type.HEdgeType;
import io.zrz.hai.symbolic.type.HNodeType;
import io.zrz.hai.symbolic.type.HType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * context for performing planning of an executable, including @live, @batch,
 * and @defer as well as subscription handling.
 *
 * See {@link package-info.java} for more detail on the planning and execution
 * process.
 *
 */

@Slf4j
public class EPlannerContext {

  private final GExecutableImpl exec;

  // the initial entry node for all executions.
  private final EStep viewNode;

  /**
   * initial planner context.
   */

  public EPlannerContext(GExecutableImpl exec) {
    this.exec = exec;
    this.viewNode = new EViewerStage();
  }

  /**
   * entry point for planning.
   *
   * the initial step depends on if it is a mutation, query, or subscription.
   *
   * mutations have different handling for sequential execution and selections
   * being made at the time of the mutation completing. queries are done in
   * parallel on a single snapshot.
   *
   */

  public EExecutionPlan plan() {

    switch (this.exec.getKind()) {
      case QUERY:
        // simple case, traverse down.
        return this.analize(this.query(this.exec));
      case MUTATION:
        // first invoke all of the mutations, then query on the
        // results of them all if they succeed.
        return this.analize(this.mutation(this.exec));
      case SUBSCRIPTION:
      default:
        throw new IllegalArgumentException(this.exec.getKind().toString());
    }

  }

  private EExecutionPlan analize(EExecutionPlan plan) {
    new EPlanAnalizer(plan).analize();
    return plan;
  }

  /**
   * plan a query for execution. each root field receives the view as the starting
   * expression. we unfold the expression to generate either an expression or
   * another step, which is then merged into the result.
   *
   * we track expression sources which refer to the same item, and share them
   * using a {@link ECommonStep}. The optimisation step will then either split or
   * merge them depending on the associated costs.
   *
   * at this stage we don't bother trying to detect if we should use a merge or
   * simple object selection, and instead have a merge container that everything
   * gets added into. In the optimisation stage we replaces merges with just one
   * item.
   *
   */

  private EExecutionPlan query(GExecutable exec) {

    // final EMergeFieldsStep merge = new EMergeFieldsStep();
    final EResultIntent rootIntent = new EResultIntent();

    // run through each selection.
    for (final GSelection sel : exec.getSelections()) {
      this.select(this.viewNode, sel, rootIntent, PathToken.emptyToken());
    }

    return new EExecutionPlan(exec, null, rootIntent);

  }

  /**
   * calculate a single selection
   */

  private void select(EStep context, GSelection sel, EResultIntent intent, PathToken path) {

    Objects.requireNonNull(context, path.toString());

    //
    // build the step which fetches the source. the output will be whatever
    // the selection type is.
    //
    // the result is places as a shared source. this allows us to split it off to
    // run in parallel on different nodes if it is more optimal.
    //

    EStep source;

    if (sel.getSelectionKind() != GSelectionKind.SPREAD) {

      final GFieldSelection fsel = (GFieldSelection) sel;

      switch (sel.getField().getFieldKind()) {

        case STATE:

          // illegal to make any extra selections on a state field.
          source = new EStateExpr(context, (HState) sel.getField().getMember());
          break;

        case CONNECTION: {
          // connection field on the current context.
          source = new EConnectionStep(context, (HConnection) sel.getField().getMember());
          break;
        }
        case CONTEXT:
        case LINK: {
          // a relation on the current context.
          final HLink link = (HLink) sel.getField().getMember();
          source = new EEdgeTraverseStep(new ELinkStep(context, link), link);
          break;
        }
        case DYNAMIC:

          switch (sel.getField().getOutputShape()) {

            case SINGLE:
            case MAYBE:

              // hmmrph.
              final MDynamicFieldImpl field = (MDynamicFieldImpl) sel.getField();

              switch (field.getDynamicFieldKind()) {
                case NODE:
                  source = new EEdgeTraverseStep(context, (HDeclType) field.getOutputType().getType());
                  break;
                case COUNT:
                case EDGES:
                case NODES:
                case PAGEINFO:
                case SCHEMA:
                case TYPEINFO:
                default:
                  throw new IllegalArgumentException(sel.getField().toString());

              }

              break;
            case LIST:
              source = context;
              break;
            default:
              throw new IllegalArgumentException(sel.getField().getOutputShape().toString());
          }

          break;

        case METHOD:
          source = this.method(context, fsel, (HMethod) sel.getField().getMember());
          break;

        default:
          throw new IllegalArgumentException(sel.getField().getFieldKind().toString());
      }

      switch (sel.getSelectionKind()) {
        case CONNECTION:
          source = new EConnectionIteratorStep(source, fsel.getArguments());
          break;
        case LIST:
        case OBJECT:
        case SCALAR:
        case SPREAD:
        default:
          break;

      }

    } else {

      source = context;

    }

    // now, apply the source to the selection
    switch (sel.getSelectionKind()) {

      case CONNECTION: {

        final HConnectionType conn = (HConnectionType) sel.getField().getOutputType().getType();
        final EStep fsource = source;
        sel.getSelections().forEach(subsel -> this.connection(conn, fsource, path.with(sel.getOutputName()), subsel, intent));
        return;
      }

      case LIST: {
        // this is an iteration on a connection or other iterable
        // throw new IllegalArgumentException();
        return;
      }

      case OBJECT: {
        // the source is a node, so we merge all of the selections
        final EStep fsource = source;
        sel.getSelections().forEach(subsel -> this.select(fsource, subsel, intent, path.with(sel.getOutputName())));
        return;
      }

      case SCALAR: {
        Objects.requireNonNull(source, path.with(sel.getOutputName()).toString());
        intent.add(path.with(sel.getOutputName()).toString(), (EExpr) source); // new EStateExpr(source, (HState) sel.getField().getMember()));
        return;
      }

      case SPREAD: {
        // spread is handled as a sub-selection based on the type
        // matching at execution time. treat the source as the
        // input, and apply the selection.
        final EStep fsource = source;
        sel.getSelections().forEach(subsel -> this.select(fsource, subsel, intent, path));
        return;
      }
    }

    throw new IllegalArgumentException(sel.getSelectionKind().toString());

  }

  /**
   * a selection on a connection
   */

  private void connection(HConnectionType conn, EStep source, PathToken path, GSelection sel, EResultIntent intent) {

    //
    // build the step which fetches the source. the output will be whatever
    // the selection type is.
    //
    // the result is places as a shared source. this allows us to split it off to
    // run in parallel on different nodes if it is more optimal.
    //

    // now, apply the source to the selection
    switch (sel.getSelectionKind()) {
      case LIST:
        switch (sel.getField().getFieldKind()) {
          case DYNAMIC: {
            final MDynamicFieldImpl field = (MDynamicFieldImpl) sel.getField();
            switch (field.getDynamicFieldKind()) {

              case EDGES: {
                final EEdgeScanStep scan = new EEdgeScanStep(source, conn, path.with(sel.getOutputName()).toString());
                final EResultIntent subintent = new EResultIntent();
                sel.getSelections().forEach(subsel -> this.select(scan, subsel, subintent, PathToken.emptyToken()));
                intent.add(path.with(sel.getOutputName()).toString(), subintent, scan);
                return;
              }

              case NODES: {
                final EEdgeScanStep scan = new EEdgeScanStep(source, conn, path.with(sel.getOutputName()).toString());
                final EEdgeTraverseStep end = new EEdgeTraverseStep(scan, (HDeclType) field.getOutputType().getType());
                final EResultIntent subintent = new EResultIntent();
                sel.getSelections().forEach(subsel -> this.select(end, subsel, subintent, PathToken.emptyToken()));
                intent.add(path.with(sel.getOutputName()).toString(), subintent, scan);
                return;
              }
              case COUNT:
                break;
              case NODE:
                break;
              case PAGEINFO:
                break;
              case SCHEMA:
                break;
              case TYPEINFO:
                break;
            }
            throw new IllegalArgumentException(field.getDynamicFieldKind().toString());
          }
          case CONNECTION:
          case CONTEXT:
          case LINK:
          case METHOD:
          case STATE:
            break;
        }
        throw new IllegalArgumentException();
      case SCALAR:
        switch (sel.getField().getFieldKind()) {
          case DYNAMIC: {
            final MDynamicFieldImpl field = (MDynamicFieldImpl) sel.getField();
            switch (field.getDynamicFieldKind()) {
              case COUNT:
                // the source will be the iterator. change to the underlying connection for the
                // total count.
                intent.add(path.with(sel.getOutputName()).toString(), new EConnectionCountExpr(((EConnectionIteratorStep) source).getSource()));
                return;
              case EDGES:
                break;
              case NODE:
                break;
              case NODES:
                break;
              case PAGEINFO:
                break;
              case SCHEMA:
                break;
              case TYPEINFO:
                break;
            }
            throw new IllegalArgumentException(field.getDynamicFieldKind().toString());
          }
          case CONNECTION:
          case CONTEXT:
          case LINK:
          case METHOD:
          case STATE:
            break;
        }
        throw new IllegalArgumentException();

      case SPREAD: {
        sel.getSelections().forEach(subsel -> this.select(source, subsel, intent, path));
        return;
      }

      case OBJECT:
      case CONNECTION:
        break;

    }

    throw new IllegalArgumentException(sel.getSelectionKind().toString());

  }

  /**
   * almost the same as a query, except each root selection gets put into a
   * separate sequence container.
   */

  private EExecutionPlan mutation(GExecutable exec) {
    return this.query(exec);
  }

  // --------------------------------------------------------------------------------
  // Method Inlining
  // --------------------------------------------------------------------------------

  /**
   * given a method, returns an inline expression tree for it, deferring only to
   * method references when it is not possible to inline.
   *
   * The result in a somewhat back-to-front tree, where the innermost dependencies
   * are at the bottom.
   *
   * Traversals are generated as a single expression, e.g
   * viewer.root.users['theo'] is converted to a single path expression, with the
   * name expression being 'viewer' (the initial context).
   *
   * All branches are traversed and put inline. join points are handled as a
   * common expression, and depend on the branches. This makes it easier for
   * hauling up common expressions.
   *
   * Binary expressions such as '(user !in users)' are converted into two branches
   * of the tree, resulting in them meeting at the conditional branch point. We
   * will commonly merge them back together, but the execution planner takes care
   * of that, and having them independent means we don't need to do any dependency
   * analysis at execution planning time, which can be expensive.
   *
   * @return
   *
   */

  private final Map<EStep, StubNode> stubs = new HashMap<>();

  private EStep method(EStep context, GFieldSelection sel, HMethod method) {

    final List<ZAny> args = sel.getArguments()
        .getInitializers()
        .stream()
        .map(index -> index.accept(new SelectionArgResolver(sel)))
        .collect(Collectors.toList());

    final PlannerFrame frame = new PlannerFrame(method.getExecutable(), this);

    final ZAny res = frame.run(method.getReturnType(),
        this.stubs.computeIfAbsent(context, x -> {
          return new StubNode(frame, context, sel.getReceiverType());
        }),
        args);

    final List<EAction> actions = frame.getRecorder().getActions();

    if (res == null) {
      System.err.println("  ----[RES]> " + res);
      return context;
    }

    if (res instanceof ZValue) {
      return new EScalarStep((ZValue) res);
    }

    if (!(res instanceof StepAccess)) {
      throw new IllegalArgumentException(res.getClass().toGenericString());
    }

    final EStep result = ((StepAccess) res).getStep();

    if (actions.isEmpty()) {
      System.err.println("  ----[RES]> " + result);
      return result;
    }

    return new EInvokeStep(actions, result);

  }

  interface StepAccess {

    EStep getStep();

  }

  /**
   * a referenced node.
   */

  private class StubNode implements ZNode, StepAccess {

    private final HType type;

    private final Map<HConnection, StubConnection> steps = new HashMap<>();
    private final Map<HLink, StubLinkEdge> edges = new HashMap<>();

    @Getter
    private final EStep step;

    private final PlannerFrame frame;

    public StubNode(PlannerFrame frame, HDeclType type) {
      this.frame = frame;
      this.step = new ENewNodeStep(type);
      this.type = Objects.requireNonNull(type);
    }

    public StubNode(PlannerFrame frame, EStep context, HType type) {
      this.frame = frame;
      this.step = context;
      this.type = Objects.requireNonNull(type);
    }

    public StubNode(EStep step, StubLinkEdge edge, HType type) {
      this.frame = edge.frame;
      this.step = step;
      this.type = Objects.requireNonNull(type);
    }

    public StubNode(EStep step, StubConnectionEdge edge, HNodeType type) {
      this.frame = edge.frame;
      this.step = step;
      this.type = Objects.requireNonNull(type);
    }

    @Override
    public HDeclType getType() {
      return (HDeclType) this.type;
    }

    @Override
    public ZValue getState(HState state) {
      throw new IllegalArgumentException();
    }

    @Override
    public void setState(HState state, ZValue value) {
      this.frame.getRecorder().set(this.step, state, value);
    }

    @Override
    public ZEdge getEdge(HLink link) {
      return this.edges.computeIfAbsent(link, l -> {
        return new StubLinkEdge(this, link, new ELinkStep(this.step, link));
      });
    }

    @Override
    public ZConnection getConnection(HConnection connection) {
      return this.steps.computeIfAbsent(connection, c -> {
        return new StubConnection(this, connection, new EConnectionStep(this.step, c));
      });

    }

    @Override
    public ZEdge createLinkTo(ZNode target, HLink link) {
      final StubNode ztarget = (StubNode) target;
      this.frame.getRecorder().link(this.step, link, ztarget.step);
      return this.edges.computeIfAbsent(link, l -> {
        return new StubLinkEdge(this, link, ztarget.step);
      });
    }

    @Override
    public String toString() {
      if (this.step != null) {
        return this.step.toString();
      }
      return "NEW-NODE:" + this.type;
    }

  }

  private class StubLinkEdge implements ZEdge, StepAccess {

    private final HLink link;
    private final ZNode target;
    private final StubNode src;

    @Getter
    private final EStep step;
    private final PlannerFrame frame;

    StubLinkEdge(StubNode node, HLink link, EStep step) {
      this.frame = node.frame;
      this.link = Objects.requireNonNull(link);
      this.step = step;
      this.src = node;
      this.target = new StubNode(new EEdgeTraverseStep(step, link), this, link.getType());
    }

    @Override
    public ZValue getState(HState state) {
      throw new IllegalArgumentException();
    }

    @Override
    public void setState(HState state, ZValue value) {
      throw new IllegalArgumentException();
    }

    @Override
    public ZKind getZKind() {
      throw new IllegalArgumentException();
    }

    @Override
    public HEdgeType getType() {
      throw new IllegalArgumentException();
    }

    @Override
    public ZNode getStartNode() {
      throw new IllegalArgumentException();
    }

    @Override
    public ZNode getEndNode() {
      return this.target;
    }

    @Override
    public String toString() {
      return this.src.toString() + "->" + this.link.getName();
    }

  }

  private class StubConnectionEdge implements ZEdge, StepAccess {

    PlannerFrame frame;
    private final HConnection connection;
    private final StubNode node;
    private final ZNode target;
    private final ZAny indexed;

    @Getter
    private final EStep step;

    public StubConnectionEdge(StubNode node, HConnection conn, ZAny value, EStep step) {
      this.frame = node.frame;
      this.step = Objects.requireNonNull(step);
      this.node = Objects.requireNonNull(node);
      this.connection = Objects.requireNonNull(conn);
      this.target = new StubNode(new EEdgeTraverseStep(step, conn), this, this.connection.getConnectionType().getNodeType());
      this.indexed = value;
    }

    public StubConnectionEdge(StubNode node, HConnection conn, ZAny value) {
      this.frame = node.frame;
      this.step = null;
      this.node = Objects.requireNonNull(node);
      this.connection = Objects.requireNonNull(conn);
      this.target = new StubNode(new EConnectionIndexStep(this.step, conn, value), this, this.connection.getConnectionType().getNodeType());
      this.indexed = value;
    }

    @Override
    public ZValue getState(HState state) {
      throw new IllegalArgumentException();
    }

    @Override
    public void setState(HState state, ZValue value) {
      throw new IllegalArgumentException();
    }

    @Override
    public ZKind getZKind() {
      throw new IllegalArgumentException();
    }

    @Override
    public HEdgeType getType() {
      throw new IllegalArgumentException();
    }

    @Override
    public ZNode getStartNode() {
      throw new IllegalArgumentException();
    }

    @Override
    public ZNode getEndNode() {
      return this.target;
    }

    @Override
    public String toString() {
      return this.node + "->" + this.connection.getName() + "[" + this.indexed + "]";
    }

  }

  private class StubConnection implements ZConnection, StepAccess {

    private final StubNode node;
    private final HConnection connection;

    @Getter
    private final EConnectionStep step;
    private ZEdge target;

    private final Map<ZAny, StubConnectionEdge> indexed = new HashMap<>();
    private final List<StubConnectionEdge> added = new LinkedList<>();
    private final PlannerFrame frame;

    public StubConnection(StubNode node, HConnection connection, EConnectionStep step) {
      this.frame = node.frame;
      this.step = Objects.requireNonNull(step);
      this.node = Objects.requireNonNull(node);
      this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public ZEdge addNode(ZNode target, ZPropertyContainer edge) {
      final StubConnectionEdge xtarget = new StubConnectionEdge(this.node, this.connection, target);
      this.added.add(xtarget);
      this.frame.getRecorder().add(this.step, this.connection, ((StubNode) target).step);
      return xtarget;
    }

    @Override
    public boolean remove(ZNode remove) {
      throw new IllegalArgumentException();
    }

    @Override
    public boolean remove(ZEdge edge) {
      throw new IllegalArgumentException();
    }

    @Override
    public ZEdge getEdge(HLambdaExpr unique) {
      throw new IllegalArgumentException();
    }

    @Override
    public ZEdge getNode(HLambdaExpr unique) {
      throw new IllegalArgumentException();
    }

    @Override
    public ZConnection filter(HLambdaExpr filter) {
      throw new IllegalArgumentException();
    }

    @Override
    public ZConnection reduce(HLambdaExpr filter) {
      throw new IllegalArgumentException();
    }

    @Override
    public int count() {
      throw new IllegalArgumentException();
    }

    @Override
    public ZIterator iterator() {
      throw new IllegalArgumentException();
    }

    @Override
    public ZEdge getIndexedNode(ZAny value) {
      return this.indexed.computeIfAbsent(value, (v) -> {
        final EConnectionIndexStep s = new EConnectionIndexStep(this.step, this.connection, value);
        return new StubConnectionEdge(this.node, this.connection, value, s);
      });
    }

    @Override
    public void forEach(Consumer<ZEdge> consumer) {
      throw new IllegalArgumentException();
    }

    @Override
    public ZConnection params(PagingParams params) {
      throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
      return this.node + "->" + this.connection.getName();
    }

  }

  public ZNode createNode(PlannerFrame frame, HNodeType type) {
    final StubNode node = new StubNode(frame, type);
    frame.getRecorder().create(type);
    return node;
  }

}
