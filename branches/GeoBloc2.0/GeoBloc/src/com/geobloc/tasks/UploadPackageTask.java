/**
 * 
 */
package com.geobloc.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.geobloc.internet.HttpFileMultipartPost;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.shared.GBSharedPreferences;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class UploadPackageTask extends AsyncTask<String, Integer, String> {

	private Context context;
	private String serverAddress = "";
	private int attempts;
	private HttpClient httpClient;
	
	private List<String> report;
	
	private IStandardTaskListener listener;
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
	public void setListener(IStandardTaskListener listener) {
		this.listener = listener;
	}
	
	/* 
	 * Runs on GUI Thread
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		report = new ArrayList<String>();
	}
	
	/*
	 * Runs on Background Thread
	 */
	@Override
	protected String doInBackground(String... packagePaths) {
		int count = packagePaths.length;
		String results = "";
		if ((httpClient != null) && (context != null)) {
			initConfig();
			for (int i = 0; i < count; i++) {
				// sendPackage updates the report ArrayList
				sendPackage(packagePaths[i]);
				publishProgress((int) ((i / (float) count) * 100), count);
			}
			for (String res : report)
				results += res;
		}
		else {
			results =  "Error! Could not perform task due to missing parameters.";
		}
		
		return results;
	}
		
	private void initConfig() {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		serverAddress = prefs.getString(GBSharedPreferences.__UPLOAD_PACKAGES_SERVLET_ADDRESS_KEY__, 
				GBSharedPreferences.__DEFAULT_UPLOAD_PACKACGES_SERVLET_ADDRESS__);
		
		//this.serverAddress = address;
		attempts = Integer.parseInt(prefs.getString(GBSharedPreferences.__NUMBER_OF_INTERNET_ATTEMPTS_KEY__, 
				GBSharedPreferences.__DEFAULT_NUMBER_OF_INTERNET_ATTEMPTS__));
	}
	
	private String sendPackage(String packagePath) {
		HttpFileMultipartPost post;
		
		String serverResponse = "Error!";
		
		// boolean to check for Exception
		boolean exception = false;

		// boolean to exit loop (success or failure)
		boolean done = false;
		
		// i less than... preference
		for (int i = 1; (!exception && !done && (i <= attempts)); i++) {
			post = new HttpFileMultipartPost();
			try {
				
				// we got HttpClient in the constructor
				//serverResponse = post.executeMultipartPackagePost(packagePath, serverAddress, httpClient);
				
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
		// fill in reports
		if (done)
			report.add("Package " + packagePath + " was succesfully delivered.");
		else // exception or error
			report.add("Package " + packagePath + " failed to be delievered.");
		return serverResponse;	
	}
	
	/*
	 * Runs on UI Thread
	 */
	@Override
	protected void onProgressUpdate(Integer... progress) {
		if (listener != null) {
			listener.progressUpdate(progress[0], progress[1]);
		}
    }
    
	
	/*
	 * Runs on UI Thread
	 */
	@Override
	protected void onPostExecute(String result) {
		if (listener != null) {
			listener.taskComplete(result);
		}
    }
}
