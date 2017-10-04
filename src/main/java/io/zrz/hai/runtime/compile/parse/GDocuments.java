package io.zrz.hai.runtime.compile.parse;

import java.nio.file.Path;

import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.parser.GQLParser;
import io.zrz.hai.runtime.compile.facade.MView;
import io.zrz.hai.runtime.compile.parse.impl.GDocumentImpl;

public class GDocuments {

  public static GDocumentResult parse(MView view, Path path) {
    return parse(view, GQLParser.parseDocument(path));
  }

  public static GDocumentResult parse(MView view, String query) {
    return parse(view, GQLParser.parseDocument(query));
  }

  public static GDocumentResult parse(MView view, GQLDocument doc) {
    final GDocumentBuilder builder = new GDocumentBuilder(view, doc);
    final GDocumentImpl gd = builder.build();
    return GDocumentResult.of(gd, builder.getDiagnostics());
  }

}
