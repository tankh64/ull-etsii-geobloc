package com.geobloc.prompt;

/**
 * Enumerated type indicating the type of question
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
enum QuestionType {GB_DATAINPUT, GB_LABEL, GB_BUTTON};

/**
 * Class that represents a question on the form. This question can be of various
 * types, which are represented by subclasses.
 * 
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public abstract class QuestionPrompt {
	private String questionName;
	protected QuestionType type;
	
	///////// Builders
	/*
	public QuestionPrompt (String name) {
		setQuestionName(name);
	}*/
	
	
	///////// Methods
	/**
	 * Sets the type of the question
	 */
	abstract void setType ();
	
	/**
	 * Returns the type of question
	 */
	public QuestionType getType () {
		return type;
	}
	
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
