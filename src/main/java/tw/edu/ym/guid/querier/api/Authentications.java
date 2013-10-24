package tw.edu.ym.guid.querier.api;

import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;

import wmw.mybatis.Example;
import wmw.mybatis.MyBatisBase;
import exceldb.dao.AuthenticationMapper;
import exceldb.model.Authentication;
import exceldb.model.AuthenticationExample;

/**
 * 
 * Authentications is an API class which contains lot of helpers of
 * Authentication model.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class Authentications extends
    MyBatisBase<Authentication, AuthenticationExample, AuthenticationMapper> {

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

  /**
   * Finds an Authentication record by given role and password.
   * 
   * @param role
   *          to be searched
   * @param password
   *          to be searched
   * @return an Authentication if found, null otherwise
   */
  public static Authentication findByRoleAndPassword(final String role,
      final String password) {
    List<Authentication> auths =
        new Authentications().select(new Example<AuthenticationExample>() {

          @Override
          public void build(AuthenticationExample example) {
            example.or().andRoleEqualTo(role).andPasswordEqualTo(password);
          }

        });

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
  public static void setPassword(final String role, final String oldPassword,
      String newPassword) {
    Authentication auth = new Authentication();
    auth.setRole(role.toString());
    auth.setPassword(newPassword);

    new Authentications().update(auth, new Example<AuthenticationExample>() {

      @Override
      public void build(AuthenticationExample example) {
        example.or().andRoleEqualTo(role).andPasswordEqualTo(oldPassword);
      }

    });
  }

  @Override
  protected SqlSessionFactory getSessionFactory() {
    return EXCELDB.getSessionFactory();
  }

  @Override
  protected Class<AuthenticationExample> getExampleClass() {
    return AuthenticationExample.class;
  }

  @Override
  protected Class<AuthenticationMapper> getMapperClass() {
    return AuthenticationMapper.class;
  }

}
