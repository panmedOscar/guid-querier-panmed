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

import com.google.common.base.Strings;

/**
 * 
 * @author Wei-Ming Wu
 * 
 * 
 *         Copyright 2013 Wei-Ming Wu
 * 
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License.
 * 
 */
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
    if (Strings.isNullOrEmpty(str))
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
