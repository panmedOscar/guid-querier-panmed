/**
 * 
 * @author Wei-Ming Wu
 * 
 * 
 *         Copyright 2013 Wei-Ming Wu
 * 
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License.
 * 
 */
package wmw.mybatis;

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

/**
 * 
 * MybatisGenerator generates Mybatis models by given configuration xml file.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class MyBatisGenerator {

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
    org.mybatis.generator.api.MyBatisGenerator myBatisGenerator =
        new org.mybatis.generator.api.MyBatisGenerator(
            cp.parseConfiguration(configFile), new DefaultShellCallback(
                OVERWRITE), warnings);
    myBatisGenerator.generate(null);
    for (String warning : warnings)
      System.out.println(warning);
  }

}
