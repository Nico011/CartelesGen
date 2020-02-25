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
import com.sun.javafx.scene.control.SelectedCellsMap;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Cell;
import javafx.scene.control.ChoiceBox;
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
    
    private ObservableList<String> listaOpciones = FXCollections.
            observableArrayList("Nacional", "Exportación");
    
    Document doc;
    PdfDocument pdfdoc;
    
    
    @FXML
    private ChoiceBox selectorTipoLetrero;
    
    @FXML
    private void initialize()
    {
        this.selectorTipoLetrero.setValue("Nacional");
        this.selectorTipoLetrero.setItems(listaOpciones);
        System.out.println("inicialización choicebox");
    }
    
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
        //System.out.println("creando pdf");
        pathOut = System.getProperty("user.dir") + "\\Cartel despacho nacional.pdf";
        //System.out.println(pathOut);
        
        //System.out.println("boton presionado");
        
        //System.out.println(System.getProperty("user.dir"));
        //String path;
        FileChooser fileChooser = new FileChooser();
        String chooserPath = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize().toString();
        fileChooser.getExtensionFilters().addAll( 
                new FileChooser.ExtensionFilter("Excel Worksheet", "*.xlsx"),
                new FileChooser.ExtensionFilter("Excel Worksheet", "*.xls"));
        fileChooser.setInitialDirectory(new File(chooserPath));
        File selected = fileChooser.showOpenDialog(null);
        
        if(selected != null)
        {
            final String path = getExcelPath(selected);
            if(!path.endsWith(".xlsx"))
            {
                this.mensaje.setText("El archivo seleccionado no corresponde al formato esperado "
                        + "(use .xlsx o .xls).");
            }
            else
            {
                flag = true;
                //this.mensaje.setText("Convirtiendo...");
                
                selectorTipoLetrero.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>()
                {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
                    {
                        try
                        {
                            if(newValue.intValue() == 0)
                            {
                                System.out.println("Letreros nacionales");
                                LetrerosNacional letrerosNacional = new LetrerosNacional(doc, pdfdoc, path,
                                    nombreCliente, oc, producto, medidas, cantidad, pallet);
                            }
                            if(newValue.intValue() == 1)
                            {
                                System.out.println("letreros exportacion");
                                LetrerosExportacion letrerosExportacion = new LetrerosExportacion();
                            }
                            
                        } catch (IOException ex)
                        {
                            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InvalidFormatException ex)
                        {
                            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
            
        }
        else 
        {
            
        }
        
        this.mensaje = new Label();
        this.mensaje.setText("¡Listo!");
        this.guardadoEn.setText("Guardado en: ");
        this.pathLbl.wrapTextProperty().setValue(true);
        this.pathLbl.setText(pathOut);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {        
    }   
    
    private String getExcelPath(File file)
    {
        return file.getAbsolutePath();
    }
 

    
}
