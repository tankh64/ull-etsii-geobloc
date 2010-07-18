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
import android.util.Log;

import com.geobloc.db.DbForm;
import com.geobloc.db.DbFormInstance;
import com.geobloc.db.DbFormInstanceSQLiteHelper;
import com.geobloc.db.DbFormSQLiteHelper;
import com.geobloc.form.FormClass;
import com.geobloc.form.FormDataPage;
import com.geobloc.form.FormPage;
import com.geobloc.form.FormPage.PageType;
import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.persistance.SDFilePersistance;
import com.geobloc.xml.IField;
import com.geobloc.xml.TextMultiField;
import com.geobloc.xml.TextXMLWriter;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class JavaInstances implements IJavaToDatabaseInstance {
	private static final String LOG_TAG = "JavaInstances";

	private SQLiteDatabase formsDb, instancesDb;
	private Context context;
	private SharedPreferences prefs;
	private GeoBlocPackageManager pm;
	
	public JavaInstances(Context context){
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		formsDb = (new DbFormSQLiteHelper(this.context)).getReadableDatabase();
		instancesDb = (new DbFormInstanceSQLiteHelper(this.context)).getWritableDatabase();
		pm = new GeoBlocPackageManager();
	}
	
	/* (non-Javadoc)
	 * @see com.geobloc.shared.IJavaToDatabaseInstance#close()
	 */
	@Override
	public void close() {
		Log.i(LOG_TAG, "Closing databases.");
		formsDb.close();
		instancesDb.close();
	}

	/* (non-Javadoc)
	 * @see com.geobloc.shared.IJavaToDatabaseInstance#deleteInstance(long)
	 */
	@Override
	public void deleteInstance(long instanceLocalId) throws Exception {
		Log.i(LOG_TAG, "Deletion of instance with locald='" + instanceLocalId + "' requested.");
		DbFormInstance dbi = DbFormInstance.loadFrom(instancesDb, formsDb, instanceLocalId);
		if (dbi == null) {
			Log.e(LOG_TAG, "Cannot perform delete operation. The instance does not exist.");
			throw new Exception("The instance does not exist. Load operation returned null.");
		}
		// first, we erase from external storage
		pm.openOrBuildPackage(prefs.getString(GBSharedPreferences.__PACKAGES_PATH_KEY__, GBSharedPreferences.__DEFAULT_PACKAGES_PATH__));
		String[] strings = dbi.getPackage_path().split("/");
		if (!pm.eraseDirectory(strings[strings.length-1])) {
			Log.e(LOG_TAG, "Cannot perform delete operation. Delete in external storage failed.");
			throw new Exception("Could not perform operation. Delete operation in external storage failed.");
		}
		// erased from external storage, now we erase the entry
		dbi.delete(instancesDb);
	}

	/* (non-Javadoc)
	 * @see com.geobloc.shared.IJavaToDatabaseInstance#getListOfLocalInstances()
	 */
	@Override
	public List<IInstanceDefinition> getListOfLocalInstances() throws Exception {
		Log.i(LOG_TAG, "List of local instances requested.");
		ArrayList<IInstanceDefinition> list = new ArrayList<IInstanceDefinition>();
		Cursor c = DbFormInstance.getAll(instancesDb);
		DbFormInstance dbi;
		c.moveToFirst();
		while (!c.isAfterLast()) {
			dbi = new DbFormInstance();
			dbi.loadFrom(instancesDb, c);
			list.add((IInstanceDefinition)dbi);
			c.moveToNext();
		}
		c.close();
		return list;
	}

	/* (non-Javadoc)
	 * @see com.geobloc.shared.IJavaToDatabaseInstance#newInstance(long)
	 */
	@Override
	public IInstanceDefinition newInstance(long formLocalId) throws Exception {
		Log.i(LOG_TAG, "New instance from form with locald='" + formLocalId + "' requested.");
		DbForm dbf = DbForm.loadFrom(formsDb, formLocalId);
		if (dbf == null) {
			Log.e(LOG_TAG, "Could not create instance. The form does not exist.");
			throw new Exception("The form does not exist. Load operation returned null.");
		}
		// form exists, now let's create the package in the external storage
		String path =  prefs.getString(GBSharedPreferences.__PACKAGES_PATH_KEY__, GBSharedPreferences.__DEFAULT_PACKAGES_PATH__);
		String packageName = Utilities.buildPackageName(context, dbf.getForm_id());
		path += packageName;
		if (!pm.openOrBuildPackage(path)) {
			Log.e(LOG_TAG, "Could not create instance. Could not create package directory..");
			throw new Exception("Could not create package directory.");
		}
		// add the form.xml file to the package
		if (!SDFilePersistance.copyFile(dbf.getForm_file_path(), pm.getPackageFullpath()+"form.xml")) {
			Log.e(LOG_TAG, "Could not create instance. Copy operation of the instance0s form file failed.");
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
		dbi.save(instancesDb);
		// we need to reload
		dbi = DbFormInstance.loadFrom(instancesDb, formsDb, dbi.getInstance_local_id());
		if (dbi == null) {
			Log.e(LOG_TAG, "Could not create instance. Database operation failed.");
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
		Log.i(LOG_TAG, "Saving of instance with locald='" + instance.getInstance_local_id() + "' requested.");
		DbFormInstance dbi = (DbFormInstance) instance;
		XMLBuilder builder = new XMLBuilder(context);
		String xml = builder.transformToXML(instance, form);
    	if (pm.openPackage(instance.getPackage_path())) {
    		if (!pm.addFile("instance.xml", xml)) {
    			Log.e(LOG_TAG, "Save operation failed. Could not write 'instance.xml' file.");
    			throw new Exception ("Could not write 'instance.xml' file");
    		}
    	}
    	else {
    		Log.e(LOG_TAG, "Save operation failed. Could not open instance package/folder");
    		throw new Exception ("Could not open instance package/folder.");
    	}
		// update instance copy in the database
		dbi.setDate(new Date());
		dbi.save(instancesDb);
	}

}
