package tw.edu.ym.guid.querier;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public enum ExcelField {
  編碼日期(false), GUID(false), MRN(false), 身份證字號(false), 姓氏(false), 名字(false),
  出生月(false), 出生日(false), 出生年(false), 聯絡電話(false), 性別(false), 收案醫師(false),
  收案醫院名稱(false);

  private final boolean unique;

  private ExcelField(Boolean unique) {
    this.unique = unique;
  }

  public boolean isUnique() {
    return unique;
  }

  public static String sheetName() {
    return "pii";
  }

  public static List<List<String>> multiColumnsIndexes() {
    ExcelField[][] multiColumnsIndexes =
        { { GUID, MRN, 身份證字號, 姓氏, 名字, 出生月, 出生日, 出生年, 聯絡電話, 性別, 收案醫師, 收案醫院名稱 } };
    List<List<String>> list = newArrayList();
    for (ExcelField[] multiColumnsIndex : multiColumnsIndexes) {
      List<String> fields = newArrayList();
      for (ExcelField ef : multiColumnsIndex)
        fields.add(ef.name());
    }
    return list;
  }

}
