package io.zrz.hai.runtime.engine.eval;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * global interactions with the store.
 *
 * fetching nodes from this return a node that is directly backed by the store,
 * at the most recent store version.
 *
 * operating on these nodes will result in the changes being made to the store.
 *
 */

public class NeoStore {

  private final GraphDatabaseService graph;

  public NeoStore(GraphDatabaseService graph) {
    this.graph = graph;
  }

  /**
   * create a new node.
   */

  public Node createNode() {
    return this.graph.createNode();
  }

}
