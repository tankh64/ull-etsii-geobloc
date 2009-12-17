package com.geobloc.appengine.server;


import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geobloc.appengine.forms.BasicForm;
import com.google.appengine.api.datastore.KeyFactory;

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
		
		
		// Hashtable test lab
		Hashtable<String, String> map = new Hashtable<String, String>();
		DatastoreQueries datastoreQueries = new DatastoreQueries();
		String key;
		try {
    		List<BasicForm> forms = datastoreQueries.getListOfBasicForms();
    		for (BasicForm form : forms) {
    			key = KeyFactory.keyToString(form.getKey());
    			map.put(form.getName(), key);
    		}
		}
		finally {
			datastoreQueries.closeConnection();
		}
		
		resp.getWriter().println(map.toString());
		
	}

}



