package tw.edu.ym.guid.querier.api;

import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;

import wmw.db.mybatis.Example;
import wmw.db.mybatis.MyBatisBase;
import exceldb.dao.FolderMapper;
import exceldb.model.Folder;
import exceldb.model.FolderExample;

/**
 * 
 * Folders is an API class which contains lot of helpers of Folder model.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class Folders extends
    MyBatisBase<Folder, FolderExample, FolderMapper> {

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

  /**
   * Returns the first record of the specified usage.
   * 
   * @param usage
   *          of the folder
   * @return a Folder
   */
  public static Folder findFirst(final FolderType usage) {
    List<Folder> folders = new Folders().select(new Example<FolderExample>() {

      @Override
      public void set(FolderExample example) {
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
      new Folders().update(folder, new Example<FolderExample>() {

        @Override
        public void set(FolderExample example) {
          example.or().andUsageEqualTo(usage.toString());
        }

      });
    } else {
      new Folders().insert(folder);
    }
  }

  /**
   * Removes the path of specified usage in Folder table.
   * 
   * @param usage
   *          of the folder
   */
  public static void removeFolderPath(final FolderType usage) {
    new Folders().delete(new Example<FolderExample>() {

      @Override
      public void set(FolderExample example) {
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
