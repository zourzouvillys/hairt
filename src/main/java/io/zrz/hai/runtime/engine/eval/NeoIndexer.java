package io.zrz.hai.runtime.engine.eval;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.helpers.collection.MapUtil;

/**
 * the indexer which keeps up to date as updates occur to the graph.
 */

public class NeoIndexer {

  private final Index<Node> nodes;
  // private final RelationshipIndex rels;

  public NeoIndexer(GraphDatabaseService graph) {

    this.nodes = graph.index().forNodes(
        "exact-case-insensitive",
        MapUtil.stringMap("type", "exact", "to_lower_case", "true"));

    // this.rels = graph.index().forRelationships(
    // "connections",
    // MapUtil.stringMap("type", "exact", "to_lower_case", "true"));

  }

  void index() {

    //
    // final RelationshipIndex relidx =
    //
    // // relidx.get("", "", xxx, yyy);
    // index.add(root, "year-numeric", new ValueContext(1999).indexNumeric());
    //

    // final IndexHits<Node> hits = this.nodes.query(
    // QueryContext
    // .numericRange("year", 1, 10)
    // .defaultOperator(QueryParser.Operator.AND)
    // .sortNumeric("year", false)
    // .sort("year", "xxx")
    // .top(1));

    //
    // final Node hit = hits.getSingle();

    // for (final Node hit : hits) {
    // // all movies with a title in the index, ordered by year, then title
    // System.err.println(hit);
    // }

    // final IndexHits<Node> movies = this.nodes.query(new WildcardQuery(new
    // Term("title", "The Matrix*")));

    this.nodes.query(new TermQuery(new Term("name", "Keanu Reeves"))).getSingle();

  }

}
