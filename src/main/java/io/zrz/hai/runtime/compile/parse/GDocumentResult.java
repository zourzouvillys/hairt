package io.zrz.hai.runtime.compile.parse;

import lombok.Value;

@Value(staticConstructor = "of")
public class GDocumentResult {

  GDocument document;
  GDiagnostics diagnostics;

}
