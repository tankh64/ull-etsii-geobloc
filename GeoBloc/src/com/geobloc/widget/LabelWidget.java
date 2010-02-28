package com.geobloc.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;

public class LabelWidget extends TextView implements QuestionWidget {

	public LabelWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public LabelWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public LabelWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void buildView(QuestionPrompt qP) {
		// TODO Auto-generated method stub
		
		this.setText(((LabelQuestionPrompt)qP).getQuestionTitle());
		this.setPadding(0, 5, 0, 5);
		this.setTextSize(20);
		
		if (qP.isRequired()) {
			this.setBackgroundColor(Color.YELLOW);
		}
	}

	@Override
	public void clearAnswer() {
		// TODO Auto-generated method stub
		this.setText("");
	}

}
