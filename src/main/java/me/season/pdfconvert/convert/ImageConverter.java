package me.season.pdfconvert.convert;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import me.season.pdfconvert.service.ConverterManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by zhangshichen on 2017/8/3.
 */
@Component
public class ImageConverter implements Converter {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void init() {
        ConverterManager.register("jpg", this);
        ConverterManager.register("png", this);
        ConverterManager.register("gif", this);
    }

    public boolean convert(String source, String target) {
        File file = new File(source);
        if (!file.exists()) {
            return false;
        }
        Document document = new Document();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(target);
            PdfWriter.getInstance(document, fos);

            // 添加PDF文档的某些信息，比如作者，主题等等
//            document.addAuthor("root");
            document.addSubject(StringUtils.substringAfterLast(source, File.separator));
            // 设置文档的大小
            document.setPageSize(PageSize.A4);
            // 打开文档
            document.open();
            // 写入一段文字
            // document.add(new Paragraph("JUST TEST ..."));
            // 读取一个图片
            Image image = Image.getInstance(source);
            float imageHeight = image.getScaledHeight();
            float imageWidth = image.getScaledWidth();
            int i = 0;
            while (imageHeight > 500 || imageWidth > 500) {
                image.scalePercent(100 - i);
                i++;
                imageHeight = image.getScaledHeight();
                imageWidth = image.getScaledWidth();
//                System.out.println("imageHeight->" + imageHeight);
//                System.out.println("imageWidth->" + imageWidth);
            }

            image.setAlignment(Image.ALIGN_CENTER);
            // //设置图片的绝对位置
            // image.setAbsolutePosition(0, 0);
            // image.scaleAbsolute(500, 400);
            // 插入一个图片
            document.add(image);
        } catch (Exception e) {
            LOG.error("convert exception:", e);
            return false;
        } finally {
            document.close();
            IOUtils.closeQuietly(fos);
        }
        return true;
    }
}
