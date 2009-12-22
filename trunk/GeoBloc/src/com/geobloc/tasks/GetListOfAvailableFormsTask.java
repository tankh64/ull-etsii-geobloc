/**
 * 
 */
package com.geobloc.tasks;

import java.util.Hashtable;

import org.apache.http.client.HttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.geobloc.ApplicationEx;
import com.geobloc.internet.SimpleHttpPost;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.shared.GBSharedPreferences;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class GetListOfAvailableFormsTask extends
		AsyncTask<String, Integer, Hashtable<String, String>> {

	private Context context;
	private int attempts;
	private HttpClient httpClient;
	
	
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
	 * Runs on Background Thread
	 */
	@Override
	protected Hashtable<String, String> doInBackground(String... packagePaths) {
		int count = packagePaths.length;
		Hashtable<String, String> data = null;
		if ((httpClient != null) && (context != null)) {
			initConfig();
			data = getHashtable(packagePaths[0]);
		}
		/*
		else {
			results =  "Error! Could not perform task due to missing parameters.";
		}
		*/
		return data;
	}
	
	private void initConfig() {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		//this.serverAddress = address;
		attempts = Integer.parseInt(prefs.getString(GBSharedPreferences.__NUMBER_OF_INTERNET_ATTEMPTS_KEY__, 
				GBSharedPreferences.__DEFAULT_NUMBER_OF_INTERNET_ATTEMPTS__));
	}
	
	private Hashtable<String, String> getHashtable(String url) {
		// boolean to check for Exception
		boolean exception = false;

		// boolean to exit loop (success or failrue)
		boolean done = false;
		
		SimpleHttpPost post;
		Hashtable<String, String> response = null;
		
		for (int i = 1; (!exception && !done && (i <= 3)); i++) {
			post = new SimpleHttpPost();
			// tell the handler in which attempt we are
			//handler.sendEmptyMessage(i);
			try {
				response = post.executeHttpPostAvailableForms(url, httpClient);
				// should be enough to check
				if (response != null)
					done = true;
			}
			catch (Exception e){
				// if exception, exit loop
				exception = true;
				e.printStackTrace();
				response = null;
			}
		}
		return response;
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
	protected void onPostExecute(Hashtable<String, String> result) {
		if (listener != null) {
			listener.taskComplete(result);
		}
    }
}
