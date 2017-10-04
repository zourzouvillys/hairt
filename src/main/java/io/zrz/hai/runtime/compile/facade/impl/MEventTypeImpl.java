package io.zrz.hai.runtime.compile.facade.impl;

import java.util.stream.Stream;

import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MOutputKind;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MViewKind;
import io.zrz.hai.type.HConnection;
import io.zrz.hai.type.HEventType;
import io.zrz.hai.type.HLink;
import io.zrz.hai.type.HMethod;
import io.zrz.hai.type.HState;
import io.zrz.hai.type.HTypeUtils;

/**
 * an event type.
 */

public class MEventTypeImpl extends MDeclTypeImpl {

  public MEventTypeImpl(MViewContextImpl ctx, HEventType type) {
    super(ctx, type);
  }

  @Override
  public Stream<? extends MField> fields() {
    return HTypeUtils.getMembers(this.type)
        .filter(m -> m.getModifiers().isExport())
        .map(member -> {
          switch (member.getMemberKind()) {
            case METHOD:
              return new MMethodImpl(this.ctx, this, (HMethod) member);
            case STATE:
              return new MStateFieldImpl(this.ctx, this, (HState) member);
            case AMBIENT:
            case LINK:
              return new MLinkFieldImpl(this.ctx, this, (HLink) member);
            case CONNECTION:
              return new MConnectionFieldImpl(this.ctx, this, (HConnection) member);
            case PERMISSION:
            case SELECTION:
              break;
          }
          throw new IllegalArgumentException(member.getMemberKind().toString());
        })
        .filter(m -> this.ctx.isVisible(m));
  }

  @Override
  public MOutputType withViewKind(MViewKind viewKind) {
    if (viewKind != this.ctx.viewKind) {
      return this.ctx.withViewKind(viewKind).output(this.type);
    }
    return this;
  }

  @Override
  public MOutputKind outputKind() {
    return MOutputKind.EVENT;
  }

  @Override
  public MViewContextImpl getView() {
    return this.ctx;
  }

}
