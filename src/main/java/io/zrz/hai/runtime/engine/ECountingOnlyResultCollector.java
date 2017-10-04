package io.zrz.hai.runtime.engine;

import io.zrz.hai.runtime.ZValue;
import lombok.Getter;

public class ECountingOnlyResultCollector implements EHistoricResultCollector {

  @Getter
  int batches = 0;
  @Getter
  int objects = 0;
  @Getter
  int connections = 0;
  @Getter
  int edges = 0;
  @Getter
  int scalars = 0;

  @Override
  public void write(String name, ZValue val) {
    this.scalars++;
  }

  @Override
  public void write(String outputName, String value) {
    this.scalars++;
  }

  @Override
  public void write(String outputName, int count) {
    this.scalars++;
  }

  @Override
  public void writeNull(String name) {
    this.scalars++;
  }

  @Override
  public void writeStartArray(String outputName) {
    this.connections++;
  }

  @Override
  public void writeStartObject() {
    this.edges++;
  }

  @Override
  public void writeEnd() {
  }

  @Override
  public void writeStartObject(String outputName) {
    this.objects++;
  }

  @Override
  public void writeStartData() {
    this.batches++;
  }

  @Override
  public void writeEndData() {
  }

  @Override
  public void close() {
  }

  @Override
  public String toString() {
    return String.format("batches=%,d, objects=%,d, connections=%,d, edges=%,d, scalars=%,d",
        this.batches,
        this.objects,
        this.connections,
        this.edges,
        this.scalars);
  }

}
