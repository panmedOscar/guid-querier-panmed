package wmw.mybatis;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import static com.google.common.collect.Lists.newArrayList;

abstract public class MybatisBase<T, E, M> implements MybatisCRUD<T, E> {

  abstract protected SqlSessionFactory getSessionFactory();

  abstract protected Class<E> getExampleClass();

  abstract protected Class<M> getMapperClass();

  @Override
  public void insert(T record) {
    SqlSession session = null;
    try {
      session = getSessionFactory().openSession();
      M mapper = session.getMapper(getMapperClass());
      Method[] methods = getMapperClass().getDeclaredMethods();
      for (Method method : methods) {
        if (method.getName().equals("insert"))
          method.invoke(mapper, record);
      }
      session.commit();
    } catch (Exception e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
    } finally {
      if (session != null)
        session.close();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<T> select(MybatisBlock<E> block) {
    List<T> records = newArrayList();
    SqlSession session = null;
    try {
      session = getSessionFactory().openSession();
      M mapper = session.getMapper(getMapperClass());
      E example = getExampleClass().newInstance();
      block.yield(example);
      Method[] methods = getMapperClass().getDeclaredMethods();
      for (Method method : methods) {
        if (method.getName().equals("selectByExample"))
          records = (List<T>) method.invoke(mapper, example);
      }
    } catch (Exception e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
    } finally {
      if (session != null)
        session.close();
    }
    return records;
  }

  @Override
  public void update(T record, MybatisBlock<E> block) {
    SqlSession session = null;
    try {
      session = getSessionFactory().openSession();
      M mapper = session.getMapper(getMapperClass());
      E example = getExampleClass().newInstance();
      block.yield(example);
      Method[] methods = getMapperClass().getDeclaredMethods();
      for (Method method : methods) {
        if (method.getName().equals("updateByExample"))
          method.invoke(mapper, record, example);
      }
      session.commit();
    } catch (Exception e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
    } finally {
      if (session != null)
        session.close();
    }
  }

  @Override
  public void delete(MybatisBlock<E> block) {
    SqlSession session = null;
    try {
      session = getSessionFactory().openSession();
      M mapper = session.getMapper(getMapperClass());
      E example = getExampleClass().newInstance();
      block.yield(example);
      Method[] methods = getMapperClass().getDeclaredMethods();
      for (Method method : methods) {
        if (method.getName().equals("deleteByExample"))
          method.invoke(mapper, example);
      }
      session.commit();
    } catch (Exception e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
    } finally {
      if (session != null)
        session.close();
    }
  }

  @Override
  public int count(MybatisBlock<E> block) {
    int count = -1;
    SqlSession session = null;
    try {
      session = getSessionFactory().openSession();
      M mapper = session.getMapper(getMapperClass());
      E example = getExampleClass().newInstance();
      block.yield(example);
      Method[] methods = getMapperClass().getDeclaredMethods();
      for (Method method : methods) {
        if (method.getName().equals("countByExample"))
          count = (Integer) method.invoke(mapper, example);
      }
    } catch (Exception e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
    } finally {
      if (session != null)
        session.close();
    }
    return count;
  }

}
