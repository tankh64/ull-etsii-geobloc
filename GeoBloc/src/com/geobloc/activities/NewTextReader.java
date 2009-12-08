/**
 * 
 */
package com.geobloc.activities;

import java.util.Calendar;

import org.apache.http.client.HttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geobloc.ApplicationEx;
import com.geobloc.R;
import com.geobloc.internet.HttpFileMultipartPost;
import com.geobloc.persistance.SDFilePersistance;

/**
 * Activity for Development purposes, designed to display XML files during testing before 
 * they're sent to the server. 
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class NewTextReader extends Activity implements Runnable {
	
	private TextView text;
	public static String __TEXT_READER_TEXT__ = "textToBeDisplayedByTextReader";
	public static String __TEXT_READER_PACKAGE_LOCATION__ = "locationOfPackageToBeSentByTextReader";
	public static String __TEXT_READER_OUTPUT__ = "/TextReader/";
	
	
	private String serverResponse = "No Response";
	private ProgressDialog pd;
	
	private String packageDirectory;
	private boolean written;
	
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
		pd = ProgressDialog.show(this, "Working", "Uploading package to Server...", true,
                false);
 
        Thread thread = new Thread(this);
        thread.start();
	}
	
	// thread code
	public void run() {
		// HttpGet
		/*
		SimpleHttpGet get = new SimpleHttpGet();
		String url = "http://ull-etsii-geobloc.appspot.com/geobloc_server1?firstname=AndroidClient";
		try {
			/ get httpClient from ApplicationEx
			ApplicationEx app = (ApplicationEx)this.getApplication();
			HttpClient httpClient = app.getHttpClient();
			serverResponse = get.ExecuteHttpGet(url, httpClient);
		}
		catch (Exception e){
			e.printStackTrace();
			serverResponse = e.toString();
		}
        handler.sendEmptyMessage(0);
        */
		HttpFileMultipartPost post = new HttpFileMultipartPost();
		String url = "http://ull-etsii-geobloc.appspot.com/upload_basicpackageform";
		try {
			// get httpClient from ApplicationEx
			ApplicationEx app = (ApplicationEx)this.getApplication();
			HttpClient httpClient = app.getHttpClient();
			//serverResponse = post.executeMultipartPost(formDirectory, "form.xml", url, httpClient);
			serverResponse = post.executeMultipartPackagePost(packageDirectory, url, httpClient);
		}
		catch (Exception e){
			e.printStackTrace();
			serverResponse = e.toString();
		}
        handler.sendEmptyMessage(0);
    }
 
	// thread handler code (activated when thread is finished)
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pd.dismiss();
            text.setText(serverResponse);
        }
    };
}
