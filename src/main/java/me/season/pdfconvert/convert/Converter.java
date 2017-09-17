package me.season.pdfconvert.convert;

/**
 * Created by zhangshichen on 2017/8/3.
 */
public interface Converter {
    int wdNotSaveChanges = 0;// word 不保存待定的更改。
    int xlDoNotSaveChanges = 2; // xls不保存修改
    int docSaveAsPDF = 17;// word转PDF 格式
    int pptSaveAsPDF = 32;// ppt 转PDF 格式
    int xlsSaveAsPDF = 57;

    boolean convert(String source, String target);
}
