package com.geobloc.widget;

import com.geobloc.prompt.CheckboxQuestionPrompt;
import com.geobloc.prompt.CheckboxThreeQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;
import com.geobloc.shared.Utilities;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class CheckboxThreeWidget extends ThreeStateCheckBox implements QuestionWidget {

	
	public CheckboxThreeWidget(Context context) {
		super(context);
		
		//this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
           //     ViewGroup.LayoutParams.WRAP_CONTENT));
		// TODO Auto-generated constructor stub
	}

	public CheckboxThreeWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

	}

	/*public ButtonWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}*/
	
	public void buildView (QuestionPrompt qP) {

		this.setText((CharSequence)((CheckboxThreeQuestionPrompt)qP).getTitle());
		
		if (qP.isRequired()) {
			this.setBackgroundColor(Utilities.requiredColor);
		}
		
	}

	@Override
	public void clearAnswer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mySetListener(OnTouchListener list) {
		
	}

}
