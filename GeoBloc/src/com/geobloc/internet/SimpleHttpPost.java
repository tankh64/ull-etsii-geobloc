/**
 * 
 */
package com.geobloc.internet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */

/*
 * PARTIAL WORK-AROUND
 */
public class SimpleHttpPost {
	@SuppressWarnings("unchecked")
	public Hashtable<String, String> executeHttpPostAvailableForms(String url, HttpClient httpClient) 
	throws Exception {
		Hashtable data = null;
		data = new Hashtable<String, String>();
		HttpPost postRequest = new HttpPost(url);
		// if we need to add user identification, we add it here
		//  verification info should be added to this formEntity
		// dummy info in formEntity
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>(); 
		postParameters.add(new BasicNameValuePair("device", "DEVICE_IMEI"));
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
		postRequest.setEntity(formEntity);
		HttpResponse response = httpClient.execute(postRequest);
		
		String res = EntityUtils.toString(response.getEntity());
		JSONArray array = new JSONArray(res);
		JSONObject o;
		for (int i = 0; i < array.length(); i++) {
			o = array.getJSONObject(i);
			data.put(o.get("gb_name"), "gb_form_id");
		}
		
		//JSONObject resp = new JSONObject();
		
		
		/*
		InputStream is = response.getEntity().getContent();
		byte[] bytes = IOUtils.toByteArray(is);
		
		ByteArrayInputStream bs = new ByteArrayInputStream(bytes); // bytes es el byte[]
		ObjectInputStream ois = new ObjectInputStream(bs);
		data = (Hashtable<String, String>) ois.readObject();
		ois.close();
		is.close();
		bs.close();
		*/

		return data;
	}
	
	public byte[] executeHttpPostFetchFile(String key, String url, HttpClient httpClient) 
	throws Exception {
		byte[] data = null;
		HttpPost postRequest = new HttpPost(url);
		//  verification info should be added to this formEntity
		// dummy info in formEntity
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>(); 
		postParameters.add(new BasicNameValuePair("device", "DEVICE_IMEI"));
		// key
		postParameters.add(new BasicNameValuePair("key", key));
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
		postRequest.setEntity(formEntity);
		HttpResponse response = httpClient.execute(postRequest);
		InputStream is = response.getEntity().getContent();
		data = IOUtils.toByteArray(is);
		
		is.close();
		return data;
	}
}
