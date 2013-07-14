package tw.edu.ym.guid.querier.api;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import static tw.edu.ym.guid.querier.api.QuerierResource.EXCELDB;

import exceldb.dao.HistoryMapper;
import exceldb.model.History;
import exceldb.model.HistoryExample;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;

public final class Histories {

  private static SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder()
      .build(EXCELDB.getResource());
  private static SqlSession sqlSession;

  private Histories() {}

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
