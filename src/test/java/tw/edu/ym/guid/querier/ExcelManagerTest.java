package tw.edu.ym.guid.querier;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static tw.edu.ym.guid.querier.ExcelManager.newExcelManager;
import static tw.edu.ym.guid.querier.api.Authentications.RoleType.ADMIN;
import static wmw.util.FolderTraverser.retrieveAllFiles;

public class ExcelManagerTest {

  private static ExcelManager manager;
  private static Properties manager_props;
  private static Properties db_props;

  @BeforeClass
  public static void setUp() throws Exception {
    manager_props = new Properties();
    manager_props.load(ExcelManagerTest.class.getClassLoader()
        .getResourceAsStream("test_excel_manager.properties"));
    db_props = new Properties();
    db_props.load(ExcelManagerTest.class.getClassLoader().getResourceAsStream(
        "test_database.properties"));
    manager = newExcelManager("test_excel_manager.properties");
    manager.importExcels("src/test/resources/example");
  }

  @AfterClass
  public static void tearDown() throws Exception {
    new File("testdb.h2.db").delete();
    new File("testdb.trace.db").delete();
    new File("guid_querier.log").delete();
  }

  @Test
  public void testConstructor() {
    assertTrue(manager instanceof ExcelManager);
  }

  @Test
  public void testGetHeader() {
    String[] header = manager.getHeader();
    for (int i = 0; i < header.length; i++)
      assertEquals(ExcelField.values()[i].toString(), header[i]);
  }

  @Test
  public void testAuthenticate() {
    assertTrue(manager.authenticate(ADMIN.toString(),
        manager_props.getProperty("default_password_1")));
    assertTrue(manager.authenticate(ADMIN.toString(),
        manager_props.getProperty("default_password_2")));
    assertFalse(manager.authenticate(ADMIN.toString(), "haha"));
  }

  @Test
  public void testSetAdminPassword() {
    manager.setPassword(ADMIN.toString(),
        manager_props.getProperty("default_password_1"), "yaya");
    assertTrue(manager.authenticate(ADMIN.toString(), "yaya"));
    manager.setPassword(ADMIN.toString(),
        manager_props.getProperty("default_password_2"), "ohoh");
    assertFalse(manager.authenticate(ADMIN.toString(),
        manager_props.getProperty("default_password_1")));
    assertTrue(manager.authenticate(ADMIN.toString(), "ohoh"));
  }

  @Test
  public void testImportExcelsInFolder() {
    assertEquals(5009, manager.totalRecord());
  }

  @Test
  public void testGetAll() {
    assertEquals(manager.totalRecord(), manager.getAll().size());
  }

  @Test
  public void testSelectAll() {
    assertEquals(manager.totalRecord(), manager.selectAll().size());
  }

  @Test
  public void testSelectAllWithLimit() {
    assertEquals(100, manager.selectAll(100).size());
  }

  @Test
  public void testQuery() {
    assertEquals(1, manager.query("12345").size());
    assertEquals(568, manager.query("12").size());
    assertEquals(703, manager.query("12", "23").size());
  }

  @Test
  public void testBackup() {
    List<File> oldFiles = retrieveAllFiles("src/test/resources/backup", "zip");
    for (File file : oldFiles)
      file.delete();
    manager.setBackupFolder("src/test/resources/backup");
    List<File> backupFiles =
        retrieveAllFiles("src/test/resources/backup", "zip");
    assertEquals(1, backupFiles.size());
    assertEquals("PII_20130328.zip", backupFiles.get(0).getName());
    backupFiles.get(0).delete();
  }

  @Test
  public void testToString() {
    assertEquals("ExcelManager{Sheet=pii}", manager.toString());
  }

}
