package com.geobloc.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.geobloc.form.FormClass;
import com.geobloc.form.FormDataPage;
import com.geobloc.form.FormPage;
import com.geobloc.form.FormPhotoPage;
import com.geobloc.prompt.CheckboxQuestionPrompt;
import com.geobloc.prompt.CheckboxThreeQuestionPrompt;
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.prompt.ListQuestionPrompt;
import com.geobloc.prompt.MediaQuestionPrompt;
import com.geobloc.shared.Utilities;
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
	
	private static enum IntegerTag {IGB_FORM, IGB_NAME, IGB_VERSION, IGB_DATE, IGB_DESCRIPTION, IGB_DATA_PAGE, IGB_PHOTO_PAGE, IGB_VIDEO_PAGE, IGB_AUDIO_PAGE, IGB_LOCATION_PAGE, IGB_PAGE_NAME,
		IGB_LABEL, IGB_LABEL_TEXT, IGB_FIELD, IGB_FIELD_LABEL, IGB_FIELD_DEFAULT_VALUE, IGB_CHECKBOX, IGB_CHECKBOX_TEXT, IGB_CHECKBOX_THREE, IGB_CHECKBOX_THREE_TEXT,
		IGB_LIST, IGB_LIST_LABEL, IGB_LIST_ITEM, IGB_LIST_ITEM_LABEL, IGB_LIST_ITEM_VALUE};
	
	private static Map<String, IntegerTag> MapTags;
	
	/** Tags Valid XML */
	private static String GB_FORM = "gb_form";
	private static String GB_NAME = "gb_name";
	private static String GB_VERSION = "gb_version";
	private static String GB_DATE = "gb_date";
	private static String GB_DESCRIPTION = "gb_description";
	
	private static String GB_DATA_PAGE = "gb_dataPage";
	private static String GB_PHOTO_PAGE = "gb_photoPage";
	private static String GB_VIDEO_PAGE = "gb_videoPage";
	private static String GB_AUDIO_PAGE = "gb_audioPage";
	private static String GB_LOCATION_PAGE = "gb_locationPage";
	
	private static String GB_PAGE_NAME = "gb_pageName";
	
	private static String GB_LABEL = "gb_label";
	private static String GB_LABEL_TEXT = "gb_labelText";
	
	private static String GB_FIELD = "gb_field";
	private static String GB_FIELD_LABEL = "gb_fieldLabel";
	private static String GB_FIELD_DEFAULT_VALUE = "gb_fieldDefaultValue";
	
	private static String GB_CHECKBOX = "gb_checkboxthree";
	private static String GB_CHECKBOX_TEXT = "gb_checkboxthreeText";
	
	private static String GB_CHECKBOX_THREE = "gb_checkbox";
	private static String GB_CHECKBOX_THREE_TEXT = "gb_checkboxText";
	
	private static String GB_LIST = "gb_list";
	private static String GB_LIST_LABEL = "gb_listLabel";
	private static String GB_LIST_ITEM = "gb_listItem";
	private static String GB_LIST_ITEM_LABEL = "gb_listItemLabel";
	private static String GB_LIST_ITEM_VALUE = "gb_listItemValue";
	

	// fields
	private boolean in_gb_form = false;
	private boolean in_gb_name = false;
	private boolean in_gb_version = false;
	private boolean in_gb_date = false;
	private boolean in_gb_description = false;
	
	private boolean in_gb_dataPage = false;
	private boolean in_gb_photoPage = false;
	private boolean in_gb_videoPage = false;
	private boolean in_gb_audioPage = false;
	private boolean in_gb_locationPage = false;
	
	private boolean in_gb_pageName = false;
	
	private boolean in_gb_label = false;
	private boolean in_gb_labelText = false;
	
	private boolean in_gb_field = false;
	private boolean in_gb_fieldLabel = false;
	private boolean in_gb_fieldDefaultValue = false;
	
	private boolean in_gb_checkbox = false;
	private boolean in_gb_checkboxText = false;
	
	private boolean in_gb_checkboxthree = false;
	private boolean in_gb_checkboxthreeText = false;
	
	private boolean in_gb_list = false;
	private boolean in_gb_listLabel = false;
	private boolean in_gb_listItem = false;
	private boolean in_gb_listItemLabel = false;
	private boolean in_gb_listItemValue = false;

	
	private boolean parseError;
    
    List<IField> fields;

    /** We use a stack of Attributes */
    private Stack<AttributeTag> attrStack;
    private AttributeTag actualAtt;

      
    public XMLHandler ()
    {
    	super();
    	
        myForm = new FormClass();
        parseError = false;
        attrStack = new Stack<AttributeTag>();
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
    
    
    public void pushAttrStack (String localName, AttributeTag atts) throws SAXException {
		/** We insert the Attributes in the stack */
    	attrStack.push(atts);
    	
    	Log.v(TAG, "("+attrStack.size()+") Push <"+localName+">  - {"+atts.attMap.size()+"}");
    }

    /** It runs when it encounters tags as:
     * <tag>
     * That can read attributes when faced with:
     * <tag attribute="attributeValue">*/
    @Override
    public void startElement(String namespaceURI, String localName,
              String qName, Attributes atts) throws SAXException {
    	
    	actualAtt = new AttributeTag(atts);
    	
    	try {
    		
    	switch (XMLHandler.MapTags.get(localName)) {
    		case IGB_FORM:
    			this.in_gb_form = true;
    			Log.v(TAG, "Nuevo formulario");
                break;
    		case IGB_DATA_PAGE:
    			this.in_gb_dataPage = true;
    			myPage = (FormPage) new FormDataPage();
           	 	break;
    		case IGB_PHOTO_PAGE:
    			this.in_gb_photoPage = true;
    			myPage = (FormPage) new FormPhotoPage();
    			break;
    		case IGB_VIDEO_PAGE:
    			this.in_gb_videoPage = true;
    			break;
    		case IGB_AUDIO_PAGE:
    			this.in_gb_audioPage = true;
    			break;
    		case IGB_LOCATION_PAGE:
    			this.in_gb_locationPage = true;
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
    		case IGB_CHECKBOX:
    			this.in_gb_checkbox = true;
    			break;
    		case IGB_CHECKBOX_TEXT:
    			this.in_gb_checkboxText = true;
    			break;
    		case IGB_CHECKBOX_THREE:
    			this.in_gb_checkbox = true;
    			break;
    		case IGB_CHECKBOX_THREE_TEXT:
    			this.in_gb_checkboxText = true;
    			break;
    		case IGB_LIST:
    			this.in_gb_list = true;
    			break;
    		case IGB_LIST_LABEL:
    			this.in_gb_listLabel = true;
    			break;
    		case IGB_LIST_ITEM:
    			this.in_gb_listItem = true;
    			break;
    		case IGB_LIST_ITEM_LABEL:
    			this.in_gb_listItemLabel = true;
    			break;
    		case IGB_LIST_ITEM_VALUE:
    			this.in_gb_listItemValue = true;
    			break;
    		/*case IGB_:
    			this
    			break;*/
    			default:
    				Log.e(TAG, "Etiqueta no definida: <"+localName+">");
    				break;
    	}
          	} catch (Exception e) {
        		Log.e(TAG, "Etiqueta no definida: </"+localName+">");
        	}
        	
    	
    	pushAttrStack (localName, actualAtt);
    }
    
    private AttributeTag popAttrStack () {
    	return attrStack.pop();
    }
    
    /** It runs in closing tags:
     * </tag> */
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
              throws SAXException {
    	
    	AttributeTag attMap = popAttrStack();
    	
    	try {
    	switch (XMLHandler.MapTags.get(localName)) {
    	
		case IGB_FORM:
			this.in_gb_form = false;

	    	if (!attrStack.empty()) {
	    		Log.e(TAG, "PILA NO VACIA");
	    	}
	    	
			break;
		case IGB_DATA_PAGE:
			this.in_gb_dataPage = false;
            if (this.in_gb_form) {
            	myForm.addPage((FormPage)myPage);
            	Log.v(TAG, "New page added");
       	 	}
       	 	else {
       	 		setParseError();
       	 	}
       	 	break;
		case IGB_PHOTO_PAGE:
			this.in_gb_photoPage = false;
        	myForm.addPage((FormPhotoPage)myPage);
        	Log.v(TAG, "New photo page added");
			break;
		case IGB_VIDEO_PAGE:
			this.in_gb_videoPage = false;
			break;
		case IGB_AUDIO_PAGE:
			this.in_gb_audioPage = false;
			break;
		case IGB_LOCATION_PAGE:
			this.in_gb_locationPage = false;
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
			LabelQuestionPrompt lqPrompt = new LabelQuestionPrompt(title, attMap);
			((FormDataPage)myPage).addQuestion(lqPrompt);
			
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
        	DataInputQuestionPrompt iQPrompt = new DataInputQuestionPrompt (title, this.textContent, attMap);
			((FormDataPage)myPage).addQuestion(iQPrompt);
			
            this.in_gb_field = false;
            clearValues();
			break;
		case IGB_FIELD_LABEL:
			this.in_gb_fieldLabel = false;
			break;
		case IGB_FIELD_DEFAULT_VALUE:
			this.in_gb_fieldDefaultValue = false;
			break;
		case IGB_CHECKBOX:
        	CheckboxQuestionPrompt bPrompt = new CheckboxQuestionPrompt (title, attMap);
			((FormDataPage)myPage).addQuestion(bPrompt);
        	
            this.in_gb_checkbox = false;
            clearValues();
			break;
		case IGB_CHECKBOX_TEXT:
			this.in_gb_checkboxText = false;
			break;
		case IGB_CHECKBOX_THREE:
        	CheckboxThreeQuestionPrompt ch3Prompt = new CheckboxThreeQuestionPrompt (title, attMap);
			((FormDataPage)myPage).addQuestion(ch3Prompt);
        	
            this.in_gb_checkboxthree = false;
            clearValues();
			break;
		case IGB_CHECKBOX_THREE_TEXT:
			this.in_gb_checkboxText = false;
			break;
		case IGB_LIST:
        	ListQuestionPrompt listPrompt = new ListQuestionPrompt (title, attMap);
			((FormDataPage)myPage).addQuestion(listPrompt);
			
			this.in_gb_list = false;
			clearValues();
			break;
		case IGB_LIST_LABEL:
			this.in_gb_listLabel = false;
			break;
		case IGB_LIST_ITEM:
			this.in_gb_listItem = false;
			break;
		case IGB_LIST_ITEM_LABEL:
			this.in_gb_listItemLabel = false;
			break;
		case IGB_LIST_ITEM_VALUE:
			this.in_gb_listItemValue = false;
			break;
		/*case IGB_:
			this
			break;*/
			default:
				Log.e(TAG, "Etiqueta no definida: </"+localName+">");
				break;
    	}
    	} catch (Exception e) {
    		Log.e(TAG, "Etiqueta no definida: </"+localName+">");
    	}
    	


    } 
    
    /** It runs when faced with the following structure:
     * <tag>characters</tag> */
    @Override
    public void characters(char ch[], int start, int length) {
    	String cadena = new String(ch, start, length).trim();
    	
    	if(this.in_gb_form){
    		if(this.in_gb_dataPage) {
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
    			} else if (this.in_gb_checkbox) {
    				if (this.in_gb_checkboxText) {
    					this.title = cadena;
    				}
    			} else if (this.in_gb_checkboxthree) {
    				if (this.in_gb_checkboxthreeText) {
    					this.title = cadena;
    				}
    			} else if (this.in_gb_list) {
    				if (this.in_gb_listLabel) {
    					Log.v(TAG, "En la lista "+cadena);
    				}
    			}
    		}
    		else if (this.in_gb_photoPage) {
    			if (this.in_gb_pageName) {
    				myPage.setNamePage(cadena);
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
    	MapTags.put(GB_DATA_PAGE, IntegerTag.IGB_DATA_PAGE);
    	MapTags.put(GB_PHOTO_PAGE, IntegerTag.IGB_PHOTO_PAGE);
    	MapTags.put(GB_VIDEO_PAGE, IntegerTag.IGB_VIDEO_PAGE);
    	MapTags.put(GB_AUDIO_PAGE, IntegerTag.IGB_AUDIO_PAGE);
    	MapTags.put(GB_LOCATION_PAGE, IntegerTag.IGB_LOCATION_PAGE);
    	MapTags.put(GB_PAGE_NAME, IntegerTag.IGB_PAGE_NAME);
    	MapTags.put(GB_LABEL, IntegerTag.IGB_LABEL);
    	MapTags.put(GB_LABEL_TEXT, IntegerTag.IGB_LABEL_TEXT);
    	MapTags.put(GB_FIELD, IntegerTag.IGB_FIELD);
    	MapTags.put(GB_FIELD_LABEL, IntegerTag.IGB_FIELD_LABEL);
    	MapTags.put(GB_FIELD_DEFAULT_VALUE, IntegerTag.IGB_FIELD_DEFAULT_VALUE);
    	MapTags.put(GB_CHECKBOX, IntegerTag.IGB_CHECKBOX);
    	MapTags.put(GB_CHECKBOX_TEXT, IntegerTag.IGB_CHECKBOX_TEXT);
    	MapTags.put(GB_CHECKBOX_THREE, IntegerTag.IGB_CHECKBOX_THREE);
    	MapTags.put(GB_CHECKBOX_THREE_TEXT, IntegerTag.IGB_CHECKBOX_THREE_TEXT);
    	MapTags.put(GB_LIST, IntegerTag.IGB_LIST);
    	MapTags.put(GB_LIST_LABEL, IntegerTag.IGB_LIST_LABEL);
    	MapTags.put(GB_LIST_ITEM, IntegerTag.IGB_LIST_ITEM);
    	MapTags.put(GB_LIST_ITEM_LABEL, IntegerTag.IGB_LIST_ITEM_LABEL);
    	MapTags.put(GB_LIST_ITEM_VALUE, IntegerTag.IGB_LIST_ITEM_VALUE);
    	//MapTags.put(GB_, IntegerTag.IGB_);
    }
}
