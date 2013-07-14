package tw.edu.ym.guid.querier.db;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum QuerierResource {
  EXCELDB("exceldbMapperConfig.xml");

  private static final Logger log = LoggerFactory
      .getLogger(QuerierResource.class);
  private final String resource;

  private QuerierResource(String resource) {
    this.resource = resource;
  }

  public Reader getResource() {
    Reader reader = null;

    try {
      reader = Resources.getResourceAsReader(resource);
    } catch (IOException e) {
      log.error(e.getMessage());
    }

    return reader;
  }
}
