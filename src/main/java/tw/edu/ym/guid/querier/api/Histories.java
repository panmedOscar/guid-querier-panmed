package tw.edu.ym.guid.querier.api;

import static com.google.common.collect.Sets.newHashSet;
import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.SqlSessionFactory;

import wmw.db.mybatis.MyBatisBase;
import exceldb.dao.HistoryMapper;
import exceldb.model.History;
import exceldb.model.HistoryExample;

/**
 * 
 * Histories is an API class which contains lot of helpers of History model.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class Histories extends
    MyBatisBase<History, HistoryExample, HistoryMapper> {

  /**
   * Adds a file name to the History.
   * 
   * @param fileName
   *          to be added to the History
   */
  public static void create(String fileName) {
    History record = new History();
    record.setFileName(fileName);
    new Histories().insert(record);
  }

  /**
   * Compares a List of file names with records of History table and returns an
   * Set of unprocessed file names.
   * 
   * @param files
   *          to be filtered
   * @return a Set of unprocessed file names
   */
  public static Set<String> filterUnprocessedFiles(List<String> files) {
    List<History> histories = new Histories().selectAll();
    Set<String> unprocessedFiles = newHashSet(files);
    for (History history : histories) {
      unprocessedFiles.remove(history.getFileName());
    }
    return unprocessedFiles;
  }

  @Override
  protected SqlSessionFactory getSessionFactory() {
    return EXCELDB.getSessionFactory();
  }

  @Override
  protected Class<HistoryExample> getExampleClass() {
    return HistoryExample.class;
  }

  @Override
  protected Class<HistoryMapper> getMapperClass() {
    return HistoryMapper.class;
  }

}
