package tw.edu.ym.guid.querier.mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import static com.google.common.collect.Lists.newArrayList;

public final class MybatisGenerator {

  public static void generate(InputStream configFile) {
    List<String> warnings = newArrayList();
    boolean overwrite = true;
    ConfigurationParser cp = new ConfigurationParser(warnings);
    Configuration config = null;
    try {
      config = cp.parseConfiguration(configFile);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (XMLParserException e) {
      e.printStackTrace();
    }
    DefaultShellCallback callback = new DefaultShellCallback(overwrite);
    MyBatisGenerator myBatisGenerator = null;
    try {
      myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
    } catch (InvalidConfigurationException e) {
      e.printStackTrace();
    }
    try {
      myBatisGenerator.generate(null);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
