/**
 * 
 * @author Wei-Ming Wu
 * 
 * 
 *         Copyright 2012 Wei-Ming Wu
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

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public final class FolderTraverser {

  public static List<File> retrieveAllFiles(String path) {
    return retrieveAllFiles(new File(path), null);
  }

  public static List<File> retrieveAllFiles(String path, String ext) {
    return retrieveAllFiles(new File(path), ext);
  }

  public static List<File> retrieveAllFiles(File folder) {
    return retrieveAllFiles(folder, null);
  }

  public static List<File> retrieveAllFiles(File folder, String ext) {
    return traverseFolder(folder, ext);
  }

  private static List<File> traverseFolder(File file, String ext) {
    List<File> files = newArrayList();
    List<File> tempFiles = newArrayList(Arrays.asList(file.listFiles()));

    while (!(tempFiles.isEmpty())) {
      File item = tempFiles.remove(0);
      if (item.isDirectory()) {
        tempFiles.addAll(Arrays.asList(item.listFiles()));
      } else {
        if (ext == null)
          files.add(item);
        else if (item.getName().endsWith(ext))
          files.add(item);
      }
    }

    return files;
  }

}
