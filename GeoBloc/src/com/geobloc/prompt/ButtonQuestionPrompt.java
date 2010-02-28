package com.geobloc.prompt;

import android.util.Log;

import com.geobloc.handlers.AttributeTag;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.QuestionType;

public class ButtonQuestionPrompt extends QuestionPrompt {
	private static final String TAG = "ButtonQuestionPrompt";
	
	String title;
	
	public ButtonQuestionPrompt (String titleText, AttributeTag att) {
		
		if (att.attMap.containsKey(Utilities.ATTR_ID)) {
			this.setQuestionId(att.attMap.get(Utilities.ATTR_ID));
		} else {
			Log.e(TAG, "<"+titleText+"> has not ID");
		}
		
		this.setButtonTitle(titleText);
		this.setType();
	}
	
	@Override
	public void setType() {
		type = QuestionType.GB_BUTTON;	
	}
	
	/**
	 * Sets the text on the label
	 * @param name
	 */
	public void setButtonTitle (String name) {
		title = name;
	}
	
	public String getButtonTitle () {
		return title;
	}
}
