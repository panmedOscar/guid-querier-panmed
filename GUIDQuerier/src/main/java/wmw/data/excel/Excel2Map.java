package wmw.data.excel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public final class Excel2Map {
  private Excel2Map() {}

  public static Map<String, List<Map<String, String>>> convert(Workbook wb) {
    Map<String, List<Map<String, String>>> map =
        new LinkedHashMap<String, List<Map<String, String>>>();

    for (int i = 0; i < wb.getNumberOfSheets(); i++) {
      Sheet sheet = wb.getSheetAt(i);

      Row header = sheet.getRow(0);
      List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
      for (Row row : sheet) {
        Map<String, String> record = new LinkedHashMap<String, String>();
        for (int j = 0; j < header.getLastCellNum(); j++)
          record.put(cell2Str(header.getCell(j)), cell2Str(row.getCell(j)));
        rows.add(record);
      }

      rows.remove(0);
      map.put(sheet.getSheetName(), rows);
    }

    return map;
  }

  private static String cell2Str(Cell cell) {
    String value = cell.toString();
    if (value.matches("^.*\\.0$"))
      return value.substring(0, value.lastIndexOf("."));
    return value;
  }

}
