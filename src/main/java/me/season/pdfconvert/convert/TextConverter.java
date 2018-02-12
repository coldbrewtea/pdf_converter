package me.season.pdfconvert.convert;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import me.season.pdfconvert.service.ConverterManager;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by zhangshichen on 2017/8/3.
 * convert txt file using itextpdf
 * https://itextpdf.com
 */
@Component
public class TextConverter implements Converter {
    @PostConstruct
    public void init() {
        ConverterManager.register("default", this);
        ConverterManager.register("txt", this);
    }

    public boolean convert(String source, String target) {
        BufferedReader input = null;
        Document output = null;
        try {
            input = new BufferedReader(new InputStreamReader(new FileInputStream(source), "utf-8"));
            output = new Document(PageSize.A4);
            /*
             * 新建一个字体,iText的方法 STSongStd-Light 是字体，在iTextAsian.jar 中以property为后缀
             * UniGB-UCS2-H 是编码，在iTextAsian.jar 中以cmap为后缀 H 代表文字版式是 横版， 相应的 V 代表竖版
             */
            BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
            Font normal_fontChinese = new Font(bfChinese, 12, Font.NORMAL, BaseColor.BLACK);

            PdfWriter.getInstance(output, new FileOutputStream(target));
            output.open();

            String line;
            while (null != (line = input.readLine())) {
//                System.out.println(line);
                Paragraph p = new Paragraph(line, normal_fontChinese);
                p.setAlignment(Element.ALIGN_JUSTIFIED);
                output.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (output != null) {
                output.close();
            }
            IOUtils.closeQuietly(input);
        }
        return true;
    }
}
