package com.geobloc.appengine.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geobloc.appengine.forms.BasicPackageForm;
import com.geobloc.appengine.server.PMF;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * Based on ShowBasicForm, this servlet allows the user to see the files stored inside a BasicPackageForm, and 
 * thanks to other servlet, also allows them to download any file they need.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */

public class ShowBasicPackageForm extends HttpServlet {
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
    		
			BasicPackageForm form = pm.getObjectById(BasicPackageForm.class, key);
			
    		out.println("<p>--------------</p>");
			out.println("<p>packageName = " + form.getName() + "</p>");
			out.println("<p>packageAuthor = " + form.getAuthor() + "</p>");
			out.println("");
			out.println("<p> Package contents:" + "</p>");
			List<String> filenames = form.getFilenames();
			//<p>An anonymous person uploaded: - <a href="show_basicpackageform?key=<%=key%>"> <%= form.getName() %>  </a></p>
			//for (String filename : filenames)
			for (int i = 0; i < filenames.size(); i++)
			{
				out.println("	<p><a href=get_basicpackageformfile?key=" + parameter + 
						"&fileId=" + i + ">" + filenames.get(i) + "</a></p>");
			}
			
			out.println("");
			out.println("<p> * End of package contents * </p>");			
		}
		finally {
			pm.close();
		}

	}
}
