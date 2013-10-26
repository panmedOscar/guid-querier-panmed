package tw.edu.ym.guid.querier.api;

import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

import java.nio.charset.Charset;
import java.util.List;

import net.sf.rubycollect4j.RubyObject;

import org.apache.ibatis.session.SqlSessionFactory;

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

  public static void update(final Pii record) {
    new Piis().update(record, new Example<PiiExample>() {

      @Override
      public void set(PiiExample example) {
        example.or().and編碼日期EqualTo(record.get編碼日期())
            .andGuidEqualTo(record.getGuid());
      }

    });
  }

  /**
   * Searches Piis by given keywords.
   * 
   * @param keywords
   *          used to query
   * @return a List of Pii
   */
  public static List<Pii> globalSearch(final String... keywords) {
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
      RubyObject.send(c, "and" + capitalize(ef.toString()) + "EqualTo", value);
      piiEx.or(c);
    }
  }

  private static void buildLikeQuery(PiiExample piiEx, String value) {
    for (ExcelField ef : ExcelField.values()) {
      Criteria c = piiEx.createCriteria();
      RubyObject.send(c, "and" + capitalize(ef.toString()) + "Like", "%"
          + value + "%");
      piiEx.or(c);
    }
  }

  private static String capitalize(String word) {
    return word.toUpperCase().charAt(0) + word.toLowerCase().substring(1);
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
