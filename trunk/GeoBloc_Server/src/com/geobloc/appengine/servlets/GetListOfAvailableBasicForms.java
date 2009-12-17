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
import com.google.appengine.api.datastore.KeyFactory;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class GetListOfAvailableBasicForms extends HttpServlet {
	
	// required for safe serializing 
	static final long serialVersionUID = 6376503374069212249L;
	
	@Override 
	public void doPost(HttpServletRequest request, HttpServletResponse resp)
    throws ServletException, IOException
    {
		DatastoreQueries datastoreQueries = new DatastoreQueries();
		Hashtable<String, String> map = new Hashtable<String, String>();
		String key;
		try {
    		List<BasicForm> forms = datastoreQueries.getListOfBasicForms();
    		for (BasicForm form : forms) {
    			key = KeyFactory.keyToString(form.getKey());
    			map.put(form.getName(), key);
    		}
    		
    	    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    	    ObjectOutputStream os = new ObjectOutputStream (bs);
    	    os.writeObject((Object)map);
    	    byte[] bytes =  bs.toByteArray(); // returns byte[]
    	    os.close();
    	    
    	    ServletOutputStream souts = resp.getOutputStream();
			souts.write(bytes);
			souts.flush();
			souts.close();
			bs.close();
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
