package com.geobloc.appengine.servlets;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.geobloc.appengine.forms.BasicForm;
import com.geobloc.appengine.forms.BasicPackageForm;
import com.geobloc.appengine.server.PMF;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Allows the users to upload packages, made up of any files. Still working on package name.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */

@SuppressWarnings("serial")
public class UploadBasicFormServlet extends HttpServlet {
		
	// server OK Signature
	public static String __OK_SIGNATURE__ = "12122009_ALL_OK";
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
		
		try {
			ServletFileUpload upload = new ServletFileUpload();
			// 500KB
			upload.setSizeMax(500000);
			res.setContentType("text/plain");
			PrintWriter out = res.getWriter();
			// add OK Signature
			out.println(UploadBasicFormServlet.__OK_SIGNATURE__);
			try {
				
				FileItemIterator iterator = upload.getItemIterator(req);
				while (iterator.hasNext()) {
					FileItemStream item = iterator.next();
					InputStream in = item.openStream();

					if (item.isFormField()) {
						out.println("Got a form field: " + item.getFieldName());
					} else {
						
						String fieldName = item.getFieldName();
						String fileName = item.getName();
						String contentType = item.getContentType();

						try {
							Blob blob = new Blob(IOUtils.toByteArray(in));
							byte[] array = blob.getBytes();							
							out.println("length: " + array.length);
							if (array.length < 1)
								out.println("No content.");
							else {
								out.println("--------------");
								out.println("fileName = " + fileName);
								out.println("field name = " + fieldName);
								out.println("contentType = " + contentType);
								//out.println(fileContents);
								
								BasicForm form = new BasicForm(user, fileName, blob, new Date());
								PersistenceManager pm = PMF.get().getPersistenceManager();
								try {
									pm.makePersistent(form);
								}
								finally {
									pm.close();
								}
								
								out.println(form.getKey());
								//out.println(form.getXml());
							}
						} finally {
							IOUtils.closeQuietly(in);
						}

					}
				}
			} catch (SizeLimitExceededException e) {
				out.println("You exceeded the maximum size ("
						+ e.getPermittedSize() + ") of the file ("
						+ e.getActualSize() + ")");
			}
		} catch (Exception ex) {
			throw new ServletException(ex);
		}
	}
}



