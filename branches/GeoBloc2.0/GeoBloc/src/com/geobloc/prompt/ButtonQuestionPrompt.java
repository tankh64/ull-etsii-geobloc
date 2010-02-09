package com.geobloc.prompt;

import com.geobloc.shared.Utilities.QuestionType;

public class ButtonQuestionPrompt extends QuestionPrompt {
	
	String title;
	
	public ButtonQuestionPrompt (String myId, String titleText) {
		this.setQuestionId(myId);
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
