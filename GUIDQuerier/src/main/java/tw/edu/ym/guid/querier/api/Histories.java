package tw.edu.ym.guid.querier.api;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import exceldb.dao.HistoryMapper;
import exceldb.model.History;
import exceldb.model.HistoryExample;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;
import static tw.edu.ym.guid.querier.api.QuerierResource.EXCELDB;

/**
 * 
 * Histories is an API class which contains lot of helpers of History model.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class Histories {

  private static SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder()
      .build(EXCELDB.getResource());
  private static SqlSession sqlSession;

  private Histories() {}

  /**
   * Returns all History records.
   * 
   * @return a List of History
   */
  public static List<History> all() {
    List<History> histories = emptyList();

    try {
      sqlSession = sqlMapper.openSession();
      HistoryMapper historyMap = sqlSession.getMapper(HistoryMapper.class);

      HistoryExample historyEx = new HistoryExample();
      historyEx.or().andFileNameIsNotNull();
      histories = historyMap.selectByExample(historyEx);
    } finally {
      sqlSession.close();
    }

    return histories;
  }

  /**
   * Adds a file name to the History.
   * 
   * @param fileName
   *          to be added to the History
   */
  public static void create(String fileName) {
    History record = new History();
    record.setFileName(fileName);

    try {
      sqlSession = sqlMapper.openSession();
      HistoryMapper historyMap = sqlSession.getMapper(HistoryMapper.class);
      historyMap.insert(record);
      sqlSession.commit();
    } finally {
      sqlSession.close();
    }
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
    Set<String> unprocessedFiles = newHashSet(files);

    try {
      sqlSession = sqlMapper.openSession();
      HistoryMapper historyMap = sqlSession.getMapper(HistoryMapper.class);

      HistoryExample historyEx = new HistoryExample();
      historyEx.or().andFileNameIsNotNull();
      List<History> histories = historyMap.selectByExample(historyEx);

      for (History history : histories)
        unprocessedFiles.remove(history.getFileName());
    } finally {
      sqlSession.close();
    }

    return unprocessedFiles;
  }

}
