package tw.edu.ym.guid.querier;

import java.io.File;
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
import tw.edu.ym.guid.querier.api.Folders.FolderType;
import tw.edu.ym.guid.querier.api.Histories;
import tw.edu.ym.guid.querier.api.Piis;
import wmw.util.BackupUtil;
import wmw.util.BeanConverter;
import wmw.util.EncryptedZip;
import wmw.util.Excel2Map;
import wmw.util.db.EmbeddedStorage;
import wmw.util.db.TableField;

import com.google.common.base.Objects;
import com.google.common.collect.Multimap;

import exceldb.model.Authentication;
import exceldb.model.Folder;
import exceldb.model.Pii;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static tw.edu.ym.guid.querier.api.Authentications.RoleType.ADMIN;
import static wmw.util.FolderTraverser.retrieveAllFiles;
import static wmw.util.db.EmbeddedStorage.newEmbeddedStorage;
import static wmw.util.db.TableField.Varchar;

/**
 * 
 * ExcelManager manages data of a Excel by an embedded database.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class ExcelManager implements RecordManager {

  private static final Logger log = LoggerFactory.getLogger(ExcelManager.class);

  private final String sheet;
  private final String zipPassword;
  private final String defaultPassword1;
  private final String defaultPassword2;
  private final EmbeddedStorage es;

  /**
   * 
   * Creates a ExcelManager by given properties path.
   * 
   * @param propertiesPath
   *          the classpath to the properties
   * @return an ExcelManager
   * @throws IOException
   *           if properties not found
   * @throws ClassNotFoundException
   *           if DB driver not found
   * @throws SQLException
   *           if DB creation failed
   */
  public static ExcelManager newExcelManager(String propertiesPath)
      throws IOException, ClassNotFoundException, SQLException {
    Properties props = new Properties();
    InputStream in =
        ExcelManager.class.getClassLoader().getResourceAsStream(propertiesPath);
    props.load(in);
    in.close();
    return new ExcelManager(props);
  }

  /**
   * Creates a ExcelManager by given Properties.
   * 
   * @param props
   *          a Properties
   * @return an ExcelManager
   * @throws IOException
   *           if properties not found
   * @throws ClassNotFoundException
   *           if DB driver not found
   * @throws SQLException
   *           if DB creation failed
   */
  public ExcelManager(Properties props) throws SQLException,
      ClassNotFoundException, IOException {
    sheet = props.getProperty("sheet");
    zipPassword = props.getProperty("zip_password");
    defaultPassword1 = props.getProperty("default_password_1");
    defaultPassword2 = props.getProperty("default_password_2");
    es = newEmbeddedStorage(props.getProperty("db_props"));
    initDatabase();
    updateExcels();
  }

  /**
   * Returns the total of records.
   * 
   * @return the total of records
   */
  @Override
  public int totalRecord() {
    return Piis.count();
  }

  private void updateExcels() {
    Folder folder = Folders.findFirst(FolderType.IMPORT);

    if (folder != null)
      importExcels(folder.getPath());
  }

  /**
   * Returns the header of the excel.
   * 
   * @return the header of the excel
   */
  @Override
  public String[] getHeader() {
    List<String> header = newArrayList();
    try {
      for (String field : es.getColumns(sheet))
        header.add(field.toUpperCase());
    } catch (SQLException e) {
      log.error(e.getMessage());
    }
    return header.toArray(new String[header.size()]);
  }

  /**
   * Check if a column editable.
   * 
   * @return true if the column editable, false otherwise
   */
  @Override
  public boolean isColumnEditableAt(int index) {
    return ExcelField.values()[index].isEditable();
  }

  /**
   * Returns all Piis.
   * 
   * @return a List of Pii
   */
  public List<Pii> getAll() {
    return Piis.all();
  }

  /**
   * Returns all Piis by storing the properties of each Pii in an Object Array.
   * 
   * @return a List of Object Array which contains the properties of each Pii
   */
  @Override
  public List<Object[]> selectAll() {
    try {
      return es.selectAll(sheet, ExcelField.orderBy());
    } catch (SQLException e) {
      log.error(e.getMessage());
    }
    return emptyList();
  }

  /**
   * Returns all Piis by storing the properties of each Pii in an Object Array.
   * 
   * @param limit
   *          the maximum records to return
   * @return a List of Object Array which contains the properties of each Pii
   */
  @Override
  public List<Object[]> selectAll(int limit) {
    try {
      return es.selectAll(sheet, limit, ExcelField.orderBy());
    } catch (SQLException e) {
      log.error(e.getMessage());
    }
    return emptyList();
  }

  /**
   * Updates a row of record in database.
   * 
   * @param record
   *          a row of record in database
   */
  @Override
  public void update(Map<String, String> record) {
    Piis.update(record);
  }

  /**
   * Searches Piis by given keywords.
   * 
   * @param keywords
   *          used to query
   * @return a List of Object[]
   */
  @Override
  public List<Object[]> query(String... keywords) {
    List<Object[]> results = newArrayList();
    for (Pii pii : Piis.globalSearch(keywords))
      results.add(BeanConverter.toObjectArray(pii));
    return results;
  }

  /**
   * Imports all excels in encrypted zips within a folder.
   * 
   * @param folderPath
   *          where encrypted zips located
   */
  @Override
  public void importExcels(final String folderPath) {
    List<File> files = retrieveAllFiles(folderPath, "zip");
    List<EncryptedZip> encryptedZips = filterEncryptedZips(files);
    Map<String, InputStream> excels = emptyMap();
    try {
      excels = filterUnprocessedExcels(encryptedZips);
      List<Workbook> wbs = newArrayList();
      for (InputStream is : excels.values())
        wbs.add(WorkbookFactory.create(is));
      insertExcelRecords(wbs);
      recordProcessedFiles(excels.keySet());
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    if (files.isEmpty())
      Folders.removeFolderPath(FolderType.IMPORT);
    else
      Folders.setFolderPath(FolderType.IMPORT, folderPath);
  }

  /**
   * Authenticates the password of a role.
   * 
   * @param role
   *          to be authenticated
   * @param password
   *          to be verified
   * @return true if authentication passed, false otherwise
   */
  @Override
  public boolean authenticate(String role, String password) {
    Authentication auth = Authentications.findByRoleAndPassword(role, password);
    return auth != null;
  }

  /**
   * Sets a new password of the role.
   * 
   * @param role
   *          to be reset
   * @param oldPassword
   *          the old password
   * @param newPassword
   *          the new password
   */
  @Override
  public void setPassword(String role, String oldPassword, String newPassword) {
    Authentications.setPassword(role, oldPassword, newPassword);
  }

  /**
   * Sets backup folder.
   * 
   * @param backupFolder
   *          used to backup encrypted zips
   */
  @Override
  public void setBackupFolder(String backupFolder) {
    Folders.setFolderPath(FolderType.BACKUP,
        new File(backupFolder).getAbsolutePath());
    backup();
  }

  /**
   * Backups all encrypted zips from import folder to backup folder.
   */
  @Override
  public void backup() {
    Folder src = Folders.findFirst(FolderType.IMPORT);
    Folder dest = Folders.findFirst(FolderType.BACKUP);
    if (src != null && dest != null) {
      File srcFolder = new File(src.getPath());
      File destFolder = new File(dest.getPath());
      if (srcFolder.exists() && destFolder.exists()) {
        List<File> files = retrieveAllFiles(srcFolder.getAbsolutePath(), "zip");
        List<File> encryptedFiles = filterEncryptedFiles(files);
        try {
          BackupUtil.backup(encryptedFiles, destFolder);
        } catch (IOException e) {
          log.warn(e.getMessage());
        }
      }
    }
  }

  private List<File> filterEncryptedFiles(List<File> files) {
    List<File> encryptedFiles = newArrayList();
    for (File file : files)
      if (file.getName().matches("^.*\\.zip$"))
        try {
          new EncryptedZip(file.getAbsolutePath(), zipPassword);
          encryptedFiles.add(file);
        } catch (Exception e) {
          log.warn(e.getMessage());
        }
    return encryptedFiles;
  }

  private void recordProcessedFiles(Collection<String> fileNames) {
    for (String fileName : fileNames)
      Histories.create(fileName);
  }

  private void insertExcelRecords(List<Workbook> wbs) {
    for (Workbook wb : wbs) {
      Multimap<String, Map<String, String>> maps = Excel2Map.convert(wb);
      for (String sheet : maps.keySet()) {
        if (sheet.trim().matches("(?i)" + sheet + ".*"))
          try {
            es.safeInsertRecords(this.sheet, maps.get(sheet));
          } catch (SQLException e) {
            log.error(e.getMessage());
          }
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
      TableField[] fields = new TableField[ExcelField.values().length];
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
      es.createTable("folder", Varchar("usage"), Varchar("path"));
  }

  private void createHistoryTable() throws SQLException {
    es.createTable("history", Varchar("file_name"));
    es.unique("history", "file_name");
    es.index("history", "file_name");
  }

  @SuppressWarnings("unchecked")
  private void createAuthenticationTable() throws SQLException {
    es.createTable("authentication", Varchar("role"), Varchar("password"));
    es.insertRecords("authentication",
        of("role", ADMIN.toString(), "password", defaultPassword1),
        of("role", ADMIN.toString(), "password", defaultPassword2));
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this.getClass()).add("Sheet", sheet)
        .toString();
  }

}
