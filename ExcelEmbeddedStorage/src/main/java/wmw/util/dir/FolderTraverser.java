package wmw.util.dir;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class FolderTraverser {

  public static List<File> retrieveAllFiles(String path) {
    return retrieveAllFiles(new File(path));
  }

  public static List<File> retrieveAllFiles(File folder) {
    List<File> files = new ArrayList<File>();
    files = traverseFolder(folder);
    return files;
  }

  // private static void traverseFolder(File file, List<File> files) {
  // if (file.isFile())
  // files.add(file);
  // else
  // for (File f : file.listFiles())
  // traverseFolder(f, files);
  // }

  private static List<File> traverseFolder(File file) {
    List<File> files = new ArrayList<File>();
    List<File> tempFiles = new ArrayList<File>(Arrays.asList(file.listFiles()));

    while (!(tempFiles.isEmpty())) {
      File item = tempFiles.remove(0);
      if (item.isDirectory())
        tempFiles.addAll(Arrays.asList(item.listFiles()));
      else
        files.add(item);
    }

    return files;
  }

}
