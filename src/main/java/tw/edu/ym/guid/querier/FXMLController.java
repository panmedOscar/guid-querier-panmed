package tw.edu.ym.guid.querier;

import static com.google.common.collect.Lists.newArrayList;
import static net.sf.rubycollect4j.RubyCollections.ra;
import static tw.edu.ym.guid.querier.ExcelManager.newExcelManager;
import static tw.edu.ym.guid.querier.api.Authentications.RoleType.ADMIN;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
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
import net.sf.rubycollect4j.block.Block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wmw.util.javafx.PasswordDialog;
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

  private void initTable() {
    for (Pii p : manager.findAll(500)) {
      piis.add(p);
    }
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
          ExcelField ef = ExcelField.valueOf(property.toUpperCase());
          if (ef.isEditable()) {
            Callback<TableColumn<Pii, String>, TableCell<Pii, String>> callback =
                TextFieldTableCell.forTableColumn();
            @SuppressWarnings("unchecked")
            TableColumn<Pii, String> convertedTc =
                (TableColumn<Pii, String>) tc;
            convertedTc.setCellFactory(callback);
            convertedTc
                .setOnEditCommit(new EventHandler<CellEditEvent<Pii, String>>() {

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
  }

}
