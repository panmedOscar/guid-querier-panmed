package tw.edu.ym.guid.querier;

/**
 * 
 * ExcelField defines all fields that an ExcelManger needs to display and query.
 * Remembers to execute ExceldbGenerator each time after ExcelField has been
 * modified.
 * 
 * @author Wei-Ming Wu
 * 
 */
public enum ExcelField {

  編碼日期(false, false), GUID(false, false), MRN(false, true),
  身份證字號(false, false), 姓氏(false, false), 名字(false, false), 出生月(false, false),
  出生日(false, false), 出生年(false, false), 性別(false, false), 聯絡電話(false, true),
  地址(false, true), 收案醫師(false, true), 收案醫院名稱(false, true);

  private final boolean unique;
  private final boolean editable;

  private ExcelField(boolean unique, boolean editable) {
    this.unique = unique;
    this.editable = editable;
  }

  /**
   * Check if this field is unique.
   * 
   * @return true if this field is unique, false otherwise
   */
  public boolean isUnique() {
    return unique;
  }

  /**
   * Check if this field is editable.
   * 
   * @return true if this field is editable, false otherwise
   */
  public boolean isEditable() {
    return editable;
  }

  public static String orderBy() {
    return GUID.toString();
  }

}
