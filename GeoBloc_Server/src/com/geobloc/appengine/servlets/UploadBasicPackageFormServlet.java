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

import com.geobloc.appengine.forms.BasicPackageForm;
import com.geobloc.appengine.server.PMF;
import com.google.appengine.api.datastore.Blob;
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
public class UploadBasicPackageFormServlet extends HttpServlet {
		
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
			out.println(UploadBasicPackageFormServlet.__OK_SIGNATURE__);
			try {
				
				FileItemIterator iterator = upload.getItemIterator(req);
				BasicPackageForm packageForm = new BasicPackageForm();
				int packageSize = 0;
				while (iterator.hasNext()) {
					FileItemStream item = iterator.next();
					InputStream in = item.openStream();
					
					
					if (item.isFormField()) {
						out.println("Got a form field: " + item.getFieldName());
					} else {
						out.println("Got a file");

						String fieldName = item.getFieldName();
						String fileName = item.getName();
						String contentType = item.getContentType();

						try {
							
							Blob blob = new Blob(IOUtils.toByteArray(in));
							//out.println(blob.toString());
							byte[] array = blob.getBytes();
							packageSize += array.length;
							out.println(array.length);
							
							if (array.length < 1)
								out.println("No content.");
							else {
								
								// there is content
								out.println("--------------");
								out.println("fileName = " + fileName);
								out.println("field name = " + fieldName);
								out.println("contentType = " + contentType);
								// add to package
								packageForm.addFile(blob, fileName);								
								out.println("");
								out.println("");
								
							}
							
							
						} finally {
							IOUtils.closeQuietly(in);
						}
						
					}
				}
				
				
				out.println("Got " + packageForm.getFilenames().size() + " files packaged for a total of " + packageSize + " bytes.");
				Date d = new Date();
				
				// set packageForm info
				packageForm.setName(d.toString()); // for the time being
				packageForm.setAuthor(user);
				packageForm.setDate(d);
				// make package persistent
				PersistenceManager pm = PMF.get().getPersistenceManager();
				try {
					pm.makePersistent(packageForm);
				}
				finally {
					pm.close();
				}
				
			} catch (SizeLimitExceededException e) {
				out.println("You exceeded the maximu size ("
						+ e.getPermittedSize() + ") of the file ("
						+ e.getActualSize() + ")");
						
			}
		} catch (Exception ex) {
			throw new ServletException(ex);
		}
	}
}



