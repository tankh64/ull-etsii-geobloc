package com.geobloc.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.geobloc.R;
import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.prompt.ListQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;
import com.geobloc.shared.Utilities;

public class ListWidget extends Spinner implements QuestionWidget {
	private static String TAG = "ListWidget";

	public ListWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ListWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public ListWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onClick (DialogInterface dialog, int which) {
		Log.i(TAG, "En el OnClick del spinner");
		
		super.onClick(dialog, which);
	}

	@Override
	public void buildView(QuestionPrompt qP) {
		// TODO Auto-generated method stub
	    /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            getContext(), R.array.planets_array, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    this.setAdapter(adapter);*/
		
		this.setPrompt(((ListQuestionPrompt)qP).getQuestionTitle());
	}

	@Override
	public void clearAnswer() {
		// TODO Auto-generated method stub
	}

}
