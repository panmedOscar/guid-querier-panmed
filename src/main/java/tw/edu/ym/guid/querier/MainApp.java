package tw.edu.ym.guid.querier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import net.sf.rubycollect4j.RubyDir;
import org.apache.commons.io.FileUtils;

import app.models.Authentication;
import app.models.Folder;
import app.models.History;
import app.models.Pii;

import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MainApp.class);

  /**
   * The main() method is ignored in correctly deployed JavaFX application.
   * main() serves only as fallback in case the application can not be launched
   * through deployment artifacts, e.g., in IDEs with limited FX support.
   * NetBeans ignores main().
   *
   * @param args
   *     the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }

  /*private final Injector injector =
      Guice.createInjector(new CountdownTerminatorModule(AUTO_SHUTDOWN_TIME));*/

  @Override
  public void start(final Stage stage) throws Exception {

    RubyDir.mkdir(System.getProperty(
        "user.home") + File.separator + "guid_querier_workspace");

//    copyDefaultDb();
    configEbean();

    FXMLLoader fxmlLoader =
        new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"),
            ResourceBundle.getBundle("GuidQuerier", new Locale("zh-tw")));
    Parent root = fxmlLoader.load();

    Scene scene = new Scene(root);
    scene.getStylesheets().add(
        Objects.requireNonNull(getClass().getResource("/styles/Styles.css"))
            .toExternalForm());

    stage.setTitle("Guid Querier 3.0.6");
    stage.setScene(scene);
    stage.show();

    logger.info("Application started at: " + LocalDateTime.now());

  }

  @Override
  public void stop() {
    logger.info("Application closed at: " + LocalDateTime.now());
  }

  private void copyDefaultDb() throws IOException, URISyntaxException {

    File excelDb = new File(System.getProperty(
        "user.home") + File.separator + "guid_querier_workspace" + File.separator + "querierdb.h2.db");

    if (!excelDb.exists()) {
      InputStream defaultDb = ExcelManager.class.getClassLoader()
          .getResourceAsStream("exceldb.h2.db");

      URL resourceUrl = ExcelManager.class.getClassLoader().getResource("exceldb.h2.db");
      String path = Paths.get(resourceUrl.toURI()).toString();
      logger.info("Actual path: " + path);

      FileUtils.copyInputStreamToFile(defaultDb, excelDb);
      defaultDb.close();
    }
  }

  private void configEbean() {
    ServerConfig config = new ServerConfig();
    config.setName("h2");

    DataSourceConfig h2Db = new DataSourceConfig();
    h2Db.setDriver("org.h2.Driver");
    h2Db.setUsername("sa_pro");
    h2Db.setPassword("bD@F7$6iv*2#%)EgIH?SD976~5o4h^g55`54$o}gZ,NOsqdwS{");

    String userHome = System.getProperty("user.home");
    String dbPath = userHome + File.separator + "guid_querier_workspace" + File.separator + "querierdb";
    h2Db.setUrl("jdbc:h2:file:" + dbPath);
    h2Db.setHeartbeatSql("select 1");

    config.setDataSourceConfig(h2Db);

    File dbFile = new File(System.getProperty(
        "user.home") + File.separator + "guid_querier_workspace" + File.separator + "querierdb.h2.db");
    if (!dbFile.exists()) {
      config.setDdlGenerate(true);
      config.setDdlRun(true);
    } else {
      config.setDdlGenerate(false);
      config.setDdlRun(false);
    }

    config.setDefaultServer(true);
    config.setRegister(true);

    config.addClass(Authentication.class);
    config.addClass(Folder.class);
    config.addClass(History.class);
    config.addClass(Pii.class);

    EbeanServerFactory.create(config);
  }


}
