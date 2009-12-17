package com.geobloc.appengine.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geobloc.appengine.forms.BasicForm;
import com.geobloc.appengine.server.PMF;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * Latest servlet. Allows the user to get a specific form from the server, providing the Key.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */

public class GetBasicFormFile extends HttpServlet {

	// required for safe serializing
	//private static final long serialVersionUID = 2253850561530384157L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {		
		
		String parameter = req.getParameter("key");
		
		Key key = KeyFactory.stringToKey(parameter);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
    		
			BasicForm form = pm.getObjectById(BasicForm.class, key);
			Blob blob = form.getFile();
			byte[] bytes = blob.getBytes();
			
			OutputStream outs = resp.getOutputStream();
			outs.write(bytes);
			outs.flush();
			outs.close();

		}
		finally {
			pm.close();
		}

	}
}
