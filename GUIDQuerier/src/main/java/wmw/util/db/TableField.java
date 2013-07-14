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
package wmw.util.db;

import com.google.common.base.Strings;

import static wmw.util.db.DataType.BINARY;
import static wmw.util.db.DataType.BOOLEAN;
import static wmw.util.db.DataType.DATE;
import static wmw.util.db.DataType.DATETIME;
import static wmw.util.db.DataType.DECIMAL;
import static wmw.util.db.DataType.FLOAT;
import static wmw.util.db.DataType.INTEGER;
import static wmw.util.db.DataType.TEXT;
import static wmw.util.db.DataType.TIME;
import static wmw.util.db.DataType.TIMESTAMP;
import static wmw.util.db.DataType.VARCHAR;

public final class TableField {

  private final String name;
  private final DataType dataType;

  private TableField(String name, DataType dataType) {
    this.name = name;
    this.dataType = dataType;
  }

  public static TableField Binary(String name) {
    validateString(name);
    return new TableField(name, BINARY);
  }

  public static TableField Boolean(String name) {
    validateString(name);
    return new TableField(name, BOOLEAN);
  }

  public static TableField Date(String name) {
    validateString(name);
    return new TableField(name, DATE);
  }

  public static TableField Datetime(String name) {
    validateString(name);
    return new TableField(name, DATETIME);
  }

  public static TableField Decimal(String name) {
    validateString(name);
    return new TableField(name, DECIMAL);
  }

  public static TableField Float(String name) {
    validateString(name);
    return new TableField(name, FLOAT);
  }

  public static TableField Integer(String name) {
    validateString(name);
    return new TableField(name, INTEGER);
  }

  public static TableField Text(String name) {
    validateString(name);
    return new TableField(name, TEXT);
  }

  public static TableField Time(String name) {
    validateString(name);
    return new TableField(name, TIME);
  }

  public static TableField Timestamp(String name) {
    validateString(name);
    return new TableField(name, TIMESTAMP);
  }

  public static TableField Varchar(String name) {
    validateString(name);
    return new TableField(name, VARCHAR);
  }

  private static void validateString(String str) {
    if (Strings.isNullOrEmpty(str) || str.trim().isEmpty())
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
