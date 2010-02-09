package com.geobloc.prompt;

import com.geobloc.shared.Utilities.QuestionType;


/**
 * Class that represents a question on the form. This question can be of various
 * types, which are represented by subclasses.
 * 
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public abstract class QuestionPrompt {
	private String questionId;
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
	 * Gets the ID of the question
	 * @return
	 */
	public String getQuestionId () {
		return questionId;
	}
	
	/**
	 * Sets the Id of question
	 * @param name Id of the question
	 */
	public void setQuestionId (String name) {
		questionId = name;
	}
}
