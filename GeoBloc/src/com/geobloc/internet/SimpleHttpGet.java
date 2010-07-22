/**
 * 
 */
package com.geobloc.internet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * Class for testing http connection, strongly tied to the current Google App Engine Server.
 * This class will perform a get Request, and return the server's response with a String.
 * This class was built to perform a Get Request to GeoblocServlet1 and get its response prior to uploading 
 * the xml files.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class SimpleHttpGet {
	
	private static String LOG_TAG = "SimpleHttpGet";
	
	private String url;
	private HttpClient httpClient;
	private HashMap<String, String> header;
	
	
	private String executeHttpGetRequest() throws Exception {
		String resp = "Error!";
		//try {
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			if (header != null) {
				Iterator it = header.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();
					request.setHeader((String)e.getKey(), (String)e.getValue());
				}
			}
			HttpResponse response = httpClient.execute(request);
			resp = EntityUtils.toString(response.getEntity());
		return resp;
	}
	
	public String performHttpGetRequest(int noAttempts, String url, HttpClient httpClient, HashMap<String, String> header) {
		this.url = url;
		this.httpClient = httpClient;
		this.header = header;

		// boolean to exit loop (success or failure)
		boolean done = false;
		
		String response = null;
		
		for (int i = 1; (!done && (i <= noAttempts)); i++) {
			try {
				response = executeHttpGetRequest();
				// should be enough to check
				if (response != null)
					done = true;
			}
			catch (Exception e){
				Log.e(LOG_TAG, "Exception ocurred while executing HttpGet request to the server.");
				// if exception, exit loop
				done = true;
				e.printStackTrace();
				if (e.getMessage() != null)
					response = e.getMessage();
			}
		}
		return response;
	}
}
