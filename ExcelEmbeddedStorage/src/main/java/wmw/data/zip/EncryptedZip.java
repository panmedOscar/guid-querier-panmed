package wmw.data.zip;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

public final class EncryptedZip {
  private final ZipFile zipFile;
  private final List<FileHeader> fileHeaders;

  @SuppressWarnings("unchecked")
  public EncryptedZip(String path, String password) throws ZipException {
    zipFile = new ZipFile(path);

    if (zipFile.isEncrypted())
      zipFile.setPassword(password);
    else
      throw new IllegalArgumentException("Zip file is not encrypted.");

    fileHeaders = zipFile.getFileHeaders();
  }

  public List<String> getAllFileNames() throws ZipException {
    List<String> fileNames = new ArrayList<String>();
    for (FileHeader fh : fileHeaders)
      fileNames.add(fh.getFileName());
    return fileNames;
  }

  public InputStream getInputStreamByFileName(String fileName)
      throws ZipException {
    for (FileHeader fh : fileHeaders)
      if (fileName.equals(fh.getFileName()))
        return zipFile.getInputStream(fh);
    return null;
  }

  public static void main(String[] args) throws ZipException {
    EncryptedZip ez =
        new EncryptedZip(
            "/Users/WMW/Documents/workspace/ExcelEmbeddedStorage/PII_20130319.zip",
            "H9z6gaYajuSA");
    System.out.println(ez.getAllFileNames());
    System.out.println(ez.getInputStreamByFileName("PII_20130319151710.xls"));
  }
}
