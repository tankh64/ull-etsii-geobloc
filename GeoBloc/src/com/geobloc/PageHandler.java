package com.geobloc;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.geobloc.QuestionActivity.TextType;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.WidgetType;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PageHandler extends DefaultHandler {
	
	/** El layout a Devolver */
	private LinearLayout linearLayout;
	private LinearLayout pageToSend;
	
	List<WidgetType> listTypes;	// List containing the types of widgets
	
	/** Tags válidos de la Página */
	private String GB_BUTTON = "gb_button";
	private String GB_LABEL = "gb_label";
	private String GB_STRING = "gb_string";
	private String GB_INT = "gb_int";
	private String GB_CHECKBOX = "gb_checkbox";
	//private String GB_ = "gb_";
	
	// Campos
	private boolean in_gb_button = false;
	private boolean in_gb_label = false;
	private boolean in_gb_string = false;
	private boolean in_gb_int = false;
	private boolean in_gb_checkbox = false;
	
	private Context myContext;	

    /** Devuelve el LinearLayout que contiene el Formulario */
    public LinearLayout getParsedData(Context contexto) {
    	
        /*TextView tv = new TextView(contexto);
        tv.setText("Final del Parseado");
        tv.setPadding(5, 5, 0, 5);
      		
		linearLayout.addView(tv);*/
    	
        return linearLayout;
    }
    
    public LinearLayout getPageToSend () {
    	return pageToSend;
    }
    
    
    /**
     * Method that returns the information necessary to send form data
     */
    public List<WidgetType> getInfoToSend () {
    	return listTypes;
    }
    
    /**
     * Returns the list containing the types of widgets in the current form page
     * @return list containing the types of widgets
     */
    public List<WidgetType> getListTypes () {
    	return listTypes;
    }
    
    /** Inicializamos el linearLayout y el Contexto */
    public void Initialize (Context contexto) {
    	
    	listTypes = new ArrayList<WidgetType>();
    	
    	linearLayout = new LinearLayout(contexto);
    	myContext = contexto;
    	
    	linearLayout.setOrientation(LinearLayout.VERTICAL);
    	
    	pageToSend = new LinearLayout (contexto);
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
	
	/**
	 * Añade un boton al formulario, y un evento, que al ser pulsado el botón,
	 * mostrará el texto que tiene en pantalla
	 * @param texto El texto que contendrá el botón
	 */
	private void AddButton (String texto) {
		final Button but = new Button(myContext);
		but.setText(texto);
		but.setPadding(3,3,3,3);
		
        but.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	Toast.makeText(myContext,
            			but.getText(),
            			Toast.LENGTH_SHORT).show();
            }
        });
		
		linearLayout.addView(but);
		
		pageToSend.addView(but);
	}
	
	/**
	 * Añade un campo para insertar texto en el formulario
	 * @param Tt Tipo de texto a insertar
	 * @param text Texto que acompañará al EditText
	 */
	private void AddEditText (TextType Tt, String text) {
		
		/* Contendrá el Texto y el EditText */
		LinearLayout mView = new LinearLayout(myContext);
		mView.setPadding(5, 5, 5, 5);
		mView.setOrientation(LinearLayout.HORIZONTAL);
		
		/* Texto antes del EditText */
		TextView Text = new TextView(myContext);
		Text.setText(text+":  ");
		Text.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 3));
		
		EditText ed = new EditText(myContext);
		ed.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 2));
		
		switch (Tt) {
			case INT:
				ed.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
				break;
			case FLOAT:
				ed.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
	    				break;
			case STRING:
				ed.setInputType(InputType.TYPE_CLASS_TEXT);
	    				break;
				default:
					break;
		}
		
		mView.addView(Text);
		mView.addView(ed);
	
		linearLayout.addView(mView);
		
		pageToSend.addView(Text);
		pageToSend.addView(ed);
	}
	
	/**
	 * Añade un CheckBox al formulario
	 * @param text Texto que contendrá el checkbox
	 */
	private void AddCheckBox (String text) {
        CheckBox box = new CheckBox(myContext);
        box.setText(text);
      		
		linearLayout.addView(box);
		
		pageToSend.addView(box);
	}
	

    // ===========================================================
    // Metodos
    // ===========================================================
    @Override
    public void startDocument() throws SAXException {
         //this.myParsedXMLDataSet = new ParsedXMLDataSet();
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
    	if (localName.equals(GB_BUTTON)) {
             this.in_gb_button = true;
         }else if (localName.equals(GB_LABEL)) {
             this.in_gb_label = true;
         }else if (localName.equals(GB_STRING)) {
        	 this.in_gb_string = true;
         }else if (localName.equals(GB_INT)) {
        	 this.in_gb_int = true;
         }else if (localName.equals(GB_CHECKBOX)) {
        	 this.in_gb_checkbox = true;
         }
         
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
        if (localName.equals(GB_BUTTON)) {
            this.in_gb_button = false;
        }else if (localName.equals(GB_LABEL)) {
            this.in_gb_label = false;
        }else if (localName.equals(GB_STRING)) {
       	 	this.in_gb_string = false;
        }else if (localName.equals(GB_INT)) {
       	 	this.in_gb_int = false;
        }else if (localName.equals(GB_CHECKBOX)) {
       	 	this.in_gb_checkbox = false;
        }
    } 
    
    /** Se ejecuta cuando se encuentra con la siguiente estructura:
     * <tag>characters</tag> */
    @Override
   public void characters(char ch[], int start, int length) {
         if(this.in_gb_button){        	 
        	 AddButton(new String(ch, start, length));
        	 addTypeToList (WidgetType.BUTTON);
         }
         if(this.in_gb_label){
        	 AddText(new String(ch, start, length));
        	 addTypeToList (WidgetType.LABEL);
         }
         if(this.in_gb_string){
        	 AddEditText(TextType.STRING, new String(ch, start, length));
        	 addTypeToList (WidgetType.STRING);
         }
         if(this.in_gb_int){
        	 AddEditText(TextType.INT, new String(ch, start, length));
        	 addTypeToList (WidgetType.INT);
         }
         if(this.in_gb_checkbox) {
        	 AddCheckBox(new String(ch, start, length));
        	 addTypeToList (WidgetType.CHECKBOX);
         }
   }
    
   private void addTypeToList (WidgetType type) {
	   listTypes.add(type);
   }
	
}
