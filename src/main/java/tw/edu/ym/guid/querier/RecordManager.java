package tw.edu.ym.guid.querier;

import java.util.List;

/**
 * 
 * RecordManager defines general methods to manage documents.
 * 
 * @author Wei-Ming Wu
 * 
 * @param <E>
 *          the type of records
 */
public interface RecordManager<E> {

  /**
   * Verifies a user.
   * 
   * @param role
   *          type of user
   * @param password
   *          of user
   * @return true if verified, false otherwise
   */
  boolean authenticate(String role, String password);

  /**
   * Sets password for a user.
   * 
   * @param role
   *          type of user
   * @param oldPassword
   *          of user
   * @param newPassword
   *          of user
   */
  void setPassword(String role, String oldPassword, String newPassword);

  /**
   * Returns all header column names.
   * 
   * @return a List of header column names
   */
  List<String> getHeader();

  /**
   * Returns all records.
   * 
   * @return a List of records
   */
  List<E> findAll();

  /**
   * Returns all records with a limit.
   * 
   * @param limit
   *          max number of records
   * @return a List of records
   */
  List<E> findAll(int limit);

  /**
   * Updates a record.
   * 
   * @param record
   *          an element
   */
  void update(E record);

  /**
   * Returns records which are matched by given keywords.
   * 
   * @param keywords
   *          used to search records
   * @return a List of records
   */
  List<E> query(String... keywords);

  /**
   * Returns the number of all records.
   * 
   * @return the number of all records
   */
  int getNumberOfRecords();

  /**
   * Imports all documents within the folder.
   * 
   * @param path
   *          of a folder
   */
  void importExcels(String path);

  /**
   * Sets a folder to backup.
   * 
   * @param path
   *          of a folder
   */
  void setBackupFolder(String path);

  /**
   * Backups source files.
   */
  void backup();

  /**
   * Checks if a column is editable.
   * 
   * @param column
   *          name of a column
   * @return true if column is editable, false otherwise
   */
  boolean isColumnEditable(String column);

  void resetPassword(long id, String newPassword);
}
