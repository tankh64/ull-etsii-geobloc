package com.geobloc.appengine.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geobloc.appengine.forms.BasicPackageForm;
import com.geobloc.appengine.server.PMF;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * Latest servlet. Allows the user to get a file from a BasicPackageForm, no matter its type.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */

public class GetBasicPackageFormFile extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {		
		
		String parameter = req.getParameter("key");
		String stringFileId = req.getParameter("fileId");
		int fileId = Integer.parseInt(stringFileId);
		
		Key key = KeyFactory.stringToKey(parameter);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
    		
			BasicPackageForm form = pm.getObjectById(BasicPackageForm.class, key);
			// look for the requested file
			List<Blob> files = form.getFiles();
			List<String> fileNames = form.getFilenames();
			if ((fileId < files.size() && (fileId < fileNames.size()))) {
				// get the file
				Blob file = new Blob(files.get(fileId).getBytes());
							
				//send the file
				resp.setContentType("application/x-download"); // x-download forces browser to download the file, instead of octet
				resp.setHeader("Content-Disposition", "attachment; filename=" + fileNames.get(fileId));
				OutputStream outs = resp.getOutputStream();
				outs.write(file.getBytes());
			}
			else {
				// file not found
				resp.setContentType("text/plain");
				resp.getWriter().println("Sorry! File not found.");
			}
		
		}
		finally {
			pm.close();
		}

	}
}
