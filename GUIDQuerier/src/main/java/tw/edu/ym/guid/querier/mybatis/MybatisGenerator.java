package tw.edu.ym.guid.querier.mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import static com.google.common.collect.Lists.newArrayList;

/**
 * 
 * MybatisGenerator generates Mybatis models by given configuration xml file.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class MybatisGenerator {

  private static final boolean OVERWRITE = true;

  /**
   * Generates Mybatis models.
   * 
   * @param configFile
   *          an InputStream of configuration xml file
   */
  public static void generate(InputStream configFile) throws IOException,
      XMLParserException, InvalidConfigurationException, SQLException,
      InterruptedException {
    List<String> warnings = newArrayList();
    ConfigurationParser cp = new ConfigurationParser(warnings);
    MyBatisGenerator myBatisGenerator =
        new MyBatisGenerator(cp.parseConfiguration(configFile),
            new DefaultShellCallback(OVERWRITE), warnings);
    myBatisGenerator.generate(null);
    for (String warning : warnings)
      System.out.println(warning);
  }

}
