package com.geobloc.activities;


import com.geobloc.R;
//import com.geobloc.activities.FormActivity.FormsLoader_FormsTaskListener;
import com.geobloc.handlers.FormHandler;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.shared.Utilities;
import com.geobloc.tasks.LoadFormTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * Activity that loads the form and is responsible to handle it graphically
 * 
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public class FormActivity extends Activity {
	
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
    private Animation slideRightOut;
    private ViewFlipper viewFlipper;
	
	private class FormsLoader_FormsTaskListener implements IStandardTaskListener {

		private Context callerContext;
		private Context appContext;
		
		public FormsLoader_FormsTaskListener(Context appContext, Context callerContext) {
			this.callerContext = callerContext;
			this.appContext = appContext;
		}
		
		@Override
		public void taskComplete(Object result) {
			pDialog.dismiss();
			
			formH = (FormHandler)result;
			if (formH == null) {
				//Utilities.showToast(appContext, "No load form", Toast.LENGTH_SHORT);
				finish();
			}
			else {
				postTaskFinished();

				
			}
			
			
		}

		@Override
		public void progressUpdate(int progress, int total) {
			// TODO Auto-generated method stub
			
		}
    	
    }
	
	public static final String FILE_NAME = "filename";
	public static final String FILE_PATH = "filepath";
	
	private String filename;
	private String filepath;
	
	private static ProgressDialog pDialog;
	private LoadFormTask loadTask;
	private IStandardTaskListener listener;
	
	private static FormHandler formH;
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Aqui debemos conocer el nombre del fichero
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
        	filename = bundle.getString(FormActivity.FILE_NAME);
        	filepath = bundle.getString(FormActivity.FILE_PATH);
        }
        else {
        	Utilities.showToast(getApplicationContext(),
            		"No se ha seleccionado fichero",
                    Toast.LENGTH_SHORT);
        	finish();
        }
        
		
        /* Create a new TextView to display the parsing result later. */
        TextView tv = new TextView(getApplicationContext());

        //setContentView(R.layout.form_question);
        setContentView(R.layout.flipper_question);
		setTitle(getString(R.string.app_name)+ " > " + filename);
        
		pDialog = ProgressDialog.show(this, "Working", "Loading form "+filename);
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		

		
		/*** Flipper *********/
	    viewFlipper = (ViewFlipper)findViewById(R.id.flipper);
	    slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
	    slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
	    slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
	    slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
	    
	    
	    gestureDetector = new GestureDetector(new MyGestureDetector());
	    gestureListener = new View.OnTouchListener() {
	    	public boolean onTouch(View v, MotionEvent event) {
	    		if (gestureDetector.onTouchEvent(event)) {
	    			return true;
	    		}
	    		return false;
	    	}
	    };
	    /**********************/
		
        
        loadTask = new LoadFormTask();
        loadTask.setContext(getApplicationContext());
        loadTask.setListener(new FormsLoader_FormsTaskListener(getApplicationContext(), this));
        
        loadTask.execute(filepath);
        
	}
	
	private void postTaskFinished() {
		/** Rellenamos el Titulo y la descripción del formulario */
		/*** Colocamos texto en el viewFlipper */
		TextView tView = (TextView)findViewById(R.id.TitleForm);
		tView.setText(getString(R.string.form_loaded, formH.getNameForm()));
		tView = (TextView)findViewById(R.id.FormDescription);
		tView.setText(formH.getDescription());
		
		//setFlipperPages();
	}
	
	private void setFlipperPages () {
	    LinearLayout layout = new LinearLayout(getApplicationContext());
	    
	    // Añadimos al ViewFlipper las páginas del formulario
	    if (formH != null) {
	    	layout = (LinearLayout)formH.getLayout(getApplicationContext());
	    	if (layout != null) {
	    		for (int i=0; i < layout.getChildCount(); i++) {
	    			// Fail
	    			viewFlipper.addView(layout.getChildAt(i));
	    		}
	    	}
	    }
	    
	    Utilities.showToast(getApplicationContext(), "El Flipper tiene "+viewFlipper.getChildCount(), Toast.LENGTH_LONG);
	}
	
    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	if (viewFlipper.getDisplayedChild() < (viewFlipper.getChildCount()-1)) {
                		viewFlipper.setInAnimation(slideLeftIn);
                		viewFlipper.setOutAnimation(slideLeftOut);
                		viewFlipper.showNext();
                	} else {
                		Utilities.showToast(getApplicationContext(), getString(R.string.no_more_pages_at_rigth), Toast.LENGTH_SHORT);
                	}
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	if (viewFlipper.getDisplayedChild() > 0) {
                		viewFlipper.setInAnimation(slideRightIn);
                		viewFlipper.setOutAnimation(slideRightOut);
                		viewFlipper.showPrevious();
                	} else {
                		Utilities.showToast(getApplicationContext(), getString(R.string.no_more_pages_at_left), Toast.LENGTH_SHORT);
                	}
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
	        return true;
	    else
	    	return false;
    }
	
	/*@Override
	protected void onResume () {
		if (loadTask != null) {
			if (loadTask.getStatus() == AsyncTask.Status.FINISHED) {
			}
		}

		super.onResume();
	}*/


	public void setListener(IStandardTaskListener listener) {
		this.listener = listener;
	}
	
	
}
