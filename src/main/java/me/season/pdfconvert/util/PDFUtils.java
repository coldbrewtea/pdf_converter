package me.season.pdfconvert.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PDFUtils {
    public static void mergePDF(String target, String... sources) {
        /*List<InputStream> pdfs = new ArrayList<>();
        for (String source : sources) {
            pdfs.add(new FileInputStream(source));
        }
        OutputStream output = new FileOutputStream(target);
        concatPDFs(pdfs, output, true);*/
        mergeFiles(target, sources, true);
    }

    public static void mergeFiles(String result, String[] files, boolean smart) {
        Document document = new Document();
        PdfCopy copy;
        try {
            if (smart)
                copy = new PdfSmartCopy(document, new FileOutputStream(result));
            else
                copy = new PdfCopy(document, new FileOutputStream(result));
            document.open();
            PdfReader[] reader = new PdfReader[3];
            for (int i = 0; i < files.length; i++) {
                reader[i] = new PdfReader(files[i]);
                copy.addDocument(reader[i]);
                copy.freeReader(reader[i]);
                reader[i].close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

    }


    private static void concatPDFs(List<InputStream> streamOfPDFFiles, OutputStream outputStream, boolean paginate) {
        Document document = new Document();
        try {
            List<InputStream> pdfs = streamOfPDFFiles;
            List<PdfReader> readers = new ArrayList<PdfReader>();
            int totalPages = 0;
            Iterator<InputStream> iteratorPDFs = pdfs.iterator();

            // Create Readers for the pdfs.
            while (iteratorPDFs.hasNext()) {
                InputStream pdf = iteratorPDFs.next();
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                totalPages += pdfReader.getNumberOfPages();
            }
            // Create a writer for the outputstream
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            document.open();
            PdfContentByte cb = writer.getDirectContent(); // Holds the PDF
            // data
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
                    BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

            PdfImportedPage page;
            int currentPageNumber = 0;
            int pageOfCurrentReaderPDF = 0;
            Iterator<PdfReader> iteratorPDFReader = readers.iterator();

            // Loop through the PDF files and add to the output.
            while (iteratorPDFReader.hasNext()) {
                PdfReader pdfReader = iteratorPDFReader.next();

                // Create a new page in the target for each source page.
                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
                    document.newPage();
                    pageOfCurrentReaderPDF++;
                    currentPageNumber++;

                    page = writer.getImportedPage(pdfReader, pageOfCurrentReaderPDF);
                    cb.addTemplate(page, 0, 0);

                    // Code for pagination.
                    if (paginate) {
                        cb.beginText();
                        cb.setFontAndSize(bf, 9);
                        cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
                                "" + currentPageNumber + " of " + totalPages,
                                520, 5, 0);
                        cb.endText();
                    }
                }
                pageOfCurrentReaderPDF = 0;
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (document.isOpen()) {
                    document.close();
                }
            } catch (Exception e) {
                // ignore
            }
            IOUtils.closeQuietly(outputStream);
            streamOfPDFFiles.forEach(e -> IOUtils.closeQuietly(e));
        }
    }
}
