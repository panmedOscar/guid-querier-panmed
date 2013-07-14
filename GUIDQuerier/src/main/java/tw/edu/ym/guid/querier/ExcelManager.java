package tw.edu.ym.guid.querier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.lingala.zip4j.exception.ZipException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ym.guid.querier.api.Authentications;
import tw.edu.ym.guid.querier.api.Folders;
import tw.edu.ym.guid.querier.api.Histories;
import tw.edu.ym.guid.querier.api.Piis;
import wmw.db.embedded.EmbeddedStorage;
import wmw.db.jdbc.Field;
import wmw.file.excel.Excel2Map;
import wmw.file.zip.EncryptedZip;

import com.google.common.collect.Multimap;

import static wmw.db.jdbc.Field.Varchar;

import static wmw.db.embedded.EmbeddedStorage.newEmbeddedStorage;

import exceldb.model.Authentication;
import exceldb.model.Folder;
import exceldb.model.Pii;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static wmw.util.dir.FolderTraverser.retrieveAllFiles;

/**
 * 
 * ExcelManager manages data of a Excel by an embedded database.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class ExcelManager {

  private static final Logger log = LoggerFactory.getLogger(ExcelManager.class);

  private final String sheet;
  private final String zipPassword;
  private final String defaultPassword1;
  private final String defaultPassword2;
  private final EmbeddedStorage es;

  public static ExcelManager newExcelManager(String propertiesPath)
      throws IOException, SQLException, ClassNotFoundException {
    Properties props = new Properties();
    InputStream in =
        ExcelManager.class.getClassLoader().getResourceAsStream(propertiesPath);
    props.load(in);
    in.close();
    return new ExcelManager(props);
  }

  public ExcelManager(Properties props) throws SQLException,
      ClassNotFoundException, FileNotFoundException, IOException {
    sheet = props.getProperty("sheet");
    zipPassword = props.getProperty("zip_password");
    defaultPassword1 = props.getProperty("default_password_1");
    defaultPassword2 = props.getProperty("default_password_2");
    es = newEmbeddedStorage(props.getProperty("db_props"));
    initDatabase();
    updateExcels();
  }

  public int total() {
    return Piis.count();
  }

  private void updateExcels() {
    Folder folder = Folders.findFirst();

    if (folder != null)
      importExcelsInFolder(folder.getPath());
  }

  public String[] getHeader() {
    List<String> header = emptyList();
    try {
      header = es.getColumns(sheet);
    } catch (SQLException e) {
      log.error(e.getMessage());
    }
    return header.toArray(new String[header.size()]);
  }

  public List<Pii> getAll() {
    return Piis.all();
  }

  public List<Object[]> selectAll() {
    try {
      return es.selectAll(sheet);
    } catch (SQLException e) {
      log.error(e.getMessage());
    }
    return emptyList();
  }

  public List<Object[]> selectAll(int limit) {
    try {
      return es.selectAll(sheet, limit);
    } catch (SQLException e) {
      log.error(e.getMessage());
    }
    return emptyList();
  }

  public List<Pii> query(String... values) {
    return Piis.globalSearch(values);
  }

  public void importExcelsInFolder(final String folder) {
    List<File> files = retrieveAllFiles(folder, "zip");
    List<EncryptedZip> encryptedZips = filterEncryptedZips(files);
    Map<String, InputStream> excels = emptyMap();
    try {
      excels = filterUnprocessedExcels(encryptedZips);
      insertExcelRecords(excels.values());
    } catch (ZipException e) {
      log.error(e.getMessage());
    }
    recordProcessedFiles(excels.keySet());
    if (files.isEmpty())
      Folders.removeFolderPath();
    else
      Folders.setFolderPath(folder);
  }

  public boolean authenticate(String role, String password) {
    Authentication auth = Authentications.findByRoleAndPassword(role, password);
    return auth != null;
  }

  public void setAdminPassword(String role, String newpwd) {
    Authentications.setAdminPassword(role, newpwd);
  }

  private void recordProcessedFiles(Collection<String> fileNames) {
    for (String fileName : fileNames)
      Histories.create(fileName);
  }

  private void insertExcelRecords(Collection<InputStream> excels) {
    for (InputStream is : excels) {
      Workbook wb;
      try {
        wb = WorkbookFactory.create(is);
        Multimap<String, Map<String, String>> maps = Excel2Map.convert(wb);
        for (String sheet : maps.keySet()) {
          if (sheet.trim().matches("(?i)" + sheet + ".*"))
            es.safeInsertRecords(this.sheet, maps.get(sheet));
        }
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    }
  }

  private Map<String, InputStream> filterUnprocessedExcels(
      List<EncryptedZip> encryptedZips) throws ZipException {
    List<String> allFiles = newArrayList();
    for (EncryptedZip ez : encryptedZips)
      for (String fileName : ez.getAllFileNames())
        if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))
          allFiles.add(fileName);

    Set<String> unprocessedFiles = Histories.filterUnprocessedFiles(allFiles);
    Map<String, InputStream> excels = newHashMap();
    for (EncryptedZip ez : encryptedZips)
      for (String fileName : ez.getAllFileNames())
        if (unprocessedFiles.contains(fileName))
          excels.put(fileName, ez.getInputStreamByFileName(fileName));
    return excels;
  }

  private List<EncryptedZip> filterEncryptedZips(List<File> files) {
    List<EncryptedZip> encryptedZips = newArrayList();
    for (File file : files)
      if (file.getName().matches("^.*\\.zip$"))
        try {
          encryptedZips.add(new EncryptedZip(file.getAbsolutePath(),
              zipPassword));
        } catch (Exception e) {
          log.warn(e.getMessage());
        }
    return encryptedZips;
  }

  private void initDatabase() throws SQLException {
    if (!(es.hasTable(sheet))) {
      Field[] fields = new Field[ExcelField.values().length];
      for (int i = 0; i < ExcelField.values().length; i++)
        fields[i] = Varchar(ExcelField.values()[i].toString());
      es.createTable(sheet, fields);

      for (ExcelField ef : ExcelField.values()) {
        es.index(sheet, ef.toString());
        if (ef.isUnique())
          es.unique(sheet, ef.toString());
      }
    }

    if (!(es.hasTable("history")))
      createHistoryTable();

    if (!(es.hasTable("authentication")))
      createAuthenticationTable();

    if (!(es.hasTable("folder")))
      es.createTable("folder", Varchar("path"));
  }

  private void createHistoryTable() throws SQLException {
    es.createTable("history", Varchar("file_name"));
    es.unique("history", "file_name");
    es.index("history", "file_name");
  }

  @SuppressWarnings("unchecked")
  private void createAuthenticationTable() throws SQLException {
    es.createTable("authentication", Varchar("role"), Varchar("password"));
    Map<String, String> defaultPassword =
        of("role", "admin1", "password", defaultPassword1);
    es.insertRecords("authentication", defaultPassword);
    defaultPassword = of("role", "admin2", "password", defaultPassword2);
    es.insertRecords("authentication", defaultPassword);
  }

}
