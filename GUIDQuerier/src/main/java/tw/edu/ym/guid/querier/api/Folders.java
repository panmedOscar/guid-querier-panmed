package tw.edu.ym.guid.querier.api;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import tw.edu.ym.guid.querier.mybatis.MybatisBlock;
import tw.edu.ym.guid.querier.mybatis.MybatisCRUD;
import exceldb.dao.FolderMapper;
import exceldb.model.Folder;
import exceldb.model.FolderExample;

import static com.google.common.collect.Lists.newArrayList;
import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

/**
 * 
 * Folders is an API class which contains lot of helpers of Folder model.
 * 
 * @author Wei-Ming Wu
 * 
 */
public enum Folders implements MybatisCRUD<Folder, FolderExample> {
  INSTANCE;

  /**
   * 
   * FolderType defines all usages of folder paths.
   * 
   * @author Wei-Ming Wu
   * 
   */
  public enum FolderType {
    IMPORT, BACKUP;
  }

  private static SqlSessionFactory sessionFactory =
      new SqlSessionFactoryBuilder().build(EXCELDB.getResource());
  private static SqlSession session;

  public static List<Folder> all() {
    return INSTANCE.select(new MybatisBlock<FolderExample>() {

      @Override
      public void yield(FolderExample example) {}

    });
  }

  /**
   * Returns the first record of the specified usage.
   * 
   * @param usage
   *          of the folder
   * @return a Folder
   */
  public static Folder findFirst(final FolderType usage) {
    List<Folder> folders = INSTANCE.select(new MybatisBlock<FolderExample>() {

      @Override
      public void yield(FolderExample example) {
        example.or().andUsageEqualTo(usage.toString());
      }

    });

    return folders.isEmpty() ? null : folders.get(0);
  }

  /**
   * Sets the path of specified usage in Folder table.
   * 
   * @param usage
   *          of the folder
   * @param path
   *          of the folder
   */
  public static void setFolderPath(final FolderType usage, String path) {
    boolean isPathExisted = findFirst(usage) != null;
    Folder folder = new Folder();
    folder.setUsage(usage.toString());
    folder.setPath(path);

    if (isPathExisted) {
      INSTANCE.update(folder, new MybatisBlock<FolderExample>() {

        @Override
        public void yield(FolderExample example) {
          example.or().andUsageEqualTo(usage.toString());
        }

      });
    } else {
      INSTANCE.insert(folder);
    }
  }

  /**
   * Removes the path of specified usage in Folder table.
   * 
   * @param usage
   *          of the folder
   */
  public static void removeFolderPath(final FolderType usage) {
    INSTANCE.delete(new MybatisBlock<FolderExample>() {

      @Override
      public void yield(FolderExample example) {
        example.or().andUsageEqualTo(usage.toString());
      }

    });
  }

  @Override
  public void insert(Folder record) {
    try {
      session = sessionFactory.openSession();
      FolderMapper mapper = session.getMapper(FolderMapper.class);
      mapper.insert(record);
      session.commit();
    } finally {
      if (session != null)
        session.close();
    }
  }

  @Override
  public List<Folder> select(MybatisBlock<FolderExample> block) {
    List<Folder> records = newArrayList();
    try {
      session = sessionFactory.openSession();
      FolderMapper mapper = session.getMapper(FolderMapper.class);
      FolderExample example = new FolderExample();
      block.yield(example);
      records = mapper.selectByExample(example);
    } finally {
      if (session != null)
        session.close();
    }
    return records;
  }

  @Override
  public void update(Folder record, MybatisBlock<FolderExample> block) {
    try {
      session = sessionFactory.openSession();
      FolderMapper mapper = session.getMapper(FolderMapper.class);
      FolderExample example = new FolderExample();
      block.yield(example);
      mapper.updateByExample(record, example);
      session.commit();
    } finally {
      if (session != null)
        session.close();
    }
  }

  @Override
  public void delete(MybatisBlock<FolderExample> block) {
    try {
      session = sessionFactory.openSession();
      FolderMapper mapper = session.getMapper(FolderMapper.class);
      FolderExample example = new FolderExample();
      block.yield(example);
      mapper.deleteByExample(example);
      session.commit();
    } finally {
      if (session != null)
        session.close();
    }
  }

  @Override
  public int count(MybatisBlock<FolderExample> block) {
    int count;
    try {
      session = sessionFactory.openSession();
      FolderMapper mapper = session.getMapper(FolderMapper.class);
      FolderExample example = new FolderExample();
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
