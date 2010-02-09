package com.geobloc.prompt;

import com.geobloc.shared.Utilities.QuestionType;

public class DataInputQuestionPrompt extends QuestionPrompt {
	/** label of the Input */
	private String title;
	
	/** input of the question */
	private String input;
	
	
	////// Builders
	/**
	 * 
	 */
	public DataInputQuestionPrompt (String id, String titleText, String inputText) {
		this.setQuestionId(id);
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

}
