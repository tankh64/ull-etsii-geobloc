package com.geobloc.prompt;

public class QuestionPrompt {
	private String questionName;
	
	
	///////// Builders
	public QuestionPrompt () {
		
	}
	
	public QuestionPrompt (String name) {
		setQuestionName(name);
	}
	
	
	///////// Methods
	/**
	 * Gets the name of the question
	 * @return
	 */
	public String getQuestionName () {
		return questionName;
	}
	
	/**
	 * Sets the name of question
	 * @param name Name of the question
	 */
	public void setQuestionName (String name) {
		questionName = name;
	}
}
