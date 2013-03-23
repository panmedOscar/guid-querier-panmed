package tw.edu.ym.guid.querier;

import static wmw.util.dir.FolderTraverser.retrieveAllFiles;
import static wmw.util.jdbc.Field.Varchar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.lingala.zip4j.exception.ZipException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ym.guid.querier.db.Histories;
import tw.edu.ym.guid.querier.db.Piis;
import wmw.data.embedded.EmbeddedStorage;
import wmw.data.excel.Excel2Map;
import wmw.data.zip.EncryptedZip;
import exceldb.model.Pii;

public final class ExcelManager {
  static final Logger logger = LoggerFactory.getLogger(ExcelManager.class);
  private static final String ZIP_PASSWORD = "H9z6gaYajuSA";
  private final EmbeddedStorage es;

  public ExcelManager() throws SQLException, ClassNotFoundException {
    es = new EmbeddedStorage("exceldb");
    initDatabase();
  }

  public String[] getHeader() {
    List<String> header = Collections.emptyList();
    try {
      header = es.getColumns("pii");
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
    return header.toArray(new String[header.size()]);
  }

  public List<Pii> getAll() {
    return Piis.all();
  }

  public List<String[]> getAll2ListOfStrAry() {
    List<String[]> listOfStrAry = new ArrayList<String[]>();

    for (Pii pii : Piis.all())
      listOfStrAry.add(new String[] { pii.getLocalId(), pii.getGuid(),
          pii.getMrn(), pii.get身份證字號(), pii.get姓氏(), pii.get名字(), pii.get出生月(),
          pii.get出生日(), pii.get出生年(), pii.get聯絡電話(), pii.get性別(),
          pii.get收案醫師(), pii.get醫院名稱() });

    return listOfStrAry;
  }

  public List<Object[]> selectAll() {
    try {
      return es.selectAll();
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
    return Collections.emptyList();
  }

  public List<Object[]> selectAll(int limit) {
    try {
      return es.selectAll(limit);
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
    return Collections.emptyList();
  }

  public List<Pii> query(String... values) {
    return Piis.globalSearch(values);
  }

  public List<String[]> query2ListOfStrAry(String... values) {
    List<String[]> listOfStrAry = new ArrayList<String[]>();

    for (Pii pii : Piis.globalSearch(values))
      listOfStrAry.add(new String[] { pii.getLocalId(), pii.getGuid(),
          pii.getMrn(), pii.get身份證字號(), pii.get姓氏(), pii.get名字(), pii.get出生月(),
          pii.get出生日(), pii.get出生年(), pii.get聯絡電話(), pii.get性別(),
          pii.get收案醫師(), pii.get醫院名稱() });

    return listOfStrAry;
  }

  public void importExcelsInFolder(String folder) {
    List<File> files = retrieveAllFiles(folder);
    List<EncryptedZip> encryptedZips = getEncryptedZips(files);
    Map<String, InputStream> excels = Collections.emptyMap();
    try {
      excels = getUnprocessedExcels(encryptedZips);
      insertExcelRecords(excels.values());
    } catch (ZipException e) {
      logger.error(e.getMessage());
    }
    recordProcessedFiles(excels.keySet());
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
          es.safeInsertRecords("pii", list);
      } catch (InvalidFormatException e) {
        logger.error(e.getMessage());
      } catch (IOException e) {
        logger.error(e.getMessage());
      } catch (SQLException e) {
        logger.error(e.getMessage());
      }
    }
  }

  private Map<String, InputStream> getUnprocessedExcels(
      List<EncryptedZip> encryptedZips) throws ZipException {
    List<String> allFiles = new ArrayList<String>();
    for (EncryptedZip ez : encryptedZips)
      for (String fileName : ez.getAllFileNames())
        if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))
          allFiles.add(fileName);

    Set<String> unprocessedFiles = Histories.filterUnprocessedFiles(allFiles);
    Map<String, InputStream> excels = new HashMap<String, InputStream>();
    for (EncryptedZip ez : encryptedZips)
      for (String fileName : ez.getAllFileNames())
        if (unprocessedFiles.contains(fileName))
          excels.put(fileName, ez.getInputStreamByFileName(fileName));
    return excels;
  }

  private List<EncryptedZip> getEncryptedZips(List<File> files) {
    List<EncryptedZip> encryptedZips = new ArrayList<EncryptedZip>();
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
    if (!(es.hasTable("pii"))) {
      es.createTable("pii", Varchar("Local_ID"), Varchar("GUID"),
          Varchar("MRN"), Varchar("身份證字號"), Varchar("姓氏"), Varchar("名字"),
          Varchar("出生月"), Varchar("出生日"), Varchar("出生年"), Varchar("聯絡電話"),
          Varchar("性別"), Varchar("收案醫師"), Varchar("醫院名稱"));
      es.unique("pii", "GUID");
      es.index("pii", "Local_ID");
      es.index("pii", "GUID");
      es.index("pii", "MRN");
      es.index("pii", "身份證字號");
      es.index("pii", "姓氏");
      es.index("pii", "名字");
      es.index("pii", "出生月");
      es.index("pii", "出生日");
      es.index("pii", "出生年");
      es.index("pii", "聯絡電話");
      es.index("pii", "性別");
      es.index("pii", "收案醫師");
      es.index("pii", "醫院名稱");
    }
    if (!(es.hasTable("history"))) {
      es.createTable("history", Varchar("file_name"));
      es.unique("history", "file_name");
      es.index("history", "file_name");
    }
  }

}
