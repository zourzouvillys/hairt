package io.zrz.hai.runtime.engine.eval;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import io.zrz.hai.runtime.ZValue;
import io.zrz.hai.type.HConnection;
import io.zrz.hai.type.HDeclType;
import io.zrz.hai.type.HLink;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NeoNode implements INode {

  @Getter
  private final NeoStoreHandle handle;

  /**
   * if there is a backing store node.
   */

  @Getter
  private Node storeNode;

  /**
   * the model type of this node.
   */

  @Getter
  private HDeclType type;

  /**
   * the property set.
   */

  private Map<String, ZValue> properties;

  /**
   * the links
   */

  private Map<HLink, NeoLink> links;

  /**
   * the connections.
   */

  private Map<HConnection, NeoConnection> connections;

  /**
   *
   */

  public NeoNode(NeoStoreHandle handle, Node node) {
    this.handle = handle;
    this.storeNode = node;
  }

  public NeoNode(NeoStoreHandle handle, HDeclType type) {
    this.handle = handle;
    this.type = type;
  }

  /**
   * dynamically instantiate a connection as needed.
   */

  @Override
  public IConnection connection(HConnection connection) {
    if (this.connections == null) {
      this.connections = new HashMap<>();
    }
    return this.connections.computeIfAbsent(connection, x -> new NeoConnection(this, connection));
  }

  /**
   * fetch a link.
   */

  @Override
  public IEdge getLink(HLink link) {

    log.debug("Get link {}.{}", this, link.getName());

    if (this.links == null) {
      this.links = new HashMap<>();
    } else if (this.links.containsKey(link)) {
      return this.links.get(link);
    }

    final Relationship rel = this.storeNode.getSingleRelationship(ENeoUtils.relFor(link), Direction.OUTGOING);

    if (rel == null) {

      return null;
    }

    return this.links.computeIfAbsent(link, l -> new NeoLink(this.handle, rel));

  }

  @Override
  public IEdge setLink(HLink link, INode target) {

    if (this.links == null) {
      this.links = new HashMap<>();
    }

    log.debug("Setting {}.{} to {}", this, link.getName(), target);

    NeoLink nl = this.links.get(link);

    if (nl != null) {
      nl.setTarget((NeoNode) target);
      return nl;
    }

    nl = new NeoLink(this.handle, (NeoNode) target);

    this.links.put(link, nl);

    return nl;

  }

  @Override
  public boolean hasProperty(String key) {
    if (this.properties != null && this.properties.containsKey(key)) {
      return true;
    }
    if (this.storeNode != null) {
      return this.storeNode.hasProperty(key);
    }
    return false;
  }

  @Override
  public ZValue getProperty(String key) {
    if (this.properties != null && this.properties.containsKey(key)) {
      return this.properties.get(key);
    }
    if (this.storeNode != null) {
      return ZValue.from((String) this.storeNode.getProperty(key));
    }
    return null;
  }

  @Override
  public void setProperty(String name, ZValue value) {
    log.debug("SET {} = {}", name, value);
    if (this.properties == null) {
      this.properties = new HashMap<>();
    }
    this.properties.put(name, value);
  }

  /**
   *
   */

  @Override
  public String toString() {
    return (this.storeNode != null)
        ? this.storeNode.toString() + "/" + Integer.toHexString(this.hashCode())
        : "NewNode(" + Integer.toHexString(this.hashCode()) + ")";
  }

}
