package io.zrz.hai.runtime.engine.eval;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.factory.primitive.LongObjectMaps;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;

import com.google.common.collect.Iterators;

import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.symbolic.HConnection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NeoConnection implements IConnection {

  @Getter
  private final NeoNode node;

  @Getter
  private final HConnection connection;

  private final NeoStoreHandle handle;

  @Getter
  private final LinkedList<NeoEdge> added = new LinkedList<>();
  private final MutableLongObjectMap<NeoEdge> edges = LongObjectMaps.mutable.empty();

  public NeoConnection(NeoNode node, HConnection connection) {
    this.handle = node.getHandle();
    this.node = node;
    this.connection = connection;
  }

  @Override
  public Iterator<IEdge> iterator() {

    log.debug("iterate {}.{}", this.node, this.connection.getName());

    if (this.node.getStoreNode() == null) {
      return Iterators.transform(this.getAdded().iterator(), a -> a);
    }

    final Iterator<Relationship> rit = this.node.getStoreNode()
        .getRelationships(ENeoUtils.relFor(this.connection), Direction.OUTGOING)
        .iterator();

    return Iterators.concat(
        Iterators.transform(rit, rel -> this.getEdge(rel)),
        this.getAdded().iterator());

  }

  @Override
  public HConnection getMember() {
    return this.connection;
  }

  @Override
  public IEdge lookup(String key, ZValue value) {
    log.debug("lookup {} = {}", key, value);
    return null;
  }

  @Override
  public IEdge add(INode target) {
    log.debug("Add edge {}.{} = {}", this.node, this.connection.getName(), target);
    final NeoEdge edge = new NeoEdge(this, (NeoNode) target);
    this.added.add(edge);
    return edge;
  }

  @Override
  public int count() {
    // TODO Auto-generated method stub
    return 0;
  }

  public NeoEdge getEdge(Relationship next) {
    return this.edges.getIfAbsentPut(next.getId(), () -> new NeoEdge(this, next));
  }

}
