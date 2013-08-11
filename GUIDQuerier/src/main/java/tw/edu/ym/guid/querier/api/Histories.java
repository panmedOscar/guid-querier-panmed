package tw.edu.ym.guid.querier.api;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import tw.edu.ym.guid.querier.mybatis.MybatisBlock;
import tw.edu.ym.guid.querier.mybatis.MybatisCRUD;
import exceldb.dao.HistoryMapper;
import exceldb.model.History;
import exceldb.model.HistoryExample;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

/**
 * 
 * Histories is an API class which contains lot of helpers of History model.
 * 
 * @author Wei-Ming Wu
 * 
 */
public enum Histories implements MybatisCRUD<History, HistoryExample> {
  INSTANCE;

  private static SqlSessionFactory sessionFactory =
      new SqlSessionFactoryBuilder().build(EXCELDB.getResource());
  private static SqlSession session;

  /**
   * Returns all History records.
   * 
   * @return a List of History
   */
  public static List<History> all() {
    return INSTANCE.select(new MybatisBlock<HistoryExample>() {

      @Override
      public void yield(HistoryExample example) {}

    });
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

    INSTANCE.insert(record);
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
    List<History> histories =
        INSTANCE.select(new MybatisBlock<HistoryExample>() {

          @Override
          public void yield(HistoryExample example) {}

        });

    Set<String> unprocessedFiles = newHashSet(files);
    for (History history : histories)
      unprocessedFiles.remove(history.getFileName());

    return unprocessedFiles;
  }

  @Override
  public void insert(History record) {
    try {
      session = sessionFactory.openSession();
      HistoryMapper mapper = session.getMapper(HistoryMapper.class);
      mapper.insert(record);
      session.commit();
    } finally {
      if (session != null)
        session.close();
    }
  }

  @Override
  public List<History> select(MybatisBlock<HistoryExample> block) {
    List<History> records = newArrayList();
    try {
      session = sessionFactory.openSession();
      HistoryMapper mapper = session.getMapper(HistoryMapper.class);
      HistoryExample example = new HistoryExample();
      block.yield(example);
      records = mapper.selectByExample(example);
    } finally {
      if (session != null)
        session.close();
    }
    return records;
  }

  @Override
  public void update(History record, MybatisBlock<HistoryExample> block) {
    try {
      session = sessionFactory.openSession();
      HistoryMapper mapper = session.getMapper(HistoryMapper.class);
      HistoryExample example = new HistoryExample();
      block.yield(example);
      mapper.updateByExample(record, example);
      session.commit();
    } finally {
      if (session != null)
        session.close();
    }
  }

  @Override
  public void delete(MybatisBlock<HistoryExample> block) {
    try {
      session = sessionFactory.openSession();
      HistoryMapper mapper = session.getMapper(HistoryMapper.class);
      HistoryExample example = new HistoryExample();
      block.yield(example);
      mapper.deleteByExample(example);
      session.commit();
    } finally {
      if (session != null)
        session.close();
    }
  }

  @Override
  public int count(MybatisBlock<HistoryExample> block) {
    int count;
    try {
      session = sessionFactory.openSession();
      HistoryMapper mapper = session.getMapper(HistoryMapper.class);
      HistoryExample example = new HistoryExample();
      block.yield(example);
      count = mapper.countByExample(example);
      session.commit();
    } finally {
      if (session != null)
        session.close();
    }
    return count;
  }

}
