package com.geobloc.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.geobloc.R;
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.prompt.MultipleListQuestionPrompt;
import com.geobloc.prompt.SingleListQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;
import com.geobloc.shared.Utilities;

public class MultipleListWidget extends LinearLayout implements QuestionWidget {
	private static String TAG = "ListWidget";

	public MultipleListWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MultipleListWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	


	@Override
	public void buildView(QuestionPrompt qP) {
		// TODO Auto-generated method stub
	    /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            getContext(), R.array.planets_array, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    this.setAdapter(adapter);*/
		this.setPadding(0, 20, 0, 20);
		this.setOrientation(LinearLayout.VERTICAL);
		
		TextView Text = new TextView(getContext());
        Text.setTextSize(20);
        Text.setTextColor(Utilities.fontColor);
        Text.setText(((MultipleListQuestionPrompt)qP).getQuestionTitle()+":  ");
        addView(Text);
        
        CheckBox rCheckBox;
        int size = ((MultipleListQuestionPrompt)qP).getSizeOfList();
        for (int i=0; i<size; i++) {
        	rCheckBox = new CheckBox (getContext());
    		rCheckBox.setTextColor(Utilities.fontColor);
    		rCheckBox.setText(((MultipleListQuestionPrompt)qP).getItem(i).getLabel());
    		addView(rCheckBox);
        }		
		
	}

	@Override
	public void clearAnswer() {
		// TODO Auto-generated method stub
	}

}
