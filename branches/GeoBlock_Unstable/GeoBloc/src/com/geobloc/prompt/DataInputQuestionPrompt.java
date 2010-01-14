package com.geobloc.prompt;

/**
 * Represent a question of form with text input
 * 
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public class DataInputQuestionPrompt extends QuestionPrompt {

	/** label of the Input */
	private String label;
	
	/** input of the question */
	private String input;
	
	
	////// Builders
	public DataInputQuestionPrompt () {
		
	}
	
	public DataInputQuestionPrompt (String nameText, String labelText, String inputText) {
		this.setQuestionName(nameText);
		this.setLabelQuestion(labelText);
		this.setInputQuestion(inputText);
	}
	
	////// Methods
	public String getLabelQuestion () {
		return label;
	}
	
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
