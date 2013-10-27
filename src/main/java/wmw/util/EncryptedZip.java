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

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

public final class EncryptedZip {

  private final ZipFile zipFile;
  private final List<FileHeader> fileHeaders;

  @SuppressWarnings("unchecked")
  public EncryptedZip(String path, String password) throws ZipException,
      IOException {
    zipFile = new ZipFile(path);
    fileHeaders = zipFile.getFileHeaders();

    if (zipFile.isEncrypted()) {
      zipFile.setPassword(password);
      if (!(isPasswordValid(zipFile)))
        throw new IllegalArgumentException("Zip password is wrong.");
    } else {
      throw new IllegalStateException("Zip file is not encrypted.");
    }
  }

  private boolean isPasswordValid(ZipFile zip) {
    try {
      InputStream is = zip.getInputStream(fileHeaders.get(0));
      is.read();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public List<String> getAllFileNames() throws ZipException {
    List<String> fileNames = newArrayList();
    for (FileHeader fh : fileHeaders) {
      fileNames.add(fh.getFileName());
    }
    return fileNames;
  }

  public InputStream getInputStreamByFileName(String fileName)
      throws ZipException {
    for (FileHeader fh : fileHeaders) {
      if (fileName.equals(fh.getFileName()))
        return zipFile.getInputStream(fh);
    }
    return null;
  }

}
