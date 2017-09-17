package me.season.pdfconvert.controller;

import me.season.pdfconvert.convert.ConvertException;
import me.season.pdfconvert.service.ConverterManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

@RestController
public class ConvertController {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private static final String TMP_DIR = "tmp";
    private static final String SUFFIX = ".pdf";

    @Autowired
    private ConverterManager converter;

    @RequestMapping(value = "/convert_pdf", method = RequestMethod.POST)
    public String convertHandler(@RequestParam MultipartFile file, HttpServletResponse request, HttpServletResponse response) {
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding("UTF-8");
        }
        String localPath = saveLocal(file);
        String target = localPath.substring(0, localPath.lastIndexOf(".")) + SUFFIX;
        LOG.info("source: " + localPath);
        LOG.info("target: " + target);

        InputStream input = null;
        OutputStream output = null;
        File pdfFile = null;
        try {
            boolean flag = converter.convert(localPath, target);
            if (!flag) {
                throw new ConvertException("fail to convert " + localPath);
            }

            pdfFile = new File(target);
            input = new FileInputStream(pdfFile);
            output = response.getOutputStream();
            IOUtils.copy(input, output);

            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + pdfFile.getName() + "\"");
            return "successfully convert to " + pdfFile.getName();
        } catch (Exception e) {
            response.setStatus(500);
            return "exception:" + e.getMessage();
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
            File localFile = new File(localPath);
            if (localFile != null) {
                localFile.delete();
            }
            if (pdfFile != null) {
                pdfFile.delete();
            }
        }
    }

    private String saveLocal(MultipartFile file) {
        InputStream input = null;
        String filename = file.getOriginalFilename();
        if (StringUtils.lastIndexOf(filename, "/") != -1) {
            filename = StringUtils.substringAfterLast(filename, "/");
        }
        if (StringUtils.lastIndexOf(filename, "\\") != -1) {
            filename = StringUtils.substringAfterLast(filename, "\\");
        }
        String localName = TMP_DIR + File.separator + new Date().getTime() + "_" + filename;
        try {
            input = file.getInputStream();
            File destination = new File(localName);
            FileUtils.copyToFile(input, destination);
            localName = destination.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(input);
        }
        return localName;
    }
}
