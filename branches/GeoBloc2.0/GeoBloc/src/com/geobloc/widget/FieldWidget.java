package com.geobloc.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;

public class FieldWidget extends LinearLayout implements QuestionWidget {
	
	public FieldWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FieldWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void buildView(QuestionPrompt qP) {
		// TODO Auto-generated method stub
		
        /* Contendrá el Texto y el EditText */
        /* Texto antes del EditText */
        TextView Text = new TextView(getContext());
        Text.setTextSize(20);
        Text.setText(((DataInputQuestionPrompt)qP).getQuestionTitle()+":  ");
        Text.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 3));
        
        EditText ed = new EditText(getContext());
        ed.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 2));
        ed.setText(((DataInputQuestionPrompt)qP).getQuestionInput());
        
        int numLines = ((DataInputQuestionPrompt)qP).getNumLines();
        if (numLines > 1) {
        	this.setOrientation(LinearLayout.VERTICAL);
        }
        ed.setLines(numLines);
        ed.setMaxLines(numLines);
        
        addView(Text);
        addView(ed);
        
		if (qP.isRequired()) {
			this.setBackgroundColor(Color.YELLOW);
		}

	}

	@Override
	public void clearAnswer() {
		// TODO Auto-generated method stub
		((EditText)this.getChildAt(1)).setText("");
	}

}
