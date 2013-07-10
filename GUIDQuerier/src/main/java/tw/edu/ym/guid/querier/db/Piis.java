package tw.edu.ym.guid.querier.db;

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
import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

public final class Piis {

  static final Logger logger = LoggerFactory.getLogger(Piis.class);

  private static SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder()
      .build(EXCELDB.getResource());
  private static SqlSession sqlSession;

  private Piis() {}

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

  public static List<Pii> globalSearch(String[] values) {
    return globalSearch(Arrays.asList(values));
  }

  public static List<Pii> globalSearch(List<String> values) {
    List<Pii> piis = emptyList();

    try {
      sqlSession = sqlMapper.openSession();
      PiiMapper piiMap = sqlSession.getMapper(PiiMapper.class);

      PiiExample piiEx = new PiiExample();
      for (String value : values) {
        value = value.trim();
        if (value.getBytes(Charset.forName("UTF-8")).length < 3) {
          for (ExcelField ef : ExcelField.values()) {
            Criteria c = piiEx.createCriteria();
            try {
              Method method =
                  Criteria.class.getDeclaredMethod(
                      equalToMethod(ef.toString()), String.class);
              method.invoke(c, value);
            } catch (Exception e) {
              logger.error(e.getMessage());
            }
            piiEx.or(c);
          }
        } else {
          for (ExcelField ef : ExcelField.values()) {
            Criteria c = piiEx.createCriteria();
            try {
              Method method =
                  Criteria.class.getDeclaredMethod(likeMethod(ef.toString()),
                      String.class);
              method.invoke(c, "%" + value + "%");
            } catch (Exception e) {
              logger.error(e.getMessage());
            }
            piiEx.or(c);
          }
        }
      }

      piis = piiMap.selectByExample(piiEx);
    } finally {
      sqlSession.close();
    }

    return piis;
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
