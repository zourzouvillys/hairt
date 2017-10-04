package io.zrz.hai.runtime;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;

/**
 *
 *
 */

public interface PathToken extends Iterable<PathToken> {

  int depth();

  PathToken at(int pos);

  default FieldPathToken with(String fieldName) {
    if (this instanceof EmptyToken) {
      return rootToken(fieldName);
    }
    return new FieldPathToken(this, fieldName);
  }

  default IndexPathToken with(int index) {
    return new IndexPathToken(this, index);
  }

  @EqualsAndHashCode
  static abstract class AbstractPathToken implements PathToken {

    PathToken parent;

    AbstractPathToken(PathToken parent) {
      this.parent = parent;
    }

    @Override
    public PathToken previous() {
      return parent;
    }

    @Override
    public int depth() {
      return parent == null ? 0 : parent.depth() + 1;
    }

    @Override
    public PathToken at(int pos) {
      return parent.at(pos - 1);
    }

    @Override
    public String toString() {

      final StringBuilder sb = new StringBuilder();

      this.apply(new Visitor() {

        @Override
        public void visitIndex(IndexPathToken token) {
          if (token.parent != null) {
            token.parent.apply(this);
            sb.append(".");
          }
          sb.append(token.index);
        }

        @Override
        public void visitField(FieldPathToken token) {
          if (token.parent != null) {
            token.parent.apply(this);
            sb.append(".");
          }
          sb.append(token.fieldName);
        }

      });

      return sb.toString();

    }

  }

  /**
   * a path which is a field in an object.
   */

  @EqualsAndHashCode(callSuper = true)
  static class FieldPathToken extends AbstractPathToken {

    private final String fieldName;

    FieldPathToken(PathToken parent, String fieldName) {
      super(parent);
      this.fieldName = fieldName;
    }

    @Override
    public void apply(Visitor v) {
      v.visitField(this);
    }

    public String fieldName() {
      return this.fieldName;
    }

  }

  /**
   * a path which is an index in an array
   */

  @EqualsAndHashCode(callSuper = true)
  static class IndexPathToken extends AbstractPathToken {

    private final int index;

    IndexPathToken(PathToken parent, int index) {
      super(parent);
      this.index = index;
    }

    @Override
    public void apply(Visitor v) {
      v.visitIndex(this);
    }

    public int index() {
      return this.index;
    }

  }

  @Override
  default Iterator<PathToken> iterator() {

    final Iterator<PathToken> it = new Iterator<PathToken>() {

      PathToken next = PathToken.this;

      @Override
      public boolean hasNext() {
        return next != null;
      }

      @Override
      public PathToken next() {
        try {
          return next;
        } finally {
          next = next.previous();
        }
      }

    };

    return ImmutableList.copyOf(it).reverse().iterator();

  }

  public static FieldPathToken rootToken(String fieldName) {
    return new FieldPathToken(null, fieldName);
  }

  PathToken previous();

  public static interface Visitor {

    void visitField(FieldPathToken token);

    void visitIndex(IndexPathToken token);

  }

  void apply(Visitor v);

  /**
   * a path which is a field in an object.
   */

  @EqualsAndHashCode(callSuper = true)
  static class EmptyToken extends AbstractPathToken {

    EmptyToken() {
      super(null);
    }

    @Override
    public void apply(Visitor v) {
    }

  }

  public static PathToken emptyToken() {
    return new EmptyToken();
  }

}
