/**
 * 
 */
package com.geobloc.shared;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.geobloc.db.DbForm;
import com.geobloc.db.DbFormInstance;
import com.geobloc.db.DbFormInstanceSQLiteHelper;
import com.geobloc.form.FormClass;
import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.persistance.SDFilePersistance;
import com.geobloc.xml.IField;
import com.geobloc.xml.MultiField;
import com.geobloc.xml.TextMultiField;
import com.geobloc.xml.TextXMLWriter;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class JavaInstances implements IJavaToDatabaseInstance {

	private SQLiteDatabase db;
	private Context context;
	private SharedPreferences prefs;
	private GeoBlocPackageManager pm;
	
	public JavaInstances(Context context){
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		db = (new DbFormInstanceSQLiteHelper(this.context)).getWritableDatabase();
		pm = new GeoBlocPackageManager();
	}
	
	/* (non-Javadoc)
	 * @see com.geobloc.shared.IJavaToDatabaseInstance#close()
	 */
	@Override
	public void close() {
		db.close();
	}

	/* (non-Javadoc)
	 * @see com.geobloc.shared.IJavaToDatabaseInstance#deleteInstance(long)
	 */
	@Override
	public void deleteInstance(long instanceLocalId) throws Exception {
		DbFormInstance dbi = DbFormInstance.loadFrom(db, instanceLocalId);
		if (dbi == null)
			throw new Exception("The instance does not exist. Load operation returned null.");
		// first, we erase from external storage
		pm.openOrBuildPackage(prefs.getString(GBSharedPreferences.__PACKAGES_PATH_KEY__, GBSharedPreferences.__DEFAULT_PACKAGES_PATH__));
		String[] strings = dbi.getPackage_path().split("/");
		if (!pm.eraseDirectory(strings[strings.length-1]))
			throw new Exception("Could not perform operation. Delete operation in external storage failed.");
		// erased from external storage, now we erase the entry
		dbi.delete(db);
	}

	/* (non-Javadoc)
	 * @see com.geobloc.shared.IJavaToDatabaseInstance#getListOfLocalInstances()
	 */
	@Override
	public List<IInstanceDefinition> getListOfLocalInstances() throws Exception {
		ArrayList<IInstanceDefinition> list = new ArrayList<IInstanceDefinition>();
		Cursor c = DbFormInstance.getAll(db);
		DbFormInstance dbi;
		c.moveToFirst();
		while (!c.isAfterLast()) {
			dbi = new DbFormInstance();
			dbi.loadFrom(db, c);
			list.add((IInstanceDefinition)dbi);
			c.moveToNext();
		}
		//c.close();
		return list;
	}

	/* (non-Javadoc)
	 * @see com.geobloc.shared.IJavaToDatabaseInstance#newInstance(long)
	 */
	@Override
	public IInstanceDefinition newInstance(long formLocalId) throws Exception {
		DbForm dbf = DbForm.loadFrom(db, formLocalId);
		if (dbf == null)
			throw new Exception("The form does not exist. Load operation returned null.");
		// form exists, now let's create the package in the external storage
		String path =  prefs.getString(GBSharedPreferences.__PACKAGES_PATH_KEY__, GBSharedPreferences.__DEFAULT_PACKAGES_PATH__);
		String packageName = Utilities.buildPackageName(context, dbf.getForm_id());
		path += packageName;
		if (!pm.openOrBuildPackage(path))
			throw new Exception("Could not create package directory.");
		// add the form.xml file to the package
		if (!SDFilePersistance.copyFile(dbf.getForm_file_path(), pm.getPackageFullpath()+"form.xml")) {
			pm.openOrBuildPackage(prefs.getString(GBSharedPreferences.__PACKAGES_PATH_KEY__, GBSharedPreferences.__DEFAULT_PACKAGES_PATH__));
			pm.eraseDirectory(packageName.substring(0, packageName.length()-1));
			throw new Exception("Could not finish creating package. Copy operation of the instance's form failed.");
		}
		// package is open, now let's handle the database
		DbFormInstance dbi = new DbFormInstance();
		dbi.setLabel(packageName.substring(0, packageName.length()-1));
		dbi.setForm(dbf);
		dbi.setInstance_form_version(dbf.getForm_version());
		dbi.setCompressedPackageFileLocation(null);
		dbi.setPackage_path(path);
		dbi.setComplete(false);
		dbi.setCreatedDate(new Date());
		dbi.setDate(null);
		dbi.save(db);
		// we need to reload
		dbi = DbFormInstance.loadFrom(db, dbi.getInstance_local_id());
		if (dbi == null) {
			pm.openOrBuildPackage(prefs.getString(GBSharedPreferences.__PACKAGES_PATH_KEY__, GBSharedPreferences.__DEFAULT_PACKAGES_PATH__));
			pm.eraseDirectory(packageName.substring(0, packageName.length()-1));
			throw new Exception("Could not create instance. Database operation failed.");
		}
		return (IInstanceDefinition) dbi;
	}

	/* (non-Javadoc)
	 * @see com.geobloc.shared.IJavaToDatabaseInstance#saveInstance(com.geobloc.shared.IInstanceDefinition, com.geobloc.form.FormClass)
	 */
	@Override
	public void saveInstance(IInstanceDefinition instance, FormClass form)
			throws Exception {
		DbFormInstance dbi = (DbFormInstance) instance;
		// build XML file
		List<IField> myFields = new ArrayList<IField>();
		TextMultiField instanceFields = new TextMultiField("gb_instance");
		
		instanceFields.addTag("gb_scheme", instance.getForm_definition().getForm_name());
		instanceFields.addTag("gb_device", Utilities.getDeviceID(context));
		instanceFields.addTag("gb_formId", instance.getForm_definition().getForm_id());
		instanceFields.addTag("gb_formVersion", Long.toString(instance.getInstance_form_version()));
		
		String complete = "";
		if (instance.isComplete())
			complete = "TRUE";
		else
			complete = "FALSE";
		instanceFields.addTag("gb_complete", complete);
		myFields.add(instanceFields);
		
		// saving
		TextXMLWriter writer = new TextXMLWriter();
    	String xml = writer.WriteXML(myFields);
    	if (pm.openPackage(instance.getPackage_path())) {
    		if (!pm.addFile("instance.xml", xml))
    			throw new Exception ("Could not write instance.xml file");
    	}
    	else
    		throw new Exception ("Could not open instance package/folder.");
		// update instance copy in the database
		dbi.setDate(new Date());
		dbi.save(db);
	}

}
