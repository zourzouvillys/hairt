package io.zrz.hai.runtime.engine.eval;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import io.zrz.hai.symbolic.HConnection;
import io.zrz.hai.symbolic.HLink;
import io.zrz.hai.symbolic.HModule;
import io.zrz.hai.symbolic.HState;
import io.zrz.hai.symbolic.type.HDeclType;
import io.zrz.hai.symbolic.type.HType;

public class ENeoUtils {

  /**
   * creates a new node that is the content of the given link from the start node.
   */

  public static Node createLinkedNode(Node start, HLink link) {
    final Node end = start.getGraphDatabase().createNode(labelFor(link.getType()));
    start.createRelationshipTo(end, relFor(link));
    return end;
  }

  public static Relationship link(Node start, HLink link, Node end) {
    return start.createRelationshipTo(end, relFor(link));
  }

  public static Label labelFor(HType type) {
    return Label.label(((HDeclType) type).getQualifiedName());
  }

  public static RelationshipType relFor(HLink link) {
    return RelationshipType.withName(link.getName());
  }

  public static RelationshipType relFor(HConnection conn) {
    return RelationshipType.withName(conn.getName());
  }

  public static String propertyFor(HState state) {
    return state.getName();
  }

  public static Node createNode(GraphDatabaseService graph, HDeclType type) {
    return graph.createNode(labelFor(type));
  }

  public static void setState(Node node, HState state, Object value) {
    node.setProperty(propertyFor(state), value);
  }

  /**
   *
   * @param start
   * @param connection
   * @param end
   * @return
   */

  public static Relationship append(Node start, HConnection connection, Node end) {

    return start.createRelationshipTo(end, relFor(connection));

  }

  public static HDeclType getType(HModule module, Node node) {
    for (final Label label : node.getLabels()) {
      return module.getType(label.name());
    }
    throw new IllegalArgumentException();
  }

}
