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
package wmw.util.bean;

import java.lang.reflect.Field;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public final class BeanConverter {

  private BeanConverter() {}

  public static String[] toStringArray(Object obj) {
    Field[] fields = obj.getClass().getDeclaredFields();
    String[] strings = new String[fields.length];
    for (int i = 0; i < fields.length; i++)
      try {
        fields[i].setAccessible(true);
        strings[i] =
            fields[i].get(obj) == null ? "" : fields[i].get(obj).toString();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    return strings;
  }

  public static List<String> toStringList(Object obj) {
    Field[] fields = obj.getClass().getDeclaredFields();
    List<String> strings = newArrayList();
    for (Field field : fields)
      try {
        field.setAccessible(true);
        strings.add(field.get(obj) == null ? "" : field.get(obj).toString());
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    return strings;
  }

  public static Object[] toObjectArray(Object obj) {
    Field[] fields = obj.getClass().getDeclaredFields();
    Object[] objects = new Object[fields.length];
    for (int i = 0; i < fields.length; i++)
      try {
        fields[i].setAccessible(true);
        objects[i] = fields[i].get(obj);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    return objects;
  }

  public static List<Object> toObjectList(Object obj) {
    Field[] fields = obj.getClass().getDeclaredFields();
    List<Object> objects = newArrayList();
    for (Field field : fields)
      try {
        field.setAccessible(true);
        objects.add(field.get(obj));
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    return objects;
  }

}
