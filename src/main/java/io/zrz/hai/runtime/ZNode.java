package io.zrz.hai.runtime;

import javax.annotation.Nullable;

import io.zrz.hai.type.HConnection;
import io.zrz.hai.type.HLink;
import io.zrz.hai.type.HTypeUtils;

public interface ZNode extends ZPropertyContainer, ZEntity, ZObject {

  /**
   * fetch an edge that is set for the given link, or null if it is not set.
   */

  @Nullable
  ZEdge getEdge(HLink link);

  /**
   * fetch a connection on this node.
   */

  ZConnection getConnection(HConnection connection);

  /**
   * populate a link by creating an edge between this node and another.
   *
   * if there is currently a link to a node, it will first be disconnected, and
   * then set to the new target. If it fails, then no changes will be visible.
   *
   */

  ZEdge createLinkTo(ZNode target, HLink link);

  /**
   *
   */

  /**
   * create an edge from this node to the other, using the specified link name.
   */

  default ZEdge createLinkTo(ZNode node, String linkName) {
    return createLinkTo(node, HTypeUtils
        .getMember(getType(), linkName)
        .map(HLink.class::cast)
        .orElseThrow(IllegalArgumentException::new));
  }

  default ZConnection getConnection(String fieldName) {
    return getConnection(HTypeUtils
        .getMember(getType(), fieldName)
        .map(HConnection.class::cast)
        .orElseThrow(IllegalArgumentException::new));
  }

  @Override
  default ZKind getZKind() {
    return ZKind.NODE;
  }
}
