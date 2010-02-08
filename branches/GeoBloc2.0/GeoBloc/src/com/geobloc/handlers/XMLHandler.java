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
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.shared.Utilities;
import com.geobloc.xml.IField;

import android.content.Context;
import android.text.InputType;
import android.util.Xml;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class XMLHandler extends DefaultHandler {
	
	private static final String TAG = "XMLHandler";
	/**
	 * Loaded form
	 */
	private final FormClass myForm;
	private FormPage myPage;
	
	/** Tags Valid XML */
	private String GB_FORM = "gb_form";
	private String GB_NAME = "gb_name";
	private String GB_VERSION = "gb_version";
	private String GB_DATE = "gb_date";
	private String GB_DESCRIPTION = "gb_description";
	
	private String GB_PAGE = "gb_page";
	private String GB_PAGE_NAME = "gb_pageName";
	
	private String GB_LABEL = "gb_label";
	private String GB_LABEL_TEXT = "gb_labelText";
	
	private String GB_FIELD = "gb_field";
	private String GB_FIELD_LABEL = "gb_fieldLabel";

	// fields
	private boolean in_gb_form = false;
	private boolean in_gb_name = false;
	private boolean in_gb_version = false;
	private boolean in_gb_date = false;
	private boolean in_gb_description = false;
	
	private boolean in_gb_page = false;
	private boolean in_gb_pageName = false;
	
	private boolean in_gb_label = false;
	private boolean in_gb_labelText = false;
	
	private boolean in_gb_field = false;
	private boolean in_gb_fieldLabel = false;

	
	private boolean parseError;
	
	private Context myContext;
    
    List<IField> fields;
    
      
    public XMLHandler ()
    {
    	super();
    	
        myForm = new FormClass();
        parseError = false;
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
             
             Log.v(TAG, "Nuevo formulario");
             
         } else if (localName.equals(GB_PAGE)) {
        	 this.in_gb_page = true;
        	 
        	 myPage = new FormPage();
        	 
         } else if (localName.equals(GB_NAME)) {
        	 this.in_gb_name = true;
         } else if (localName.equals(GB_VERSION)) {
        	 this.in_gb_version = true;
         } else if (localName.equals(GB_DATE)) {
        	 this.in_gb_date = true;
         } else if (localName.equals(GB_PAGE_NAME)) {
        	 this.in_gb_pageName = true;
         } else if (localName.equals(GB_LABEL)) {
        	 this.in_gb_label = true;
         } else if (localName.equals(GB_LABEL_TEXT)) {
        	 this.in_gb_labelText = true;
         } else if (localName.equals(GB_DESCRIPTION)) {
        	 this.in_gb_description = true;
         } else if (localName.equals(GB_FIELD_LABEL)) {
        	 this.in_gb_fieldLabel = true;
         } else if (localName.equals(GB_FIELD)) {
        	 this.in_gb_field = true;
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
            	Log.v(TAG, "New page added");
       	 	}
       	 	else {
       	 		setParseError();
       	 	}
            
        } else if (localName.equals(GB_VERSION)) {
            this.in_gb_version = false;
        } else if (localName.equals(GB_DATE)) {
            this.in_gb_date = false;
        } else if (localName.equals(GB_PAGE_NAME)) {
            this.in_gb_pageName = false;
        } else if (localName.equals(GB_LABEL)) {
            this.in_gb_label = false;
        } else if (localName.equals(GB_LABEL_TEXT)) {
            this.in_gb_labelText = false;
        } else if (localName.equals(GB_DESCRIPTION)) {
            this.in_gb_description = false;
        } else if (localName.equals(GB_FIELD_LABEL)) {
            this.in_gb_fieldLabel = false;
        } else if (localName.equals(GB_FIELD)) {
            this.in_gb_field = false;
        } else {
        	
        }
    } 
    
    /** It runs when faced with the following structure:
     * <tag>characters</tag> */
    @Override
    public void characters(char ch[], int start, int length) {
    	String cadena = new String(ch, start, length).trim();
    	String aux = new String();
    	
    	if(this.in_gb_form){
    		if(this.in_gb_page) {
    			if (this.in_gb_pageName) {
    				myPage.setNamePage(cadena);
    			} else if ((this.in_gb_label) && (this.in_gb_labelText)) {
    				LabelQuestionPrompt qPrompt = new LabelQuestionPrompt(cadena);
    				myPage.addQuestion(qPrompt);
    			} else if (this.in_gb_field) {
    				if (this.in_gb_fieldLabel) {
    					DataInputQuestionPrompt qPrompt = new DataInputQuestionPrompt ("", cadena, "");
    					myPage.addQuestion(qPrompt);
    				}
    			}
    		}
    		else {
    			if (this.in_gb_name) {
    				//We must set the form name
    				myForm.setNameForm(cadena);
    			}
    			else if (this.in_gb_version) {
    				myForm.setVersionForm(cadena);
    			} else if (this.in_gb_description) {
    				myForm.setDescription(cadena);
    			} 
    		}
    	} else {
    		// We must indicate that the parsing is not correct
    		setParseError();
    	}
    } 
    
    public int getNumPages () {
    	return myForm.getNumPages();
    }
    
    
    /** Returns the form loaded */
    public FormClass getForm() {
    	return myForm;
    }
    
    private void setParseError() {
    	parseError = true;
    }
}
