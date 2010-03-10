package com.geobloc.widget;

import com.geobloc.R;
import com.geobloc.prompt.ButtonQuestionPrompt;
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;
import com.geobloc.shared.Utilities;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class MediaWidget extends RelativeLayout implements QuestionWidget {
	
	private static final int REQUEST_CAMERA = 0;
	
	Context mContext;
	
	public MediaWidget(Context context) {
		super(context);
		
		mContext = context;
		
		//inflate(getContext(), R.layout.gallery, null);
		//findViewById(R.id.photoGalleryLayout);
		// TODO Auto-generated constructor stub
	}

	public MediaWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		mContext = context;
	}

	/*public ButtonWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}*/
	
	public void buildView (QuestionPrompt qP) {
        EditText ed = new EditText(getContext());
        ed.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 2));
        ed.setText("Debería ir el Layout de las Fotos");
             
        addView(ed);
	}
	
	public void buildViewParent (ViewGroup parent) {
		inflate(getContext(), R.layout.gallery, parent);
	}

	@Override
	public void clearAnswer() {
		// TODO Auto-generated method stub
		
	}
	
}
