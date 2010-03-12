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

public class CheckboxThreeWidget extends ThreeStateCheckBox implements QuestionWidget {
	
	Context mContext;
	
	public CheckboxThreeWidget(Context context) {
		super(context);
		
		mContext = context;
		// TODO Auto-generated constructor stub
	}

	public CheckboxThreeWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		mContext = context;
	}

	/*public ButtonWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}*/
	
	public void buildView (QuestionPrompt qP) {

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
