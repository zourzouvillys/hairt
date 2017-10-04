package io.zrz.hai.runtime.engine;

import java.io.OutputStream;

import io.zrz.hai.syntax.IndentPrintWriter;

public class EExecutionPlanPrinter {

  public static void print(OutputStream strm, EExecutionPlan plan) {
    final IndentPrintWriter w = new IndentPrintWriter(strm);
    plan.dump(w);
    w.flush();
  }

}
