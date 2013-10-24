package tw.edu.ym.guid.querier.api;

import static com.google.common.collect.Lists.newArrayList;
import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ym.guid.querier.ExcelField;
import wmw.db.mybatis.Example;
import wmw.db.mybatis.MyBatisBase;
import exceldb.dao.PiiMapper;
import exceldb.model.Pii;
import exceldb.model.PiiExample;
import exceldb.model.PiiExample.Criteria;

/**
 * 
 * Piis is an API class which contains lot of helpers of Pii model.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class Piis extends MyBatisBase<Pii, PiiExample, PiiMapper> {

  private static final Logger log = LoggerFactory.getLogger(Piis.class);

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

    new Piis().update(pii, new Example<PiiExample>() {

      @Override
      public void set(PiiExample example) {
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
    return new Piis().select(new Example<PiiExample>() {

      @Override
      public void set(PiiExample example) {
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
  protected SqlSessionFactory getSessionFactory() {
    return EXCELDB.getSessionFactory();
  }

  @Override
  protected Class<PiiExample> getExampleClass() {
    return PiiExample.class;
  }

  @Override
  protected Class<PiiMapper> getMapperClass() {
    return PiiMapper.class;
  }

}
