# pdf_converter
A web service or executable jar for converting to PDF file, supporting text,word,excel,ppt,image and compress package

# usage
 - Converting *Text(.txt) and Image(supporting .gif .jpg .png)* using [itextpdf](http://itextpdf.com)
 - Converting *Word(.doc, .docx), Excel(.xls, .xlsx), Powerpoint(.ppt, .pptx)* by calling Microsoft Office toolkit **SaveAs**, so the server needs to install Microsoft Offices. Ref. [JACOB](http://danadler.com/jacob/)
 - Converting *Compressed Package(supporting .zip .rar)* by decompressing and converting each file, then merge them into **ONE** PDF file.
 - supporting format means author has done some simple test and register into a suffix manager, you can add some code to support more file format.
