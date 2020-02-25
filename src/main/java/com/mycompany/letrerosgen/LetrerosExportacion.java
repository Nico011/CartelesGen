/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.letrerosgen;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;

/**
 *
 * @author Nicolás Hervias
 */
public class LetrerosExportacion
{
    String nombreCliente;
    String oc;
    String producto;
    String medidas;
    String cantidad;
    String pallet;
    
    String path;
    String encabezado;
    
    int fontSizeText = 30;
    int fontSizeRes = 34;
    int prodFontSize = 34; 
    int cliFontSize = 38;
    
    Document doc;
    PdfDocument pdfdoc;
    
    public LetrerosExportacion(Document doc, PdfDocument pdfdoc, String path, String pathOut,
            String cliente, String oc, String producto, String medidas, String cantidad, String pallet)
    {
        this.doc = doc;
        this.pdfdoc = pdfdoc;
        this.path = path;
        this.nombreCliente = cliente;
        this.oc = oc;
        this.producto = producto;
        this.medidas = medidas;
        this.cantidad = cantidad;
        this.pallet = pallet;
        
        convertirCarteles(path);
    }
    
    private void convertirCarteles(String path)
    {
        
    }
}
