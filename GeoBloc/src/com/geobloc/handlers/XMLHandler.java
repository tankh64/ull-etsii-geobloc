package com.geobloc.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.geobloc.form.FormClass;
import com.geobloc.form.FormPage;
import com.geobloc.prompt.ButtonQuestionPrompt;
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.xml.IField;

import android.content.Context;
import android.util.Log;

public class XMLHandler extends DefaultHandler {
	
	private static final String TAG = "XMLHandler";
	/**
	 * Loaded form
	 */
	private final FormClass myForm;
	private FormPage myPage;

	/**
	 * 
	 */
	private String id;
	private String title;
	private String textContent;
	
	private static enum IntegerTag {IGB_FORM, IGB_NAME, IGB_VERSION, IGB_DATE, IGB_DESCRIPTION, IGB_PAGE, IGB_PAGE_NAME,
		IGB_LABEL, IGB_LABEL_TEXT, IGB_FIELD, IGB_FIELD_LABEL, IGB_FIELD_DEFAULT_VALUE, IGB_BUTTON, IGB_BUTTON_TEXT};
	
	private static Map<String, IntegerTag> MapTags;
	
	/** Tags Valid XML */
	private static String GB_FORM = "gb_form";
	private static String GB_NAME = "gb_name";
	private static String GB_VERSION = "gb_version";
	private static String GB_DATE = "gb_date";
	private static String GB_DESCRIPTION = "gb_description";
	
	private static String GB_PAGE = "gb_page";
	private static String GB_PAGE_NAME = "gb_pageName";
	
	private static String GB_LABEL = "gb_label";
	private static String GB_LABEL_TEXT = "gb_labelText";
	
	private static String GB_FIELD = "gb_field";
	private static String GB_FIELD_LABEL = "gb_fieldLabel";
	private static String GB_FIELD_DEFAULT_VALUE = "gb_fieldDefaultValue";
	
	private static String GB_BUTTON = "gb_button";
	private static String GB_BUTTON_TEXT = "gb_buttonText";

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
	private boolean in_gb_fieldDefaultValue = false;
	
	private boolean in_gb_button = false;
	private boolean in_gb_buttonText = false;

	
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
    	XMLHandler.MapTags = new HashMap<String, IntegerTag>();
    	
    	InitMap();
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
    	    	
    	switch (XMLHandler.MapTags.get(localName)) {
    		case IGB_FORM:
    			this.in_gb_form = true;
    			Log.v(TAG, "Nuevo formulario");
    			break;
    		case IGB_PAGE:
    			this.in_gb_page = true;
    			myPage = new FormPage();
           	 	break;
    		case IGB_NAME:
    			this.in_gb_name = true;
    			break;
    		case IGB_VERSION:
    			this.in_gb_version = true;
    			break;
    		case IGB_DATE:
    			this.in_gb_date = true;
    			break;
    		case IGB_PAGE_NAME:
    			this.in_gb_pageName = true;
    			break;
    		case IGB_LABEL:
    			this.in_gb_label = true;
    			break;
    		case IGB_LABEL_TEXT:
    			this.in_gb_labelText = true;
    			break;
    		case IGB_DESCRIPTION:
    			this.in_gb_description = true;
    			break;
    		case IGB_FIELD:
    			this.in_gb_field = true;
    			break;
    		case IGB_FIELD_LABEL:
    			this.in_gb_fieldLabel = true;
    			break;
    		case IGB_FIELD_DEFAULT_VALUE:
    			this.in_gb_fieldDefaultValue = true;
    			break;
    		case IGB_BUTTON:
    			this.in_gb_button = true;
    			break;
    		case IGB_BUTTON_TEXT:
    			this.in_gb_buttonText = true;
    			break;
    		/*case IGB_:
    			this
    			break;*/
    			default:
    				Log.e(TAG, "Etiqueta no definida: <"+localName+">");
    	}
    	
    }
    
    /** It runs in closing tags:
     * </tag> */
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
              throws SAXException {
    	
    	switch (XMLHandler.MapTags.get(localName)) {
    	
		case IGB_FORM:
			this.in_gb_form = false;
			break;
		case IGB_PAGE:
			this.in_gb_page = false;
            if (this.in_gb_form) {
            	myForm.addPage(myPage);
            	Log.v(TAG, "New page added");
       	 	}
       	 	else {
       	 		setParseError();
       	 	}
       	 	break;
		case IGB_NAME:
			this.in_gb_name = false;
			break;
		case IGB_VERSION:
			this.in_gb_version = false;
			break;
		case IGB_DATE:
			this.in_gb_date = false;
			break;
		case IGB_PAGE_NAME:
			this.in_gb_pageName = false;
			break;
		case IGB_LABEL:
			LabelQuestionPrompt lqPrompt = new LabelQuestionPrompt(id, title);
			myPage.addQuestion(lqPrompt);
			
            this.in_gb_label = false;
            clearValues();
			break;
		case IGB_LABEL_TEXT:
			this.in_gb_labelText = false;
			break;
		case IGB_DESCRIPTION:
			this.in_gb_description = false;
			break;
		case IGB_FIELD:
        	DataInputQuestionPrompt iQPrompt = new DataInputQuestionPrompt (id, title, this.textContent);
			myPage.addQuestion(iQPrompt);
			
            this.in_gb_field = false;
            clearValues();
			break;
		case IGB_FIELD_LABEL:
			this.in_gb_fieldLabel = false;
			break;
		case IGB_FIELD_DEFAULT_VALUE:
			this.in_gb_fieldDefaultValue = false;
			break;
		case IGB_BUTTON:
        	ButtonQuestionPrompt bPrompt = new ButtonQuestionPrompt (id, title);
			myPage.addQuestion(bPrompt);
        	
            this.in_gb_button = false;
            clearValues();
			break;
		case IGB_BUTTON_TEXT:
			this.in_gb_buttonText = false;
			break;
		/*case IGB_:
			this
			break;*/
			default:
				Log.e(TAG, "Etiqueta no definida: </"+localName+">");
				break;
    	}

    } 
    
    /** It runs when faced with the following structure:
     * <tag>characters</tag> */
    @Override
    public void characters(char ch[], int start, int length) {
    	String cadena = new String(ch, start, length).trim();
    	
    	if(this.in_gb_form){
    		if(this.in_gb_page) {
    			if (this.in_gb_pageName) {
    				myPage.setNamePage(cadena);
    			} else if ((this.in_gb_label) && (this.in_gb_labelText)) {
    				this.title = cadena;
    			} else if (this.in_gb_field) {
    				if (this.in_gb_fieldLabel) {
    					this.title = cadena;
    				} else if (this.in_gb_fieldDefaultValue) {
    					this.textContent = cadena;
    				}
    			} else if (this.in_gb_button) {
    				if (this.in_gb_buttonText) {
    					this.title = cadena;
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
    
    /**
     * Clear the values read from the current node
     */
    private void clearValues () {
    	this.id = "";
    	this.title = "";
    	this.textContent = "";
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
    
    /**
     * Initialize the HashMap
     */
    private void InitMap () {
    	MapTags.put(GB_FORM, IntegerTag.IGB_FORM);
    	MapTags.put(GB_NAME, IntegerTag.IGB_NAME);
    	MapTags.put(GB_VERSION, IntegerTag.IGB_VERSION);
    	MapTags.put(GB_DATE, IntegerTag.IGB_DATE);
    	MapTags.put(GB_DESCRIPTION, IntegerTag.IGB_DESCRIPTION);
    	MapTags.put(GB_PAGE, IntegerTag.IGB_PAGE);
    	MapTags.put(GB_PAGE_NAME, IntegerTag.IGB_PAGE_NAME);
    	MapTags.put(GB_LABEL, IntegerTag.IGB_LABEL);
    	MapTags.put(GB_LABEL_TEXT, IntegerTag.IGB_LABEL_TEXT);
    	MapTags.put(GB_FIELD, IntegerTag.IGB_FIELD);
    	MapTags.put(GB_FIELD_LABEL, IntegerTag.IGB_FIELD_LABEL);
    	MapTags.put(GB_FIELD_DEFAULT_VALUE, IntegerTag.IGB_FIELD_DEFAULT_VALUE);
    	MapTags.put(GB_BUTTON, IntegerTag.IGB_BUTTON);
    	MapTags.put(GB_BUTTON_TEXT, IntegerTag.IGB_BUTTON_TEXT);
    	//MapTags.put(GB_, IntegerTag.IGB_);
    }
}
