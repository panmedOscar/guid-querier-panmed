package tw.edu.ym.guid.querier;

import static com.google.common.collect.Lists.newArrayList;
import static net.sf.rubycollect4j.RubyCollections.ra;
import static tw.edu.ym.guid.querier.ExcelManager.newExcelManager;
import static tw.edu.ym.guid.querier.api.Authentications.RoleType.ADMIN;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

import javax.swing.Timer;

import net.sf.rubycollect4j.RubyObject;
import net.sf.rubycollect4j.block.Block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wmw.aop.terminator.ResetTerminator;
import wmw.javafx.JavaFXHelper;
import wmw.javafx.MessageDialog;
import wmw.javafx.PasswordDialog;
import exceldb.model.Pii;

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
  public static final long AUTO_SHUTDOWN_TIME = 300000000000L;

  private RecordManager<Pii> manager;

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
  @FXML
  private TableView<Pii> piiTable;

  private ObservableList<Pii> piis;

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
      oldPassword = getPassword("請輸入舊密碼:");
    } while (oldPassword != null
        && !manager.authenticate(ADMIN.toString(), oldPassword));

    if (oldPassword == null) {
      new MessageDialog().showMessages("認證失敗");
      return;
    }

    do {
      newPassword = getPassword("請輸入新密碼(最少四個字元):");
    } while (newPassword != null && newPassword.length() < 4);

    manager.setPassword(ADMIN.toString(), oldPassword, newPassword);
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
      for (Pii pii : manager.query(keywords)) {
        piis.add(pii);
      }
    }
  }

  private void resetTable() {
    piis.clear();
    for (Pii p : manager.findAll(500)) {
      piis.add(p);
    }
  }

  private void initTable() {
    resetTable();
    piiTable.setEditable(true);
    List<TableColumn<Pii, ?>> tcs = newArrayList();
    for (TableColumn<Pii, ?> tc : piiTable.getColumns()) {
      tcs.add(tc);
      tcs.addAll(tc.getColumns());
    }
    ra(tcs).uniqǃ();
    for (TableColumn<Pii, ?> tc : tcs) {
      Object o = tc.cellValueFactoryProperty().getValue();
      if (o != null) {
        @SuppressWarnings("rawtypes")
        final String property = ((PropertyValueFactory) o).getProperty();
        try {
          if (manager.isColumnEditable(property)) {
            Callback<TableColumn<Pii, String>, TableCell<Pii, String>> callback =
                TextFieldTableCell.forTableColumn();
            @SuppressWarnings("unchecked")
            TableColumn<Pii, String> convertedTc =
                (TableColumn<Pii, String>) tc;
            convertedTc.setCellFactory(callback);
            convertedTc
                .setOnEditCommit(new EventHandler<CellEditEvent<Pii, String>>() {

                  @ResetTerminator
                  @Override
                  public void handle(CellEditEvent<Pii, String> t) {
                    Pii pii = (Pii) piis.get(t.getTablePosition().getRow());
                    RubyObject.send(pii, "set" + property, t.getNewValue());
                    manager.update(pii);
                  }

                });
          }
        } catch (Exception e) {}
      }
    }
  }

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

  private void autoBackup() {
    Timer timer = new Timer(600000, new ActionListener() {

      @Override
      public void actionPerformed(java.awt.event.ActionEvent e) {
        manager.backup();
      }

    });
    timer.start();
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    try {
      manager = newExcelManager(PROPS_PATH);
    } catch (ClassNotFoundException | IOException | SQLException e) {
      log.error(e.getMessage(), e);
    }
    piis = (ObservableList<Pii>) piiTable.getItems();
    authenticate();
    initTable();
    autoBackup();
  }

}
