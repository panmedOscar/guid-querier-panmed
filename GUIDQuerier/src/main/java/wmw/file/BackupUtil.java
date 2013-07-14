package wmw.file;

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
