package com.geobloc;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import com.geobloc.QuestionActivity.TextType;
import com.geobloc.shared.Utilities;
import com.geobloc.xml.IField;

import android.content.Context;
import android.text.InputType;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class XMLHandler extends DefaultHandler {
	
	/** El layout a Devolver */
	private LinearLayout linearLayout;
	
	private String pageActual = "";	// Texto que está en la página actual
	
	List<FormPage> listPages;	// Lista de páginas del formulario
	
	FormPage myPage;	// Página actual
	
	/** Tags válidos del XML */
	private String GB_FORM = "gb_form";
	private String GB_PAGE = "gb_page";
	private String GB_NAME = "gb_name";
	/*private String GB_BUTTON = "gb_button";
	private String GB_LABEL = "gb_label";
	private String GB_STRING = "gb_string";
	private String GB_INT = "gb_int";*/
	//private String GB_ = "gb_";
	
	// Campos
	private boolean in_gb_form = false;
	private boolean in_gb_page = false;
	private boolean in_gb_name = false;
	/*private boolean in_gb_button = false;
	private boolean in_gb_label = false;
	private boolean in_gb_string = false;
	private boolean in_gb_int = false;*/
	
	private Context myContext;

	/* Por si queremos guardar los datos para usarlos d otra forma*/
    private ParsedXMLDataSet myParsedXMLDataSet = new ParsedXMLDataSet();
    
    XmlSerializer serializer;
    StringWriter writer;
    
    List<IField> fields;
    
    
    
    public XMLHandler ()
    {
    	super();
    	
    	//myPage = new FormPage();
    	listPages = new ArrayList<FormPage>();
    	
    	// Para escribir el XML mejor
        serializer = Xml.newSerializer();
        /*writer = new StringWriter();
        
        try {
            serializer.setOutput(writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } */
        
    }
    
    /**
     * 
     * @return Lista de FormPages del Formulario
     */
    public List<FormPage> getListPages () {
    	return listPages;
    }

    /** Devuelve el LinearLayout que contiene el Formulario */
    public LinearLayout getParsedData(Context contexto) {
    	
        TextView tv = new TextView(contexto);
        tv.setText("Final del Parseado");
        tv.setPadding(5, 5, 0, 5);
      		
		linearLayout.addView(tv);
    	
        return linearLayout;
    }
    
    /** Inicializamos el linearLayout y el Contexto */
    public void Initialize (Context contexto) {
    	linearLayout = new LinearLayout(contexto);
    	myContext = contexto;
    	
    	linearLayout.setOrientation(LinearLayout.VERTICAL);
    }
    
    
	/**
	 * Añade un TextView al formulario
	 * @param texto El texto que se añadirá
	 */
	private void AddText(String texto) {	
        TextView tv = new TextView(myContext);
        tv.setText(texto);
        tv.setPadding(5, 5, 0, 5);
      		
		linearLayout.addView(tv);
	}
	


    // ===========================================================
    // Metodos
    // ===========================================================
    @Override
    public void startDocument() throws SAXException {
         this.myParsedXMLDataSet = new ParsedXMLDataSet();
    }

    @Override
    public void endDocument() throws SAXException {
         // No hacemos nada
    }     

    /** Se ejecuta cuando encuentra tags como:
     * <tag>
     * Puede leer atributos cuando se encuentra con:
     * <tag attribute="attributeValue">*/
    @Override
    public void startElement(String namespaceURI, String localName,
              String qName, Attributes atts) throws SAXException {
         if (localName.equals(GB_FORM)) {
             this.in_gb_form = true;
         } else if (localName.equals(GB_PAGE)) {
        	 this.in_gb_page = true;
        	 //AddText ("Init pagina");
        	 myPage = new FormPage();
        	 writer = new StringWriter();
             
             try {
                 serializer.setOutput(writer);
             } catch (Exception e) {
                 throw new RuntimeException(e);
             } 
         }
         else if (localName.equals(GB_NAME)) {
        	 this.in_gb_name = true;
         }
         else {
        	 addTextToPage ("<"+localName+">");
        	 
             try {
            	 serializer.startTag("", localName);
             } catch (Exception e) {
                 throw new RuntimeException(e);
             } 
         }
         /*else if (localName.equals(GB_BUTTON)) {
         
             this.in_gb_button = true;
         }else if (localName.equals(GB_LABEL)) {
             this.in_gb_label = true;
         }else if (localName.equals(GB_STRING)) {
        	 this.in_gb_string = true;
         }else if (localName.equals(GB_INT)) {
        	 this.in_gb_int = true;
         }*/
         
         //////////////// Aqui se muestra como extraer atributos
         /*else if (localName.equals("numero")) {
             // Extraemos el atributo
             String attrValue = atts.getValue("numerodecalle");
             int i = Integer.parseInt(attrValue);
             myParsedXMLDataSet.setNumCalle(i);        	 
         }else if (localName.equals("ciudad")) {
        	 this.in_ciudad = true;
         }else if (localName.equals("categoria")) {
             // Extraemos el atributo
             String attrValue = atts.getValue("lang");
             myParsedXMLDataSet.setCategoria(attrValue);       

         }*/
    } 
    
    /** Se ejecuta en tags de cierre:
     * </tag> */
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
              throws SAXException {
        if (localName.equals(GB_FORM)) {
            this.in_gb_form = false;
        } else if (localName.equals(GB_NAME)) {
        	this.in_gb_name = false;
        } else if (localName.equals(GB_PAGE)) {
            this.in_gb_page = false;
            //AddText ("Cierra pagina");

            try {
            	serializer.endDocument();
            	serializer.flush();
            	pageActual = writer.toString();
            	
            	
            } catch (Exception e) {
                throw new RuntimeException(e);
            } 
            
			Utilities.showToast(myContext,
	        		"Pagina: \n"+pageActual,
	                Toast.LENGTH_LONG);
            
            myPage.setCodeXML(pageActual);
            listPages.add(myPage);
            
            AddText ("Pagina "+listPages.size()+" es:");
            AddText (listPages.get(listPages.size()-1).getCodeXML());
            
            pageActual = "";
        } else {
        	localName.trim();
        	addTextToPage ("</"+localName+">");
        	
        	try {
                serializer.endTag("", localName);
                serializer.text("\n");
            } catch (Exception e) {
                throw new RuntimeException(e);
            } 
        	
        	
        }
        	/*else if (localName.equals(GB_BUTTON)) {
            this.in_gb_button = false;
        }else if (localName.equals(GB_LABEL)) {
            this.in_gb_label = false;
        }else if (localName.equals(GB_STRING)) {
       	 	this.in_gb_string = false;
        }else if (localName.equals(GB_INT)) {
       	 	this.in_gb_int = false;
        }*/
    } 
    
    /** Se ejecuta cuando se encuentra con la siguiente estructura:
     * <tag>characters</tag> */
    @Override
   public void characters(char ch[], int start, int length) {
         if(this.in_gb_form){
        	 if(this.in_gb_page) {
        		 // TODO: No es eficiente
        		 String cadena = new String(ch, start, length).trim();
        		 if (cadena.length() > 1) {
        			 addTextToPage(cadena);
        			 
        			 try {
                         serializer.text(cadena);
                     } catch (Exception e) {
                         throw new RuntimeException(e);
                     } 
        		 }
        	 }
        	 else {
        		 if (this.in_gb_name) {
        			 // Debemos establecer el nombre del formulario
        		 }
        	 }
         }
         /*if(this.in_gb_button){        	 
        	 AddButton(new String(ch, start, length));
        	 
        	 // Si estamos dentro de una página
        	 if (this.in_gb_page) {
        		 addTextToPage("<"+GB_BUTTON+">"
        			 		+ new String(ch, start, length)
        	 				+ "</"+GB_BUTTON+">\n");
        	 }
         }
         if(this.in_gb_label){
        	 AddText(new String(ch, start, length));
        	 
        	 // Si estamos dentro de una página
        	 if (this.in_gb_page) {
        		 addTextToPage("<"+GB_LABEL+">"
        			 		+ new String(ch, start, length)
        	 				+ "</"+GB_LABEL+">\n");
        	 }
         }
         if(this.in_gb_string){
        	 AddEditText(TextType.STRING, new String(ch, start, length));
        	 
        	 // Si estamos dentro de una página
        	 if (this.in_gb_page) {
        		 addTextToPage("<"+GB_STRING+">"
        			 		+ new String(ch, start, length)
        	 				+ "</"+GB_STRING+">\n");
        	 }
         }
         if(this.in_gb_int){
        	 AddEditText(TextType.INT, new String(ch, start, length));
        	 
        	 // Si estamos dentro de una página
        	 if (this.in_gb_page) {
        		 addTextToPage("<"+GB_STRING+">"
        			 		+ new String(ch, start, length)
        	 				+ "</"+GB_STRING+">\n");
        	 }
         }     */    
   } 
	

    private void addTextToPage (String text) {
    	//pageActual += text;
    }
}
