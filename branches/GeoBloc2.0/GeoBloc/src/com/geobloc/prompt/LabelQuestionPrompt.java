package com.geobloc.prompt;

import com.geobloc.shared.Utilities.QuestionType;

public class LabelQuestionPrompt extends QuestionPrompt {
	
	private String title;
	
	public LabelQuestionPrompt (String name) {
		this.setQuestionId(name);
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
