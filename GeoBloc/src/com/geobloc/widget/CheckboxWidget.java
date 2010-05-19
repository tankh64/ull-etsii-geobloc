package com.geobloc.widget;

import com.geobloc.prompt.CheckboxQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;
import com.geobloc.shared.Utilities;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class CheckboxWidget extends CheckBox implements QuestionWidget {
	
	public CheckboxWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CheckboxWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void buildView (QuestionPrompt qP) {

		this.setTextColor(Utilities.fontColor);
		this.setText(((CheckboxQuestionPrompt)qP).getTitle());
		
		if (qP.isRequired()) {
			this.setBackgroundColor(Utilities.requiredColor);
		}
		
	}

	@Override
	public void clearAnswer() {
		// TODO Auto-generated method stub
		
	}
	
}
