package io.zrz.hai.runtime.engine;

import org.apache.commons.lang3.StringUtils;

import io.zrz.hai.runtime.compile.parse.GDocument;
import io.zrz.hai.runtime.compile.parse.GExecutable;

public class EExecutionUtils {

  public static GExecutable select(GDocument doc, String operationName) {

    operationName = StringUtils.trimToNull(operationName);

    if (operationName == null) {

      if (doc.getDefaultExecutable() != null) {
        return doc.getDefaultExecutable();
      } else {
        return doc.getExecutables().get(0);
      }
    }

    return doc.getExecutable(operationName);

  }

}
