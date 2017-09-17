package me.season.pdfconvert.util;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DecompressUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DecompressUtils.class);

    public static void unzip(String zipFile, String outputDir) throws IOException {
        File pathFile = new File(outputDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile, Charset.forName("gbk"));
        for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (outputDir + zipEntryName).replaceAll("\\*", "/");
            // create file if not exists
            File file = new File(outPath);
            if (!file.getParentFile().exists()) {
                file.mkdirs();
            }
            if (file.isDirectory()) {
                continue;
            }
            LOG.info(outPath);

            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
        LOG.info("******************Decompress Finish********************");
    }

    public static boolean unrar(String rarFile, String outputDir) throws Exception {
        boolean flag = false;
        try {
            Archive archive = new Archive(new File(rarFile));
            if (archive == null) {
                throw new FileNotFoundException(rarFile + " NOT FOUND!");
            }
            if (archive.isEncrypted()) {
                throw new Exception(rarFile + " IS ENCRYPTED!");
            }
            List<FileHeader> files = archive.getFileHeaders();
            for (FileHeader fh : files) {
                if (fh.isEncrypted()) {
                    throw new Exception(rarFile + " IS ENCRYPTED!");
                }
                String fileName = fh.getFileNameW();
                if (fileName != null && fileName.trim().length() > 0) {
                    String saveFileName = outputDir + "\\" + fileName;
                    File saveFile = new File(saveFileName);
                    File parent = saveFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    if (!saveFile.exists()) {
                        saveFile.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(saveFile);
                    try {
                        archive.extractFile(fh, fos);
                        fos.flush();
                        fos.close();
                    } catch (RarException e) {
                        if (e.getType().equals(RarException.RarExceptionType.notImplementedYet)) {
                        }
                    }
                }
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return flag;
    }
}
