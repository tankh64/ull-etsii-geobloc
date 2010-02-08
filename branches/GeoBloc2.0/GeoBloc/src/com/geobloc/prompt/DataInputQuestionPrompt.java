package com.geobloc.prompt;

import com.geobloc.shared.Utilities.QuestionType;

public class DataInputQuestionPrompt extends QuestionPrompt {
	/** label of the Input */
	private String label;
	
	/** input of the question */
	private String input;
	
	
	////// Builders
	public DataInputQuestionPrompt (String nameText, String labelText, String inputText) {
		this.setQuestionName(nameText);
		this.setLabelQuestion(labelText);
		this.setInputQuestion(inputText);
		this.setType();
	}
	
	////// Methods
	@Override
	public void setType() {
			type = QuestionType.GB_DATAINPUT;	
	}
	
	/**
	 * 
	 * @return Label of the question (Title)
	 */
	public String getLabelQuestion () {
		return label;
	}
	
	/**
	 * 
	 * @return Input of the question
	 */
	public String getInputQuestion () {
		return input;
	}
	
	public void setLabelQuestion (String text) {
		label = text;
	}
	
	public void setInputQuestion (String text) {
		input = text;
	}

}
