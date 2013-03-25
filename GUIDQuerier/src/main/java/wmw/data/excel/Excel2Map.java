package wmw.data.excel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 
 * @author Wei-Ming Wu
 * 
 * 
 *         Copyright 2013 Wei-Ming Wu
 * 
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License.
 * 
 */
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
