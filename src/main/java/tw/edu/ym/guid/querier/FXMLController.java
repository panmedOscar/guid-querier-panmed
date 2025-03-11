package tw.edu.ym.guid.querier;

import static com.google.common.collect.Lists.newArrayList;
import static net.sf.rubycollect4j.RubyCollections.ra;
import static tw.edu.ym.guid.querier.ExcelManager.ADMIN;
import static tw.edu.ym.guid.querier.ExcelManager.newExcelManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Duration;
import net.sf.rubycollect4j.RubyObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wmw.aop.terminator.ResetTerminator;
import wmw.javafx.JavaFXHelper;
import wmw.javafx.MessageDialog;
import wmw.javafx.PasswordDialog;
import app.models.Pii;

/**
 * 
 * FXMLController is the controller of GuidClient GUI.
 * 
 */
public class FXMLController implements Initializable {

  private static final Logger log = LoggerFactory
      .getLogger(FXMLController.class);

  public static final boolean DEV = false;
  public static final String PROPS_PATH = "excel_manager.properties";
  public static final long AUTO_SHUTDOWN_TIME = 300000000000L;

  private RecordManager<Pii> manager;

  @FXML
  private GridPane mainPane;
  @FXML
  private Button modeBtn;
  @FXML
  private Menu totalLbl;
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
  @FXML
  private TableView<Pii> piiTable;
  @FXML
  private TableColumn<Pii, String> dateCol;
  @FXML
  private TableColumn<Pii, String> telCol;
  @FXML
  private TableColumn<Pii, String> addrCol;
  @FXML
  private TableColumn<Pii, String> drCol;
  @FXML
  private TableColumn<Pii, String> hospitalCol;

  private ObservableList<Pii> piis;
  private ResourceBundle rb;

  @ResetTerminator
  @FXML
  private void modeAction(ActionEvent event) {
    try {
      if (modeBtn.getText().equals(rb.getString("simple-mode"))) {
        modeBtn.setText(rb.getString("complete-mode"));
        setColumnsVisible(false, dateCol, telCol, addrCol, drCol, hospitalCol);
      } else {
        modeBtn.setText(rb.getString("simple-mode"));
        setColumnsVisible(true, dateCol, telCol, addrCol, drCol, hospitalCol);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void setColumnsVisible(boolean visible, TableColumn<?, ?>... columns) {
    Arrays.stream(columns).forEach(col -> col.setVisible(visible));
  }

  @ResetTerminator
  @FXML
  private void backup(ActionEvent event) {
    File folder = JavaFXHelper.FolderSelector();
    if (folder != null)
      manager.setBackupFolder(folder.getAbsolutePath());
  }

  @ResetTerminator
  @FXML
  private void setPassword(ActionEvent event) {
    String oldPassword = null;
    String newPassword = null;
    do {
      oldPassword = getPassword(rb.getString("oldpwd-prompt"));
    } while (oldPassword != null && !manager.authenticate(ADMIN, oldPassword));

    if (oldPassword == null) {
      new MessageDialog().showMessages(rb.getString("auth-fail"));
      return;
    }

    do {
      newPassword = getPassword(rb.getString("newpwd-prompt"));
    } while (newPassword != null && newPassword.length() < 4);

    manager.setPassword(ADMIN, oldPassword, newPassword);
  }

  @ResetTerminator
  @FXML
  private void importExcels(ActionEvent event) {
    File folder = JavaFXHelper.FolderSelector();
    if (folder != null) {
      manager.importExcels(folder.getAbsolutePath());
      resetTable();
    }
  }

  @ResetTerminator
  @FXML
  public void queryAction(ActionEvent event) {
    String query = searchTF.getText().trim();
    if (query.isEmpty()) {
      resetTable();
    } else {
      piis.clear();
      String[] keywords = query.trim().split("\\s+");
      piis.addAll(manager.query(keywords));
    }
  }

  private void resetTable() {
    piis.clear();
    piis.addAll(manager.findAll(500));
    totalLbl.setText("Total: " + manager.getNumberOfRecords());
  }

  private void initTable() {
    piiTable.setEditable(true);
    List<TableColumn<Pii, ?>> columns = newArrayList();
    for (TableColumn<Pii, ?> tc : piiTable.getColumns()) {
      columns.add(tc);
      columns.addAll(tc.getColumns());
    }

    for (TableColumn<Pii, ?> tc : ra(columns).uniq()) {
      Callback<?, ?> o = tc.cellValueFactoryProperty().getValue();
      if (o != null) {
        String property = ((PropertyValueFactory<?, ?>) o).getProperty();
        if (manager.isColumnEditable(property))
          setEditableColumn(tc, property);
      }
    }
  }

  private void setEditableColumn(TableColumn<Pii, ?> tc, final String name) {
    Callback<TableColumn<Pii, String>, TableCell<Pii, String>> callback =
        TextFieldTableCell.forTableColumn();
    @SuppressWarnings("unchecked")
    TableColumn<Pii, String> convertedTc = (TableColumn<Pii, String>) tc;
    convertedTc.setCellFactory(callback);
    convertedTc.setOnEditCommit(new EventHandler<CellEditEvent<Pii, String>>() {

      @ResetTerminator
      @Override
      public void handle(CellEditEvent<Pii, String> t) {
        Pii pii = (Pii) piis.get(t.getTablePosition().getRow());
        RubyObject.send(pii, "set" + name, t.getNewValue());
        manager.update(pii);
      }

    });
  }

  private void authenticate() {
    mainPane.setDisable(true);
    String password1 = null;
    String password2 = null;
    int retry = 0;
    do {
      if (retry >= 3)
        System.exit(0);
      password1 = getPassword(rb.getString("pwd1-prompt"));
      password2 = getPassword(rb.getString("pwd2-prompt"));
      retry++;
      if (DEV)
        break;
    } while (!manager.authenticate(ADMIN, password1)
        || !manager.authenticate(ADMIN, password2));
    mainPane.setDisable(false);
  }

  private String getPassword(String message) {
    final SimpleStringProperty password = new SimpleStringProperty();
    new PasswordDialog(message, new Consumer<String>() {

      @Override
      public void accept(String item) {
        password.set(item);
      }

    });
    return password.getValue();
  }

  private void autoBackup() {
    Timeline backuper =
        new Timeline(new KeyFrame(Duration.seconds(300),
            new EventHandler<ActionEvent>() {

              @Override
              public void handle(ActionEvent event) {
                manager.backup();
              }

            }));
    backuper.setCycleCount(Timeline.INDEFINITE);
    backuper.play();
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    this.rb = rb;
    try {
      manager = newExcelManager(PROPS_PATH);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    piis = (ObservableList<Pii>) piiTable.getItems();
    authenticate();
    initTable();
    resetTable();
    autoBackup();
  }

}
