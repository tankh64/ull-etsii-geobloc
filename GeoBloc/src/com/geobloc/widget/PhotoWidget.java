package com.geobloc.widget;

import com.geobloc.R;
import com.geobloc.prompt.CheckboxQuestionPrompt;
import com.geobloc.prompt.DataInputQuestionPrompt;
import com.geobloc.prompt.QuestionPrompt;
import com.geobloc.shared.Utilities;

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

public class PhotoWidget extends RelativeLayout implements QuestionWidget {
	
	private static final int REQUEST_CAMERA = 0;
	
	Context mContext;
	
	LayoutInflater inflater;
	
	public PhotoWidget(Context context, ViewGroup parent) {
		super(context);
		
		mContext = context;
		
		/*inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.gallery, null);*/
		/*inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.gallery, null);*/
				
		/* Así funciona, pero añade un Nivel mas en la jerarquía */
		inflate(context, R.layout.form_photo_gallery_page, this);
        Button but = (Button)findViewById(R.id.takePhotoButton);
        but.setText("Hacer foto");
        but = (Button)findViewById(R.id.loadFromGalleryButton);
        but.setText("Añadir foto desde galeria");
        but = (Button)findViewById(R.id.clearButton);
        but.setText("Borrar fotos");
	}

	public PhotoWidget(Context context, AttributeSet attrs) {
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
}
