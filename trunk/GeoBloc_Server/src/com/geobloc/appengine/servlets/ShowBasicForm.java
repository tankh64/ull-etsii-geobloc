package com.geobloc.appengine.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geobloc.appengine.forms.BasicForm;
import com.geobloc.appengine.server.PMF;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

/**
 * 
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */

public class ShowBasicForm extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		
		String parameter = req.getParameter("key");
		out.println("<p>ParameterKey: " + parameter + "</p>");
		Key key = KeyFactory.stringToKey(parameter);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
    		
			BasicForm form = pm.getObjectById(BasicForm.class, key);
			
    		out.println("<p>--------------</p>");
			out.println("<p>fileName = " + form.getName() + "</p>");
			out.println("<p>fileAuthor = " + form.getAuthor() + "</p>");
			Blob blob = form.getFile();
			byte[] array = blob.getBytes();
			out.println("<p>contentLength = " + array.length + "</p>");
			//Text text = form.getXml();
			out.println("<p><a href=../basicFormsMain.jsp>Back to basicFormsMain</a></p>");
			//out.println("<p><PLAINTEXT>"+form.getXml().getValue());
			
		}
		finally {
			pm.close();
		}

	}
}
