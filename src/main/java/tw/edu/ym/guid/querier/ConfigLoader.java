package tw.edu.ym.guid.querier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
  private static final Properties properties = new Properties();

  static {
    try (InputStream in = ConfigLoader.class.getResourceAsStream("/config.properties")) {
      if (in == null) {
        throw new RuntimeException("找不到 config.properties 設定檔！");
      }
      properties.load(in);
    } catch (IOException e) {
      throw new RuntimeException("讀取 config.properties 失敗", e);
    }
  }

  public static String get(String key) {
    return properties.getProperty(key);
  }
}
