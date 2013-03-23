package wmw.util.jdbc;

import static wmw.util.jdbc.DataType.BINARY;
import static wmw.util.jdbc.DataType.BOOLEAN;
import static wmw.util.jdbc.DataType.DATE;
import static wmw.util.jdbc.DataType.DATETIME;
import static wmw.util.jdbc.DataType.DECIMAL;
import static wmw.util.jdbc.DataType.FLOAT;
import static wmw.util.jdbc.DataType.INTEGER;
import static wmw.util.jdbc.DataType.TEXT;
import static wmw.util.jdbc.DataType.TIME;
import static wmw.util.jdbc.DataType.TIMESTAMP;
import static wmw.util.jdbc.DataType.VARCHAR;

public final class Field {
  private final String name;
  private final DataType dataType;

  private Field(String name, DataType dataType) {
    this.name = name;
    this.dataType = dataType;
  }

  public static Field Binary(String name) {
    validateString(name);
    return new Field(name, BINARY);
  }

  public static Field Boolean(String name) {
    validateString(name);
    return new Field(name, BOOLEAN);
  }

  public static Field Date(String name) {
    validateString(name);
    return new Field(name, DATE);
  }

  public static Field Datetime(String name) {
    validateString(name);
    return new Field(name, DATETIME);
  }

  public static Field Decimal(String name) {
    validateString(name);
    return new Field(name, DECIMAL);
  }

  public static Field Float(String name) {
    validateString(name);
    return new Field(name, FLOAT);
  }

  public static Field Integer(String name) {
    validateString(name);
    return new Field(name, INTEGER);
  }

  public static Field Text(String name) {
    validateString(name);
    return new Field(name, TEXT);
  }

  public static Field Time(String name) {
    validateString(name);
    return new Field(name, TIME);
  }

  public static Field Timestamp(String name) {
    validateString(name);
    return new Field(name, TIMESTAMP);
  }

  public static Field Varchar(String name) {
    validateString(name);
    return new Field(name, VARCHAR);
  }

  private static void validateString(String str) {
    if (str == null || "".equals(str.trim()))
      throw new IllegalArgumentException("Input can't be null or empty.");
  }

  public String getName() {
    return name;
  }

  public DataType getDataType() {
    return dataType;
  }

  public String toString() {
    if (dataType == VARCHAR)
      return name + " " + dataType + "(255)";
    else
      return name + " " + dataType;
  }

}
