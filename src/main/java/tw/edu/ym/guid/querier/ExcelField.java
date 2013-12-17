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

  編碼日期(false), GUID(false), MRN(true), 身份證字號(false), 姓氏(false), 名字(false), 出生月(
      false), 出生日(false), 出生年(false), 性別(false), 聯絡電話(true), 地址(true), 收案醫師(
      true), 收案醫院名稱(true);

  private final boolean editable;

  private ExcelField(boolean editable) {
    this.editable = editable;
  }

  /**
   * Check if this field is editable.
   * 
   * @return true if this field is editable, false otherwise
   */
  public boolean isEditable() {
    return editable;
  }

  @Override
  public String toString() {
    return name().toLowerCase();
  }

}
