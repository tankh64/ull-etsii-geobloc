package com.geobloc.db;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geobloc.shared.IFormDefinition;
import com.geobloc.shared.IJavaToDatabaseForm;
import com.geobloc.shared.JavaForms;

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
	
	/**
	 *  database private primary key
	 */
	public static final String __LOCALFORMSDB_ID_KEY__ = "_id";
	/**
	 *  server primary key
	 */
	public static final String __LOCALFORMSDB_FORM_ID_KEY__ = "form_id";
	/**
	 *  server primary key
	 */
	public static final String __LOCALFORMSDB_FORM_VERSION_KEY__ = "form_version";
	/**
	 *  full path to form.xml file in SD Card
	 */
	public static final String __LOCALFORMSDB_FORM_FILE_PATH__ = "form_file_path";
	/**
	 *  form given name
	 */
	public static final String __LOCALFORMSDB_FORM_NAME_KEY__ = "form_name";
	/**
	 *  form description info
	 */
	public static final String __LOCALFORMSDB_FORM_DESCRIPTION_KEY__ = "form_description";
	/**
	 *  form upload date given by server
	 */
	public static final String __LOCALFORMSDB_FORM_DATE_KEY__ = "form_date";

	/**
	 * If state is equal, then the form is only in the server (new for the device)
	 */
	public static final int __FORM_SERVER_STATE_NEW__ = 0;
	/**
	 * If equal, the device has an older version than the server and can be updated.
	 */
	public static final int __FORM_SERVER_STATE_MORE_RECENT_AVAILABLE__ = 1;
	/**
	 * If equal, the device has the latest version and does not require update.
	 */
	public static final int __FORM_SERVER_STATE_LATEST_VERSION__ = 2;
	/**
	 * If equal, the form has not been found on the server. Erasing is recommended.
	 */
	public static final int __FORM_SERVER_STATE_NOT_FOUND__ = 3;
	/**
	 * If a form's state is lower or equal than, then it can only be found in the server (there is no copy on the device9
	 */
	public static final int __FORM_STATE_SERVER_ONLY__ = 0; // equal or lower than
	/**
	 * If the form's state is greater or equal than, then there's a local copy of the form, but versions might not match. 
	 */
	public static final int __FORM_STATE_LOCAL__ = 1; // equal or greater than
	/**
	 * Can take the following values:
	 * 0 -> new (we don't have it)
	 * 1 -> there's a more recent version
	 * 2 -> latest version
	 * 3 -> not found in server
	 * 
	 * it's local if state > 0, else it's on the server only
	 */
	public static final String __LOCALFORMSDB_FORM_SERVERSTATE_KEY__ = "server_state";

	/*
	 * SERVER KEYS
	 */
	/**
	 * Parameter name of the requested form's server id.
	 */
	public static final String __SERVER_FORM_ID_PARAMETER__ = "id";
	/**
	 * Key to recover a form's name from the server's response.
	 */
	public static final String __SERVER_FORM_NAME_KEY__ = "gb_name";
	/**
	 * Key to recover a form's id from the server's response.
	 */
	public static final String __SERVER_FORM_ID_KEY__ = "gb_form_id";
	/**
	 * Key to recover the form's current version from the server's response.
	 */
	public static final String __SERVER_FORM_VERSION_KEY__ = "gb_form_version";
	/**
	 * Key to recover the form's published date from the server's response.
	 */
	public static final String __SERVER_FORM_DATE_KEY__ = "gb_date";
	/**
	 * Key to recover the form's current description from the server's response.
	 */
	public static final String __SERVER_FORM_DESCRIPTION_KEY__ = "gb_description";
	/**
	 * Key to recover the form's XML file contents from the server's response.
	 */
	public static final String __SERVER_FORM_XML_KEY__ = "gb_xml";
	

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
	
	/**
	 * A method to query all forms in the database.
	 * @param db An open database with reading permissions.
	 * @return A cursor containing all the forms in the database.
	 */
	public static Cursor getAllForms(SQLiteDatabase db) {
		Cursor c = db.rawQuery("SELECT * FROM " + DbFormSQLiteHelper.__LOCALFORMSDB_TABLE_NAME__, null);
		return c;
		
	}
	/**
	 * Performs a query returning all forms stored locally on the device.
	 * @param db An open database with reading permissions.
	 * @return A cursor containing all the local forms stored.
	 */
	public static Cursor getAllLocalForms(SQLiteDatabase db) {
		Cursor c = db.rawQuery("SELECT * FROM " + DbFormSQLiteHelper.__LOCALFORMSDB_TABLE_NAME__ + 
				" WHERE " + DbForm.__LOCALFORMSDB_FORM_SERVERSTATE_KEY__ + " >= " + DbForm.__FORM_STATE_LOCAL__, 
				null);
		return c;
	}
	/**
	 * An auxiliary method required to aid detection of updated forms in the database when it is updated 
	 * with the server's list.
	 * @param db An open database with reading permissions.
	 * @return A {@link HashMap} with every form stored in the database's logical id (form_id) paired with 
	 * a boolean set to false.
	 */
	public static HashMap<String, Boolean> getLocalHashMap(SQLiteDatabase db) {
		Cursor c = DbForm.getAllForms(db);
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		c.moveToFirst();
		while (!c.isAfterLast()) {
			map.put(c.getString(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_ID_KEY__)), false);
			c.moveToNext();
		}
		c.close();
		return map;
	}
	/**
	 * An auxiliary method used to detect when we're downloading new forms that there aren't any two forms 
	 * with the same filename, which leads to more than one form attached 
	 * to a single file, which means we lose one form's data.
	 * @param db An open database with reading permissions.
	 * @return A {@link HashMap} with every form stored in the database's filename and local id.  
	 */
	public static HashMap<String, Long> getFormIdLocalHashMap(SQLiteDatabase db) {
		DbForm dbf = new DbForm();
		Cursor c = DbForm.getAllForms(db);
		HashMap<String, Long> map = new HashMap<String, Long>();
		c.moveToFirst();
		while (!c.isAfterLast()) {
			dbf.loadFrom(c);
			if (dbf.getServer_state() >= DbForm.__FORM_STATE_LOCAL__)
				map.put(dbf.getForm_file_name().substring(0, dbf.getForm_file_name().indexOf('.')), dbf.getForm_local_id());
			c.moveToNext();
		}
		c.close();
		return map;
	}
	/**
	 * A method which allows us to get a form as a Java Object from its local database id, not its logical 
	 * id by which it is known to the server.
	 * @param db An open database with writing permissions.
	 * @param id The form's local id.
	 * @return A {@link DbForm} loaded with the desired form from its local database id.
	 */
	public static DbForm loadFrom(SQLiteDatabase db, long id) {
		Cursor c = db.rawQuery("SELECT * FROM " + DbFormSQLiteHelper.__LOCALFORMSDB_TABLE_NAME__ + 
				" WHERE " + DbForm.__LOCALFORMSDB_ID_KEY__ + " = " + id, null);
		
		c.moveToFirst();
		if (c.isFirst()) {
			DbForm dbf = new DbForm();
			dbf.loadFrom(c);
			c.close();
			return dbf;
		}
		c.close();
		return null;		
	}
	/**
	 * It allows faster access to the database by providing a method to get a form in a Java Object form 
	 * providing the form's logical "form_id", not the local id with which it is stored in the local database.
	 * @param db An open database with reading permissions.
	 * @param form_id The form's logical id; version is not required since we only keep one copy of a specific form.
	 * @return A {@link DbForm} loaded with the desired form id or null if it wasn't found.
	 */
	public static DbForm findByFormId(SQLiteDatabase db, String form_id) {
		Cursor c = db.rawQuery("SELECT * FROM " + DbFormSQLiteHelper.__LOCALFORMSDB_TABLE_NAME__ + 
				" WHERE " + DbForm.__LOCALFORMSDB_FORM_ID_KEY__ + " = '" + form_id + "'", null);
		
		c.moveToFirst();
		if (c.isFirst()) {
			DbForm dbf = new DbForm();
			dbf.loadFrom(c);
			c.close();
			return dbf;
		}
		c.close();
		return null;
	}
	
	/**
	 * Loads an entry from the database into the current {@link DbForm} Object, effectively losing its 
	 * previous contents. The {@link Object} must've been initialized first.
	 * NOTE: The cursor passed as parameter is not closed nor deactivated within this call.
	 * @param c A cursor pointing to an entry in the database.
	 * @return A copy of the same Object after loading the contents from the database.
	 */
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
	public String getForm_file_name() {
		return form_file_path.substring(form_file_path.lastIndexOf('/')+1);
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
	
	/**
	 * Deletes the matching entry in the database to the DbForm Object.
	 * @param db An open database with writing permissions.
	 */
	public void delete(SQLiteDatabase db) {
		if (id != -1) {
			Log.i(LOG_TAG, "Deleting row in the database with id= " + id);
			db.delete(DbFormSQLiteHelper.__LOCALFORMSDB_TABLE_NAME__,
					DbForm.__LOCALFORMSDB_ID_KEY__ + " = " + id, null);
		}
	}
	
	/**
	 * Saves the DbForm's contents into the database. If the entry exists (id is not -1) then it automatically 
	 * updates the database entry, else it will insert it as a new entry.
	 * @param db An open database with writing permissions.
	 */	
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
			if (res != -1) {
				id = res;
				Log.d(LOG_TAG, "Saved as a new row in the database succesfully.");
			}
			else
				Log.e(LOG_TAG, "Saving of the new row failed.");
		}
		else {
			// else we update
			res = db.update(DbFormSQLiteHelper.__LOCALFORMSDB_TABLE_NAME__, cv
					, DbForm.__LOCALFORMSDB_ID_KEY__ + " = " + id, null);
			if (res == 1)
				Log.d(LOG_TAG, "Update of row with id= " + id + " in the database was succesful.");
			else
				Log.e(LOG_TAG, "Update of row with id= " + id + " in the database failed.");
		}
	}
	/**
	 * Method designed to allow the parser to access the Forms database content without knowing its design 
	 * through an {@link Object} implementing the {@link IJavaToDatabaseForm} interface.
	 * @param context The context in which the object will be used.
	 * @return An initialized object meeting the {@link IJavaToDatabaseForm} interface.
	 */
	public static IJavaToDatabaseForm getParserInterfaceInstance(Context context) {
		return new JavaForms(context);
		
	}
}
