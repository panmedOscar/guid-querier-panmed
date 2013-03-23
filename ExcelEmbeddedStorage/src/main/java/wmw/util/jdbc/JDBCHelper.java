package wmw.util.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class JDBCHelper {
  private JDBCHelper() {}

  public static List<String> getTables(Connection c) throws SQLException {
    List<String> tables = new ArrayList<String>();
    DatabaseMetaData meta = c.getMetaData();
    ResultSet rs = meta.getTables(null, null, null, new String[] { "TABLE" });
    while (rs.next())
      tables.add(rs.getString("TABLE_NAME"));
    return tables;
  }

  public static List<String> getColumns(Connection c, String table)
      throws SQLException {
    List<String> columns = new ArrayList<String>();
    Statement stmt = c.createStatement();
    String sql = "SELECT * FROM " + table + " LIMIT 1";
    ResultSet rs = stmt.executeQuery(sql);
    ResultSetMetaData md = rs.getMetaData();
    for (int i = 1; i <= md.getColumnCount(); i++)
      columns.add(md.getColumnName(i));
    stmt.close();
    return columns;
  }

  public static void createTable(Connection c, String name, Field... fields)
      throws SQLException {
    List<Field> list = Arrays.asList(fields);
    Statement stmt = c.createStatement();
    String createTable =
        "CREATE TABLE " + name.toLowerCase()
            + list.toString().replace("[", "(").replace("]", ")");
    stmt.executeUpdate(createTable);
    stmt.close();
  }

  public static void insertRecords(Connection c, String table,
      Map<String, String>... records) throws SQLException {
    for (Map<String, String> record : records) {
      String insertRecords =
          "INSERT INTO " + table + buildSQLColumns(record.keySet()) + " VALUES"
              + buildSQLInterpolations(record.keySet().size());
      PreparedStatement prepStmt = c.prepareStatement(insertRecords);
      int setIndex = 1;
      for (String column : record.keySet()) {
        prepStmt.setObject(setIndex, record.get(column));
        setIndex++;
      }
      prepStmt.executeUpdate();
      prepStmt.close();
    }
  }

  public static void insertRecords(Connection c, String table,
      List<Map<String, String>> records) throws SQLException {
    for (Map<String, String> record : records) {
      String insertRecords =
          "INSERT INTO " + table + buildSQLColumns(record.keySet()) + " VALUES"
              + buildSQLInterpolations(record.keySet().size());
      PreparedStatement prepStmt = c.prepareStatement(insertRecords);
      int setIndex = 1;
      for (String column : record.keySet()) {
        prepStmt.setObject(setIndex, record.get(column));
        setIndex++;
      }
      prepStmt.executeUpdate();
      prepStmt.close();
    }
  }

  public static void safeInsertRecords(Connection c, String table,
      List<Map<String, String>> records) throws SQLException {
    for (Map<String, String> record : records) {
      try {
        String insertRecords =
            "INSERT INTO " + table + buildSQLColumns(record.keySet())
                + " VALUES" + buildSQLInterpolations(record.keySet().size());
        PreparedStatement prepStmt = c.prepareStatement(insertRecords);
        int setIndex = 1;
        for (String column : record.keySet()) {
          prepStmt.setObject(setIndex, record.get(column));
          setIndex++;
        }
        prepStmt.executeUpdate();
        prepStmt.close();
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
  }

  public static void unique(Connection c, String table, String column)
      throws SQLException {
    String unique = "ALTER TABLE " + table + " ADD UNIQUE (" + column + ")";
    Statement stmt = c.createStatement();
    stmt.executeUpdate(unique);
    stmt.close();
  }

  private static String buildSQLInterpolations(int times) {
    List<String> interpolations = new ArrayList<String>();
    for (int i = 0; i < times; i++)
      interpolations.add("?");
    return interpolations.toString().replace("[", "(").replace("]", ")");
  }

  private static String buildSQLColumns(Iterable<String> i) {
    List<String> columns = new ArrayList<String>();
    for (String column : i)
      columns.add(column);
    return columns.toString().replace("[", "(").replace("]", ")");
  }

  public static void index(Connection c, String table, String column)
      throws SQLException {
    String index =
        "CREATE INDEX IDX_" + table + "_" + column + " on " + table + " ("
            + column + ")";
    Statement stmt = c.createStatement();
    stmt.executeUpdate(index);
    stmt.close();
  }

}
