package tw.edu.ym.guid.querier;

public enum ExcelField {
  Local_ID(false), GUID(false), MRN(false), 身份證字號(false), 姓氏(false), 名字(false),
  出生月(false), 出生日(false), 出生年(false), 聯絡電話(false), 性別(false), 收案醫師(false),
  收案醫院名稱(false);

  private final boolean unique;

  private ExcelField(Boolean unique) {
    this.unique = unique;
  }

  public boolean isUnique() {
    return unique;
  }

  public static String[] allFields() {
    String[] fields = new String[values().length];
    for (int i = 0; i < values().length; i++)
      fields[i] = values()[i].name();
    return fields;
  }

}
