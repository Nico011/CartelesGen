package com.mycompany.letrerosgen;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import static com.itextpdf.kernel.pdf.PdfName.Image;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
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
    String encabezado = "";
    
    int fontSizeText = 30;
    int fontSizeRes = 34;
    int prodFontSize = 34; 
    int cliFontSize = 38; 
    
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
        String chooserPath = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize().toString();
        fileChooser.getExtensionFilters().addAll( 
                new FileChooser.ExtensionFilter("Excel Worksheet", "*.xlsx"),
                new FileChooser.ExtensionFilter("Excel Worksheet", "*.xls"));
        fileChooser.setInitialDirectory(new File(chooserPath));
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
        
                        
        PdfFont negrita = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
        
        com.itextpdf.kernel.color.Color myGreen = new DeviceRgb(71, 141, 43);
        
        
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
                    // nombreCliente toma el valor de la celda de la derecha
                    nombreCliente = dataFormatter.formatCellValue( row.getCell(cell.getColumnIndex() + 1) );
                    //System.out.println("nombre cliente: " + nombreCliente);
                }
                if(cellValue.trim().toUpperCase().equals("ORDEN DE COMPRA") || 
                        cellValue.trim().toUpperCase().equals("ORDEN COMPRA") ||
                        cellValue.trim().toUpperCase().equals("OC") || 
                        cellValue.trim().toUpperCase().equals("O/C") || 
                        cellValue.trim().toUpperCase().contains("CODIGO") ||
                        cellValue.trim().toUpperCase().contains("CÓDIGO"))
                {
                    // en la fila siguiente empiezan los datos
                    encabezado = cellValue.trim().toUpperCase();
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
                
                
                
                //PdfCanvas canvas = new PdfCanvas(newPage);
                //canvas.addImage(ImageDataFactory.create("bg.jpg"), PageSize.LETTER.rotate(), false);
                
                //agregarHojaPDF(nombreCliente, oc, producto, medidas, cantidad, pallet, i);
                
                PdfCanvas canvas1 = new PdfCanvas(this.doc.getPdfDocument().getPage(i+1));
                canvas1.addImage(ImageDataFactory.create("bg3.jpg"), PageSize.LETTER.rotate(), false);
                
                
                //Paragraph pCliente = new Paragraph("CLIENTE");
                //pCliente.setFont(negrita);
                //pCliente.setFontSize(28);
                
                Rectangle rectCli = new Rectangle(280, 460, 460, 130);
                
                Paragraph resCliente = new Paragraph(this.nombreCliente);
                resCliente.setFont(negrita);
                resCliente.setFontSize(cliFontSize);
                resCliente.setUnderline();
                
                
                
                IRenderer pr1 = resCliente.createRendererSubTree().setParent(doc.getRenderer()); 
                LayoutArea lr1 = new LayoutArea(1, rectCli); 
                
                //new Canvas(new PdfCanvas(pdfdoc.getPage(i + 1)), pdfdoc, new Rectangle(290, 480, 130, 80))
                //        .add(pCliente);
                //new Canvas(new PdfCanvas(pdfdoc.getPage(i + 1)), pdfdoc, new Rectangle(280, 460, 460, 130))
                //        .add(resCliente);
                
                float lFontSize = 0.0001f, rFontSize = cliFontSize; 
 
                for(int j = 0; j < 100; j++) 
                { 
                    float mFontSize = (lFontSize + rFontSize) / 2; 
                    resCliente.setFontSize(mFontSize); 
                    LayoutResult res = pr1.layout(new LayoutContext(lr1)); 
                    if (res.getStatus() == LayoutResult.FULL) 
                    { 
                        lFontSize = mFontSize; 
                    } 
                    else 
                    { 
                        rFontSize = mFontSize; 
                    } 
                } 
                 
                float finalFontSize = lFontSize; 
                resCliente.setFontSize(finalFontSize); 
                 
                //resCliente.setPaddingLeft(15); 
                 
                pr1.layout(new LayoutContext(lr1)); 
                pr1.draw(new DrawContext(pdfdoc, new PdfCanvas(pdfdoc.getPage(i + 1))));
                
                
                //pCliente.setBorder(Border.NO_BORDER);
                resCliente.setBorder(Border.NO_BORDER);
                
                Paragraph pOC;
                if(encabezado.toUpperCase().contains("CODIGO") ||
                        encabezado.toUpperCase().contains("CÓDIGO"))
                {
                    pOC = new Paragraph("CÓDIGO");
                    pOC.setPaddingLeft(20);
                    pOC.setFont(negrita);
                    pOC.setFontSize(fontSizeText);
                    pOC.setFontColor(com.itextpdf.kernel.color.Color.WHITE);
                    pOC.setBackgroundColor(myGreen);
                }
                else
                {
                    pOC = new Paragraph("O/C");
                    pOC.setPaddingLeft(20);
                    pOC.setFont(negrita);
                    pOC.setFontSize(fontSizeText);
                    pOC.setFontColor(com.itextpdf.kernel.color.Color.WHITE);
                    pOC.setBackgroundColor(myGreen);
                }
                
                
                Paragraph resOC = new Paragraph(this.oc);
                resOC.setFont(negrita);
                resOC.setPaddingLeft(15);
                resOC.setFontSize(fontSizeRes);
                
                new Canvas(new PdfCanvas(pdfdoc.getPage(i + 1)), pdfdoc, new Rectangle(65, 390, 200, 80))
                        .add(pOC);
                new Canvas(new PdfCanvas(pdfdoc.getPage(i + 1)), pdfdoc, new Rectangle(265, 390, 500, 80))
                        .add(resOC);
                
                pOC.setBorder(Border.NO_BORDER);
                resOC.setBorder(Border.NO_BORDER);
                
                
                Paragraph pProducto = new Paragraph("PRODUCTO");
                pProducto.setPaddingLeft(10);
                pProducto.setFont(negrita);
                pProducto.setFontSize(fontSizeText);
                pProducto.setFontColor(com.itextpdf.kernel.color.Color.WHITE);
                pProducto.setBackgroundColor(myGreen);
                
                
                
                Rectangle rectProd = new Rectangle(265, 300, 500, 80); 
                
                
                Paragraph resProducto = new Paragraph(this.producto);
                resProducto.setFont(negrita);
                resProducto.setFontSize(fontSizeRes);
                resProducto.setPaddingLeft(15);
                
                IRenderer pr = resProducto.createRendererSubTree().setParent(doc.getRenderer()); 
                LayoutArea lr = new LayoutArea(1, rectProd); 
                
                float lFontSize1 = 0.0001f, rFontSize1 = prodFontSize; 
 
                for(int j = 0; j < 100; j++) 
                { 
                    float mFontSize1 = (lFontSize1 + rFontSize1) / 2; 
                    resProducto.setFontSize(mFontSize1); 
                    LayoutResult res = pr.layout(new LayoutContext(lr)); 
                    if (res.getStatus() == LayoutResult.FULL) 
                    { 
                        lFontSize1 = mFontSize1; 
                    } 
                    else 
                    { 
                        rFontSize1 = mFontSize1; 
                    } 
                } 
                 
                float finalFontSize1 = lFontSize1; 
                resProducto.setFontSize(finalFontSize1); 
                
                pr.layout(new LayoutContext(lr)); 
                pr.draw(new DrawContext(pdfdoc, new PdfCanvas(pdfdoc.getPage(i + 1)))); 
                
                
                
                new Canvas(new PdfCanvas(pdfdoc.getPage(i + 1)), pdfdoc, new Rectangle(65, 300, 200, 80))
                        .add(pProducto);
//                new Canvas(new PdfCanvas(pdfdoc.getPage(i + 1)), pdfdoc, new Rectangle(265, 220, 500, 160))
//                        .add(resProducto);
                
                pProducto.setBorder(Border.NO_BORDER);
                resProducto.setBorder(Border.NO_BORDER);
                
                Paragraph pMedidas = new Paragraph("MEDIDAS");
                pMedidas.setPaddingLeft(20);
                pMedidas.setFont(negrita);
                pMedidas.setFontSize(fontSizeText);
                pMedidas.setFontColor(com.itextpdf.kernel.color.Color.WHITE);
                pMedidas.setBackgroundColor(myGreen);
                
                Paragraph resMedidas = new Paragraph(this.medidas);
                resMedidas.setPaddingLeft(15);
                resMedidas.setFont(negrita);
                resMedidas.setFontSize(fontSizeRes);
                
                new Canvas(new PdfCanvas(pdfdoc.getPage(i + 1)), pdfdoc, new Rectangle(65, 210, 200, 80))
                        .add(pMedidas);
                new Canvas(new PdfCanvas(pdfdoc.getPage(i + 1)), pdfdoc, new Rectangle(265, 210, 500, 80))
                        .add(resMedidas);
                
                pMedidas.setBorder(Border.NO_BORDER);
                resMedidas.setBorder(Border.NO_BORDER);
                
                Paragraph pCantidad = new Paragraph("CANTIDAD");
                pCantidad.setPaddingLeft(20);
                pCantidad.setFont(negrita);
                pCantidad.setFontSize(fontSizeText);
                pCantidad.setBackgroundColor(myGreen);
                pCantidad.setFontColor(com.itextpdf.kernel.color.Color.WHITE);
                
                Paragraph resCantidad = new Paragraph(this.cantidad);
                resCantidad.setPaddingLeft(15);
                resCantidad.setFont(negrita);
                resCantidad.setFontSize(fontSizeRes);
                
                new Canvas(new PdfCanvas(pdfdoc.getPage(i + 1)), pdfdoc, new Rectangle(65, 120, 200, 80))
                        .add(pCantidad);
                new Canvas(new PdfCanvas(pdfdoc.getPage(i + 1)), pdfdoc, new Rectangle(265, 120, 500, 80))
                        .add(resCantidad);
                
                pCantidad.setBorder(Border.NO_BORDER);
                resCantidad.setBorder(Border.NO_BORDER);
                
                Paragraph pPallet = new Paragraph("BODEGA");
                pPallet.setPaddingLeft(20);
                pPallet.setFont(negrita);
                pPallet.setFontSize(fontSizeText);
                pPallet.setFontColor(com.itextpdf.kernel.color.Color.WHITE);
                pPallet.setBackgroundColor(myGreen);
                
                Paragraph resPallet = new Paragraph(this.pallet);
                
                //Rectangle recPallet = new Rectangle(265, 0, 500, 120);
                Rectangle recPallet = new Rectangle(265, 40, 500, 80);
                                
                IRenderer pr2 = resPallet.createRendererSubTree().setParent(doc.getRenderer()); 
                LayoutArea lr2 = new LayoutArea(1, recPallet); 
                
                resPallet.setPaddingLeft(15);
                resPallet.setFont(negrita);
                
                float lFontSize2 = 0.0001f, rFontSize2 = fontSizeRes; 
 
                for(int j = 0; j < 100; j++) 
                { 
                    float mFontSize2 = (lFontSize2 + rFontSize2) / 2; 
                    resPallet.setFontSize(mFontSize2); 
                    LayoutResult res = pr2.layout(new LayoutContext(lr2)); 
                    if (res.getStatus() == LayoutResult.FULL) 
                    { 
                        lFontSize2 = mFontSize2; 
                    } 
                    else 
                    { 
                        rFontSize2 = mFontSize2; 
                    } 
                } 
                 
                float finalFontSize2 = lFontSize2; 
                resPallet.setFontSize(finalFontSize2); 
                 
                //resCliente.setPaddingLeft(15); 
                 
                pr2.layout(new LayoutContext(lr2)); 
                pr2.draw(new DrawContext(pdfdoc, new PdfCanvas(pdfdoc.getPage(i + 1)))); 
                
                
                new Canvas(new PdfCanvas(pdfdoc.getPage(i + 1)), pdfdoc, new Rectangle(65, 40, 200, 80))
                        .add(pPallet);
//                new Canvas(new PdfCanvas(pdfdoc.getPage(i + 1)), pdfdoc, new Rectangle(265, 0, 500, 120))
//                        .add(resPallet);
                
                pPallet.setBorder(Border.NO_BORDER);
                resPallet.setBorder(Border.NO_BORDER);
                
                
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
    
/*
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
*/
}
