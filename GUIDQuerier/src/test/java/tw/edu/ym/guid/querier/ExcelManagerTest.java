package tw.edu.ym.guid.querier;

import java.io.File;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static tw.edu.ym.guid.querier.ExcelManager.newExcelManager;

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
    manager.importExcelsInFolder("src/test/resources");
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
    assertTrue(manager.authenticate("admin1",
        manager_props.getProperty("default_password_1")));
    assertTrue(manager.authenticate("admin2",
        manager_props.getProperty("default_password_2")));
    assertFalse(manager.authenticate("admin3", "haha"));
  }

  @Test
  public void testSetAdminPassword() {
    manager.setAdminPassword("admin3", "yaya");
    assertFalse(manager.authenticate("admin3", "yaya"));
    manager.setAdminPassword("admin1", "ohoh");
    assertFalse(manager.authenticate("admin1",
        manager_props.getProperty("default_password_1")));
    manager.setAdminPassword("admin1", "ohoh");
    assertTrue(manager.authenticate("admin1", "ohoh"));
  }

  @Test
  public void testImportExcelsInFolder() {
    manager.importExcelsInFolder("src/test/resources");
    assertEquals(5009, manager.total());
  }

  @Test
  public void testGetAll() {
    assertEquals(manager.total(), manager.getAll().size());
  }

  @Test
  public void testSelectAll() {
    assertEquals(manager.total(), manager.selectAll().size());
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
  public void testToString() {
    assertEquals("ExcelManager{Sheet=pii}", manager.toString());
  }

}
