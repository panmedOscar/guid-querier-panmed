package tw.edu.ym.guid.querier.api;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import tw.edu.ym.guid.querier.mybatis.MybatisBlock;
import tw.edu.ym.guid.querier.mybatis.MybatisCRUD;
import exceldb.dao.AuthenticationMapper;
import exceldb.model.Authentication;
import exceldb.model.AuthenticationExample;

import static com.google.common.collect.Lists.newArrayList;
import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

/**
 * 
 * Authentications is an API class which contains lot of helpers of
 * Authentication model.
 * 
 * @author Wei-Ming Wu
 * 
 */
public enum Authentications implements
    MybatisCRUD<Authentication, AuthenticationExample> {
  INSTANCE;

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

  private static SqlSessionFactory sessionFactory =
      new SqlSessionFactoryBuilder().build(EXCELDB.getResource());
  private static SqlSession session;

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
        INSTANCE.select(new MybatisBlock<AuthenticationExample>() {

          @Override
          public void yield(AuthenticationExample example) {
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

    INSTANCE.update(auth, new MybatisBlock<AuthenticationExample>() {

      @Override
      public void yield(AuthenticationExample example) {
        example.or().andRoleEqualTo(role).andPasswordEqualTo(oldPassword);
      }

    });
  }

  @Override
  public void insert(Authentication record) {
    try {
      session = sessionFactory.openSession();
      AuthenticationMapper mapper =
          session.getMapper(AuthenticationMapper.class);
      mapper.insert(record);
      session.commit();
    } finally {
      if (session != null)
        session.close();
    }
  }

  @Override
  public List<Authentication> select(MybatisBlock<AuthenticationExample> block) {
    List<Authentication> records = newArrayList();
    try {
      session = sessionFactory.openSession();
      AuthenticationMapper mapper =
          session.getMapper(AuthenticationMapper.class);
      AuthenticationExample example = new AuthenticationExample();
      block.yield(example);
      records = mapper.selectByExample(example);
    } finally {
      if (session != null)
        session.close();
    }
    return records;
  }

  @Override
  public void update(Authentication record,
      MybatisBlock<AuthenticationExample> block) {
    try {
      session = sessionFactory.openSession();
      AuthenticationMapper mapper =
          session.getMapper(AuthenticationMapper.class);
      AuthenticationExample example = new AuthenticationExample();
      block.yield(example);
      mapper.updateByExample(record, example);
      session.commit();
    } finally {
      if (session != null)
        session.close();
    }
  }

  @Override
  public void delete(MybatisBlock<AuthenticationExample> block) {
    try {
      session = sessionFactory.openSession();
      AuthenticationMapper mapper =
          session.getMapper(AuthenticationMapper.class);
      AuthenticationExample example = new AuthenticationExample();
      block.yield(example);
      mapper.deleteByExample(example);
      session.commit();
    } finally {
      if (session != null)
        session.close();
    }
  }

  @Override
  public int count(MybatisBlock<AuthenticationExample> block) {
    int count;
    try {
      session = sessionFactory.openSession();
      AuthenticationMapper mapper =
          session.getMapper(AuthenticationMapper.class);
      AuthenticationExample example = new AuthenticationExample();
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
