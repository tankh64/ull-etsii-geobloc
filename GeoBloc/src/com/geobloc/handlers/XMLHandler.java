package com.geobloc.handlers;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import com.geobloc.QuestionActivity;
import com.geobloc.QuestionActivity.*;
import com.geobloc.form.FormClass;
import com.geobloc.form.FormPage;
import com.geobloc.shared.Utilities;
import com.geobloc.xml.IField;

import android.content.Context;
import android.text.InputType;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class XMLHandler extends DefaultHandler {
	
	/**
	 * Loaded form
	 */
	FormClass myForm;
	
	FormPage myPage;
	
	/** Tags Valid XML */
	private String GB_FORM = "gb_form";
	private String GB_PAGE = "gb_page";
	private String GB_NAME = "gb_name";
	private String GB_VERSION = "gb_version";
	private String GB_DATE = "gb_date";

	
	// fields
	private boolean in_gb_form = false;
	private boolean in_gb_page = false;
	private boolean in_gb_name = false;
	private boolean in_gb_version = false;
	private boolean in_gb_date = false;

	
	private Context myContext;
    
    List<IField> fields;
    
      
    public XMLHandler ()
    {
    	super();
    	
        
    }


    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void startDocument() throws SAXException {

    }

    @Override
    public void endDocument() throws SAXException {
         // 
    }     

    /** It runs when it encounters tags as:
     * <tag>
     * That can read attributes when faced with:
     * <tag attribute="attributeValue">*/
    @Override
    public void startElement(String namespaceURI, String localName,
              String qName, Attributes atts) throws SAXException {
         if (localName.equals(GB_FORM)) {
             this.in_gb_form = true;
             
             myForm = new FormClass();
             
         } else if (localName.equals(GB_PAGE)) {
        	 this.in_gb_page = true;
        	 
        	 myPage = new FormPage();
        	 
         } else if (localName.equals(GB_NAME)) {
        	 this.in_gb_name = true;
         } else if (localName.equals(GB_VERSION)) {
        	 this.in_gb_version = true;
         } else if (localName.equals(GB_DATE)) {
        	 this.in_gb_date = true;
         } else {
 
         }

    }
    
    /** It runs in closing tags:
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
            
            if (this.in_gb_form) {
            	myForm.addPage(myPage);
       	 	}
       	 	else {
       	 		//ErrorParsing();
       	 	}
        } else if (localName.equals(GB_VERSION)) {
            this.in_gb_version = false;
        } else if (localName.equals(GB_DATE)) {
            this.in_gb_date = false;
        } else {
        	
        }
    } 
    
    /** It runs when faced with the following structure:
     * <tag>characters</tag> */
    @Override
   public void characters(char ch[], int start, int length) {
         if(this.in_gb_form){
        	 if(this.in_gb_page) {
        	 }
        	 else {
        		 if (this.in_gb_name) {
        			 // We must set the form name
        		 }
        	 }
         } else {
        	 // We must indicate that the parsing is not correct
         }
   } 
    
    public int getNumPages () {
    	return myForm.getNumPages();
    }
    
}
