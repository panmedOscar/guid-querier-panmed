package tw.edu.ym.guid.querier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.lingala.zip4j.exception.ZipException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ym.guid.querier.db.Authentications;
import tw.edu.ym.guid.querier.db.Folders;
import tw.edu.ym.guid.querier.db.Histories;
import tw.edu.ym.guid.querier.db.Piis;
import wmw.data.embedded.EmbeddedStorage;
import wmw.data.excel.Excel2Map;
import wmw.data.zip.EncryptedZip;
import wmw.util.jdbc.Field;
import exceldb.model.Authentication;
import exceldb.model.Folder;
import exceldb.model.Pii;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static wmw.util.dir.FolderTraverser.retrieveAllFiles;
import static wmw.util.jdbc.Field.Varchar;

public final class ExcelManager {

  static final Logger logger = LoggerFactory.getLogger(ExcelManager.class);

  public static final String SHEET = "pii";
  private static final String ZIP_PASSWORD =
      "4b565f5@a6d8d395e!73616f$ab41e361#b618f7c386def2f25f&eef28dded0e";
  private static final String DEFAULT_PASSWORD_1 = "ERY!VB%";
  private static final String DEFAULT_PASSWORD_2 = "Ur)TH#G";
  private final EmbeddedStorage es;

  public ExcelManager() throws SQLException, ClassNotFoundException,
      FileNotFoundException, IOException {
    es = new EmbeddedStorage();
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
      header = es.getColumns(SHEET);
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
    return header.toArray(new String[header.size()]);
  }

  public List<Pii> getAll() {
    return Piis.all();
  }

  public List<Object[]> selectAll() {
    try {
      return es.selectAll();
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
    return emptyList();
  }

  public List<Object[]> selectAll(int limit) {
    try {
      return es.selectAll(limit);
    } catch (SQLException e) {
      logger.error(e.getMessage());
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
      logger.error(e.getMessage());
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
        Map<String, List<Map<String, String>>> maps = Excel2Map.convert(wb);
        for (List<Map<String, String>> list : maps.values())
          es.safeInsertRecords(SHEET, list);
      } catch (Exception e) {
        logger.error(e.getMessage());
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
              ZIP_PASSWORD));
        } catch (Exception e) {
          logger.warn(e.getMessage());
        }
    return encryptedZips;
  }

  private void initDatabase() throws SQLException {
    if (!(es.hasTable(SHEET))) {
      Field[] fields = new Field[ExcelField.values().length];
      for (int i = 0; i < ExcelField.values().length; i++)
        fields[i] = Varchar(ExcelField.values()[i].toString());
      es.createTable(SHEET, fields);

      for (ExcelField ef : ExcelField.values()) {
        es.index(SHEET, ef.toString());
        if (ef.isUnique())
          es.unique(SHEET, ef.toString());
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
        of("role", "admin1", "password", DEFAULT_PASSWORD_1);
    es.insertRecords("authentication", defaultPassword);
    defaultPassword = of("role", "admin2", "password", DEFAULT_PASSWORD_2);
    es.insertRecords("authentication", defaultPassword);
  }

}
