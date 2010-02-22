package com.geobloc.activities;


import com.geobloc.R;
//import com.geobloc.activities.FormActivity.FormsLoader_FormsTaskListener;
import com.geobloc.handlers.FormHandler;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.shared.Utilities;
import com.geobloc.tasks.LoadFormTask;
import com.geobloc.widget.QuestionWidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
			    new AlertDialog.Builder(callerContext)
					.setTitle("Error")
					.setMessage("At load form")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                        finish();
	                        dialog.cancel();
	                   }
	               })
			      .show();
			}
			else {
				Utilities.showTitleAndMessageDialog(callerContext, formH.getNameForm(),
						"Formulario "+formH.getNameForm()+" cargado correctamente");
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
	
	LinearLayout vista;
	
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
		tView = (TextView)findViewById(R.id.TextFingerMov);
		tView.setText(getString(R.string.help_form_mov));
		
		setFlipperPages();
	}
	
	private void setNumPage (LinearLayout layout, int page) {
		TextView tv = (TextView)layout.findViewById (R.id.NumPageForm);
		tv.setText(page+"/"+formH.getNumPages());
	}
	
	private void setFlipperPages () {
		Context context = FormActivity.this;
		
		LinearLayout vistaR = new LinearLayout(context);
		
	    if (formH != null) {
	    	for (int page=0; page < formH.getNumPages(); page++) {
	    		
	    		if (page == 0) {
	    			vistaR = (LinearLayout) findViewById(R.id.form_page_layout_1);
	    		} else if (page == 1) {
	    			vistaR = (LinearLayout) findViewById(R.id.form_page_layout_2);
	    		} else if (page == 2){
	    			vistaR = (LinearLayout) findViewById(R.id.form_page_layout_3);
	    		} else {
	    			break;
	    		}
	    		
	    		int numQuestions = formH.getNumQuestionOfPage(page);
	    		
	    		setNumPage(vistaR, page+1);
	    		
	    		for (int question=0; question < numQuestions; question++) {	    			
	    			QuestionWidget wdget = new QuestionWidget (context, formH.getQuestionOfPage(question, page));
	    			
	    			vistaR.addView (wdget);
	    		}
	    	}
	    	
	    	/*** We eliminate unnecessary pages of ViewFlipper */
	    	int numPages = formH.getNumPages();
	    	if (numPages < 3) {
	    		if (numPages == 1) {
	    			viewFlipper.removeViewAt(3);
	    			viewFlipper.removeViewAt(2);
	    		}
	    		else if (numPages == 2) {
	    			viewFlipper.removeViewAt(3);
	    		}
	    	}
	    }
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

	public void setListener(IStandardTaskListener listener) {
		this.listener = listener;
	}
	
	
}
