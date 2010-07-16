/**
 * 
 */
package com.geobloc.db;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import com.geobloc.shared.IFormDefinition;
import com.geobloc.shared.IInstanceDefinition;
import com.geobloc.shared.IJavaToDatabaseForm;
import com.geobloc.shared.IJavaToDatabaseInstance;
import com.geobloc.shared.JavaForms;
import com.geobloc.shared.JavaInstances;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * A Java class which represents a row of the localforms database for our use in the UI. It is no a JavaBean, 
 * since it does a lot more than get() and set(); each DbFormInstance can load and save itself to the 
 * SQLiteDatabase.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class DbFormInstance implements IInstanceDefinition {
	private static final String LOG_TAG = "DbFormInstance";
	
	/*
	 * PUBLIC KEYS
	 * 
	 */
	
	/**
	 * Database key for the instance's local id.
	 */
	public static final String __LOCALPACKAGESDB_ID__ = "_id";
	/**
	 * Database key for the instance's attached form's local id. However, its information might not 
	 * be accurate, since the attached form might've been updated or even non-existent.
	 */
	public static final String __LOCALPACKAGESDB_FORM_ID_KEY__ = "localFormId";
	/**
	 * The version this instance is attached to, forever.
	 */
	public static final String __LOCALPACKAGES_DB_FORM_VERSION_KEY__ = "localFormVersion";
	/**
	 * Database key for the instance's label, which is also local and does not exist in the server.
	 */
	public static final String __LOCALPACKAGESDB_LABEL_KEY__ = "label";
	/**
	 * Database key for the instance's folder full path.
	 */
	public static final String __LOCALPACKAGESDB_PATH_KEY__ = "packageLocation";
	/**
	 * Database key for the date in which the instance was created.
	 */
	public static final String __LOCALPACKAGESDB_CREATEDDATE_KEY__ = "createdDate";
	/**
	 * Database key for the date in which the instance was marked as complete (could be null).
	 */
	public static final String __LOCALPACKAGESDB_COMPLETEDDATE_KEY__ = "completedDate";
	/**
	 * Database key for the full path to the packaged instance ZIP file (could be null if it hasn't been tried to send).
	 */
	public static final String __LOCALPACKAGESDB_COMPRESSEDPACKAGEFILE_KEY__ = "compressedPackageFile";
	/**
	 * Database key for the completed state of the instance.
	 */
	public static final String __LOCALPACKAGESDB_COMPLETED_KEY__ = "completed";
	
	/*
	 * MISSING form server key: form_id and form_version 
	 */
	
	private long id = -1;
	private long formVersion = -1;
	private DbForm form = null;
	private String label = "";
	private String packagePath = "";
	private String compressedPackageFileLocation = "";
	private Date createdDate;
	private Date completedDate;
	private boolean completed;
	
	/**
	 * Performs a query to return all locally registered instances. In other words, returns a cursor with all 
	 * the local instances.
	 * @param db An open database with reading permissions.
	 * @return A cursor containing a row for each instance registered in the database.
	 */
	public static Cursor getAll(SQLiteDatabase db) {
		Cursor c = db.rawQuery("SELECT * FROM " + DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__, null);
		return c;
	}
	/**
	 * Performs a query to return all completed instances.
	 * @param db An open database with reading permissions.
	 * @return A cursor containing a row for each completed instance registered in the database.
	 */
	public static Cursor getAllCompleted(SQLiteDatabase db) {
		Cursor c = db.rawQuery("SELECT * FROM " + DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__+ 
				" WHERE " + DbFormInstance.__LOCALPACKAGESDB_COMPLETED_KEY__, null);
		return c;
	}
	/**
	 * Returns a {@link DbFormInstance} object loaded from the database.
	 * @param instancesDb An open instances database with reading permissions.
	 * @param formsDb An open forms database with reading permissions.
	 * @param id The local id of the instance to load.
	 * @return A {@link DbFormInstance} object representing the row in the instances database with the specified local id.
	 */
	public static DbFormInstance loadFrom(SQLiteDatabase instancesDb, SQLiteDatabase formsDb, long id) {
		
		Cursor c = instancesDb.rawQuery("SELECT * FROM " + DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__ + 
				" WHERE " + DbFormInstance.__LOCALPACKAGESDB_ID__ + " = " + id, null);
		
		c.moveToFirst();
		if (c.isFirst()) {
			DbFormInstance dbi = new DbFormInstance();
			dbi.loadFrom(formsDb, c);
			c.close();
			return dbi;
		}
		c.close();
		return null;
	}
	/**
	 * Loads a row in the database representing an instance into the current {@link DbFormInstance} object.
	 * @param formsDb An open forms database with reading permissions.
	 * NOTE: The cursor passed as parameter is not closed nor deactivated within this call.
	 * @param c A cursor pointing to the row with the instance to load.
	 * @return A {@link DbFormInstance} object representing the instance the cursor is pointing to.
	 */
	public DbFormInstance loadFrom(SQLiteDatabase formsDb, Cursor c) {
		id = c.getLong(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_ID__));
		formVersion = c.getLong(c.getColumnIndex(DbFormInstance.__LOCALPACKAGES_DB_FORM_VERSION_KEY__));
		if (c.getLong(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_FORM_ID_KEY__)) != -1)
			form = DbForm.loadFrom(formsDb, c.getLong(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_FORM_ID_KEY__)));
		else 
			form = null;
		label=c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_LABEL_KEY__));
		packagePath = c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_PATH_KEY__));
		compressedPackageFileLocation = c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_COMPRESSEDPACKAGEFILE_KEY__));
		DateFormat df = DateFormat.getDateInstance();
		try {
			createdDate = df.parse(c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_CREATEDDATE_KEY__)));
		} catch (ParseException e) {
			Log.e(LOG_TAG, "Exception thrown while parsing createdDate");
			e.printStackTrace();
			createdDate = null;
		}
		df = DateFormat.getDateInstance();
		try {
			String s = c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_COMPLETEDDATE_KEY__));
			completedDate = null;
			if (s != null)
				completedDate = df.parse(c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_COMPLETEDDATE_KEY__)));
		} catch (ParseException e) {
			Log.e(LOG_TAG, "Exception thrown while parsing completedDate");
			completedDate = null;
		}
		
		int completedAux = c.getInt(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_COMPLETED_KEY__));
		completed = (completedAux == 1); // if completedAux == 0 false, if equals to 1 true
		
		return (this);
	}
	/**
	 * @return A {@link DbForm} object representing the form attached to this instance.
	 */
	public DbForm getForm() {
		return form;
	}
	/**
	 * Allows to change the form attached to the current object.
	 * @param form The form to attach to this instance.
	 */
	public void setForm(DbForm form) {
		this.form = form;
	}	
	/**
	 * 
	 * @return The full path of the compressed binary file containing the instance's data. It can be null if the ZIP file hasn't been created yet.
	 */
	public String getCompressedPackageFileLocation() {
		return compressedPackageFileLocation;
	}
	/**
	 * Allows us to set which is the binary file representing this instance. 
	 * @param compressedPackageFileLocation full path to the binary file.
	 */
	public void setCompressedPackageFileLocation(
			String compressedPackageFileLocation) {
		this.compressedPackageFileLocation = compressedPackageFileLocation;
	}
	/**
	 * 
	 * @return The {@link Date} in which this instance was created.
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	/**
	 * Allows us to set the date in which the instance was created.
	 * @param createdDate {@link Date} in which the instance was created.
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	/**
	 * 
	 * @return true if the instance has been marked as complete.
	 */
	public boolean isCompleted() {
		return completed;
	}
	
	@Override
	public Date getDate() {
		return completedDate;
	}

	@Override
	public IFormDefinition getForm_definition() {
		return (IFormDefinition) form;
	}

	@Override
	public long getInstance_local_id() {
		return id;
	}

	@Override
	public long getInstance_form_version() {
		return formVersion;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getPackage_path() {
		return this.packagePath;
	}

	@Override
	public boolean isComplete() {
		return completed;
	}

	@Override
	public void setComplete(boolean complete) {
		completed = complete;
	}

	@Override
	public void setDate(Date date) {
		completedDate = date;
	}

	@Override
	public void setForm_definition(IFormDefinition formDefinition) {
		form = (DbForm) formDefinition;
	}

	@Override
	public void setInstance_local_id(long instanceLocalId) {
		id = instanceLocalId;
	}
	
	@Override
	public void setInstance_form_version(long formVersion) {
		this.formVersion = formVersion;
	}
	
	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public void setPackage_path(String packagePath) {
		this.packagePath = packagePath;
	}
	
	public void delete(SQLiteDatabase db) {
		if (id != -1) {
			Log.i(LOG_TAG, "Deleting row in the database with id= " + id);
			db.delete(DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__,
					DbFormInstance.__LOCALPACKAGESDB_ID__ + " = " + id, null);
		}
	}
	/**
	 * Saves the current {@link DbFormInstance} object into the database. This operation does not throw exceptions, and the 
	 * result is written to the system log. The method will automatically create a new row if the instance has not been saved before 
	 * (id == -1) and will update it if the id is not -1.
	 * NOTE: There are no checks regarding the attached form. 
	 * @param db An open database with writing permissions.
	 */
	public void save(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		
		if (id != -1)
			cv.put(DbFormInstance.__LOCALPACKAGESDB_ID__, id);
		if (form != null)
			cv.put(DbFormInstance.__LOCALPACKAGESDB_FORM_ID_KEY__, form.getForm_id());
		else
			cv.put(DbFormInstance.__LOCALPACKAGESDB_FORM_ID_KEY__, -1);
		cv.put(DbFormInstance.__LOCALPACKAGES_DB_FORM_VERSION_KEY__, formVersion);
		cv.put(DbFormInstance.__LOCALPACKAGESDB_LABEL_KEY__, label);
		cv.put(DbFormInstance.__LOCALPACKAGESDB_PATH_KEY__, packagePath);
		cv.put(DbFormInstance.__LOCALPACKAGESDB_COMPRESSEDPACKAGEFILE_KEY__, compressedPackageFileLocation);
		
		String myDateString;
		myDateString = DateFormat.getDateInstance().format(createdDate);
		cv.put(DbFormInstance.__LOCALPACKAGESDB_CREATEDDATE_KEY__, myDateString);
		if (completedDate != null)
			myDateString = DateFormat.getDateInstance().format(completedDate);
		else
			myDateString = null;
		cv.put(DbFormInstance.__LOCALPACKAGESDB_COMPLETEDDATE_KEY__, myDateString);
		int completedAux = 0;
		if (completed)
			completedAux = 1;
		cv.put(DbFormInstance.__LOCALPACKAGESDB_COMPLETED_KEY__, completedAux);
		// if the DbFormInstance does not exist
		long res;
		if (id == -1) 
		{
			res = db.insert(DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__, 
				DbFormInstance.__LOCALPACKAGESDB_LABEL_KEY__, cv);
			if (res != -1) {
				id = res;
				Log.d(LOG_TAG, "Saved as a new row in the database succesfully.");
			}
			else
				Log.e(LOG_TAG, "Saving of the new row failed.");
		}
		else {
			// else we update
			res = db.update(DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__, cv
					, DbFormInstance.__LOCALPACKAGESDB_ID__ + " = " + id, null);
			if (res == 1)
				Log.d(LOG_TAG, "Update of row with id= " + id + " in the database was succesful.");
			else
				Log.e(LOG_TAG, "Update of row with id= " + id + " in the database failed.");
		}
	}
	/**
	 * Method designed to allow the parser to access the Instances database content without knowing its design 
	 * through an {@link Object} implementing the {@link IJavaToDatabaseInstance} interface.
	 * @param context The context in which the object will be used.
	 * @return An initialized object meeting the {@link IJavaToDatabaseInstance} interface.
	 */
	public static IJavaToDatabaseInstance getParserInterfaceInstance(Context context) {
		return new JavaInstances(context);
		
	}

}
