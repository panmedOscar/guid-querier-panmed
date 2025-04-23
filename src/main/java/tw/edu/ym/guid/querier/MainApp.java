package tw.edu.ym.guid.querier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import app.models.Authentication;
import app.models.Folder;
import app.models.History;
import app.models.Pii;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sf.rubycollect4j.RubyDir;
import org.apache.commons.io.FileUtils;
import org.h2.tools.Backup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.EbeanServerFactory;

public class MainApp extends Application {

  private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

  // 取得工作區目錄，並支援動態替換系統屬性
  private static final String WORKSPACE_DIR = System.getProperty("user.home")
      + File.separator + ConfigLoader.get("workspace.dir");
  private static final String DB_NAME = ConfigLoader.get("db.name");
  private static final String DEFAULT_DB_FILE = ConfigLoader.get("default.db.file");
  private static final String BACKUP_DIR_NAME = ConfigLoader.get("backup.dir.name");
  private static final String DB_USER = ConfigLoader.get("db.user");
  private static final String DB_PASSWORD = ConfigLoader.get("db.password");
  // 建構 H2 URL：利用 workspace 目錄、DB 名稱以及加密參數
  private static final String H2_URL = "jdbc:h2:file:" + WORKSPACE_DIR + File.separator + DB_NAME
      + ";CIPHER=" + ConfigLoader.get("cipher");

  @Override
  public void start(final Stage stage) throws Exception {
    // 建立工作目錄（若不存在則建立）
    RubyDir.mkdir(WORKSPACE_DIR);

    // 複製預設資料庫（第一次啟動時）
    copyDefaultDb();

    // 設定並啟動 Ebean
    configEbean();

    // 載入 FXML 介面與資源
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"),
        ResourceBundle.getBundle("GuidQuerier", new Locale("zh-tw")));
    Parent root = fxmlLoader.load();
    Scene scene = new Scene(root);
    scene.getStylesheets().add(
        Objects.requireNonNull(getClass().getResource("/styles/Styles.css")).toExternalForm());
    stage.setTitle("Guid Querier 3.0.9");
    stage.setScene(scene);
    stage.show();

    logger.info("Application started at: " + LocalDateTime.now());
  }

  @Override
  public void stop() {
    // 先關閉資料庫連線，確保所有鎖定被釋放
    shutdownDatabase();
    // 延遲確保 H2 完全釋放鎖定
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    // 執行備份作業
    backupDatabase();

    logger.info("Application closed at: " + LocalDateTime.now());
  }

  /**
   * 複製預設資料庫檔案到工作目錄，僅在資料庫不存在時進行。
   */
  private void copyDefaultDb() throws IOException, URISyntaxException {
    File destDb = new File(WORKSPACE_DIR + File.separator + DEFAULT_DB_FILE);
    if (!destDb.exists()) {
      try (InputStream defaultDb = ExcelManager.class.getClassLoader().getResourceAsStream(DEFAULT_DB_FILE)) {
        if (defaultDb == null) {
          throw new IOException("預設資料庫檔案不存在於資源中：" + DEFAULT_DB_FILE);
        }
        FileUtils.copyInputStreamToFile(defaultDb, destDb);
      }
    }
  }

  /**
   * 設定 Ebean 與 H2 資料庫連線（啟用加密）。
   */
  private void configEbean() {
    ServerConfig config = new ServerConfig();
    config.setName("h2");

    logger.info("START config Ebean");

    DataSourceConfig h2Db = new DataSourceConfig();
    h2Db.setDriver("org.h2.Driver");
    h2Db.setUsername(DB_USER);
    h2Db.setPassword(DB_PASSWORD);
    h2Db.setUrl(H2_URL);
    h2Db.setHeartbeatSql("select 1");

    config.setDataSourceConfig(h2Db);
    config.setDefaultServer(true);
    config.setRegister(true);

    // 請依需求加入相關的實體類別
    config.addClass(Authentication.class);
    config.addClass(Folder.class);
    config.addClass(History.class);
    config.addClass(Pii.class);

    EbeanServerFactory.create(config);
    logger.info("END config Ebean");
  }

  /**
   * 執行 H2 資料庫的 SHUTDOWN 指令來關閉資料庫，釋放檔案鎖定。
   */
  private void shutdownDatabase() {
    try (Connection conn = DriverManager.getConnection(H2_URL, DB_USER, DB_PASSWORD);
        Statement stmt = conn.createStatement()) {
      stmt.execute("SHUTDOWN");
      logger.info("資料庫已關閉");
    } catch (SQLException e) {
      logger.info("關閉資料庫失敗：" + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * 使用 H2 備份工具進行備份。
   * 備份會產生一個 ZIP 檔案，檔名中包含當天日期。
   */
  private void backupDatabase() {
    // 備份目錄設定
    String backupDirPath = WORKSPACE_DIR + File.separator + BACKUP_DIR_NAME;
    File backupDir = new File(backupDirPath);
    if (!backupDir.exists() && !backupDir.mkdirs()) {
      logger.info("無法建立備份目錄: " + backupDir.getAbsolutePath());
      return;
    }

    // 產生日期戳記，例如 yyyyMMdd
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    String timestamp = LocalDateTime.now().format(formatter);
    String backupFileName = DB_NAME + "_" + timestamp + ".zip";
    File backupFile = new File(backupDir, backupFileName);

    try {
      // 使用 H2 備份工具，第三個參數為檔案前綴（僅備份此前綴的檔案）
      Backup.execute(backupFile.getAbsolutePath(), WORKSPACE_DIR, DB_NAME, true);
      logger.info("備份完成：" + backupFile.getAbsolutePath());
    } catch (SQLException e) {
      logger.info("備份失敗：" + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
