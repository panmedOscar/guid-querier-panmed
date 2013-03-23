package tw.edu.ym.guid.querier.db;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;

public enum QuerierResource {
  EXCELDB("exceldbMapperConfig.xml");

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
    }

    return reader;
  }
}
