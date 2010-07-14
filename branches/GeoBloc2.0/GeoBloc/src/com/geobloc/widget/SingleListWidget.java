package com.geobloc.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.geobloc.R;
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.prompt.SingleListQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.WidgetType;

public class SingleListWidget extends RadioGroup implements IQuestionWidget {
	private static String TAG = "ListWidget";
	
	int selected;
	SingleListQuestionPrompt sListQ;

	public SingleListWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SingleListWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void buildView(QuestionPrompt qP) {
		this.setPadding(0, 20, 0, 20);
		
		this.sListQ = (SingleListQuestionPrompt)qP;
		
		TextView Text = new TextView(getContext());
        Text.setTextSize(20);
        Text.setText(((SingleListQuestionPrompt)qP).getQuestionTitle()+":  ");
        addView(Text);
        
        RadioButton rButton;
        int size = ((SingleListQuestionPrompt)qP).getSizeOfList();
        for (int i=0; i<size; i++) {
        	rButton = new RadioButton (getContext());
        	rButton.setId(i+1);
    		rButton.setText(((SingleListQuestionPrompt)qP).getItem(i).getLabel());
    		addView(rButton);
        }		
		
	}

	@Override
	public void clearAnswer() {
		// TODO Auto-generated method stub
	}

	@Override
	public void mySetListener(OnTouchListener list) {
		setOnTouchListener(list);
	}

	@Override
	public Object getAnswer() {
		selected = this.getCheckedRadioButtonId() - 1;
		
		return (sListQ).getItem(selected);
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.SINGLELIST;
	}
}
