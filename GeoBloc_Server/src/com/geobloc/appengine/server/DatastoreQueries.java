/**
 * 
 */
package com.geobloc.appengine.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.geobloc.appengine.forms.BasicForm;
import com.geobloc.appengine.forms.BasicPackageForm;

/**
 * Class which allows us to perform queries without changing our JSP files.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class DatastoreQueries {
	
	private PersistenceManager pm;
	
	public DatastoreQueries() {
		// nothing to do
	}
	
	/*
	 *  Get all the BasicForms(XML Strings) from the Datastore
	 */	
	public List<BasicForm> getListOfBasicForms() {
		List<BasicForm> forms = new ArrayList<BasicForm>();
		try {
			pm = PMF.get().getPersistenceManager();
			String query = "select from " + BasicForm.class.getName() + " order by date desc range 0,5";
			forms = (List<BasicForm>) pm.newQuery(query).execute();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    	return forms;
	}
	
	/*
	 * Gett all the BasicPackageForms from the Datastore
	 */
	public List<BasicPackageForm> getListOfBasicPackageForms() {
		List<BasicPackageForm> forms = new ArrayList<BasicPackageForm>();
		try {
			pm = PMF.get().getPersistenceManager();
			String query = "select from " + BasicPackageForm.class.getName() + " order by date desc range 0,5";
			forms = (List<BasicPackageForm>) pm.newQuery(query).execute();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    	return forms;
	}
	
	/*
	 * Remember to call this method after each and every query ends.
	 */
	public void closeConnection() {
		if (!pm.isClosed())
			pm.close();
	}
}
