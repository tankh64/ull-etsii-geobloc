/**
 * 
 */
package com.geobloc.internet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;

import com.geobloc.persistance.GeoBlocPackageManager;

/**
 * Class developed for testing; all internet connections to the server should be separated from the client's 
 * code because the server can change in the future. This class should be able to send an xml file to the server.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class HttpFileMultipartPost {
	//public void executeMultipartPost(List<String> files, String url)throws Exception     {
	public String executeMultipartPost(String fileDirectory, String file, String url, HttpClient httpClient)throws Exception     {
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
	
	public String executeMultipartPackagePost(String packageDirectory, String url, HttpClient httpClient) 
	throws Exception     {
		String stringResponse = "Error!";
		BufferedReader in = null;
		GeoBlocPackageManager toDeliver = new GeoBlocPackageManager();
		toDeliver.openPackage(packageDirectory);
		if (toDeliver.OK()) {
			try {

				List<String> filenames = toDeliver.getAllFilenames();
				List<File> files = toDeliver.getAllFiles();
				HttpPost postRequest = new HttpPost(url);
				MultipartEntity multipartContent = new MultipartEntity();
			
				InputStream is;
				InputStreamBody isb;
				byte[] data;
				for (int i = 0; i < files.size(); i++) {
					is = new FileInputStream(files.get(i));
					data = IOUtils.toByteArray(is);
					isb = new InputStreamBody(new ByteArrayInputStream(data), filenames.get(i));
					multipartContent.addPart(filenames.get(i), isb);
				}
			
				//InputStream is = this.getAssets().open("data.xml");
				//HttpClient httpClient = new DefaultHttpClient();
				//HttpPost postRequest = new HttpPost("http://192.178.10.131/WS2/Upload.aspx");
			
            
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
		}
		else {
			stringResponse = "Error! Could not open package.";
		}
        return stringResponse;
    }
}
