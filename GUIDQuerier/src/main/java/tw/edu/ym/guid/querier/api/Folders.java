package tw.edu.ym.guid.querier.api;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import exceldb.dao.FolderMapper;
import exceldb.model.Folder;
import exceldb.model.FolderExample;

import static java.util.Collections.emptyList;
import static tw.edu.ym.guid.querier.api.QuerierResource.EXCELDB;

/**
 * 
 * Folders is an API class which contains lot of helpers of Folder model.
 * 
 * @author Wei-Ming Wu
 * 
 */
public final class Folders {

  private static SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder()
      .build(EXCELDB.getResource());
  private static SqlSession sqlSession;

  private Folders() {}

  public static Folder findFirst() {
    List<Folder> folders = emptyList();

    try {
      sqlSession = sqlMapper.openSession();
      FolderMapper folderMap = sqlSession.getMapper(FolderMapper.class);

      FolderExample folderEx = new FolderExample();
      folderEx.or().andPathIsNotNull();
      folders = folderMap.selectByExample(folderEx);
    } finally {
      sqlSession.close();
    }

    return folders.isEmpty() ? null : folders.get(0);
  }

  public static void setFolderPath(String path) {
    boolean isPathExisted = findFirst() != null;
    Folder folder = new Folder();
    folder.setPath(path);

    try {
      sqlSession = sqlMapper.openSession();
      FolderMapper folderMap = sqlSession.getMapper(FolderMapper.class);

      FolderExample folderEx = new FolderExample();
      folderEx.or().andPathIsNotNull();
      if (isPathExisted)
        folderMap.updateByExample(folder, folderEx);
      else
        folderMap.insert(folder);

      sqlSession.commit();
    } finally {
      sqlSession.close();
    }
  }

  public static void removeFolderPath() {
    try {
      sqlSession = sqlMapper.openSession();
      FolderMapper folderMap = sqlSession.getMapper(FolderMapper.class);

      FolderExample folderEx = new FolderExample();
      folderEx.or().andPathIsNotNull();
      folderMap.deleteByExample(folderEx);

      sqlSession.commit();
    } finally {
      sqlSession.close();
    }
  }

}
