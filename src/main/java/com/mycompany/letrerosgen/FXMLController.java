package com.mycompany.letrerosgen;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.FileChooser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

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
    
//    private ObservableList<String> listaOpciones = FXCollections.
//            observableArrayList("Nacional", "Exportación");
    
    Document doc;
    PdfDocument pdfdoc;
    
    /*
    @FXML
    private ComboBox selectorTipoLetrero;
    */
    
    @FXML
    private RadioButton rdBttnNacional;
    
    @FXML
    private RadioButton rdBttnExport;
    
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
    private void initialize()
    {
        /*
        this.selectorTipoLetrero.setValue("Exportación");
        this.selectorTipoLetrero.setItems(listaOpciones);
        System.out.println("inicialización choicebox");
        */
    }
    
    
    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException, InvalidFormatException, FileNotFoundException 
    {
        //System.out.println("creando pdf");
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
                
                if(rdBttnNacional.isSelected())
                {
                    System.out.println("Letreros nacionales");
                    
                    pathOut = System.getProperty("user.dir") + "\\Cartel despacho nacional.pdf";
                    LetrerosNacional letrerosNacional = new LetrerosNacional(doc, pdfdoc, path, pathOut,
                        nombreCliente, oc, producto, medidas, cantidad, pallet);
                    this.guardadoEn.setText("Guardado en: ");
                    this.pathLbl.wrapTextProperty().setValue(true);
                    this.pathLbl.setText(pathOut);
                }
                if(rdBttnExport.isSelected())
                {
                    pathOut = System.getProperty("user.dir") + "\\Cartel despacho exportación.pdf";
                    System.out.println("letreros exportacion");
                    LetrerosExportacion letrerosExportacion = new LetrerosExportacion(doc, pdfdoc, path, pathOut,
                        nombreCliente, oc, producto, medidas, cantidad, pallet);
                    this.guardadoEn.setText("Guardado en: ");
                    this.pathLbl.wrapTextProperty().setValue(true);
                    this.pathLbl.setText(pathOut);
                }
                
            }
            
        }
        else 
        {
            
        }
        
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
