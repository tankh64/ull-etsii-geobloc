/**
 * 
 */
package com.geobloc.internet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
	public String ExecuteHttpGet(String url) throws Exception {
		String respString = "Error!";
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI((new URI(url)));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			
			respString = sb.toString();			
		}
		finally {
			if (in != null)
				try {
					in.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
		}
		return respString;
	}
}
