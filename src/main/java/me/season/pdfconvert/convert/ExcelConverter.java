package me.season.pdfconvert.convert;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import me.season.pdfconvert.service.ConverterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by zhangshichen on 2017/8/3.
 */
@Component
public class ExcelConverter implements Converter {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void init() {
        ConverterManager.register("xls", this);
        ConverterManager.register("xlsx", this);
    }

    public boolean convert(String source, String target) {
        ComThread.InitSTA();
        ActiveXComponent app = null;
        try {
            app = new ActiveXComponent("Excel.Application");
            app.setProperty("Visible", false);
            app.setProperty("DisplayAlerts", false);

            Dispatch workbooks = app.getProperty("Workbooks").toDispatch();
            Dispatch workbook = Dispatch.invoke(workbooks, "Open", Dispatch.Method,
                    new Object[]{source, new Variant(true), new Variant(true)}, new int[1]).toDispatch();
            Dispatch.put(workbook, "CheckCompatibility", false);

            Dispatch sheets = Dispatch.get(workbook, "sheets").toDispatch();
            setPrintArea(sheets);
            int count = Dispatch.get(sheets, "count").getInt();
            for (int i = 1; i <= count; i++) {
                Dispatch sheet = Dispatch.invoke(sheets, "Item", Dispatch.Get, new Object[]{i}, new int[1]).toDispatch();
                Dispatch.call(sheet, "Select", false);
            }

            Dispatch.call(workbook, "SaveAs", target, xlsSaveAsPDF);
            Dispatch.call(workbook, "Close", false);
        } catch (Exception e) {
            LOG.error("convert exception:", e);
            return false;
        } finally {
            if (app != null) {
                try {
                    app.invoke("Quit");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ComThread.Release();
        }
        return true;
    }

    /*
     *  为每个表设置打印区域
     */
    private void setPrintArea(Dispatch sheets) {
        int count = Dispatch.get(sheets, "count").changeType(Variant.VariantInt).getInt();
        for (int i = count; i >= 1; i--) {
            Dispatch sheet = Dispatch.invoke(sheets, "Item",
                    Dispatch.Get, new Object[]{i}, new int[1]).toDispatch();
            Dispatch page = Dispatch.get(sheet, "PageSetup").toDispatch();
            Dispatch.put(page, "PrintArea", false);
            Dispatch.put(page, "Orientation", 2);
            Dispatch.put(page, "Zoom", 100);      //值为100或false
            Dispatch.put(page, "FitToPagesTall", false);  //所有行为一页
            Dispatch.put(page, "FitToPagesWide", 1);      //所有列为一页(1或false)
        }
    }
}
