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
package wmw.db.embedded;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import wmw.db.jdbc.Field;
import wmw.db.jdbc.JDBCHelper;

public final class EmbeddedStorage {

  private final Connection c;

  public static EmbeddedStorage newEmbeddedStorage(String propertiesPath)
      throws IOException, SQLException, ClassNotFoundException {
    Properties props = new Properties();
    InputStream in =
        EmbeddedStorage.class.getClassLoader().getResourceAsStream(
            propertiesPath);
    props.load(in);
    in.close();
    return new EmbeddedStorage(props);
  }

  public EmbeddedStorage(Properties props) throws SQLException,
      ClassNotFoundException {
    Class.forName(props.getProperty("db.driver"));
    c =
        DriverManager.getConnection(props.getProperty("db.url"),
            props.getProperty("db.username"), props.getProperty("db.password"));
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

  public void insertRecords(String table,
      Collection<Map<String, String>> records) throws SQLException {
    JDBCHelper.insertRecords(c, table, records);
  }

  public void safeInsertRecords(String table,
      Collection<Map<String, String>> records) throws SQLException {
    JDBCHelper.safeInsertRecords(c, table, records);
  }

  public void unique(String table, List<String> columns) throws SQLException {
    JDBCHelper.unique(c, table, columns);
  }

  public void unique(String table, String... columns) throws SQLException {
    JDBCHelper.unique(c, table, columns);
  }

  public void index(String table, String column) throws SQLException {
    JDBCHelper.index(c, table, column);
  }

  public List<Object[]> selectAll(String table) throws SQLException {
    return JDBCHelper.selectAll(c, table);
  }

  public List<Object[]> selectAll(String table, int limit) throws SQLException {
    return JDBCHelper.selectAll(c, table, limit);
  }

}
