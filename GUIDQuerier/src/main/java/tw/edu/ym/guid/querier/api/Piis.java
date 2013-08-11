package tw.edu.ym.guid.querier.api;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ym.guid.querier.ExcelField;
import tw.edu.ym.guid.querier.mybatis.MybatisBlock;
import tw.edu.ym.guid.querier.mybatis.MybatisCRUD;
import exceldb.dao.PiiMapper;
import exceldb.model.Pii;
import exceldb.model.PiiExample;
import exceldb.model.PiiExample.Criteria;

import static com.google.common.collect.Lists.newArrayList;
import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

/**
 * 
 * Piis is an API class which contains lot of helpers of Pii model.
 * 
 * @author Wei-Ming Wu
 * 
 */
public enum Piis implements MybatisCRUD<Pii, PiiExample> {
  INSTANCE;

  private static final Logger log = LoggerFactory.getLogger(Piis.class);

  private static SqlSessionFactory sessionFactory =
      new SqlSessionFactoryBuilder().build(EXCELDB.getResource());
  private static SqlSession session;

  /**
   * Returns the number of total records in Pii table.
   * 
   * @return the number of total records in Pii table
   */
  public static int count() {
    return INSTANCE.count(new MybatisBlock<PiiExample>() {

      @Override
      public void yield(PiiExample example) {}

    });
  }

  /**
   * Returns all Piis.
   * 
   * @return a List of Pii
   */
  public static List<Pii> all() {
    return INSTANCE.select(new MybatisBlock<PiiExample>() {

      @Override
      public void yield(PiiExample example) {}

    });
  }

  public static Pii update(Map<String, String> record) {
    List<String> columns = newArrayList();
    final Pii pii = new Pii();
    for (ExcelField field : ExcelField.values())
      columns.add(field.toString().toLowerCase());
    for (String key : record.keySet()) {
      if (columns.contains(key.toLowerCase()))
        setPiiProperty(pii, key, record.get(key));
    }

    if (pii.get編碼日期() == null || pii.getGuid() == null)
      return null;

    INSTANCE.update(pii, new MybatisBlock<PiiExample>() {

      @Override
      public void yield(PiiExample example) {
        example.or().and編碼日期EqualTo(pii.get編碼日期())
            .andGuidEqualTo(pii.getGuid());
      }

    });

    return pii;
  }

  private static void setPiiProperty(Pii pii, String key, String value) {
    String capitalize =
        key.toUpperCase().charAt(0) + key.toLowerCase().substring(1);
    try {
      Method method =
          Pii.class.getDeclaredMethod("set" + capitalize, String.class);
      method.invoke(pii, value);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  /**
   * Searches Piis by given keywords.
   * 
   * @param keywords
   *          used to query
   * @return a List of Pii
   */
  public static List<Pii> globalSearch(String[] keywords) {
    return globalSearch(Arrays.asList(keywords));
  }

  /**
   * Searches Piis by given keywords.
   * 
   * @param keywords
   *          used to query
   * @return a List of Pii
   */
  public static List<Pii> globalSearch(final List<String> keywords) {
    return INSTANCE.select(new MybatisBlock<PiiExample>() {

      @Override
      public void yield(PiiExample example) {
        for (String keyword : keywords) {
          keyword = keyword.trim();
          if (keyword.getBytes(Charset.forName("UTF-8")).length < 3)
            buildEqualToQuery(example, keyword);
          else
            buildLikeQuery(example, keyword);
        }
        example.setOrderByClause(ExcelField.orderBy());
      }

    });
  }

  private static void buildEqualToQuery(PiiExample piiEx, String value) {
    for (ExcelField ef : ExcelField.values()) {
      Criteria c = piiEx.createCriteria();
      try {
        Method method =
            Criteria.class.getDeclaredMethod(equalToMethod(ef.toString()),
                String.class);
        method.invoke(c, value);
      } catch (Exception e) {
        log.error(e.getMessage());
      }
      piiEx.or(c);
    }
  }

  private static void buildLikeQuery(PiiExample piiEx, String value) {
    for (ExcelField ef : ExcelField.values()) {
      Criteria c = piiEx.createCriteria();
      try {
        Method method =
            Criteria.class.getDeclaredMethod(likeMethod(ef.toString()),
                String.class);
        method.invoke(c, "%" + value + "%");
      } catch (Exception e) {
        log.error(e.getMessage());
      }
      piiEx.or(c);
    }
  }

  private static String equalToMethod(String field) {
    String capitalize =
        field.toUpperCase().charAt(0) + field.toLowerCase().substring(1);
    return "and" + capitalize + "EqualTo";
  }

  private static String likeMethod(String field) {
    String capitalize =
        field.toUpperCase().charAt(0) + field.toLowerCase().substring(1);
    return "and" + capitalize + "Like";
  }

  @Override
  public void insert(Pii record) {
    try {
      session = sessionFactory.openSession();
      PiiMapper mapper = session.getMapper(PiiMapper.class);
      mapper.insert(record);
      session.commit();
    } finally {
      if (session != null)
        session.close();
    }
  }

  @Override
  public List<Pii> select(MybatisBlock<PiiExample> block) {
    List<Pii> records = newArrayList();
    try {
      session = sessionFactory.openSession();
      PiiMapper mapper = session.getMapper(PiiMapper.class);
      PiiExample example = new PiiExample();
      block.yield(example);
      records = mapper.selectByExample(example);
    } finally {
      if (session != null)
        session.close();
    }
    return records;
  }

  @Override
  public void update(Pii record, MybatisBlock<PiiExample> block) {
    try {
      session = sessionFactory.openSession();
      PiiMapper mapper = session.getMapper(PiiMapper.class);
      PiiExample example = new PiiExample();
      block.yield(example);
      mapper.updateByExample(record, example);
      session.commit();
    } finally {
      if (session != null)
        session.close();
    }
  }

  @Override
  public void delete(MybatisBlock<PiiExample> block) {
    try {
      session = sessionFactory.openSession();
      PiiMapper mapper = session.getMapper(PiiMapper.class);
      PiiExample example = new PiiExample();
      block.yield(example);
      mapper.deleteByExample(example);
      session.commit();
    } finally {
      if (session != null)
        session.close();
    }
  }

  @Override
  public int count(MybatisBlock<PiiExample> block) {
    int count;
    try {
      session = sessionFactory.openSession();
      PiiMapper mapper = session.getMapper(PiiMapper.class);
      PiiExample example = new PiiExample();
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
