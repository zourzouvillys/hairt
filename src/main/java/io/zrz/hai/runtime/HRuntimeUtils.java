package io.zrz.hai.runtime;

import java.util.Optional;

import io.zrz.hai.expr.HExpr;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.type.HMember;
import io.zrz.hai.type.HState;
import io.zrz.hai.type.HTypeUtils;
import io.zrz.hai.type.HViewType;

public class HRuntimeUtils {

  public static String typeName(HViewType type, MViewKind kind) {
    switch (kind) {
      case QUERY:
        return getConstFieldValue(type, "queryRootName").orElse(type.getQualifiedName() + "Query");
      case MUTATION:
        return getConstFieldValue(type, "mutationRootName").orElse(type.getQualifiedName() + "Mutation");
      case SUBSCRIPTION:
        return getConstFieldValue(type, "subscriptionRootName").orElse(type.getQualifiedName() + "Subscription");
    }
    throw new IllegalArgumentException(kind.toString());

  }

  public static Optional<String> getConstFieldValue(HViewType type, String fieldName) {

    final HMember q = HTypeUtils.getMember(type, fieldName).orElse(null);

    if (q == null) {
      return Optional.empty();
    }

    if (q instanceof HState) {

      final HExpr value = ((HState) q).getDefaultValue();

      if (value != null) {
        return Optional.ofNullable(value.accept(new ConstantExprEvaluator()));
      }

    }

    return Optional.empty();

  }

}
