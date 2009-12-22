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
import com.geobloc.internet.SimpleHttpPost;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.shared.GBSharedPreferences;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class DownloadFormsTask extends AsyncTask<String, Integer, String> {
	
	private Context context;
	private String serverAddress = "";
	private String formsPath;
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
	
	@Override
	protected String doInBackground(String... files) {
		int count = files.length;
		String results = "";
		if ((httpClient != null) && (context != null)) {
			initConfig();
			// files[i] == file key (server request), files[i+] == filename (for writing to SDCard)
			for (int i = 0; i < count; i+=2) {
				// sendPackage updates the report ArrayList
				fetchFile(files[i], files[i+1]);
				publishProgress((int) ((i*2 / (float) count) * 100), count);
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
		serverAddress = prefs.getString(GBSharedPreferences.__DOWNLOAD_FORMS_SERVLET_ADDRESS_KEY__, 
				GBSharedPreferences.__DEFAULT_DOWNLOAD_FORMS_SERVLET_ADRESS__);
		
		//this.serverAddress = address;
		attempts = Integer.parseInt(prefs.getString(GBSharedPreferences.__NUMBER_OF_INTERNET_ATTEMPTS_KEY__, 
				GBSharedPreferences.__DEFAULT_NUMBER_OF_INTERNET_ATTEMPTS__));
		
		formsPath = prefs.getString(GBSharedPreferences.__FORMS_PATH_KEY__, 
				GBSharedPreferences.__DEFAULT_FORMS_PATH__);
	}
	
	private void fetchFile(String fileKey, String filename) {
		SimpleHttpPost post;
		
		byte[] serverResponse = null;
		
		// boolean to check for Exception
		boolean exception = false;

		// boolean to exit loop (success or failure)
		boolean done = false;
		
		// i less than... preference
		for (int i = 1; (!exception && !done && (i <= attempts)); i++) {
			post = new SimpleHttpPost();
			try {
				// we got HttpClient in the constructor
				serverResponse = post.executeHttpPostFetchFile(fileKey, serverAddress, httpClient);
				
				// should be enough to check
				if (serverResponse != null) {
					done = true;
				}
			}
			catch (Exception e){
				// if exception, exit loop
				exception = true;
				e.printStackTrace();
			}
		}
		
		GeoBlocPackageManager pm = new GeoBlocPackageManager();
		pm.openPackage(formsPath);
		if (pm.OK()) {
			pm.addFile(filename, serverResponse);
		}
		else
			exception = true;
		// fill in reports
		if (done)
			report.add("File " + filename + " was succesfully downloaded.\n");
		else // exception or error
			report.add("File " + filename + " failed to be downloaded\n");
		//return serverResponse;	
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
