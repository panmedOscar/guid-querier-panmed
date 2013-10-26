package tw.edu.ym.guid.querier;

import static tw.edu.ym.guid.querier.ExcelManager.newExcelManager;
import static tw.edu.ym.guid.querier.api.Authentications.RoleType.ADMIN;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import net.sf.rubycollect4j.block.Block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wmw.util.javafx.PasswordDialog;

/**
 * 
 * FXMLController is the controller of GuidClient GUI.
 * 
 */
public class FXMLController implements Initializable {

  private static final Logger log = LoggerFactory
      .getLogger(FXMLController.class);

  public static final boolean DEV = true;
  public static final String PROPS_PATH = "excel_manager.properties";

  private RecordManager manager;

  @FXML
  private GridPane mainPane;
  @FXML
  private MenuItem importMI;
  @FXML
  private MenuItem securityMI;
  @FXML
  private MenuItem backupMI;
  @FXML
  private TextField searchTF;
  @FXML
  private Button searchBtn;

  private void authenticate() {
    mainPane.setDisable(true);
    String password1 = null;
    String password2 = null;
    int retry = 0;
    do {
      if (retry >= 3)
        System.exit(0);
      password1 = getPassword("請輸入第一組密碼：");
      password2 = getPassword("請輸入第二組密碼：");
      retry++;
      if (DEV)
        break;
    } while (!manager.authenticate(ADMIN.toString(), password1)
        || !manager.authenticate(ADMIN.toString(), password2));
    mainPane.setDisable(false);
  }

  private String getPassword(String message) {
    final SimpleStringProperty password = new SimpleStringProperty();
    new PasswordDialog(message, new Block<String>() {

      @Override
      public void yield(String item) {
        password.set(item);
      }

    });
    return password.getValue();
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    try {
      manager = newExcelManager(PROPS_PATH);
    } catch (ClassNotFoundException | IOException | SQLException e) {
      log.error(e.getMessage(), e);
    }
    authenticate();
  }

}
