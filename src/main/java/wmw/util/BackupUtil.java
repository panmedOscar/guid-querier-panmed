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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import static com.google.common.collect.Maps.newHashMap;

public final class BackupUtil {

  private BackupUtil() {}

  public static void backup(List<File> srcFiles, File destFolder)
      throws IOException {
    if (destFolder.isDirectory()) {
      Map<String, File> backupFiles = newHashMap();
      for (File file : destFolder.listFiles())
        backupFiles.put(file.getName(), file);

      for (File file : srcFiles) {
        File backup = backupFiles.get(file.getName());
        if (backup != null) {
          if (file.length() != backup.length())
            FileUtils.copyFileToDirectory(file, destFolder);
        } else {
          FileUtils.copyFileToDirectory(file, destFolder);
        }
      }
    }
  }

}
