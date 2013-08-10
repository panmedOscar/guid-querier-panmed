package tw.edu.ym.guid.querier.mybatis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;

import tw.edu.ym.guid.querier.QueryPanel;

import static tw.edu.ym.guid.querier.ExcelManager.newExcelManager;

/**
 * 
 * ExceldbGenerator is needed to be executed each time after ExcelField changed
 * or any schema modification.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class ExceldbGenerator {

  public static void main(String[] args) throws SQLException,
      ClassNotFoundException, FileNotFoundException, IOException,
      XMLParserException, InvalidConfigurationException, InterruptedException {
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
    FileUtils.copyDirectory(new File("src/main/resources/exceldb"), new File(
        "src/test/resources/exceldb"));
    new File(dbFile).delete();
    new File("guid_querier.log").delete();
  }

}
