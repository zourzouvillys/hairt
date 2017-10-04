package io.zrz.hai.runtime.engine.eval;

import java.util.Objects;

import org.neo4j.graphdb.Relationship;

import io.zrz.hai.runtime.ZValue;

public class NeoLink implements IEdge {

  private final NeoStoreHandle handle;
  private final Relationship rel;
  private NeoNode target;

  public NeoLink(NeoStoreHandle handle, Relationship rel) {
    this.handle = handle;
    this.rel = rel;
    this.target = this.handle.getNode(rel.getEndNode());
  }

  public NeoLink(NeoStoreHandle handle, NeoNode target) {
    this.handle = handle;
    this.rel = null;
    this.target = Objects.requireNonNull(target);
  }

  @Override
  public boolean hasProperty(String key) {
    return this.rel.hasProperty(key);
  }

  @Override
  public ZValue getProperty(String key) {
    return ZValue.from((String) this.rel.getProperty(key));
  }

  @Override
  public void setProperty(String name, ZValue value) {
    this.rel.setProperty(name, value.toString());
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

  public void setTarget(NeoNode target) {
    this.target = target;
  }

}
