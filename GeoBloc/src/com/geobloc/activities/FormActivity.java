package com.geobloc.activities;

import com.geobloc.R;
//import com.geobloc.activities.FormActivity.FormsLoader_FormsTaskListener;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.shared.Utilities;
import com.geobloc.tasks.LoadFormTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity that loads the form and is responsible to handle it graphically
 * 
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public class FormActivity extends Activity {
	
	
	private static class FormsLoader_FormsTaskListener implements IStandardTaskListener {

		private Context callerContext;
		private Context appContext;
		
		public FormsLoader_FormsTaskListener(Context appContext, Context callerContext) {
			this.callerContext = callerContext;
			this.appContext = appContext;
		}
		
		@Override
		public void taskComplete(Object result) {

			String res = (String)result;

			pDialog.dismiss();
		}

		@Override
		public void progressUpdate(int progress, int total) {
			// TODO Auto-generated method stub
			
		}
    	
    }
	
	public static final String FILE_NAME = "filename";
	
	private String filename;
	
	private static ProgressDialog pDialog;
	private LoadFormTask loadTask;
	private IStandardTaskListener listener;
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Aqui debemos conocer el nombre del fichero
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
        	filename = bundle.getString(FormActivity.FILE_NAME);
        }
        else {
        	Utilities.showToast(getApplicationContext(),
            		"No se ha seleccionado fichero",
                    Toast.LENGTH_SHORT);
        	finish();
        }
        
		
        /* Create a new TextView to display the parsing result later. */
        TextView tv = new TextView(getApplicationContext());

        setContentView(R.layout.form_question);
		setTitle(getString(R.string.app_name)+ " > FormActivity ");
		
		
        Intent intent = getIntent();
        if (intent != null) {
            filename = intent.getStringExtra(FormActivity.FILE_NAME);
            //myLoadFormTask = new LoadFormTask();
            //myLoadFormTask.execute(filename);
            //showDialog(PROGRESS_DIALOG);
        }
        
		pDialog = ProgressDialog.show(this, "Working", "Loading form "+filename);
		pDialog.setIndeterminate(true);
		pDialog.setCancelable(true);
		
        
        loadTask = new LoadFormTask();
        loadTask.setContext(getApplicationContext());
        loadTask.setListener(new FormsLoader_FormsTaskListener(getApplicationContext(), this));
        
        loadTask.execute(filename);
        
        
	}
	
	/*@Override
	protected void onResume () {
		if (loadTask != null) {
			if (loadTask.getStatus() == AsyncTask.Status.FINISHED) {
			}
		}

		super.onResume();
	}*/
	
	
	public void taskCompleted () {
		pDialog.dismiss();
	}

	public void setListener(IStandardTaskListener listener) {
		this.listener = listener;
	}
	
	
	
}
