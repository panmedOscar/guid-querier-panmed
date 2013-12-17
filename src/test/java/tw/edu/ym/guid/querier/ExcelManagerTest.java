package tw.edu.ym.guid.querier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static tw.edu.ym.guid.querier.ExcelManager.newExcelManager;
import static wmw.util.FolderTraverser.retrieveAllFiles;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExcelManagerTest {

  private static ExcelManager manager;
  private static Properties manager_props;

  @BeforeClass
  public static void setUp() throws Exception {
    manager_props = new Properties();
    manager_props.load(ExcelManagerTest.class.getClassLoader()
        .getResourceAsStream("excel_manager.properties"));
    manager = newExcelManager("excel_manager.properties");
    manager.importExcels("src/test/resources/example");
  }

  @AfterClass
  public static void tearDown() throws Exception {
    new File("h2-create.sql").delete();
    new File("h2-drop.sql").delete();
    new File("guid_querier.log").delete();
  }

  @Test
  public void testConstructor() {
    assertTrue(manager instanceof ExcelManager);
  }

  @Test
  public void testGetHeader() {
    List<String> header = manager.getHeader();
    for (int i = 0; i < header.size(); i++) {
      assertEquals(ExcelField.values()[i].toString(), header.get(i));
    }
  }

  @Test
  public void testAuthenticate() {
    assertTrue(manager.authenticate("admin",
        manager_props.getProperty("default_password_1")));
    assertTrue(manager.authenticate("admin",
        manager_props.getProperty("default_password_2")));
    assertFalse(manager.authenticate("admin", "haha"));
  }

  @Test
  public void testSetAdminPassword() {
    manager.setPassword("admin",
        manager_props.getProperty("default_password_1"), "yaya");
    assertTrue(manager.authenticate("admin", "yaya"));
    manager.setPassword("admin",
        manager_props.getProperty("default_password_2"), "ohoh");
    assertFalse(manager.authenticate("admin",
        manager_props.getProperty("default_password_1")));
    assertTrue(manager.authenticate("admin", "ohoh"));
  }

  @Test
  public void testImportExcelsInFolder() {
    assertEquals(5009, manager.getNumberOfRecords());
  }

  @Test
  public void testGetAll() {
    assertEquals(manager.getNumberOfRecords(), manager.findAll().size());
  }

  @Test
  public void testSelectAllWithLimit() {
    assertEquals(100, manager.findAll(100).size());
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
    for (File file : oldFiles) {
      file.delete();
    }
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
