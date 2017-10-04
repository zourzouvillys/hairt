package io.zrz.hai.runtime.compile.parse;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.graphql.core.doc.GQLSelection;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.hai.runtime.compile.facade.MField;
import io.zrz.hai.runtime.compile.facade.MOutputType;
import io.zrz.hai.runtime.compile.facade.MView;
import lombok.Builder;
import lombok.Value;

public class GDiagnostics {

  private final List<Message> messages = new LinkedList<>();

  public GDiagnostics(GQLDocument doc, MView view) {
    // TODO Auto-generated constructor stub
  }

  @Value
  @Builder
  private static class Message implements GDiagnosticMessage {
    private GDiagnosticsKind kind;
    private GBodyImpl exec;
    private Object value;
    private String message;
    private Object[] args;
  }

  public void error(GDiagnosticsKind kind, GBodyImpl exec, GQLValue value, String string, Object... args) {
    this.messages.add(new Message(kind, exec, value, string, args));
  }

  public void warn(GDiagnosticsKind kind, GBodyImpl exec, GQLValue value, String string, Object... args) {
    this.messages.add(new Message(kind, exec, value, string, args));
  }

  public void warn(GDiagnosticsKind kind, GBodyImpl exec, GQLSelection value, String string, Object... args) {
    this.messages.add(new Message(kind, exec, value, string, args));
  }

  public void error(GDiagnosticsKind kind, GBodyImpl exec, GQLSelection value, MField field, String string, Object... args) {
    this.messages.add(new Message(kind, exec, value, string, args));
  }

  public void warn(GDiagnosticsKind kind, GBodyImpl exec, GQLSelection value, MField field, String string, Object... args) {
    this.messages.add(new Message(kind, exec, value, string, args));
  }

  public void warn(GDiagnosticsKind kind, GBodyImpl exec, GQLFieldSelection selection, MOutputType type, String string, Object... args) {
    this.messages.add(new Message(kind, exec, selection, string, args));
  }

  public void info(GDiagnosticsKind kind, GBodyImpl exec, GQLValue value, String string, Object... args) {
    this.messages.add(new Message(kind, exec, value, string, args));
  }

  public void forEach(Consumer<GDiagnosticMessage> consumer) {
    this.messages.forEach(consumer);
  }

}
