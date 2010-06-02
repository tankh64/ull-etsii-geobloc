package com.geobloc.db;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geobloc.shared.IFormDefinition;

/**
 * Class to represent the basic info of a form to be used by different classes.

 * 
 * @author Jorge Carballo
 * @author Dinesh Harjani
 *
 */
public class DbForm implements IFormDefinition {
	private static final String LOG_TAG = "DbForm";
	
	/*
	 * PUBLIC KEYS
	 * 
	 */
	
	// database private primary key
	public static final String __LOCALFORMSDB_ID_KEY__ = "_id";
	// server primary key
	public static final String __LOCALFORMSDB_FORM_ID_KEY__ = "form_id";
	// server primary key
	public static final String __LOCALFORMSDB_FORM_VERSION_KEY__ = "form_version";
	// full path to form.xml file in SD Card
	public static final String __LOCALFORMSDB_FORM_FILE_PATH__ = "form_file_path";
	// form given name
	public static final String __LOCALFORMSDB_FORM_NAME_KEY__ = "form_name";
	// form description info
	public static final String __LOCALFORMSDB_FORM_DESCRIPTION_KEY__ = "form_description";
	// form upload date given by server
	public static final String __LOCALFORMSDB_FORM_DATE_KEY__ = "form_date";
	/*
	 * Can take the following values:
	 * 0 -> new (we don't have it)
	 * 1 -> latest version
	 * 2 -> there's a more recent version
	 * 3 -> not found in server
	 */
	public static final int __FORM_SERVER_STATE_NEW__ = 0;
	public static final int __FORM_SERVER_STATE_LATEST_VERSION__ = 1;
	public static final int __FORM_SERVER_STATE_MORE_RECENT_AVAILABLE__ = 2;
	public static final int __FORM_SERVER_STATE_NOT_FOUND__ = 3;
	public static final String __LOCALFORMSDB_FORM_SERVERSTATE_KEY__ = "server_state";


	private long id = -1;
	private String form_id; // id in server
	private int form_version; // version in server
	private String form_file_path;
	private String form_name;
	private String form_description;
	private Date form_date;
	private int form_server_state;
	
	public DbForm() {
		super();
	}
	
	public DbForm(String formName, String formId, int formVersion, String formFilePath, 
			String formDescription, Date formDate, long formLocalId, int serverState) {
		super();
		form_name = formName;
		form_id = formId;
		form_file_path = formFilePath;
		form_version = formVersion;
		form_description = formDescription;
		form_date = formDate;
		id = formLocalId;
		form_server_state = serverState;
	}
	
	
	public static Cursor getAll(SQLiteDatabase db) {
		Cursor c = db.rawQuery("SELECT * FROM " + DbFormSQLiteHelper.__LOCALFORMSDB_TABLE_NAME__, null);
		return c;
		
	}
	
	
	/*
	public static Cursor getAllLocal(SQLiteDatabase db) {
		Cursor c = db.rawQuery("SELECT * FROM " + DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__+ 
				" WHERE " + DbFormInstance.__LOCALPACKAGESDB_COMPLETED_KEY__, null);
		return c;
		
	}
	*/
	
	
	public static DbForm loadFrom(SQLiteDatabase db, long id) {
		
		/*
		String[] DBFORM_FROM = {DbFormInstance.__LOCALPACKAGESDB_ID__, DbFormInstance.__LOCALPACKAGESDB_NAME_KEY__,
				DbFormInstance.__LOCALPACKAGESDB_CREATEDDATE_KEY__, DbFormInstance.__LOCALPACKAGESDB_COMPLETEDDATE_KEY__, 
				DbFormInstance.__LOCALPACKAGESDB_LOCATION_KEY__, DbFormInstance.__LOCALPACKAGESDB_COMPRESSEDPACKAGEFILE_KEY__, 
				DbFormInstance.__LOCALPACKAGESDB_LOCATION_KEY__, };
		*/
		
		Cursor c = db.rawQuery("SELECT * FROM " + DbFormSQLiteHelper.__LOCALFORMSDB_TABLE_NAME__ + 
				" WHERE " + DbForm.__LOCALFORMSDB_ID_KEY__ + " = " + id, null);
		
		c.moveToFirst();
		if (c.isFirst()) {
			DbForm dbf = new DbForm();
			dbf.loadFrom(c);
			return dbf;
		}
		else
			return null;
		
	}
	
	public static DbForm findByFormId(SQLiteDatabase db, String form_id) {
		Cursor c = db.rawQuery("SELECT * FROM " + DbFormSQLiteHelper.__LOCALFORMSDB_TABLE_NAME__ + 
				" WHERE " + DbForm.__LOCALFORMSDB_FORM_ID_KEY__ + " = '" + form_id + "'", null);
		
		c.moveToFirst();
		if (c.isFirst()) {
			DbForm dbf = new DbForm();
			dbf.loadFrom(c);
			return dbf;
		}
		else
			return null;
	}
	
	
	public DbForm loadFrom(Cursor c) {
		id = c.getLong(c.getColumnIndex(DbForm.__LOCALFORMSDB_ID_KEY__));
		form_id = c.getString(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_ID_KEY__));
		form_version = c.getInt(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_VERSION_KEY__));
		form_file_path = c.getString(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_FILE_PATH__));
		form_name = c.getString(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_NAME_KEY__));
		form_description = c.getString(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_DESCRIPTION_KEY__));
		
		DateFormat df = DateFormat.getDateInstance();
		try {
			form_date = df.parse(c.getString(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_DATE_KEY__)));
		} catch (ParseException e) {
			Log.e(LOG_TAG, "Exception thrown while parsing form_date");
			e.printStackTrace();
			form_date = null;
		}
		
		form_server_state = c.getInt(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_SERVERSTATE_KEY__));
		
		return (this);
	}
	
	
	public String getForm_name() {
		return form_name;
	}
	public void setForm_name(String formName) {
		form_name = formName;
	}
	public String getForm_id() {
		return form_id;
	}
	public void setForm_id(String formId) {
		form_id = formId;
	}
	public String getForm_file_path() {
		return form_file_path;
	}

	public void setForm_file_path(String formFilePath) {
		form_file_path = formFilePath;
	}

	public int getForm_version() {
		return form_version;
	}
	public void setForm_version(int formVersion) {
		form_version = formVersion;
	}
	public String getForm_description() {
		return form_description;
	}
	public void setForm_description(String formDescription) {
		form_description = formDescription;
	}
	public Date getForm_date() {
		return form_date;
	}
	public void setForm_date(Date formDate) {
		form_date = formDate;
	}
	public long getForm_local_id() {
		return id;
	}
	public void setForm_local_id(long formLocalId) {
		id = formLocalId;
	}
	public int getServer_state() {
		return form_server_state;
	}
	public void setServer_state(int serverState) {
		form_server_state = serverState;
	}
	
	
	public void delete(SQLiteDatabase db) {
		if (id != -1) {
			Log.i(LOG_TAG, "Deleting row in the database with id= " + id);
			db.delete(DbFormSQLiteHelper.__LOCALFORMSDB_TABLE_NAME__,
					DbForm.__LOCALFORMSDB_ID_KEY__ + " = " + id, null);
		}
	}
	
	
	
	public void save(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		
		if (id != -1)
			cv.put(DbForm.__LOCALFORMSDB_ID_KEY__, id);
		cv.put(DbForm.__LOCALFORMSDB_FORM_ID_KEY__, form_id);
		cv.put(DbForm.__LOCALFORMSDB_FORM_VERSION_KEY__, form_version);
		cv.put(DbForm.__LOCALFORMSDB_FORM_FILE_PATH__, form_file_path);
		cv.put(DbForm.__LOCALFORMSDB_FORM_NAME_KEY__, form_name);
		cv.put(DbForm.__LOCALFORMSDB_FORM_DESCRIPTION_KEY__, form_description);
		
		String myDateString;
		myDateString = DateFormat.getDateInstance().format(new Date());
		cv.put(DbForm.__LOCALFORMSDB_FORM_DATE_KEY__, myDateString);
		cv.put(DbForm.__LOCALFORMSDB_FORM_SERVERSTATE_KEY__, form_server_state);

		// if the DbFormInstance does not exist
		long res;
		if (id == -1) 
		{
			res = db.insert(DbFormSQLiteHelper.__LOCALFORMSDB_TABLE_NAME__, 
				DbForm.__LOCALFORMSDB_FORM_NAME_KEY__, cv);
			if (res != -1)
				Log.d(LOG_TAG, "Saved as a new row in the database succesfully.");
			else
				Log.e(LOG_TAG, "Saving of the new row failed.");
		}
		else {
			// else we update
			res = db.update(DbFormSQLiteHelper.__LOCALFORMSDB_TABLE_NAME__, cv
					, DbForm.__LOCALFORMSDB_ID_KEY__ + " = " + id, null);
			if (res != -1) 
				Log.d(LOG_TAG, "Update of with id= " + id + " in the database was succesful.");
			else
				Log.e(LOG_TAG, "Update of with id= " + id + " in the database failed.");
		}
	}
	

}
