package tw.edu.ym.guid.querier.db;

import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import exceldb.dao.PiiMapper;
import exceldb.model.Pii;
import exceldb.model.PiiExample;

public final class Piis {
  private static SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder()
      .build(EXCELDB.getResource());
  private static SqlSession sqlSession;

  private Piis() {}

  public static List<Pii> all() {
    List<Pii> piis = Collections.emptyList();

    try {
      sqlSession = sqlMapper.openSession();
      PiiMapper historyMap = sqlSession.getMapper(PiiMapper.class);

      PiiExample piiEx = new PiiExample();
      piiEx.or().andGuidIsNotNull();
      piis = historyMap.selectByExample(piiEx);
    } finally {
      sqlSession.close();
    }

    return piis;
  }

  public static List<Pii> globalSearch(String[] values) {
    return globalSearch(Arrays.asList(values));
  }

  public static List<Pii> globalSearch(List<String> values) {
    List<Pii> piis = Collections.emptyList();

    try {
      sqlSession = sqlMapper.openSession();
      PiiMapper piiMap = sqlSession.getMapper(PiiMapper.class);

      PiiExample piiEx = new PiiExample();
      for (String value : values) {
        if (value.getBytes().length < 3) {
          piiEx.or().andLocalIdEqualTo(value);
          piiEx.or().andGuidEqualTo(value);
          piiEx.or().andMrnEqualTo(value);
          piiEx.or().and身份證字號EqualTo(value);
          piiEx.or().and姓氏EqualTo(value);
          piiEx.or().and名字EqualTo(value);
          piiEx.or().and出生月EqualTo(value);
          piiEx.or().and出生日EqualTo(value);
          piiEx.or().and出生年EqualTo(value);
          piiEx.or().and聯絡電話EqualTo(value);
          piiEx.or().and性別EqualTo(value);
          piiEx.or().and收案醫師EqualTo(value);
          piiEx.or().and醫院名稱EqualTo(value);
        } else {
          piiEx.or().andLocalIdLike("%" + value + "%");
          piiEx.or().andGuidLike("%" + value + "%");
          piiEx.or().andMrnLike("%" + value + "%");
          piiEx.or().and身份證字號Like("%" + value + "%");
          piiEx.or().and姓氏Like("%" + value + "%");
          piiEx.or().and名字Like("%" + value + "%");
          piiEx.or().and出生月Like("%" + value + "%");
          piiEx.or().and出生日Like("%" + value + "%");
          piiEx.or().and出生年Like("%" + value + "%");
          piiEx.or().and聯絡電話Like("%" + value + "%");
          piiEx.or().and性別Like("%" + value + "%");
          piiEx.or().and收案醫師Like("%" + value + "%");
          piiEx.or().and醫院名稱Like("%" + value + "%");
        }
      }

      piis = piiMap.selectByExample(piiEx);
    } finally {
      sqlSession.close();
    }

    return piis;
  }
}
