package tw.edu.ym.guid.querier.api;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ym.guid.querier.ExcelField;
import exceldb.dao.PiiMapper;
import exceldb.model.Pii;
import exceldb.model.PiiExample;
import exceldb.model.PiiExample.Criteria;

import static java.util.Collections.emptyList;
import static tw.edu.ym.guid.querier.api.QuerierResource.EXCELDB;

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
      piiEx.or().andGuidIsNotNull();
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
      piiEx.or().andGuidIsNotNull();
      piis = piiMap.selectByExample(piiEx);
    } finally {
      sqlSession.close();
    }

    return piis;
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
