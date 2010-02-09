package com.geobloc.form;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geobloc.prompt.ButtonQuestionPrompt;
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.LabelQuestionPrompt;
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
	
	/**
	 * Returns a layout with the questions on the page
	 */
	public LinearLayout getLayoutPage (Context context) {
		
		LinearLayout layout = new LinearLayout (context);
		
		//layout.setOrientation(LinearLayout.VERTICAL);
		//layout.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 3));
		
		QuestionPrompt question;
		
		for (int i=0; i < listQuestions.size(); i++) {
			
			question = listQuestions.get(i);
			
			switch (question.getType()) {
			
				case GB_LABEL:
					TextView tv = new TextView(context);
					tv.setText(((LabelQuestionPrompt)question).getQuestionTitle());
					tv.setPadding(5, 5, 0, 5);		      		
					layout.addView(tv);
					break;
				case GB_DATAINPUT:
					/* Contendrá el Texto y el EditText */
					LinearLayout mView = new LinearLayout(context);
					mView.setPadding(5, 5, 5, 5);
					mView.setOrientation(LinearLayout.HORIZONTAL);
					mView.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					
					/* Texto antes del EditText */
					TextView Text = new TextView(context);
					Text.setText(((DataInputQuestionPrompt)question).getQuestionTitle()+":  ");
					Text.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 3));
					
					EditText ed = new EditText(context);
					ed.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 2));
					ed.setText(((DataInputQuestionPrompt)question).getQuestionInput());
					
					mView.addView(Text);
					mView.addView(ed);
					
					layout.addView(mView);
					break;
				case GB_BUTTON:
					Button but = new Button(context);
					but.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					but.setText(((ButtonQuestionPrompt)question).getButtonTitle());
					but.setPadding(10,10,10,10);
					
					layout.addView(but);
					break;
				default:
					break;
			}


		}
		
		return layout;
	}
}
