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

import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

import tw.edu.ym.guid.querier.ExcelField;
import exceldb.dao.PiiMapper;
import exceldb.model.Pii;
import exceldb.model.PiiExample;
import exceldb.model.PiiExample.Criteria;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;

/**
 * 
 * Piis is an API class which contains lot of helpers of Pii model.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class Piis {

  private static final Logger log = LoggerFactory.getLogger(Piis.class);

  private static SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder()
      .build(EXCELDB.getResource());
  private static SqlSession sqlSession;

  private Piis() {}

  /**
   * Returns the number of total records in Pii table.
   * 
   * @return the number of total records in Pii table
   */
  public static int count() {
    int count = 0;

    try {
      sqlSession = sqlMapper.openSession();
      PiiMapper piiMap = sqlSession.getMapper(PiiMapper.class);

      PiiExample piiEx = new PiiExample();
      count = piiMap.countByExample(piiEx);
    } finally {
      sqlSession.close();
    }

    return count;
  }

  /**
   * Returns all Piis.
   * 
   * @return a List of Pii
   */
  public static List<Pii> all() {
    List<Pii> piis = emptyList();

    try {
      sqlSession = sqlMapper.openSession();
      PiiMapper piiMap = sqlSession.getMapper(PiiMapper.class);

      PiiExample piiEx = new PiiExample();
      piis = piiMap.selectByExample(piiEx);
    } finally {
      sqlSession.close();
    }

    return piis;
  }

  public static Pii update(Map<String, String> record) {
    List<Pii> piis = emptyList();
    List<String> columns = newArrayList();
    Pii pii = new Pii();
    for (ExcelField field : ExcelField.values())
      columns.add(field.toString().toLowerCase());
    for (String key : record.keySet()) {
      if (columns.contains(key.toLowerCase()))
        setPiiProperty(pii, key, record.get(key));
    }

    if (pii.get編碼日期() == null || pii.getGuid() == null)
      return null;

    try {
      sqlSession = sqlMapper.openSession();
      PiiMapper piiMap = sqlSession.getMapper(PiiMapper.class);

      PiiExample piiEx = new PiiExample();
      piiEx.or().and編碼日期EqualTo(pii.get編碼日期()).andGuidEqualTo(pii.getGuid());
      piiMap.updateByExample(pii, piiEx);
      sqlSession.commit();
      piis = piiMap.selectByExample(piiEx);
    } finally {
      sqlSession.close();
    }

    return piis.isEmpty() ? null : piis.get(0);
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
  public static List<Pii> globalSearch(List<String> keywords) {
    List<Pii> piis = emptyList();

    try {
      sqlSession = sqlMapper.openSession();
      PiiMapper piiMap = sqlSession.getMapper(PiiMapper.class);

      PiiExample piiEx = new PiiExample();
      for (String keyword : keywords) {
        keyword = keyword.trim();
        if (keyword.getBytes(Charset.forName("UTF-8")).length < 3) {
          buildEqualToQuery(piiEx, keyword);
        } else {
          buildLikeQuery(piiEx, keyword);
        }
      }

      piiEx.setOrderByClause(ExcelField.orderBy());
      piis = piiMap.selectByExample(piiEx);
    } finally {
      sqlSession.close();
    }

    return piis;
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

}
