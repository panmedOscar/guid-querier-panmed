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
package wmw.util;

import static com.google.common.collect.Maps.newLinkedHashMap;

import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * Excel2Map converts Apache POI Excel Workbook to an Google Guava Multimap.
 * 
 */
public final class Excel2Map {

  private Excel2Map() {}

  /**
   * Converts Apache POI Excel Workbook to a Multimap.
   * 
   * @param wb
   *          an Excel Workbook
   * @return a Multimap which contains all sheets of the excel
   */
  public static Multimap<String, Map<String, String>> convert(Workbook wb) {
    Multimap<String, Map<String, String>> map = ArrayListMultimap.create();

    for (int i = 0; i < wb.getNumberOfSheets(); i++) {
      Sheet sheet = wb.getSheetAt(i);

      Row header = null;
      Iterator<Row> rows = sheet.rowIterator();
      if (rows.hasNext())
        header = rows.next();

      while (rows.hasNext()) {
        Row row = rows.next();
        Map<String, String> record = newLinkedHashMap();
        for (int j = 0; j < header.getLastCellNum(); j++) {
          record.put(cell2Str(header.getCell(j)), cell2Str(row.getCell(j)));
        }
        map.put(sheet.getSheetName(), record);
      }
    }

    return map;
  }

  private static String cell2Str(Cell cell) {
    String value = cell.toString();
    if (value.matches("^\\d+\\.0$"))
      return value.substring(0, value.lastIndexOf("."));
    return value;
  }

}
