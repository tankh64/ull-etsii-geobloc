package com.geobloc.widget;

import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.geobloc.R;
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.LabelQuestionPrompt;
import com.geobloc.prompt.MultipleListQuestionPrompt;
import com.geobloc.prompt.SingleListQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.WidgetType;

public class MultipleListWidget extends LinearLayout implements IQuestionWidget {
	private static String TAG = "ListWidget";
	
	private MultipleListQuestionPrompt mListQ;
	private ArrayList<Integer> vSelected;	/* Seleccionados */

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
		this.setPadding(0, 20, 0, 20);
		this.setOrientation(LinearLayout.VERTICAL);
		
		mListQ = (MultipleListQuestionPrompt)qP;
		
		TextView Text = new TextView(getContext());
        Text.setTextSize(20);
        Text.setText(((MultipleListQuestionPrompt)qP).getQuestionTitle()+":  ");
        addView(Text);
        
        ThreeStateCheckBox rCheckBox;
        int size = ((MultipleListQuestionPrompt)qP).getSizeOfList();
        for (int i=0; i<size; i++) {
        	rCheckBox = new ThreeStateCheckBox (getContext());
        	rCheckBox.setId(i+1);
    		rCheckBox.setText(((MultipleListQuestionPrompt)qP).getItem(i).getLabel());
    		
    		addView(rCheckBox);
        }
        // El array de seleccionado a size
        vSelected = new ArrayList<Integer>(size);
	}

	@Override
	public void clearAnswer() {
		// TODO Auto-generated method stub
	}

	@Override
	public void mySetListener(OnTouchListener list) {
		
	}

	@Override
	public Object getAnswer() {
		/* La respuesta pueden ser varios, asi que debo recorrer todos los hijos
		 * que sean CheckBox
		 */
		vSelected.clear();
		ThreeStateCheckBox rCheckBox;
		for (int i=0; i<this.getChildCount(); i++) {
			rCheckBox = (ThreeStateCheckBox) this.getChildAt(i);
			vSelected.set(i, rCheckBox.getState());//  
		}
		
		// Ahora tengo todos los seleccionados
		for (int i=0; i<vSelected.size(); i++) {
			
		}
		return null;
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.MULTIPLELIST;
	}
}
