package tw.edu.ym.guid.querier;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyMap;
import static net.sf.rubycollect4j.RubyCollections.ra;

import com.google.common.base.MoreObjects;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import javafx.scene.control.Alert;
import net.lingala.zip4j.exception.ZipException;
import net.sf.rubycollect4j.RubyArray;
import net.sf.rubycollect4j.RubyDir;
import net.sf.rubycollect4j.RubyFile;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wmw.util.BackupUtil;
import wmw.util.EncryptedZip;
import wmw.util.Excel2Map;
import app.models.Authentication;
import app.models.Folder;
import app.models.History;
import app.models.Pii;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;

import static com.google.common.collect.Lists.newArrayList;

/**
 * 
 * ExcelManager manages data of a Excel by an embedded database.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class ExcelManager implements RecordManager<Pii> {

  private static final Logger log = LoggerFactory.getLogger(ExcelManager.class);

  public static final String ADMIN = "ADMIN";
  public static final String IMPORT = "IMPORT";
  public static final String BACKUP = "BACKUP";

  private final String sheet;
  private final String zipPassword;
  private final String defaultPassword1;
  private final String defaultPassword2;

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
      throws IOException {
    Properties props = new Properties();
    InputStream in =
        ExcelManager.class.getClassLoader().getResourceAsStream(propertiesPath);
    props.load(in);
    in.close();
    return new ExcelManager(props);
  }

  private ExcelManager(Properties props) {
    sheet = props.getProperty("sheet");
    zipPassword = props.getProperty("zip_password");
    defaultPassword1 = props.getProperty("default_password_1");
    defaultPassword2 = props.getProperty("default_password_2");
    setDefaultPasswords();
    updateExcels();
  }

  @Override
  public int getNumberOfRecords() {
    return Ebean.find(Pii.class).findRowCount();
  }

  private void updateExcels() {

    List<Folder> folders =
        Ebean.find(Folder.class).where().eq("usage", IMPORT).findList();
    Folder folder = folders.isEmpty() ? null : folders.get(0);

    if (folder != null) {
//      Ebean.beginTransaction();
      if (new File(folder.getPath()).exists())
        importExcels(folder.getPath());
      else
        Ebean.delete(folders);

//      Ebean.commitTransaction();
//      Ebean.endTransaction();
    }

  }

  @Override
  public List<String> getHeader() {
    List<String> header = newArrayList();
    for (ExcelField field : ExcelField.values()) {
      header.add(field.toString());
    }
    return header;
  }

  @Override
  public boolean isColumnEditable(String column) {
    ExcelField ef = null;
    try {
      ef = ExcelField.valueOf(column.toUpperCase());
    } catch (Exception e) {
      return false;
    }
    return ef.isEditable();
  }

  @Override
  public List<Pii> findAll() {
    return Ebean.find(Pii.class).findList();
  }

  @Override
  public List<Pii> findAll(final int limit) {
    return Ebean.find(Pii.class).setMaxRows(limit).findList();
  }

  @Override
  public void importExcels(final String folderPath) {

    List<File> files = retrieveAllZips(folderPath);
    List<EncryptedZip> encryptedZips = filterEncryptedZips(files);
    Map<String, InputStream> excels = emptyMap();
    try {
      excels = filterUnprocessedExcels(encryptedZips);
      List<Workbook> wbs = newArrayList();
      for (InputStream is : excels.values()) {
        wbs.add(WorkbookFactory.create(is));
      }
      insertExcelRecords(wbs);
      recordProcessedFiles(excels.keySet());
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    Folder folder =
        Ebean.find(Folder.class).where().eq("usage", IMPORT).findUnique();
    if (files.isEmpty()) {
//      Ebean.beginTransaction();
      if (folder != null)
        Ebean.delete(folder);

//      Ebean.commitTransaction();
//      Ebean.endTransaction();
    } else {
//      Ebean.beginTransaction();
      if (folder == null) {
        folder = new Folder();
        folder.setUsage(IMPORT);
        folder.setPath(folderPath);
        Ebean.save(folder);
      } else {
        folder.setPath(folderPath);
        Ebean.save(folder);
      }
//      Ebean.commitTransaction();
//      Ebean.endTransaction();
    }


  }

  @Override
  public boolean authenticate(String role, String password) {
    if (password == null)
      return false;

    Authentication auth =
        Ebean.find(Authentication.class).where().eq("role", role)
            .eq("password", password).findUnique();
    return auth != null;
  }

  @Override
  public void setPassword(String role, String oldPassword, String newPassword) {
    Ebean.beginTransaction();
    try {
      Authentication auth = Ebean.find(Authentication.class)
          .where().eq("role", role)
          .eq("password", oldPassword).findUnique();
      auth.setPassword(newPassword);
      auth.setLastChange(new Date());
      Ebean.save(auth);

      Ebean.commitTransaction();
    } catch (Exception e) {
      Ebean.rollbackTransaction();
      e.printStackTrace();
    } finally {
      Ebean.endTransaction();
    }
  }

  @Override
  public void update(final Pii record) {
    Ebean.save(record);
  }

  @Override
  public List<Pii> query(String... keywords) {
    LinkedList<Expression> exprs = newLinkedList();
    for (String keyword : keywords) {
      for (ExcelField f : ExcelField.values()) {
        if (keyword.getBytes(Charset.forName("UTF-8")).length < 3)
          exprs.add(Expr.ieq(f.toString(), keyword));
        else
          exprs.add(Expr.ilike(f.toString(), "%" + keyword + "%"));
      }
    }
    Expression ors = exprs.pollFirst();
    for (Expression expr : exprs) {
      ors = Expr.or(ors, expr);
    }
    return Ebean.find(Pii.class).where(ors).findList();
  }

  @Override
  public void setBackupFolder(String backupFolder) {
    Folder folder =
        Ebean.find(Folder.class).where().eq("usage", BACKUP).findUnique();
    if (folder == null)
      folder = new Folder();
    folder.setUsage(BACKUP);
    folder.setPath(new File(backupFolder).getAbsolutePath());
    Ebean.save(folder);
    backup();
  }

  @Override
  public void backup() {
    Folder src =
        Ebean.find(Folder.class).where().eq("usage", IMPORT).findUnique();
    Folder dest =
        Ebean.find(Folder.class).where().eq("usage", BACKUP).findUnique();
    if (src != null && dest != null) {
      File srcFolder = new File(src.getPath());
      File destFolder = new File(dest.getPath());
      if (srcFolder.exists() && destFolder.exists()) {
        List<File> files = retrieveAllZips(srcFolder.getAbsolutePath());
        List<File> encryptedFiles = filterEncryptedFiles(files);
        try {
          BackupUtil.backup(encryptedFiles, destFolder);

          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("備份完畢");
          alert.setHeaderText(null);
          alert.setContentText("備份已成功完成！");
          alert.showAndWait();
        } catch (IOException e) {
          log.warn(e.getMessage(), e);
        }
      }
    }
  }

  private List<File> retrieveAllZips(final String folderPath) {
    return RubyDir.glob(RubyFile.join(folderPath, "**", "*.zip"))
        .map(item -> new File(RubyFile.join(folderPath, item)))
        .toList();
  }

  private List<File> filterEncryptedFiles(List<File> files) {
    List<File> encryptedFiles = newArrayList();
    for (File file : files) {
      if (file.getName().matches("^.*\\.zip$")) {
        try {
          new EncryptedZip(file.getAbsolutePath(), zipPassword);
          encryptedFiles.add(file);
        } catch (Exception e) {
          log.warn(e.getMessage(), e);
        }
      }
    }
    return encryptedFiles;
  }

  private void recordProcessedFiles(Collection<String> fileNames) {
    for (String fileName : fileNames) {
      History history = new History();
      history.setFileName(fileName);
      Ebean.save(history);
    }
  }

  private void insertExcelRecords(List<Workbook> wbs) {
    for (Workbook wb : wbs) {
      Multimap<String, Map<String, String>> maps = Excel2Map.convert(wb);
      for (String sheet : maps.keySet()) {
        if (sheet.trim().matches("(?i)" + sheet + ".*")) {
          for (Map<String, String> row : maps.get(sheet)) {
            Pii pii = new Pii();
            for (String field : row.keySet()) {
              set(pii, field.toLowerCase(), row.get(field));
            }
            Ebean.save(pii);
          }
        }
      }
    }
  }

  private boolean set(Object object, String fieldName, Object fieldValue) {
    Class<?> clazz = object.getClass();
    while (clazz != null) {
      try {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, fieldValue);
        return true;
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      } catch (Exception e) {
        return false;
      }
    }
    return false;
  }

  private Map<String, InputStream> filterUnprocessedExcels(
      List<EncryptedZip> encryptedZips) {
    Map<String, InputStream> excels = newHashMap();
    try {
      List<String> allFiles = encryptedZips.stream()
          .flatMap(ez -> {
            try {
              return ez.getAllFileNames().stream();
            } catch (ZipException e) {
              throw new RuntimeException(e);
            }
          })
          .filter(fileName -> fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))
          .collect(Collectors.toList());

      // 使用 Java Streams 代替 RubyArray
      List<String> histories = Ebean.find(History.class).findList().stream()
          .map(History::getFileName)
          .collect(Collectors.toList());

      List<String> unprocessedFiles = allFiles.stream()
          .filter(fileName -> !histories.contains(fileName))
          .collect(Collectors.toList());

      for (EncryptedZip ez : encryptedZips) {
        for (String fileName : ez.getAllFileNames()) {
          if (unprocessedFiles.contains(fileName)) {
            excels.put(fileName, ez.getInputStreamByFileName(fileName));
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return excels;
  }


  private List<EncryptedZip> filterEncryptedZips(List<File> files) {
    List<EncryptedZip> encryptedZips = newArrayList();
    for (File file : files) {
      if (file.getName().matches("^.*\\.zip$")) {
        try {
          encryptedZips.add(new EncryptedZip(file.getAbsolutePath(),
              zipPassword));
        } catch (Exception e) {
          log.warn(e.getMessage());
        }
      }
    }
    return encryptedZips;
  }

  private void setDefaultPasswords() {
    if (Ebean.find(Authentication.class).findRowCount() != 0)
      return;

    Authentication auth1 = new Authentication();
    Authentication auth2 = new Authentication();
    auth1.setRole(ADMIN);
    auth2.setRole(ADMIN);
    auth1.setPassword(defaultPassword1);
    auth2.setPassword(defaultPassword2);
    Ebean.save(auth1);
    Ebean.save(auth2);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("Sheet", sheet)
        .toString();
  }

}
