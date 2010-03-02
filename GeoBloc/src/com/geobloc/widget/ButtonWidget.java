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

public class ButtonWidget extends LinearLayout implements QuestionWidget {
	
	private static final int REQUEST_CAMERA = 0;
	
	Context mContext;
	
	Button but;
	Spinner sp;
	
	public ButtonWidget(Context context) {
		super(context);
		
		mContext = context;
		// TODO Auto-generated constructor stub
	}

	public ButtonWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		mContext = context;
	}

	/*public ButtonWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}*/
	
	public void buildView (QuestionPrompt qP) {
		but = new Button (this.getContext());
		but.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 3));
        but.setText(((ButtonQuestionPrompt)qP).getButtonTitle());
        but.setPadding(10,10,10,10);
        but.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	/*Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
            	this.startActivityForResult(i,REQUEST_CAMERA);*/
            }
        });
        
        sp = new Spinner (this.getContext());
        sp.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2));
        
        this.addView(but);
        this.addView(sp);
        
		if (qP.isRequired()) {
			this.setBackgroundColor(Utilities.requiredColor);
		}
		
	}

	@Override
	public void clearAnswer() {
		// TODO Auto-generated method stub
		
	}
	
}
