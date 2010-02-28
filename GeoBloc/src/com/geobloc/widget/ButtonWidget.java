package com.geobloc.widget;

import com.geobloc.prompt.ButtonQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;
import com.geobloc.shared.Utilities;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class ButtonWidget extends Button implements QuestionWidget {

	public ButtonWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ButtonWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ButtonWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public void buildView (QuestionPrompt qP) {
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        setText(((ButtonQuestionPrompt)qP).getButtonTitle());
        setPadding(10,10,10,10);
        
		if (qP.isRequired()) {
			this.setBackgroundColor(Utilities.requiredColor);
		}
	}

	@Override
	public void clearAnswer() {
		// TODO Auto-generated method stub
		
	}
	
}
