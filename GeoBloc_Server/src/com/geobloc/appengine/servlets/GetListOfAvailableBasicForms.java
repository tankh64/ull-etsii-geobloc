/**
 * 
 */
package com.geobloc.appengine.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geobloc.appengine.forms.BasicForm;
import com.geobloc.appengine.server.DatastoreQueries;
import com.google.appengine.api.datastore.Blob;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class GetListOfAvailableBasicForms extends HttpServlet {
	@Override 
	public void doPost(HttpServletRequest request, HttpServletResponse resp)
    throws ServletException, IOException
    {
		DatastoreQueries datastoreQueries = new DatastoreQueries();
		Hashtable<String, String> map = new Hashtable();
		try {
    		List<BasicForm> forms = datastoreQueries.getListOfBasicForms();
    		for (BasicForm form : forms) {
    			map.put(form.getName(), form.getKey().toString());
    		}
    		
    	    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    	    ObjectOutputStream os = new ObjectOutputStream (bs);
    	    os.writeObject((Object)map);
    	    os.close();
    	    byte[] bytes =  bs.toByteArray(); // returns byte[]
    	    
    	    
    	    ServletOutputStream souts = resp.getOutputStream();
			souts.write(bytes);
			souts.flush();
			souts.close();
		}
		catch (Exception e) {
			resp.setContentType("text/plain");
			resp.getWriter().println("Exception ocurred. Could not perform operation");
			resp.getWriter().println(e.toString());
		}
		finally {
			datastoreQueries.closeConnection();
		}
	}
}
