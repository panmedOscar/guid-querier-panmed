package tw.edu.ym.guid.querier;

import static com.google.common.collect.Lists.newArrayList;
import static net.sf.rubycollect4j.RubyCollections.ra;
import static tw.edu.ym.guid.querier.ExcelManager.ADMIN;
import static tw.edu.ym.guid.querier.ExcelManager.SUPER;
import static tw.edu.ym.guid.querier.ExcelManager.newExcelManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import app.models.Authentication;
import com.avaje.ebean.Ebean;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.sf.rubycollect4j.RubyObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wmw.aop.terminator.ResetTerminator;
import wmw.javafx.JavaFXHelper;
import wmw.javafx.PasswordDialog;
import app.models.Pii;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

/**
 * FXMLController is the controller of GuidClient GUI.
 */
public class FXMLController implements Initializable {

  private static final Logger log =
      LoggerFactory.getLogger(FXMLController.class);

  public static final boolean DEV = false;
  public static final String PROPS_PATH = "excel_manager.properties";
  public static final long AUTO_SHUTDOWN_TIME = 300000000000L;
  public MenuItem importMI;
  public MenuItem securityMI;
  public MenuItem backupMI;
  public Button searchBtn;
  public TableColumn mrnCol;
  public Button exportBtn;

  private RecordManager<Pii> manager;

  @FXML
  private GridPane mainPane;
  @FXML
  private Button modeBtn;
  @FXML
  private Menu totalLbl;
  @FXML
  private TextField searchTF;
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
  @FXML
  private MenuItem resetPasswordMI1;
  @FXML
  private MenuItem resetPasswordMI2;

  private ObservableList<Pii> piis;
  private ResourceBundle rb;

  @ResetTerminator
  @FXML
  private void modeAction(ActionEvent event) {
    toggleMode();
  }

  private void toggleMode() {
    try {
      if (modeBtn.getText().equals(rb.getString("simple-mode"))) {
        modeBtn.setText(rb.getString("complete-mode"));
        setColumnsVisible(false, dateCol, telCol, addrCol, drCol, hospitalCol);
      } else {
        modeBtn.setText(rb.getString("simple-mode"));
        setColumnsVisible(true, dateCol, telCol, addrCol, drCol, hospitalCol);
      }
    } catch (Exception e) {
      log.error("Error in toggling mode", e);
    }
  }

  private void setColumnsVisible(boolean visible,
      TableColumn<?, ?>... columns) {
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
  private void setPassword(ActionEvent event){
    String oldPassword = getPasswordPrompt("oldpwd-prompt");
    if (oldPassword == null || !manager.authenticate(ADMIN, oldPassword)) {
      showErrorAlert("認證失敗", rb.getString("auth-fail"));
      return;
    }

    String newPassword = getNewPasswordWithValidation();
    if (newPassword != null) {
      manager.setPassword(ADMIN, oldPassword, newPassword);
    }
  }

  @FXML
  private void resetPassword1(ActionEvent event) {
    String newPassword = getNewPasswordWithValidation();
    if (newPassword != null) {
      manager.resetPassword(1L, newPassword);
    }
  }

  @FXML
  private void resetPassword2(ActionEvent event) {
    String newPassword = getNewPasswordWithValidation();
    if (newPassword != null) {
      manager.resetPassword(2L, newPassword);
    }
  }

  private String getPasswordPrompt(String messageKey) {
    return getPassword(rb.getString(messageKey));
  }

  private String getNewPasswordWithValidation() {
    String newPassword = getPasswordPrompt("newpwd-prompt");
    String confirmPassword = getPasswordPrompt("confirm-pwd-prompt");

    if (newPassword == null || !newPassword.equals(confirmPassword)) {
      showErrorAlert("密碼不一致", "新密碼和確認密碼不一致，請重新輸入！");
      return null;
    }

    if (newPassword.length() < 8) {
      showErrorAlert("密碼錯誤", "新密碼必須大於8個字元");
      return null;
    }

    if (!isValidPassword(newPassword)) {
      showErrorAlert("密碼錯誤", "新密碼必須包含至少 3 種不同類型的字符（大寫字母、小寫字母、數字、特殊符號）");
      return null;
    }

    return newPassword;
  }

  private boolean isValidPassword(String password) {
    int typeCount = 0;
    if (password.matches(".*[A-Z].*")) typeCount++;
    if (password.matches(".*[a-z].*")) typeCount++;
    if (password.matches(".*[0-9].*")) typeCount++;
    if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) typeCount++;

    return typeCount >= 3;
  }

  private void showErrorAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
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
      exportBtn.setVisible(false);  // Hide export button when query is empty
      exportBtn.setManaged(false);
      resetTable();
    } else {
      exportBtn.setVisible(true);  // Show export button when query is not empty
      exportBtn.setManaged(true);
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
    @SuppressWarnings("unchecked") TableColumn<Pii, String> convertedTc =
        (TableColumn<Pii, String>) tc;
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
    resetPasswordMI1.setVisible(false);
    resetPasswordMI2.setVisible(false);
    String password1 = null;
    String password2 = null;
    int retry = 0;
    do {
      if (retry >= 3)
        System.exit(0);
      password1 = getPassword(rb.getString("pwd1-prompt"));

      // Check if SUPER role authentication succeeds
      if (manager.authenticate(SUPER, password1)) {
        boolean isSafeMode = true;  // Enable safe mode
        resetPasswordMI1.setVisible(true);
        resetPasswordMI2.setVisible(true);
        break;  // Skip second password if SUPER authentication succeeds
      }

      password2 = getPassword(rb.getString("pwd2-prompt"));
      retry++;
      if (DEV)
        break;
    } while (!manager.authenticate(ADMIN, password1) || !manager.authenticate(
        ADMIN, password2));
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
    //    autoBackup();
  }

  private Authentication getUser(String password) {

    return Ebean.find(Authentication.class).where().eq("role", ADMIN)
        .eq("password", password).findUnique();

  }

  public void exportAction(ActionEvent actionEvent) {

    String password = promptForPassword();
    if (password == null || password.isEmpty()) {
      return;  // Cancel if no password is provided
    } else if (!isValidPassword(password)){
      showErrorAlert("密碼錯誤", "密碼必須包含至少 3 種不同類型的字符（大寫字母、小寫字母、數字、特殊符號）");
      return;
    } else if (password.length() < 8){
      showErrorAlert("密碼錯誤", "密碼必須大於8個字元");
      return;
    }

    // Create a file chooser to allow user to select file path
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

    // Show the save file dialog
    File file = fileChooser.showSaveDialog(mainPane.getScene().getWindow());

    if (file != null) {
      try {
        // Generate the CSV data
        String csvData = generateCsvData();

        // Write the CSV data to a temporary file
        File tempCsvFile = File.createTempFile("temp_csv_", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempCsvFile), "UTF-8"))) {
          writer.write(csvData);  // Write the CSV data to the temporary file
        }

        // Create the zip file and encrypt it
        File zipFile = new File(file.getAbsolutePath().replace(".csv", ".zip"));
        ZipFile zip = new ZipFile(zipFile, password.toCharArray());

        ZipParameters parameters = new ZipParameters();
        parameters.setEncryptFiles(true);  // Enable encryption for the ZIP file
        parameters.setEncryptionMethod(EncryptionMethod.AES);  // Use AES encryption

        // Add the temporary CSV file to the zip file
        zip.addFile(tempCsvFile, parameters);

        // Delete the temporary CSV file after adding it to the zip file
        tempCsvFile.delete();

        // Inform the user about the success of the operation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("匯出成功");
        alert.setHeaderText(null);
        alert.setContentText("已將搜尋結果匯出並加密");
        alert.showAndWait();
      } catch (Exception e) {
        // Handle errors
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("匯出失敗");
        alert.setHeaderText(null);
        alert.setContentText("匯出失敗");
        alert.showAndWait();
      }
    }
  }

  private String promptForPassword() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("壓縮並加密檔案");
    dialog.setHeaderText("密碼必須大於8碼且包含至少 3 種不同類型的字符（大寫字母、小寫字母、數字、特殊符號）");
    dialog.setContentText("請設定密碼:");
    dialog.setGraphic(null);
    return dialog.showAndWait().orElse(null);  // Return null if the user cancels
  }

  // Generate CSV content from the piis data
  private String generateCsvData() {
    StringBuilder csvContent = new StringBuilder();
    // Define the CSV header
    csvContent.append("編碼日期,Guid,MRN,身份證字號,姓氏,名字,出生月,出生日,出生年,性別,聯絡電話,地址,收案醫師,收案醫院名稱\n");

    // Iterate over the list of Pii objects and append their data
    for (Pii pii : piis) {
      csvContent.append(pii.get編碼日期() != null ? pii.get編碼日期() : "").append(",");
      csvContent.append(pii.getGuid() != null ? pii.getGuid() : "").append(",");
      csvContent.append(pii.getMrn() != null ? pii.getMrn() : "").append(",");
      csvContent.append(pii.get身份證字號() != null ? pii.get身份證字號() : "").append(",");
      csvContent.append(pii.get姓氏() != null ? pii.get姓氏() : "").append(",");
      csvContent.append(pii.get名字() != null ? pii.get名字() : "").append(",");
      csvContent.append(pii.get出生月() != null ? pii.get出生月() : "").append(",");
      csvContent.append(pii.get出生日() != null ? pii.get出生日() : "").append(",");
      csvContent.append(pii.get出生年() != null ? pii.get出生年() : "").append(",");
      csvContent.append(pii.get性別() != null ? pii.get性別() : "").append(",");
      csvContent.append(pii.get聯絡電話() != null ? pii.get聯絡電話() : "").append(",");
      csvContent.append(pii.get地址() != null ? pii.get地址() : "").append(",");
      csvContent.append(pii.get收案醫師() != null ? pii.get收案醫師() : "").append(",");
      csvContent.append(pii.get收案醫院名稱() != null ? pii.get收案醫院名稱() : "").append("\n");
    }

    return csvContent.toString();
  }

}
