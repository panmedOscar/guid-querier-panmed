package tw.edu.ym.guid.querier;

import static tw.edu.ym.guid.querier.FXMLController.AUTO_SHUTDOWN_TIME;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.apache.commons.io.FileUtils;

import wmw.aop.terminator.CountdownTerminatorModule;
import wmw.javafx.GuiceFXMLLoader;
import app.models.Authentication;
import app.models.Folder;
import app.models.History;
import app.models.Pii;

import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class MainApp extends Application {

  /**
   * The main() method is ignored in correctly deployed JavaFX application.
   * main() serves only as fallback in case the application can not be launched
   * through deployment artifacts, e.g., in IDEs with limited FX support.
   * NetBeans ignores main().
   * 
   * @param args
   *          the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }

  private final Injector injector = Guice
      .createInjector(new CountdownTerminatorModule(AUTO_SHUTDOWN_TIME));

  @Override
  public void start(final Stage stage) throws Exception {
    copyDefaultDb();
    configEbean();

    GuiceFXMLLoader loader = new GuiceFXMLLoader(injector);
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setResources(ResourceBundle.getBundle("GuidQuerier", new Locale(
        "zh-tw")));
    Parent root =
        (Parent) loader.load("/fxml/Scene.fxml", getClass(), fxmlLoader);

    Scene scene = new Scene(root);
    scene.getStylesheets().add("/styles/Styles.css");

    stage.setTitle("Guid Querier");
    stage.setScene(scene);
    stage.show();
  }

  private void copyDefaultDb() throws IOException {
    File excelDb = new File("exceldb.h2.db");
    if (!excelDb.exists()) {
      InputStream defaultDb =
          ExcelManager.class.getClassLoader().getResourceAsStream(
              "exceldb.h2.db");
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
    h2Db.setUrl("jdbc:h2:exceldb");
    h2Db.setHeartbeatSql("select 1");

    config.setDataSourceConfig(h2Db);

    config.setDdlGenerate(false);
    config.setDdlRun(false);

    config.setDefaultServer(true);
    config.setRegister(true);

    config.addClass(Authentication.class);
    config.addClass(Folder.class);
    config.addClass(History.class);
    config.addClass(Pii.class);

    EbeanServerFactory.create(config);
  }

}
