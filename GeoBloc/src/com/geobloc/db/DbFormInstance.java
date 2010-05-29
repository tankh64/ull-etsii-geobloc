/**
 * 
 */
package com.geobloc.db;

import static android.provider.BaseColumns._ID;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import android.content.ContentValues;
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
public class DbFormInstance {
	private static final String LOG_TAG = "DbFormInstance";
	
	/*
	 * PUBLIC KEYS
	 * 
	 */
	
	public static final String __LOCALPACKAGESDB_ID__ = "_id";
	public static final String __LOCALPACKAGESDB_NAME_KEY__ = "name";
	public static final String __LOCALPACKAGESDB_LOCATION_KEY__ = "packageLocation";
	public static final String __LOCALPACKAGESDB_CREATEDDATE_KEY__ = "createdDate";
	public static final String __LOCALPACKAGESDB_COMPLETEDDATE_KEY__ = "completedDate";
	public static final String __LOCALPACKAGESDB_COMPRESSEDPACKAGEFILE_KEY__ = "compressedPackageFile";
	public static final String __LOCALPACKAGESDB_COMPLETED_KEY__ = "completed";
	
	/*
	 * MISSING form server key: form_id and form_version 
	 */
	
	private long id = -1;
	private String name = "";
	private String packageLocation = "";
	private String compressedPackageFileLocation = "";
	private Date createdDate;
	private Date completedDate;
	private boolean completed;
	
	public static Cursor getAll(SQLiteDatabase db) {
		Cursor c = db.rawQuery("SELECT * FROM " + DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__, null);
		return c;
	}
	
	public static Cursor getAllCompleted(SQLiteDatabase db) {
		Cursor c = db.rawQuery("SELECT * FROM " + DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__+ 
				" WHERE " + DbFormInstance.__LOCALPACKAGESDB_COMPLETED_KEY__, null);
		return c;
	}
	
	public static DbFormInstance loadFrom(SQLiteDatabase db, long id) {
		
		String[] DBFORMINSTANCE_FROM = {DbFormInstance.__LOCALPACKAGESDB_ID__, DbFormInstance.__LOCALPACKAGESDB_NAME_KEY__,
				DbFormInstance.__LOCALPACKAGESDB_CREATEDDATE_KEY__, DbFormInstance.__LOCALPACKAGESDB_COMPLETEDDATE_KEY__, 
				DbFormInstance.__LOCALPACKAGESDB_LOCATION_KEY__, DbFormInstance.__LOCALPACKAGESDB_COMPRESSEDPACKAGEFILE_KEY__, 
				DbFormInstance.__LOCALPACKAGESDB_LOCATION_KEY__, };
		
		Cursor c = db.rawQuery("SELECT * FROM " + DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__ + 
				" WHERE " + DbFormInstance.__LOCALPACKAGESDB_ID__ + " = " + id, null);
		
		c.moveToFirst();
		if (c.isFirst()) {
			DbFormInstance dbi = new DbFormInstance();
			dbi.loadFrom(c);
			return dbi;
		}
		else
			return null;
	}
	
	public DbFormInstance loadFrom(Cursor c) {
		id = c.getLong(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_ID__));
		name=c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_NAME_KEY__));
		packageLocation = c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_LOCATION_KEY__));
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
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	public String getPackageLocation() {
		return packageLocation;
	}

	public void setPackageLocation(String packageLocation) {
		this.packageLocation = packageLocation;
	}

	public String getCompressedPackageFileLocation() {
		return compressedPackageFileLocation;
	}
	
	public void setCompressedPackageFileLocation(
			String compressedPackageFileLocation) {
		this.compressedPackageFileLocation = compressedPackageFileLocation;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public void delete(SQLiteDatabase db) {
		if (id != -1) {
			Log.i(LOG_TAG, "Deleting row in the database with id= " + id);
			db.delete(DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__,
					DbFormInstance.__LOCALPACKAGESDB_ID__ + " = " + id, null);
		}
	}
	
	public void save(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		
		if (id != -1)
			cv.put(DbFormInstance.__LOCALPACKAGESDB_ID__, id);
		cv.put(DbFormInstance.__LOCALPACKAGESDB_NAME_KEY__, name);
		cv.put(DbFormInstance.__LOCALPACKAGESDB_LOCATION_KEY__, packageLocation);
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
			Log.i(LOG_TAG, "Saving as a new row in the database");
			res = db.insert(DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__, 
				DbFormInstance.__LOCALPACKAGESDB_NAME_KEY__, cv);
		}
		else {
			// else we update
			Log.i(LOG_TAG, "Updating row in the database where id= " + id);
			res = db.update(DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__, cv
					, DbFormInstance.__LOCALPACKAGESDB_ID__ + " = " + id, null);
		}
	}
}
