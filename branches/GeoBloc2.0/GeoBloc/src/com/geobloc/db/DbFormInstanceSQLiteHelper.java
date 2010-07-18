package com.geobloc.db;

import java.io.File;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.shared.GBSharedPreferences;

public class DbFormInstanceSQLiteHelper extends SQLiteOpenHelper {

	private static final String LOG_TAG = "DbFormInstanceSQLiteHelper";
	
	private static final String DATABASE_NAME = "geoblocInstances.db";
	private static final int SCHEMA_VERSION = 11;
	
	private Context context;
	
	public static final String __LOCALPACKAGESDB_TABLE_NAME__ = "localPackages";
	
	public DbFormInstanceSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
		// we need to save the context in order to rebuild the database if necessary
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE " + DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__ + 
				" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				DbFormInstance.__LOCALPACKAGESDB_FORM_ID_KEY__ + " TEXT, " + 
				DbFormInstance.__LOCALPACKAGES_DB_FORM_ID_KEY__ + " TEXT, " +
				DbFormInstance.__LOCALPACKAGES_DB_FORM_VERSION_KEY__ + " INTEGER, " + 
				DbFormInstance.__LOCALPACKAGESDB_PATH_KEY__ + " TEXT, " + 
				DbFormInstance.__LOCALPACKAGESDB_LABEL_KEY__ + " TEXT, " + 
				DbFormInstance.__LOCALPACKAGESDB_CREATEDDATE_KEY__ + " TEXT, " + 
				DbFormInstance.__LOCALPACKAGESDB_COMPLETEDDATE_KEY__ + " TEXT, " +
				DbFormInstance.__LOCALPACKAGESDB_COMPRESSEDPACKAGEFILE_KEY__ + " TEXT, " +
				DbFormInstance.__LOCALPACKAGESDB_COMPLETED_KEY__ + " INTEGER);");
		buildDatabase(db);
		Log.i(LOG_TAG, "Database built with schema version " + SCHEMA_VERSION + ".");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(LOG_TAG, "Upgrading database; old data will be destroyed.");
		db.execSQL("DROP TABLE IF EXISTS " + DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__);
		onCreate(db);

	}
	
	/**
	 * Builds the database from the list of packages in the packages directory.
	 * By default, it is set to "not completed" and the date is the date in which the file was last modified.
	 * @param db 
	 */
	private void buildDatabase(SQLiteDatabase db) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String packageDirectory =  prefs.getString(GBSharedPreferences.__PACKAGES_PATH_KEY__, 
				GBSharedPreferences.__DEFAULT_PACKAGES_PATH__);
		GeoBlocPackageManager manager = new GeoBlocPackageManager();
		manager.openOrBuildPackage(packageDirectory);
		List<File> packages = manager.getAllDirectories();
		DbFormInstance dbi;
		for (File myPackages : packages) {
			dbi = new DbFormInstance();
			dbi.setForm(null);
			dbi.setLabel(myPackages.getName());
			dbi.setInstance_form_version(1);
			dbi.setPackage_path(myPackages.getAbsolutePath());
			dbi.setCreatedDate(new Date(myPackages.lastModified()));
			dbi.setDate(null);
			dbi.setCompressedPackageFileLocation(null);
			dbi.setComplete(false);
			dbi.save(db);
		}
	}

}
