/**
 * 
 */
package com.geobloc.activities;

import org.apache.http.client.HttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.geobloc.ApplicationEx;
import com.geobloc.R;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.shared.Utilities;
import com.geobloc.tasks.UploadPackageTask;
import com.google.listeners.IStandardTaskListener;

/**
 * Activity for Development purposes, designed to display XML files during testing before 
 * they're sent to the server. 
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
//public class NewTextReader extends Activity implements Runnable {
public class NewTextReader extends Activity implements IStandardTaskListener {
	
	private TextView text;
	public static String __TEXT_READER_TEXT__ = "textToBeDisplayedByTextReader";
	public static String __TEXT_READER_PACKAGE_LOCATION__ = "locationOfPackageToBeSentByTextReader";
	public static String __TEXT_READER_OUTPUT__ = "/TextReader/";
	
	private ProgressDialog pd;
	
	private String packageDirectory;
	
	private Button outputToServerButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_reader);
        
        initialConfig();
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
        	text.setText(extras.getString(NewTextReader.__TEXT_READER_TEXT__));
        	packageDirectory = extras.getString(NewTextReader.__TEXT_READER_PACKAGE_LOCATION__);
        }
        else {
        	text.setText("No text to display.");
        }
	}
	
	private void initialConfig() {
		text = (TextView) findViewById(R.id.textReaderText);
		outputToServerButton = (Button) findViewById(R.id.outputToServerButton);
		
		/*
		// disable unless file has been written
		outputToServerButton.setEnabled(false);
		*/
	}
	
	public void outputToServerOnClickHandler(View target) {
		// Display ProgressDialog and start thread
		
		/*
        Thread thread = new Thread(this);
        thread.start();
        */
		
		pd = ProgressDialog.show(this, "Working", "Uploading package to Server...");
		pd.setIndeterminate(false);
		pd.setCancelable(false);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//pd.setMax(100);
		//pd.setProgress(0);
		
		UploadPackageTask task = new UploadPackageTask();
		task.setContext(getApplicationContext());
		// get httpClient from ApplicationEx
		ApplicationEx app = (ApplicationEx)this.getApplication();
		HttpClient httpClient = app.getHttpClient();
		task.setHttpClient(httpClient);
		task.setListener(this);
		task.execute(packageDirectory);
		
	}
	/*
	// thread code
	public void run() {
		// HttpGet
		
		
		// get servlet address from shared preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String url = prefs.getString(GBSharedPreferences.__UPLOAD_PACKAGES_SERVLET_ADDRESS_KEY__, 
				GBSharedPreferences.__DEFAULT_UPLOAD_PACKACGES_SERVLET_ADDRESS__);
		
		HttpFileMultipartPost post;
		
		
		// boolean to check for Exception
		boolean exception = false;

		// boolean to exit loop (success or failrue)
		boolean done = false;
		
		for (int i = 1; (!exception && !done && (i <= 3)); i++) {
			post = new HttpFileMultipartPost();
			// tell the handler in which attempt we are
			handler.sendEmptyMessage(i);
			try {
				// get httpClient from ApplicationEx
				ApplicationEx app = (ApplicationEx)this.getApplication();
				HttpClient httpClient = app.getHttpClient();
				//serverResponse = post.executeMultipartPost(formDirectory, "form.xml", url, httpClient);
				serverResponse = post.executeMultipartPackagePost(packageDirectory, url, httpClient);
				
				// should be enough to check
				if (serverResponse.contains((CharSequence)GBSharedPreferences.__OK_SIGNATURE__)) {
					done = true;
				}
			}
			catch (Exception e){
				// if exception, exit loop
				exception = true;
				e.printStackTrace();
				serverResponse = e.toString();
			}
		}

		// tell the handler to dismiss the dialog; we're done
        handler.sendEmptyMessage(0);
    }
	*/
	/*
	// thread handler code (activated when thread is finished)
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
        	case 0:
        		// message 0, exit
        		pd.dismiss();
                text.setText(serverResponse);
        		break;
        	default:
        		// else, we're trying
        		pd.setTitle("Attempt " + msg.what);
        		break;
        	}
        }
    };
    */

	//@Override
	public void downloadingComplete(Object result) {
		if (pd != null)
			pd.dismiss();
		
		String res = (String) result;
		Utilities.showTitleAndMessageDialog(this, "Package Report", res);
	}

	//@Override
	public void progressUpdate(int progress, int total) {
		// no update in ProgressDialog for now
	}
}
