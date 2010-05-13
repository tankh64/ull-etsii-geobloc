package com.geobloc.form;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geobloc.R;
import com.geobloc.prompt.CheckboxQuestionPrompt;
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;


/**
 * Represents a page of Form. 
 * 
 * @author Jorge Carballo
 *
 */
public class FormDataPage extends FormPage {
	
	LinearLayout layout;
	/**
	 * list of questions that contains the current page
	 */
	private List<QuestionPrompt> listQuestions;
	
	/** TODO
	 * Make the mapping from the names of the questions to the position on the page
	 */
	
	/// Constructor
	public FormDataPage () {
		listQuestions = new ArrayList<QuestionPrompt>();
		this.setPageType(PageType.DATA);
	}
	
	public FormDataPage (String name) {
		this.setNamePage(name);
		listQuestions = new ArrayList<QuestionPrompt>();
		this.setPageType(PageType.DATA);
	}

	/// Methods
	/**
	 * Add a question to the form page
	 */
	public void addQuestion (QuestionPrompt question) {
		listQuestions.add(question);
	}
	
	/**
	 * Returns the number of pages
	 */
	public int getNumQuestions () {
		return listQuestions.size();
	}
	
	/**
	 * Returns the question as QuestionPrompt
	 * @param question
	 * @return
	 */
	public QuestionPrompt getQuestion (int question) {
		return listQuestions.get(question);
	}
	
}
