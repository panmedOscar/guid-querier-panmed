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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

public final class EncryptedZip {

  private final ZipFile zipFile;
  private final List<FileHeader> fileHeaders;

  @SuppressWarnings("unchecked")
  public EncryptedZip(String path, String password) throws ZipException,
      IOException {
    zipFile = new ZipFile(path, password.toCharArray());

    fileHeaders = zipFile.getFileHeaders();

    if (zipFile.isEncrypted()) {
      validatePassword();
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
    return fileHeaders.stream()
        .map(FileHeader::getFileName)
        .collect(Collectors.toList());
  }

  public InputStream getInputStreamByFileName(String fileName) throws ZipException, IOException {
    FileHeader fileHeader = zipFile.getFileHeader(fileName);
    if (fileHeader == null) {
      throw new ZipException("File not found: " + fileName);
    }
    return zipFile.getInputStream(fileHeader);
  }

  /**
   * 驗證密碼是否正確。
   *
   * @throws ZipException 如果密碼錯誤。
   * @throws IOException   如果讀取檔案時發生 I/O 錯誤。
   */
  private void validatePassword() throws ZipException, IOException {
    if (!fileHeaders.isEmpty()) {
      FileHeader firstFileHeader = fileHeaders.get(0);
      try (InputStream is = zipFile.getInputStream(firstFileHeader)) {
        byte[] buffer = new byte[1];
        if (is.read(buffer) == -1) {
          throw new ZipException("Cannot validate password: first file is empty.");
        }
      } catch (ZipException e) {
        throw new IllegalArgumentException("Zip password is wrong.", e);
      }
    } else {
      throw new ZipException("Zip file is empty.");
    }
  }

}
