package dh.geobloc.appengine.server;


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

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Text;

/**
 * Basic servlet, used primarily for testing and learning.
 * 	doGet -> Used to test passing parameters to the servlet through an html link.
 * 	doPost -> Servlet receives a form and saves the data as BasicForms into the datastore. Note: Every file is 
 * 		saved as a new BasicForm instead of treating all the uploaded files as a whole package; 
 * 		this will be corrected in later revisions.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */

@SuppressWarnings("serial")
public class GeoBloc_Server1Servlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		resp.getWriter().println("<p>Hello, world</p>");
		String param = req.getParameter("firstname");
		resp.getWriter().println("<p>Parameter: " + param + "</p>");
		
		resp.getWriter().println();
		resp.getWriter().println("<p><a href=/main.jsp>Back to main</a></p>");
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
		
		try {
			ServletFileUpload upload = new ServletFileUpload();
			// 50KB
			upload.setSizeMax(50000);
			res.setContentType("text/plain");
			PrintWriter out = res.getWriter();
			
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

						String fileContents = null;
						try {
							fileContents = IOUtils.toString(in);
							Text text = new Text(fileContents);
							out.println("length: " + fileContents.length());
							if (fileContents.length() < 1)
								out.println("No content.");
							else {
								out.println("--------------");
								out.println("fileName = " + fileName);
								out.println("field name = " + fieldName);
								out.println("contentType = " + contentType);
								//out.println(fileContents);
								
								BasicForm form = new BasicForm(user, fileName, text, new Date());
								PersistenceManager pm = PMF.get().getPersistenceManager();
								try {
									pm.makePersistent(form);
								}
								finally {
									pm.close();
								}
								
								out.println(form.getKey());
								out.println(form.getXml());
							}
						} finally {
							IOUtils.closeQuietly(in);
						}

					}
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



