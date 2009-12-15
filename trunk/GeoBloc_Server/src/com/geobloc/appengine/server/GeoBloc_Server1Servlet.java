package com.geobloc.appengine.server;


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
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

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
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		resp.getWriter().println("<p>Hello, world</p>");
		String param = req.getParameter("firstname");
		resp.getWriter().println("<p>Parameter: " + param + "</p>");
		
		resp.getWriter().println();
		resp.getWriter().println("<p><a href=/main.jsp>Back to main</a></p>");
	}

}



