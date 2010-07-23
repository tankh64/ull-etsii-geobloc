package com.geobloc.prompt;

import org.xml.sax.Attributes;

import android.text.InputType;
import android.util.Log;

import com.geobloc.handlers.AttributeTag;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.QuestionType;

public class DataInputQuestionPrompt extends QuestionPrompt {
	private static final String TAG = "DataInputQuestionPrompt";
	
	/** label of the Input */
	private String title;
	
	/** input of the question */
	private String input;
	
	private int numLines;
	/** Type of the Text inserted */
	private Utilities.FieldType fieldType;
	
	////// Builders
	/**
	 * 
	 */
	public DataInputQuestionPrompt (String titleText, String inputText, AttributeTag att) {
		
		if (att.attMap.containsKey(Utilities.ATTR_ID)) {
			this.setQuestionId(att.attMap.get(Utilities.ATTR_ID));
		} else {
			Log.e(TAG, "<"+titleText+"> has not ID");
		}
		
		if (att.isRequired())
			this.setRequired();		
		
		if (att.attMap.containsKey(Utilities.ATTR_TYPE)) {
			this.setFieldType(att.attMap.get(Utilities.ATTR_TYPE));
		} else {
			this.setFieldType("HAVENT");
		}
		
		if (att.attMap.containsKey(Utilities.ATTR_LINES_NUMBER)) {
			this.setNumLines(Integer.parseInt(att.attMap.get(Utilities.ATTR_LINES_NUMBER)));
		} else {
			setNumLines(1);
		}
		
		this.setQuestionTitle(titleText);
		this.setQuestionInput(inputText);
		this.setType();
	}
	
	////// Methods
	@Override
	public void setType() {
			type = QuestionType.GB_DATAINPUT;	
	}
	
	/**
	 * 
	 * @return Input of the question
	 */
	public String getQuestionInput () {
		return input;
	}
	
	public String getQuestionTitle () {
		return title;
	}
	
	public void setQuestionTitle (String text) {
		title = text;
	}
	
	public void setQuestionInput (String text) {
		input = text;
	}
	
	public void setNumLines (int num) {
		numLines = num;
	}
	
	public int getNumLines () {
		return numLines;
	}

	private void setFieldType (String type) {
		if (type.equalsIgnoreCase("int")) {
			fieldType = Utilities.FieldType.INT;
		} else if (type.equalsIgnoreCase("float")) {
			fieldType = Utilities.FieldType.FLOAT;
		} else {
			fieldType = Utilities.FieldType.STRING;
		}
	}
	
	public Utilities.FieldType getFieldType () {
		return fieldType;
	}

	@Override
	public Object getAnswer() {
		return getQuestionInput();
	}

	@Override
	public void setAnswer(Object answer) {
		Log.i(TAG, "primero la respuesta es     "+input);
		input = (String)answer;
		Log.i(TAG, "Establecemos la respuesta a "+input);
	}
}
