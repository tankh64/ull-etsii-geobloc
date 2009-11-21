package dh.geobloc.appengine.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

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
