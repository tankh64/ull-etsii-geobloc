/**
 * 
 */
package com.geobloc.activities;

import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.geobloc.R;
import com.geobloc.internet.SimpleHttpGet;
import com.geobloc.persistance.SDFilePersistance;

/**
 * Activity for Development purposes, designed to display XML files during testing before they're saved to 
 * the SD Card and before they're sent to the server. Currently working on sending the XML file to the server.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class NewTextReader extends Activity implements Runnable {
	
	private TextView text;
	public static String __TEXT_READER_TEXT__ = "textToBeDisplayedByTextReader";
	public static String __TEXT_READER_OUTPUT__ = "/TextReader/";
	
	private String serverResponse = "No Response";
	private ProgressDialog pd;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_reader);
        
        initialConfig();
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
        	text.setText(extras.getString("textToBeDisplayedByTextReader"));
        }
        else {
        	text.setText("No text to display.");
        }
	}
	
	private void initialConfig() {
		text = (TextView) findViewById(R.id.textReaderText);
	}
	
	public void outputToFileOnClickHandler(View target) {
		OutputToFile("form.xml", text.getText().toString());		
	}
	
	private void OutputToFile(String fileName, String text) {
		//SDFilePersistance.createDirectory(NewTextReader.__TEXT_READER_OUTPUT__);
		
		Calendar cal = Calendar.getInstance();
		String formDirectory = Environment.getExternalStorageDirectory() + NewTextReader.__TEXT_READER_OUTPUT__+"form_";
		formDirectory += cal.get(Calendar.DATE) + "-" + (cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.YEAR) 
						+ "_" + cal.get(Calendar.HOUR_OF_DAY) + "-" + cal.get(Calendar.MINUTE) 
						+ "-" + cal.get(Calendar.SECOND)+"/";
		SDFilePersistance.createDirectory(formDirectory);
		
		boolean written = SDFilePersistance.writeToFile(formDirectory, fileName, text);
		
		CharSequence toastText;
		if (written)
			toastText = "File saved!";
		else
			toastText = "Error! File could not be saved";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(getApplicationContext(), toastText, duration);
		toast.show();
		
		
	}
	
	public void outputToServerOnClickHandler(View target) {
		// Display ProgressDialog and start thread
		pd = ProgressDialog.show(this, "Working..", "Accessing Server...", true,
                false);
 
        Thread thread = new Thread(this);
        thread.start();
	}
	
	// thread code
	public void run() {
		// HttpGet
		SimpleHttpGet get = new SimpleHttpGet();
		String url = "http://ull-etsii-geobloc.appspot.com/geobloc_server1?firstname=AndroidClient";
		try {
			serverResponse = get.ExecuteHttpGet(url);
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
