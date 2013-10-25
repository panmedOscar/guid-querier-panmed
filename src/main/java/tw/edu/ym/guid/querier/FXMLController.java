package tw.edu.ym.guid.querier;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * 
 * FXMLController is the controller of GuidClient GUI.
 * 
 */
public class FXMLController implements Initializable {

  @FXML
  private GridPane mainPane;
  @FXML
  private TextField username;
  @FXML
  private PasswordField password;

  @Override
  public void initialize(URL url, ResourceBundle rb) {

  }

}
