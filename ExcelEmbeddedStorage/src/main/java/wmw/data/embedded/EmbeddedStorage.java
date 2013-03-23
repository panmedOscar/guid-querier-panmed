package wmw.data.embedded;

import static wmw.util.jdbc.Field.Varchar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import wmw.util.jdbc.Field;
import wmw.util.jdbc.JDBCHelper;

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

  public static void main(String[] args) throws SQLException,
      ClassNotFoundException {
    EmbeddedStorage es = new EmbeddedStorage("exceldb");
    if (!(es.hasTable("pii")))
      es.createTable("pii", Varchar("Local_ID"), Varchar("GUID"),
          Varchar("MRN"), Varchar("身份證字號"), Varchar("姓氏"), Varchar("名字"),
          Varchar("出生月"), Varchar("出生日"), Varchar("出生年"), Varchar("聯絡電話"),
          Varchar("性別"), Varchar("收案醫師"), Varchar("醫院名稱"));
    es.unique("pii", "GUID");
    System.out.println(es.getTables());
  }
}
