package tw.edu.ym.guid.querier;

/**
 * 
 * ExcelField defines all fields that an ExcelManger needs to display and query.
 * 
 * @author Wei-Ming Wu
 * 
 */
public enum ExcelField {
  編碼日期(false), GUID(false), MRN(false), 身份證字號(false), 姓氏(false), 名字(false),
  出生月(false), 出生日(false), 出生年(false), 聯絡電話(false), 性別(false), 收案醫師(false),
  收案醫院名稱(false);

  private final boolean unique;

  private ExcelField(Boolean unique) {
    this.unique = unique;
  }

  /**
   * Check if this field is unique.
   * 
   * @return true if this field is unique, false otherwise
   */
  public boolean isUnique() {
    return unique;
  }

}
