package tw.edu.ym.guid.querier.api;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;

import wmw.mybatis.MybatisBase;
import wmw.mybatis.MybatisBlock;
import exceldb.dao.FolderMapper;
import exceldb.model.Folder;
import exceldb.model.FolderExample;

import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

/**
 * 
 * Folders is an API class which contains lot of helpers of Folder model.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class Folders extends
    MybatisBase<Folder, FolderExample, FolderMapper> {

  private static final Folders INSTANCE = new Folders();

  private Folders() {}

  public Folders getInstance() {
    return INSTANCE;
  }

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
  protected SqlSessionFactory getSessionFactory() {
    return EXCELDB.getSessionFactory();
  }

  @Override
  protected Class<FolderExample> getExampleClass() {
    return FolderExample.class;
  }

  @Override
  protected Class<FolderMapper> getMapperClass() {
    return FolderMapper.class;
  }

}
