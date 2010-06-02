package com.geobloc.db;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.shared.GBSharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class DbFormSQLiteHelper extends SQLiteOpenHelper {

	private static final String LOG_TAG = "Localforms";
	
	private static final String DATABASE_NAME = "local.db";
	private static final int SCHEMA_VERSION = 1;
	
	private Context context;
	
	public static final String __LOCALFORMSDB_TABLE_NAME__ = "localForms";
	
	public DbFormSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
		// we need to save the context in order to rebuild the database if necessary
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DbFormSQLiteHelper.__LOCALFORMSDB_TABLE_NAME__ + 
				" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				DbForm.__LOCALFORMSDB_FORM_ID_KEY__ + " TEXT, " +
				DbForm.__LOCALFORMSDB_FORM_VERSION_KEY__ + " INTEGER, " +
				DbForm.__LOCALFORMSDB_FORM_FILE_PATH__ + " TEXT, " +
				DbForm.__LOCALFORMSDB_FORM_NAME_KEY__ + " TEXT, " +
				DbForm.__LOCALFORMSDB_FORM_DESCRIPTION_KEY__ + " TEXT, " +
				DbForm.__LOCALFORMSDB_FORM_DATE_KEY__ + " TEXT, " +
				DbForm.__LOCALFORMSDB_FORM_SERVERSTATE_KEY__ + " INTEGER);");
		Log.i(LOG_TAG, "Database created for schema version " + SCHEMA_VERSION + ".");
		buildDatabase(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(LOG_TAG, "Upgrading database, which will destroy all old data.");
		db.execSQL("DROP TABLE IF EXISTS " + DbFormSQLiteHelper.__LOCALFORMSDB_TABLE_NAME__);
		onCreate(db);
	}
	
	/**
	 * Builds the database from the list of forms in the forms directory.
	 * By default, their server primary keys are set to -1, the dates the database creation date and server 
	 * state to not found.
	 * @param db 
	 */
	private void buildDatabase(SQLiteDatabase db) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String packageDirectory =  prefs.getString(GBSharedPreferences.__FORMS_PATH_KEY__, 
				GBSharedPreferences.__DEFAULT_FORMS_PATH__);
		GeoBlocPackageManager manager = new GeoBlocPackageManager();
		manager.openOrBuildPackage(packageDirectory);
		List<File> forms = manager.getAllFiles();
		DbForm dbf;
		for (File myForm : forms) {
			dbf = new DbForm();
			dbf.setForm_id("formAlpha152237");
			dbf.setForm_version(1);
			dbf.setForm_name(myForm.getName());
			dbf.setForm_description("Found in the SD Card.");
			dbf.setForm_date(new Date());
			dbf.setForm_file_path(myForm.getAbsolutePath());
			dbf.setServer_state(DbForm.__FORM_SERVER_STATE_LATEST_VERSION__);
			dbf.save(db);
		}
	}

}
