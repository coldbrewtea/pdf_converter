package me.season.pdfconvert.convert;

import me.season.pdfconvert.service.ConverterManager;
import me.season.pdfconvert.util.DecompressUtils;
import me.season.pdfconvert.util.PDFUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class CompressConverter implements Converter {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ConverterManager converter;

    @PostConstruct
    public void init() {
        ConverterManager.register("zip", this);
        ConverterManager.register("rar", this);
    }

    @Override
    public boolean convert(String source, String target) {
        String outputDir = source.substring(0, source.lastIndexOf(".")) + File.separator;
        try {
            if (StringUtils.endsWithIgnoreCase(source, ".zip")) {
                DecompressUtils.unzip(source, outputDir);
            } else if (StringUtils.endsWithIgnoreCase(source, ".rar")) {
                DecompressUtils.unrar(source, outputDir);
            } else {
                return false;
            }
            File dirFile = new File(outputDir);
            if (!dirFile.exists()) {
                System.err.println(dirFile.getName() + " directory not exists");
                return false;
            }
            List<String> pdfList = new ArrayList<>();
            for (File file : dirFile.listFiles()) {
                if (StringUtils.endsWithIgnoreCase(file.getName(), ".pdf")) {
                    pdfList.add(file.getAbsolutePath());
                    continue;
                }
                String pdfPath = file.getAbsolutePath() + ".pdf";
                System.out.println("tmp pdf path:" + pdfPath);
                if (converter.convert(file.getAbsolutePath(), pdfPath)) {
                    pdfList.add(pdfPath);
                }
            }
            PDFUtils.mergePDF(target, pdfList.stream().toArray(String[]::new));
        } catch (Exception e) {
            LOG.error("convert exception:", e);
            return false;
        } finally {
            FileUtils.deleteQuietly(new File(outputDir));
        }
        return true;
    }

}
