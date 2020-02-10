package com.mycompany.letrerosgen;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import static com.itextpdf.kernel.pdf.PdfName.Image;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.AreaBreakType;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

public class FXMLController implements Initializable
{
    boolean flag = false;
    
    String nombreCliente = "";
    String oc = "";
    String producto = "";
    String medidas = "";
    String cantidad = "";
    String pallet = "";
    String pathOut;
    
    Document doc;
    PdfDocument pdfdoc;
    
    @FXML
    private Label cabecera;
    
    @FXML
    private Label mensaje = new Label();
    
    @FXML
    private Label guardadoEn;
    
    @FXML
    private Label pathLbl;
    
    @FXML
    private Button btn;
    
    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException, InvalidFormatException, FileNotFoundException 
    {
        //System.out.println("boton presionado");
        
        //System.out.println(System.getProperty("user.dir"));
        String path = "";
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll( 
                new FileChooser.ExtensionFilter("Excel Worksheet", "*.xlsx"),
                new FileChooser.ExtensionFilter("Excel Worksheet", "*.xls"));
        File selected = fileChooser.showOpenDialog(null);
        
        if(selected != null)
        {
            path = getExcelPath(selected);
            if(!path.endsWith(".xlsx"))
            {
                this.mensaje.setText("El archivo seleccionado no corresponde al formato esperado "
                        + "(use .xlsx o .xls).");
            }
            else
            {
                flag = true;
                convertirCarteles(path);
                this.mensaje.setText("Convirtiendo...");
            }
            
        }
        else 
        {
            
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }   
    
    private String getExcelPath(File file)
    {
        return file.getAbsolutePath();
    }
 

    private void convertirCarteles(String path) throws IOException, InvalidFormatException, FileNotFoundException
    {
        
        //System.out.println("creando pdf");
        pathOut = System.getProperty("user.dir") + "\\Cartel Tipo[1826].pdf";
        //System.out.println(pathOut);
        
        File pdfFile = new File(pathOut);
        //pdfFile.getParentFile().mkdirs();
        
        PdfWriter pdfwr = new PdfWriter(pdfFile);
        
        pdfdoc = new PdfDocument(pdfwr);
        doc = new Document(pdfdoc, PageSize.LETTER.rotate());
        
        
        //System.out.println("pdf creado");
        
        String XLSX_FILE_PATH = path;
        //System.out.println(path);

        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook libro = WorkbookFactory.create(new File(XLSX_FILE_PATH));

        // Getting the Sheet at index zero
        Sheet hoja = libro.getSheetAt(0);
        //System.out.println("ultima fila: " + hoja.getLastRowNum());

        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();
        
        int i = 0;
        
        boolean empiezaDatos = false;
        //System.out.println("\n\nIterating over Rows and Columns using for-each loop\n");
        int numrow = 0;
        int numcell;
        for (Row row: hoja) 
        {
            //System.out.println("numrow: " + numrow);
            numcell = 0;
            for(org.apache.poi.ss.usermodel.Cell cell: row) 
            {
                //System.out.println("numcell: " + numcell);
                String cellValue = dataFormatter.formatCellValue(cell);
                //System.out.print(cellValue + "\t");
                if(cellValue.trim().toUpperCase().equals("CLIENTE"))
                {
                    nombreCliente = dataFormatter.formatCellValue( row.getCell(cell.getColumnIndex() + 1) );
                    //System.out.println("nombre cliente: " + nombreCliente);
                }
                if(cellValue.trim().toUpperCase().equals("ORDEN DE COMPRA") || 
                        cellValue.trim().toUpperCase().equals("ORDEN COMPRA") ||
                        cellValue.trim().toUpperCase().equals("OC") || 
                        cellValue.trim().toUpperCase().equals("O/C"))
                {
                    // en la fila siguiente empiezan los datos
                    empiezaDatos = true;
                }
                numcell++;
            }
            //System.out.println();
            
            if( empiezaDatos && numrow < hoja.getLastRowNum() )
            {
                
                PdfPage newPage = this.pdfdoc.addNewPage();
                //System.out.println("fila actual: " + numrow);
                this.oc = dataFormatter.formatCellValue(hoja.getRow(numrow+1).getCell(0));
                this.producto = dataFormatter.formatCellValue(hoja.getRow(numrow+1).getCell(1));
                
                this.medidas = "" 
                        + dataFormatter.formatCellValue(hoja.getRow(numrow+1).getCell(2))
                        + " X " + dataFormatter.formatCellValue(hoja.getRow(numrow+1).getCell(3))
                        + " X " + dataFormatter.formatCellValue(hoja.getRow(numrow+1).getCell(4))
                        + "";
                this.cantidad = dataFormatter.formatCellValue(hoja.getRow(numrow+1).getCell(10));
                this.pallet = dataFormatter.formatCellValue(hoja.getRow(numrow+1).getCell(11));
                
                //System.out.println(nombreCliente + ", " + oc + ", " + producto + ", " + medidas + ", " + cantidad + ", " + pallet);
                //System.out.println("agregando linea numero: " + i);
                
                
                
                PdfCanvas canvas = new PdfCanvas(newPage);
                canvas.addImage(ImageDataFactory.create("src/bg.jpg"), PageSize.LETTER.rotate(), false);
                
                //agregarHojaPDF(nombreCliente, oc, producto, medidas, cantidad, pallet, i);
                
                
                
                Paragraph p = new Paragraph("\t\t\tCLIENTE:   " + nombreCliente + "\n\n"
                        + "           O/C      : " + oc + "\n\n"
                        + "  PRODUCTO : " + producto + "\n\n"
                        + "    MEDIDAS    : " + medidas + "\n\n"
                        + "  CANTIDAD : " + cantidad + "\n\n"
                        + "    PALLET      : " + pallet + "");
                p.setFontSize(25);
                p.setFirstLineIndent(170);
                p.setBold();
                p.setPaddingLeft(80);
                p.setPaddingTop(30);
                doc.add(p);
                
                PdfCanvas canvas1 = new PdfCanvas(this.doc.getPdfDocument().getPage(i+1));
                canvas1.addImage(ImageDataFactory.create("src/bg.jpg"), PageSize.LETTER.rotate(), false);
                
                doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                
                i++;
            }
            numrow++;
        }
        
        
        pdfdoc.removePage(doc.getPdfDocument().getLastPage());
        
        doc.close();
        libro.close();
        
        mensaje = new Label();
        mensaje.setText("¡Listo!");
        //mensaje.setText("¡Listo!");
        guardadoEn.setText("Guardado en: ");
        pathLbl.wrapTextProperty().setValue(true);
        pathLbl.setText(pathOut);
    }
    
    private void agregarHojaPDF(String cliente, String oc, String producto, 
            String medidas, String cantidad, String pallet, int i) throws FileNotFoundException, IOException
    {
        //System.out.println("Creando página pdf n° " + i);
        
        
//        PdfCanvas canvas = new PdfCanvas(this.pdfdoc.addNewPage());
//        canvas.addImage(ImageDataFactory.create("src/bg.jpg"), PageSize.LETTER.rotate(), false);
       
        
        Paragraph p = new Paragraph("\t\t\tCLIENTE:   " + cliente + "\n\n"
                + "           O/C      : " + oc + "\n\n"
                + "  PRODUCTO : " + producto + "\n\n"
                + "    MEDIDAS    : " + medidas + "\n\n"
                + "  CANTIDAD : " + cantidad + "\n\n"
                + "    PALLET      : " + pallet + "");
        p.setFontSize(25);
        p.setFirstLineIndent(170);
        p.setBold();
        p.setPaddingLeft(80);
        p.setPaddingTop(30);
        doc.add(p);
    }
}
