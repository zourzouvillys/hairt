package io.zrz.hai.runtime.engine.eval;

import java.util.Objects;

import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.runtime.engine.steps.EConnectionIteratorStep;
import io.zrz.hai.symbolic.HConnection;
import io.zrz.hai.symbolic.HLink;
import lombok.extern.slf4j.Slf4j;

/**
 * tracks work context information to generate a version clock.
 *
 * we want to record and validate the bare minimum dependency information to
 * ensure that the evaluation would not change if any data has changed in other
 * versions. Therefore, we directly map how to evaluate to what is recorded, and
 * check that when there are store mutations that could potentially conflict.
 *
 * To do this, we navigate to the mutations and any expressions used in branches
 * that are taken to ensure the traversal remains the same.
 *
 * Although it seems that if the mutation is dependency free (meaning we can
 * execute in a single non blocking batch on a single node), then we should
 * perform pessimistically by locking as we go, this can trigger large amounts
 * of locking on nodes for path traversals. On the first pass, we fetch each
 * node and relationship ID, so fetching the second time (for validation) is
 * very fast, and leaves the locking to us rather than neo4j which means we can
 * perform failure detection quicker than neo4j can.
 *
 */

@Slf4j
public class NeoWorkContext implements NeoStoreContext {

  private final ENeoExecutionContext ctx;

  // --------------------------------------------------------------------------------
  //
  // --------------------------------------------------------------------------------

  NeoWorkContext(ENeoExecutionContext ctx) {
    this.ctx = ctx;
  }

  // --------------------------------------------------------------------------------
  // Mutations
  // --------------------------------------------------------------------------------

  @Override
  public void create(INode node) {
    // log.debug("CREATE NODE N{} ({})", node.getRawNode().getId(), node.getType());
  }

  @Override
  public void write(INode node, String key, String value) {
    // log.debug("WRITE PROPERTY N{} {} = {}", node.getRawNode().getId(), key,
    // value);
  }

  @Override
  public void setlink(INode node, HLink link, IEdge rel) {
    // log.debug("SET LINK N{} ->{} = (E{} -> N{})",
    // node.getRawNode().getId(),
    // link.getName(),
    // rel.getRawRelationship().getId(),
    // rel.getRawRelationship().getEndNodeId());
  }

  @Override
  public void append(INode node, HConnection connection, INode end) {
    // log.debug("APPEND NODE N{} {} += N{}", node.getRawNode().getId(),
    // connection.getName(), end.getRawNode().getId());
  }

  // --------------------------------------------------------------------------------
  // Reads
  // --------------------------------------------------------------------------------

  @Override
  public void hasprop(INode node, String key) {
    // log.debug("HASPROP N{}.{}", node.getRawNode().getId(), key);
  }

  @Override
  public void read(INode node, String key) {
    // log.debug("READ N{}.{}", node.getRawNode().getId(), key);
  }

  @Override
  public void count(INode node, HConnection connection) {
    // log.debug("COUNT N{}.{}", node.getRawNode().getId(), connection.getName());
  }

  @Override
  public void readlink(INode node, HLink link, IEdge rel) {
    Objects.requireNonNull(node);
    Objects.requireNonNull(link);
    Objects.requireNonNull(rel);
    // log.debug("GET LINK N{} -> {}", node.getRawNode().getId(), link.getName(),
    // rel.getRawRelationship().getId());
  }

  @Override
  public void readlink(IEdge rel, HLink link) {
    // log.debug("GET LINK E{} -> {}", rel.getRawRelationship().getId(),
    // link.getName());
  }

  @Override
  public void traverse(IEdge rel) {
    // log.debug("TRAVERSE N{} -> [E{}:{}] -> N{}",
    // rel.getRawRelationship().getStartNodeId(),
    // rel.getRawRelationship().getId(),
    // rel.getMember().getName(),
    // rel.getEndNode().getRawNode().getId());
  }

  @Override
  public void hit(INode node, HConnection connection, ZValue key) {
    // log.debug("INDEX HIT N{}.{} = {}", node.getRawNode().getId(),
    // connection.getName(), key);
  }

  @Override
  public void miss(INode node, HConnection connection, ZValue key) {
    // log.debug("INDEX MISS N{}.{} = {}", node.getRawNode().getId(),
    // connection.getName(), key);
  }

  @Override
  public void scan(EConnectionIteratorStep it, INode node, HConnection connection) {
    // log.debug("SCAN START {} [N{}->{}]", it, node.getRawNode().getId(),
    // connection.getName());
  }

  @Override
  public void next(EConnectionIteratorStep it, INode node, HConnection connection, IEdge e) {
    // log.debug("SCAN NEXT {} [N{}.{}.E{} (-> N{} {})]",
    // it,
    // node.getRawNode().getId(),
    // connection.getName(),
    // e.getRawRelationship().getId(),
    // e.getEndNode().getRawNode().getId(),
    // e.getEndNode().getRawNode().getAllProperties());
  }

}
