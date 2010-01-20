package com.geobloc.form;

import java.util.ArrayList;
import java.util.List;

import com.geobloc.prompt.QuestionPrompt;

/**
 * Represents a page of Form. 
 * 
 * @author Jorge Carballo
 *
 */
public class FormPage {
	
	private String namePage;
	/**
	 * list of questions that contains the current page
	 */
	private List<QuestionPrompt> listQuestions;
	
	/** TODO
	 * Make the mapping from the names of the questions to the position on the page
	 */
	
	/// Constructor
	public FormPage () {
		listQuestions = new ArrayList<QuestionPrompt>();
	}
	
	public FormPage (String name) {
		this.setNamePage(name);
		
		listQuestions = new ArrayList<QuestionPrompt>();
	}

	/// Methods
	/**
	 * Add a question to the form page
	 */
	public void addQuestion (QuestionPrompt question) {
		listQuestions.add(question);
	}
	
	/**
	 * Sets the name of the page
	 * @param name The name under which we will refer to page
	 */
	public void setNamePage (String name) {
		this.namePage = name;
	}
	
	/**
	 * Returns the number of pages
	 */
	public int getNumQuestions () {
		return listQuestions.size();
	}
}
