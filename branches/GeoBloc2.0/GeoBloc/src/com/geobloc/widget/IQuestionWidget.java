package com.geobloc.widget;

import com.geobloc.prompt.CheckboxQuestionPrompt;
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;
import com.geobloc.shared.Utilities.WidgetType;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

//public enum WidgetType {};

public interface IQuestionWidget {
	
	public void buildView (QuestionPrompt qP);
	public void clearAnswer();
	public Object getAnswer();
	
	public WidgetType getType();
	
	public void mySetListener (OnTouchListener list);
}
