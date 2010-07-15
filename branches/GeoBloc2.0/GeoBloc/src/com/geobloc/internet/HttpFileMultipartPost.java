/**
 * 
 */
package com.geobloc.internet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;

import com.geobloc.shared.IInstanceDefinition;

/**
 * Class developed for testing; all internet connections to the server should be separated from the client's 
 * code because the server can change in the future. This class should be able to send files to the server.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class HttpFileMultipartPost {
	/*
	//public void executeMultipartPost(List<String> files, String url)throws Exception     {
	public String executeMultipartPost(String fileDirectory, String file, String url, HttpClient httpClient)
	throws Exception     {
		String stringResponse = "Error!";
		BufferedReader in = null;
		try {
			InputStream is = new FileInputStream(fileDirectory+file);
			//InputStream is = this.getAssets().open("data.xml");
			//HttpClient httpClient = new DefaultHttpClient();
			//HttpPost postRequest = new HttpPost("http://192.178.10.131/WS2/Upload.aspx");
			HttpPost postRequest = new HttpPost(url);
			byte[] data = IOUtils.toByteArray(is);
            InputStreamBody isb = new InputStreamBody(new ByteArrayInputStream(data),file);
            //StringBody sb1 = new StringBody("someTextGoesHere");
            //StringBody sb2 = new StringBody("someTextGoesHere too");
            MultipartEntity multipartContent = new MultipartEntity();
            multipartContent.addPart(file, isb);
            //multipartContent.addPart("one", sb1);
            //multipartContent.addPart("two", sb2);
            postRequest.setEntity(multipartContent);
            HttpResponse res = httpClient.execute(postRequest);
            in = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			
            stringResponse = sb.toString();
            
            httpClient.getConnectionManager().closeExpiredConnections();
            //res.getEntity().consumeContent();
            //res.getEntity().getContent().close();
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
        return stringResponse;
    }
	*/
	
	/**
	 * Method designed to perform the task of sending instances. It uses the Apache Commons Multipart Entity 
	 * library, and basically adds the post parameters & files into the multipart entity, executes the post 
	 * request and returns the server's response. If something goes wrong before the request is executed, 
	 * the a null object will be returned.
	 * @param instance The Object representing the instance we're sending
	 * @param files The list of files to be included in the post request.
	 * @param url Server URL address.
	 * @param httpClient {@link HttpClient} object to perform the internet request.
	 * @return An {@link HttpResponse} Object if the request was performed or null if something went wrong internally.
	 */
	public HttpResponse executeMultipartPackagePost(IInstanceDefinition instance, List<File> files, String url, HttpClient httpClient) 
	throws Exception     {
		try {
			HttpPost postRequest = new HttpPost(url);
			MultipartEntity multipartContent = new MultipartEntity();
			
			InputStream is;
			InputStreamBody isb;
			byte[] data;
			for (int i = 0; i < files.size(); i++) {
				is = new FileInputStream(files.get(i));
				data = IOUtils.toByteArray(is);
				isb = new InputStreamBody(new ByteArrayInputStream(data), files.get(i).getName());
				multipartContent.addPart(files.get(i).getName(), isb);
			}
            
			postRequest.setEntity(multipartContent);
			HttpResponse res = httpClient.execute(postRequest);
			
			httpClient.getConnectionManager().closeExpiredConnections();
			return res;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
}
