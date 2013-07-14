package tw.edu.ym.guid.querier.mybatis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import tw.edu.ym.guid.querier.gui.QueryPanel;

import static tw.edu.ym.guid.querier.ExcelManager.newExcelManager;

public final class ExceldbGenerator {

  public static void main(String[] args) throws SQLException,
      ClassNotFoundException, FileNotFoundException, IOException {
    Properties props = new Properties();
    props.load(ExceldbGenerator.class.getClassLoader().getResourceAsStream(
        QueryPanel.PROPS_PATH));
    props.load(ExceldbGenerator.class.getClassLoader().getResourceAsStream(
        props.getProperty("db_props")));

    String[] dbURL = props.getProperty("db.url").split(":");
    String dbFile = dbURL[dbURL.length - 1] + ".h2.db";

    new File(dbFile).delete();
    newExcelManager(QueryPanel.PROPS_PATH);
    MybatisGenerator.generate(ExceldbGenerator.class.getClassLoader()
        .getResourceAsStream("generatorConfig.xml"));
    new File(dbFile).delete();
    new File("guid_querier.log").delete();
  }

}
