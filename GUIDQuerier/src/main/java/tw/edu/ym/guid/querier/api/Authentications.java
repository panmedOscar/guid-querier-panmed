package tw.edu.ym.guid.querier.api;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import exceldb.dao.AuthenticationMapper;
import exceldb.model.Authentication;
import exceldb.model.AuthenticationExample;

import static java.util.Collections.emptyList;
import static tw.edu.ym.guid.querier.api.QuerierResource.EXCELDB;

/**
 * 
 * Authentications is an API class which contains lot of helpers of
 * Authentication model.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class Authentications {

  /**
   * 
   * RoleType defines all roles of Authentication.
   * 
   * @author Wei-Ming Wu
   * 
   */
  public enum RoleType {
    ADMIN;
  }

  private static SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder()
      .build(EXCELDB.getResource());
  private static SqlSession sqlSession;

  private Authentications() {}

  /**
   * Finds an Authentication record by given role and password.
   * 
   * @param role
   *          to be searched
   * @param password
   *          to be searched
   * @return an Authentication if found, null otherwise
   */
  public static Authentication findByRoleAndPassword(String role,
      String password) {
    List<Authentication> auths = emptyList();

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

  /**
   * Sets a new password to existed role.
   * 
   * @param role
   *          to be reset
   * @param newPassword
   *          to set
   */
  public static void setPassword(String role, String oldPassword,
      String newPassword) {
    Authentication auth = new Authentication();
    auth.setRole(role.toString());
    auth.setPassword(newPassword);

    try {
      sqlSession = sqlMapper.openSession();
      AuthenticationMapper authMap =
          sqlSession.getMapper(AuthenticationMapper.class);

      AuthenticationExample authEx = new AuthenticationExample();
      authEx.or().andRoleEqualTo(role).andPasswordEqualTo(oldPassword);
      authMap.updateByExample(auth, authEx);

      sqlSession.commit();
    } finally {
      sqlSession.close();
    }
  }

}
