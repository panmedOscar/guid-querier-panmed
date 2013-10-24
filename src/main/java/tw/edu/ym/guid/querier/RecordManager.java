package tw.edu.ym.guid.querier;

import java.util.List;
import java.util.Map;

public interface RecordManager {

  boolean authenticate(String role, String password);

  void setPassword(String role, String oldPassword, String newPassword);

  String[] getHeader();

  List<Object[]> selectAll();

  List<Object[]> selectAll(int limit);

  void update(Map<String, String> record);

  List<Object[]> query(String... keywords);

  int totalRecord();

  void importExcels(String path);

  void setBackupFolder(String path);

  void backup();

  boolean isColumnEditableAt(int index);

}
