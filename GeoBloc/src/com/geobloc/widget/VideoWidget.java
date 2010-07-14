package com.geobloc.widget;

import com.geobloc.R;
import com.geobloc.prompt.CheckboxQuestionPrompt;
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.WidgetType;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class VideoWidget extends RelativeLayout implements IQuestionWidget {
	
	private static final int REQUEST_CAMERA = 0;
	
	Context mContext;
	
	LayoutInflater inflater;
	
	public VideoWidget(Context context, ViewGroup parent) {
		super(context);
		
		mContext = context;
				
		/* Así funciona, pero añade un Nivel mas en la jerarquía */
		inflate(context, R.layout.form_video_gallery_page, this);
        Button but = (Button)findViewById(R.id.takeVideoButton);
        but.setText("Capturar Video");
        but = (Button)findViewById(R.id.loadFromGalleryButton);
        but.setText("Añadir video desde galeria");
        but = (Button)findViewById(R.id.clearButton);
        but.setText("Borrar video");
	}

	public VideoWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		mContext = context;
	}

	public void buildView (QuestionPrompt qP) {

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
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.VIDEO;
	}
}
