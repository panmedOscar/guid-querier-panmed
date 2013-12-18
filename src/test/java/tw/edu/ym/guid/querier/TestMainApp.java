package tw.edu.ym.guid.querier;

import static tw.edu.ym.guid.querier.FXMLController.AUTO_SHUTDOWN_TIME;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import wmw.aop.terminator.CountdownTerminatorModule;
import wmw.javafx.GuiceFXMLLoader;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class TestMainApp extends Application {

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

}
