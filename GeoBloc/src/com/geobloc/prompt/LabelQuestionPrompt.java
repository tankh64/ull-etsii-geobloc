package com.geobloc.prompt;

import org.xml.sax.Attributes;

import android.util.Log;

import com.geobloc.handlers.AttributeTag;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.QuestionType;

public class LabelQuestionPrompt extends QuestionPrompt {
	
	private static final String TAG = "LabelQuestionPrompt";
	
	private String title;
	
	private Attributes atts;
	
	public LabelQuestionPrompt(String name, AttributeTag att) {
		
		if (att.attMap.size() != 0) {
			
			this.setQuestionId(att.attMap.get(Utilities.ATTR_ID));
			
			if (att.isRequired())
				this.setRequired();
			
		} else {
			Log.v(TAG, " es NULL");
			
			atts = null;
		}
				
		this.setQuestionTitle(name);
		this.setType();
	}


	@Override
	public void setType () {
		type = QuestionType.GB_LABEL;
	}
	
	/**
	 * Sets the text on the label
	 * @param name
	 */
	public void setQuestionTitle (String name) {
		title = name;
	}
	
	public String getQuestionTitle () {
		return title;
	}
	

}
