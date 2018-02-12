package me.season.pdfconvert.convert;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import me.season.pdfconvert.service.ConverterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Created by zhangshichen on 2017/8/3.
 * https://msdn.microsoft.com/en-us/library/office/ff747146(v=office.14).aspx
 */
@Component
public class PowerpointConverter implements Converter {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void init() {
        ConverterManager.register("ppt", this);
        ConverterManager.register("pptx", this);
    }

    public boolean convert(String source, String target) {
        ComThread.InitSTA();
        ActiveXComponent app = null;
        try {
            app = new ActiveXComponent("Powerpoint.Application");
            Dispatch presentations = app.getProperty("Presentations").toDispatch();
            Dispatch presentation = Dispatch.call(presentations, "Open", source, true).toDispatch();

            File tofile = new File(target);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.call(presentation, "SaveAs", target, pptSaveAsPDF);
            Dispatch.call(presentation, "Close");
        } catch (Exception e) {
            LOG.error("convert exception:", e);
            return false;
        } finally {
            if (app != null)
                app.invoke("Quit");
            ComThread.Release();
        }
        return true;
    }
}
