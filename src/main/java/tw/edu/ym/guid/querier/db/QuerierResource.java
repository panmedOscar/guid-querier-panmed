package tw.edu.ym.guid.querier.db;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * QuerierResource defines the DB resource of the ExcelManager.
 * 
 * @author Wei-Ming Wu
 * 
 */
public enum QuerierResource {

  EXCELDB("exceldbMapperConfig.xml");

  private static final Logger log = LoggerFactory
      .getLogger(QuerierResource.class);

  private final String resource;
  private final SqlSessionFactory sessionFactory;

  private QuerierResource(String resource) {
    this.resource = resource;
    Reader resourceReader = getResource();
    sessionFactory = new SqlSessionFactoryBuilder().build(resourceReader);
    try {
      resourceReader.close();
    } catch (IOException e) {
      java.util.logging.Logger.getLogger(this.getClass().getName()).log(
          Level.SEVERE, null, e);
    }
  }

  /**
   * Retuens the Mybatis mapper xml.
   * 
   * @return a Reader of Mybatis mapper xml
   */
  public Reader getResource() {
    Reader reader = null;

    try {
      reader = Resources.getResourceAsReader(resource);
    } catch (IOException e) {
      log.error(e.getMessage());
    }

    return reader;
  }

  public SqlSessionFactory getSessionFactory() {
    return sessionFactory;
  }

}
