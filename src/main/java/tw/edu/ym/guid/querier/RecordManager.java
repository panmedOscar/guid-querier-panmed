package tw.edu.ym.guid.querier;

import java.util.List;

public interface RecordManager<E> {

  boolean authenticate(String role, String password);

  void setPassword(String role, String oldPassword, String newPassword);

  String[] getHeader();

  List<E> findAll();

  List<E> findAll(int limit);

  void update(E record);

  List<E> query(Iterable<String> keywords);

  int totalRecord();

  void importExcels(String path);

  void setBackupFolder(String path);

  void backup();

  boolean isColumnEditableAt(int index);

}
