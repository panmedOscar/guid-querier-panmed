package wmw.util.jdbc;

public enum DataType {
  BINARY, BOOLEAN, DATE, DATETIME, DECIMAL, FLOAT, INTEGER, TEXT, TIME,
  TIMESTAMP, VARCHAR;

  public String toString() {
    return this.name().toLowerCase();
  }
}
