package wmw.data.embedded;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import wmw.util.jdbc.Field;
import wmw.util.jdbc.JDBCHelper;

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
public final class EmbeddedStorage {
  private final Connection c;

  public EmbeddedStorage(String db) throws SQLException, ClassNotFoundException {
    Class.forName("org.h2.Driver");
    c = DriverManager.getConnection("jdbc:h2:" + db, "sa", "bD@F7$6iv*2#%)E}g");
  }

  public List<String> getTables() throws SQLException {
    return JDBCHelper.getTables(c);
  }

  public boolean hasTable(String table) throws SQLException {
    return getTables().contains(table.toUpperCase());
  }

  public List<String> getColumns(String table) throws SQLException {
    return JDBCHelper.getColumns(c, table);
  }

  public void createTable(String table, List<Field> fields) throws SQLException {
    JDBCHelper.createTable(c, table, fields);
  }

  public void createTable(String table, Field... fields) throws SQLException {
    JDBCHelper.createTable(c, table, fields);
  }

  public void insertRecords(String table, Map<String, String>... records)
      throws SQLException {
    JDBCHelper.insertRecords(c, table, records);
  }

  public void insertRecords(String table, List<Map<String, String>> records)
      throws SQLException {
    JDBCHelper.insertRecords(c, table, records);
  }

  public void
      safeInsertRecords(String table, List<Map<String, String>> records)
          throws SQLException {
    JDBCHelper.safeInsertRecords(c, table, records);
  }

  public void unique(String table, String column) throws SQLException {
    JDBCHelper.unique(c, table, column);
  }

  public void index(String table, String column) throws SQLException {
    JDBCHelper.index(c, table, column);
  }

  public List<Object[]> selectAll() throws SQLException {
    return JDBCHelper.selectAll(c, "pii");
  }

  public List<Object[]> selectAll(int limit) throws SQLException {
    return JDBCHelper.selectAll(c, "pii", limit);
  }

}
