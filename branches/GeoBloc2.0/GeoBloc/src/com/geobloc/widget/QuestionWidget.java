package com.geobloc.widget;

import com.geobloc.prompt.ButtonQuestionPrompt;
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuestionWidget extends LinearLayout {
	
	QuestionPrompt mQuestion;
	Context context;
	
	public QuestionWidget(Context context, QuestionPrompt qp) {
		super(context);
		// TODO Auto-generated constructor stub
		
		this.context = context;
		mQuestion = qp;
	
		addViewsToThis();
	}

	private void addViewsToThis () {
		
		switch (mQuestion.getType()) {
		
		case GB_LABEL:
			
			mQuestion.setRequired();
			
			TextView tv = new TextView(context);
			tv.setText(((LabelQuestionPrompt)mQuestion).getQuestionTitle());
			tv.setPadding(5, 5, 0, 5);		      		
			addView(tv);
			break;
		case GB_DATAINPUT:
			/* Contendr� el Texto y el EditText */
			LinearLayout mView = new LinearLayout(context);
			mView.setPadding(5, 5, 5, 5);
			mView.setOrientation(LinearLayout.HORIZONTAL);
			mView.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			
			/* Texto antes del EditText */
			TextView Text = new TextView(context);
			Text.setText(((DataInputQuestionPrompt)mQuestion).getQuestionTitle()+":  ");
			Text.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 3));
			
			EditText ed = new EditText(context);
			ed.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 2));
			ed.setText(((DataInputQuestionPrompt)mQuestion).getQuestionInput());
			
			mView.addView(Text);
			mView.addView(ed);
			
			addView(mView);
			break;
		case GB_BUTTON:
			Button but = new Button(context);
			but.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			but.setText(((ButtonQuestionPrompt)mQuestion).getButtonTitle());
			but.setPadding(10,10,10,10);
			
			addView(but);
			break;
		default:
			break;
		}
		
		/** If required, we add a button information */
		if (mQuestion.isRequired()) {
			Button req = new Button(context);
			req.setText("Required");
			addView(req);
		}
		
	}
	
}