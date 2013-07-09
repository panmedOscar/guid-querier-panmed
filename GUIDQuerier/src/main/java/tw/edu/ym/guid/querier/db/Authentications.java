package tw.edu.ym.guid.querier.db;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import exceldb.dao.AuthenticationMapper;
import exceldb.model.Authentication;
import exceldb.model.AuthenticationExample;

import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

public final class Authentications {

  private static SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder()
      .build(EXCELDB.getResource());
  private static SqlSession sqlSession;

  private Authentications() {}

  public static Authentication findByRoleAndPassword(String role,
      String password) {
    List<Authentication> auths = Collections.emptyList();

    try {
      sqlSession = sqlMapper.openSession();
      AuthenticationMapper authMap =
          sqlSession.getMapper(AuthenticationMapper.class);

      AuthenticationExample authEx = new AuthenticationExample();
      authEx.or().andRoleEqualTo(role).andPasswordEqualTo(password);
      auths = authMap.selectByExample(authEx);
    } finally {
      sqlSession.close();
    }

    return auths.isEmpty() ? null : auths.get(0);
  }

  public static void setAdminPassword(String role, String newpwd) {
    Authentication auth = new Authentication();
    auth.setRole(role);
    auth.setPassword(newpwd);

    try {
      sqlSession = sqlMapper.openSession();
      AuthenticationMapper authMap =
          sqlSession.getMapper(AuthenticationMapper.class);

      AuthenticationExample authEx = new AuthenticationExample();
      authEx.or().andRoleEqualTo(role);
      authMap.updateByExample(auth, authEx);

      sqlSession.commit();
    } finally {
      sqlSession.close();
    }
  }

}
