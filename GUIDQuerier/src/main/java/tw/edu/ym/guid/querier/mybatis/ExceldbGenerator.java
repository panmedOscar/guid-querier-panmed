package tw.edu.ym.guid.querier.mybatis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import net.sf.rubycollect4j.RubyFile;
import tw.edu.ym.guid.querier.ExcelManager;

import static net.sf.rubycollect4j.RubyCollections.ra;

public final class ExceldbGenerator {

  public static void main(String[] args) throws SQLException,
      ClassNotFoundException, FileNotFoundException, IOException {
    Properties props = new Properties();
    props.load(ExceldbGenerator.class.getClassLoader().getResourceAsStream(
        "mybatis.properties"));
    String dbFile =
        ra(props.getProperty("db.url").split(":")).last() + ".h2.db";
    RubyFile.delete(dbFile);
    new ExcelManager();
    MybatisGenerator.generate(ExceldbGenerator.class.getClassLoader()
        .getResourceAsStream("generatorConfig.xml"));
    RubyFile.delete(dbFile);
    RubyFile.delete("guid_querier.log");
  }

}
