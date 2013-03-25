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

import tw.edu.ym.guid.querier.db.Authentications;
import tw.edu.ym.guid.querier.db.Histories;
import tw.edu.ym.guid.querier.db.Piis;
import wmw.data.embedded.EmbeddedStorage;
import wmw.data.excel.Excel2Map;
import wmw.data.zip.EncryptedZip;
import wmw.util.jdbc.Field;
import exceldb.model.Authentication;
import exceldb.model.Pii;

/**
 * 
 * @author Wei-Ming Wu
 * 
 * 
 *         Copyright 2013 Wei-Ming Wu
 * 
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License.
 * 
 */
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

  public boolean authenticate(String role, String password) {
    Authentication auth = Authentications.findByRoleAndPassword(role, password);
    return auth != null;
  }

  public void setAdminPassword(String password) {
    Authentications.setAdminPassword(password);
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

  @SuppressWarnings("unchecked")
  private void initDatabase() throws SQLException {
    if (!(es.hasTable("pii"))) {
      Field[] fields = new Field[ExcelField.values().length];
      for (int i = 0; i < ExcelField.values().length; i++)
        fields[i] = Varchar(ExcelField.values()[i].toString());
      es.createTable("pii", fields);

      for (ExcelField ef : ExcelField.values()) {
        es.index("pii", ef.toString());
        if (ef.isUnique())
          es.unique("pii", ef.toString());
      }
    }
    if (!(es.hasTable("history"))) {
      es.createTable("history", Varchar("file_name"));
      es.unique("history", "file_name");
      es.index("history", "file_name");
    }
    if (!(es.hasTable("authentication"))) {
      es.createTable("authentication", Varchar("role"), Varchar("password"));
      Map<String, String> defaultPassword = new HashMap<String, String>();
      defaultPassword.put("role", "admin");
      defaultPassword.put("password", "0000");
      es.insertRecords("authentication", defaultPassword);
    }
  }

}
