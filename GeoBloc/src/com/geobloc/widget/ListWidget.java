package com.geobloc.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.geobloc.R;
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.prompt.ListQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;
import com.geobloc.shared.Utilities;

public class ListWidget extends RadioGroup implements QuestionWidget {
	private static String TAG = "ListWidget";

	public ListWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ListWidget(Context context, AttributeSet attrs) {
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
		
		TextView Text = new TextView(getContext());
        Text.setTextSize(20);
        Text.setTextColor(Utilities.fontColor);
        Text.setText(((ListQuestionPrompt)qP).getQuestionTitle()+":  ");
        addView(Text);
        
        RadioButton rButton;
        int size = ((ListQuestionPrompt)qP).getSizeOfList();
        for (int i=0; i<size; i++) {
        	rButton = new RadioButton (getContext());
    		rButton.setTextColor(Utilities.fontColor);
    		rButton.setText(((ListQuestionPrompt)qP).getItem(i).getLabel());
    		addView(rButton);
        }		
		
	}

	@Override
	public void clearAnswer() {
		// TODO Auto-generated method stub
	}

}
