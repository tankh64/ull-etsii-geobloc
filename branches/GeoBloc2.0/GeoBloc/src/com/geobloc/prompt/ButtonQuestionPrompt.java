package com.geobloc.prompt;

import android.util.Log;

import com.geobloc.handlers.AttributeTag;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.QuestionType;

public class ButtonQuestionPrompt extends QuestionPrompt {
	private static final String TAG = "ButtonQuestionPrompt";
	
	String title;
	
	String action;
	
	public ButtonQuestionPrompt (String titleText, AttributeTag att) {
		
		if (att.attMap.containsKey(Utilities.ATTR_ID)) {
			this.setQuestionId(att.attMap.get(Utilities.ATTR_ID));
		} else {
			Log.e(TAG, "<"+titleText+"> has not ID");
		}
		
		if (att.attMap.containsKey(Utilities.ATTR_ACTION)) {
			this.setAction(att.attMap.get(Utilities.ATTR_ACTION));
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
	
	private void setAction (String mAction) {
		this.action = mAction;
	}
	
	public String getAction () {
		return this.action;
	}
}
