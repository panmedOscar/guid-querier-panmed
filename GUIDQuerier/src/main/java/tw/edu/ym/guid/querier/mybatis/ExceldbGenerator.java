package tw.edu.ym.guid.querier.mybatis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import net.sf.rubycollect4j.RubyFile;
import tw.edu.ym.guid.querier.ExcelManager;
import tw.edu.ym.guid.querier.gui.QueryPanel;

import static net.sf.rubycollect4j.RubyCollections.ra;

public final class ExceldbGenerator {

  public static void main(String[] args) throws SQLException,
      ClassNotFoundException, FileNotFoundException, IOException {
    Properties props = new Properties();
    props.load(ExceldbGenerator.class.getClassLoader().getResourceAsStream(
        QueryPanel.PROPS));
    props.load(ExceldbGenerator.class.getClassLoader().getResourceAsStream(
        props.getProperty("db_props")));
    String dbFile =
        ra(props.getProperty("db.url").split(":")).last() + ".h2.db";
    RubyFile.delete(dbFile);
    new ExcelManager(QueryPanel.PROPS);
    MybatisGenerator.generate(ExceldbGenerator.class.getClassLoader()
        .getResourceAsStream("generatorConfig.xml"));
    RubyFile.delete(dbFile);
    RubyFile.delete("guid_querier.log");
  }

}
