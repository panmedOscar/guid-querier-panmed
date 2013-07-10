package tw.edu.ym.guid.querier.mybatis;

import java.sql.SQLException;

import net.sf.rubycollect4j.RubyFile;
import tw.edu.ym.guid.querier.ExcelManager;

public final class ExceldbGenerator {

  public static void main(String[] args) throws SQLException,
      ClassNotFoundException {
    RubyFile.delete(ExcelManager.DB + ".h2.db");
    new ExcelManager();
    MybatisGenerator.generate(ExceldbGenerator.class.getClassLoader()
        .getResourceAsStream("generatorConfig.xml"));
    RubyFile.delete(ExcelManager.DB + ".h2.db");
    RubyFile.delete("guid_querier.log");
  }

}
