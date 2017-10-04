package io.zrz.hai.runtime.engine.eval;

import org.neo4j.graphdb.Relationship;

import io.zrz.hai.runtime.ZValue;

public class NeoEdge implements IEdge {

  private final NeoConnection conn;
  private final Relationship rel;
  private final NeoNode target;

  public NeoEdge(NeoConnection conn, Relationship rel) {
    this.conn = conn;
    this.rel = rel;
    this.target = conn.getNode().getHandle().getNode(rel.getEndNode());
  }

  public NeoEdge(NeoConnection conn, NeoNode target) {
    this.conn = conn;
    this.rel = null;
    this.target = target;
  }

  @Override
  public boolean hasProperty(String name) {
    return false;
  }

  @Override
  public ZValue getProperty(String name) {
    return null;
  }

  @Override
  public void setProperty(String name, ZValue value) {
    // TODO Auto-generated method stub

  }

  @Override
  public INode getStartNode() {
    throw new IllegalArgumentException();
  }

  @Override
  public INode getEndNode() {
    return this.target;
  }

  @Override
  public void delete() {
    this.rel.delete();
  }

}
