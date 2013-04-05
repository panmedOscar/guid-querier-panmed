package tw.edu.ym.guid.querier.db;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum QuerierResource {
  EXCELDB("exceldbMapperConfig.xml");

  static final Logger logger = LoggerFactory.getLogger(QuerierResource.class);
  private final String resource;

  private QuerierResource(String resource) {
    this.resource = resource;
  }

  public Reader getResource() {
    Reader reader = null;

    try {
      reader = Resources.getResourceAsReader(resource);
    } catch (IOException e) {
      e.printStackTrace();
      logger.error(e.getMessage());
    }

    return reader;
  }

}
