package tw.edu.ym.guid.querier.api;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import static tw.edu.ym.guid.querier.db.QuerierResource.EXCELDB;

import exceldb.dao.FolderMapper;
import exceldb.model.Folder;
import exceldb.model.FolderExample;
import static java.util.Collections.emptyList;

/**
 * 
 * Folders is an API class which contains lot of helpers of Folder model.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class Folders {

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

  private static SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder()
      .build(EXCELDB.getResource());
  private static SqlSession sqlSession;

  private Folders() {}

  public static List<Folder> all() {
    List<Folder> folders = emptyList();

    try {
      sqlSession = sqlMapper.openSession();
      FolderMapper folderMap = sqlSession.getMapper(FolderMapper.class);

      FolderExample folderEx = new FolderExample();
      folderEx.or().andUsageIsNotNull();
      folderEx.or().andPathIsNotNull();
      folders = folderMap.selectByExample(folderEx);
    } finally {
      sqlSession.close();
    }

    return folders;
  }

  /**
   * Returns the first record of the specified usage.
   * 
   * @param usage
   *          of the folder
   * @return a Folder
   */
  public static Folder findFirst(FolderType usage) {
    List<Folder> folders = emptyList();

    try {
      sqlSession = sqlMapper.openSession();
      FolderMapper folderMap = sqlSession.getMapper(FolderMapper.class);

      FolderExample folderEx = new FolderExample();
      folderEx.or().andUsageEqualTo(usage.toString());
      folders = folderMap.selectByExample(folderEx);
    } finally {
      sqlSession.close();
    }

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
  public static void setFolderPath(FolderType usage, String path) {
    boolean isPathExisted = findFirst(usage) != null;
    Folder folder = new Folder();
    folder.setUsage(usage.toString());
    folder.setPath(path);

    try {
      sqlSession = sqlMapper.openSession();
      FolderMapper folderMap = sqlSession.getMapper(FolderMapper.class);

      FolderExample folderEx = new FolderExample();
      folderEx.or().andUsageEqualTo(usage.toString());
      if (isPathExisted)
        folderMap.updateByExample(folder, folderEx);
      else
        folderMap.insert(folder);

      sqlSession.commit();
    } finally {
      sqlSession.close();
    }
  }

  /**
   * Removes the path of specified usage in Folder table.
   * 
   * @param usage
   *          of the folder
   */
  public static void removeFolderPath(FolderType usage) {
    try {
      sqlSession = sqlMapper.openSession();
      FolderMapper folderMap = sqlSession.getMapper(FolderMapper.class);

      FolderExample folderEx = new FolderExample();
      folderEx.or().andUsageEqualTo(usage.toString());
      folderMap.deleteByExample(folderEx);

      sqlSession.commit();
    } finally {
      sqlSession.close();
    }
  }

}
