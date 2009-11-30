package com.geobloc.appengine.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geobloc.appengine.forms.BasicForm;
import com.geobloc.appengine.server.PMF;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

/**
 * This servlet outputs a BasicForm from the datastore as html to the user. The BasicForm to be outputted is 
 * searched thanks to the key, which must be passed as a paremeter to the servlet. Currently, this servlet is 
 * called to show the uploaded files, since all of them are saved as BasicForms.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */

public class ShowBasicForm extends HttpServlet {
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
			out.println("<p>contentLength = " + form.getXml().getValue().length()+ "</p>");
			Text text = form.getXml();
			out.println("<p><a href=../main.jsp>Back to main</a></p>");
			out.println("<p><PLAINTEXT>"+form.getXml().getValue());
			
		}
		finally {
			pm.close();
		}

	}
}
