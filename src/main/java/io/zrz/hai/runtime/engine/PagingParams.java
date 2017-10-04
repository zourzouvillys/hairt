package io.zrz.hai.runtime.engine;

import java.util.function.Function;

import io.zrz.hai.expr.HExpr;
import io.zrz.hai.expr.HTupleInitExpr;
import io.zrz.hai.runtime.ZAny;
import io.zrz.hai.runtime.ZValue;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PagingParams {

  private static final PagingParams EMPTY_PARAMS = PagingParams.builder().build();

  private Long first;
  private Long last;
  private String before;
  private String after;

  public static PagingParams from(HTupleInitExpr args, Function<HExpr, ZAny> resolver) {

    final PagingParamsBuilder b = PagingParams.builder();

    // see if we can limit the connection
    if (args.getType().contains("first")) {
      b.first((long) ((ZValue) resolver.apply(args.expr("first"))).getValue());
    }

    if (args.getType().contains("last")) {
      b.last((long) ((ZValue) resolver.apply(args.expr("last"))).getValue());
    }

    if (args.getType().contains("before")) {
      b.before((String) ((ZValue) resolver.apply(args.expr("before"))).getValue());
    }

    if (args.getType().contains("after")) {
      b.after((String) ((ZValue) resolver.apply(args.expr("after"))).getValue());
    }

    return b.build();

  }

  public static PagingParams emptyParams() {
    return EMPTY_PARAMS;
  }

  public boolean isEmpty() {
    return this.first == null && this.last == null && this.before == null && this.after == null;
  }

}
