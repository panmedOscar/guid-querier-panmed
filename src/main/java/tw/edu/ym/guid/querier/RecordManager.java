package tw.edu.ym.guid.querier;

import java.util.List;

public interface RecordManager<E> {

  boolean authenticate(String role, String password);

  void setPassword(String role, String oldPassword, String newPassword);

  List<String> getHeader();

  List<E> findAll();

  List<E> findAll(int limit);

  void update(E record);

  List<E> query(String... keywords);

  int getNumberOfRecords();

  void importExcels(String path);

  void setBackupFolder(String path);

  void backup();

  boolean isColumnEditable(String column);

}
