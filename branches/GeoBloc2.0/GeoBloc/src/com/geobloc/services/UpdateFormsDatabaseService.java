/**
 * 
 */
package com.geobloc.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.geobloc.ApplicationEx;
import com.geobloc.db.DbForm;
import com.geobloc.db.DbFormSQLiteHelper;
import com.geobloc.internet.SimpleHttpGet;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.shared.GBSharedPreferences;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class UpdateFormsDatabaseService extends Service {
	private static String LOG_TAG = "UpdateFormsDatabaseService";

	public static final String __NAME_KEY__ = "gb_name";
	public static final String __FORM_ID_KEY__ = "gb_form_id";
	public static final String __FORM_VERSION_KEY__ = "gb_form_version";
	public static final String __FORM_DATE_KEY__ = "gb_date";
	public static final String __FORM_DESCRIPTION_KEY__ = "gb_description";
	
	
	private HttpClient httpClient;
	private int attempts;
	private SharedPreferences prefs;
	private String url;
	
	private LocalBinder binder;
	
	private SQLiteDatabase db;
	private DbForm dbf;
	
	@Override
	public void onCreate() {
		super.onCreate();
		initConfig();
	}
	
	private void initConfig() {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//this.serverAddress = address;
		attempts = Integer.parseInt(prefs.getString(GBSharedPreferences.__NUMBER_OF_INTERNET_ATTEMPTS_KEY__, 
				GBSharedPreferences.__DEFAULT_NUMBER_OF_INTERNET_ATTEMPTS__));
		ApplicationEx app = (ApplicationEx)this.getApplication();
		httpClient = app.getHttpClient();
		url = prefs.getString(GBSharedPreferences.__BASE_SERVER_ADDRESS_KEY__, GBSharedPreferences.__DEFAULT_BASE_SERVER_ADDRESS__);
		url += prefs.getString(GBSharedPreferences.__GET_AVAILABLE_FORMS_LIST_SERVLET_ADDRESS_KEY__, GBSharedPreferences.__DEFAULT_GET_AVAILABLE_FORMS_LIST_SERVLET_ADDRESS__);
		Log.i(LOG_TAG, "UpdateFormsDatabase initialized.");
	}

	public void onDestroy() {
		if (db != null)
			db.close();
	}
	
	public void updateFormsDatabase() {
		// boolean to check for Exception
		boolean exception = false;

		// boolean to exit loop (success or failrue)
		boolean done = false;
		
		SimpleHttpGet get;
		String response = null;
		
		for (int i = 1; (!exception && !done && (i <= attempts)); i++) {
			get = new SimpleHttpGet();
			// tell the handler in which attempt we are
			//handler.sendEmptyMessage(i);
			try {
				response = get.executeHttpGetRequest(url, httpClient);
				// should be enough to check
				if (response != null)
					done = true;
			}
			catch (Exception e){
				Log.e(LOG_TAG, "Exception ocurred while executing HttpGet request to the server.");
				// if exception, exit loop
				exception = true;
				e.printStackTrace();
				response = null;
			}
		}
		
		/*
		 * IMPORTANT TO CHECK IN DOWNLOAD SERVICE THAT THERE ARENT TWO FORMS WITH THE SAME FILENAME
		 */
		
		if (response != null) {
			// we can update the database
			db = (new DbFormSQLiteHelper(getBaseContext())).getWritableDatabase();
			try {
				JSONArray array = new JSONArray(response);
				JSONObject json;
				for (int i = 0; i < array.length(); i++) {
					json = array.getJSONObject(i);
					dbf = DbForm.findByFormId(db, json.getString(__FORM_ID_KEY__));
					DateFormat df = DateFormat.getDateInstance();
					if (dbf == null) {
						// it's not in the database
						dbf = new DbForm();
						dbf.setForm_name(json.getString(__NAME_KEY__));
						dbf.setForm_id(json.getString(__FORM_ID_KEY__));
						dbf.setForm_version(json.getInt(__FORM_VERSION_KEY__));
						dbf.setServer_state(DbForm.__FORM_SERVER_STATE_NEW__);
						dbf.setForm_file_path(null);
						dbf.setForm_description(json.getString(__FORM_DESCRIPTION_KEY__));
						try {
							dbf.setForm_date(df.parse(json.getString(__FORM_DATE_KEY__)));
						}
						catch (ParseException e) {
							Log.e(LOG_TAG, "Parsing " + json.getString(__FORM_DATE_KEY__) + " for form_id=" + dbf.getForm_id() + " failed. Filling up with null");
							dbf.setForm_date(null);
							e.printStackTrace();
						}
						dbf.save(db);
					}
					else {
						// check for update 
						if (dbf.getForm_version() < json.getInt(__FORM_VERSION_KEY__)) {
							dbf.setForm_version(DbForm.__FORM_SERVER_STATE_MORE_RECENT_AVAILABLE__);
							dbf.save(db);
						}
					}
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, "Exception ocurred while processing the server response list. Maybe server response was not the list and was an error message.");
				e.printStackTrace();
			}
		}
		Log.i(LOG_TAG, "Update complete. Stopping self.");
		stopSelf();

	}
	
	public class LocalBinder extends Binder {
		
		private IStandardTaskListener listener;
		
		public UpdateFormsDatabaseService getService() {
			return UpdateFormsDatabaseService.this;
		}

		private IStandardTaskListener getListener() {
			return listener;
		}
		
		public void setListener(IStandardTaskListener listener) {
			this.listener = listener;
		}
		
		public void detachListener() {
			this.listener = null;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public Binder onBind(Intent intent) {
		// ????????????????????????
		binder = new LocalBinder();
		binder.getListener();
		return binder;
	}

}
