package com.geobloc.prompt;

/**
 *  QuestionPrompt class is created to represent each of the possible types of
 *  survey questions
 *  
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
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
