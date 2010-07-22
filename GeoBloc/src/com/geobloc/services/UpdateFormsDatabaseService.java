/**
 * 
 */
package com.geobloc.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Binder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.geobloc.ApplicationEx;
import com.geobloc.db.DbForm;
import com.geobloc.db.DbFormSQLiteHelper;
import com.geobloc.internet.SimpleHttpGet;
import com.geobloc.shared.GBSharedPreferences;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class UpdateFormsDatabaseService extends Service {
	private static String LOG_TAG = "UpdateFormsDatabaseService";
	
	
	private HttpClient httpClient;
	private int attempts;
	private SharedPreferences prefs;
	private String url;
	
	private final LocalBinder binder = new LocalBinder();
	
	private SQLiteDatabase db;
	
	public static final String BROADCAST_ACTION=
		"com.geobloc.services.UpdateFormsDatabaseEvent";
	private Intent broadcast;
	
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
		if (db != null) {
			db.close();
			Log.i(LOG_TAG, "Forms database closed.");
		}
		super.onDestroy();
	}
	
	public void updateFormsDatabase() {
		new PerformDatabaseUpdate().execute(url);
	}
	

	public class LocalBinder extends Binder {
		
		public UpdateFormsDatabaseService getService() {
			return UpdateFormsDatabaseService.this;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public Binder onBind(Intent intent) {
		return binder;
	}

	class PerformDatabaseUpdate extends AsyncTask<String, Integer, Boolean> {
		
		private boolean updateDatabase(String response) {
			boolean updateSuccesful = true;
			if (response != null) {
				// we can update the database
				db = (new DbFormSQLiteHelper(getBaseContext())).getWritableDatabase();
				try {
					JSONArray array = new JSONArray(response);
					JSONObject json;
					DbForm dbf;
					HashMap<String, Boolean> map = DbForm.getLocalHashMap(db);
					for (int i = 0; i < array.length(); i++) {
						json = array.getJSONObject(i);
						//String id = json.getString(DbForm.__SERVER_FORM_ID_KEY__);
						String id = json.getString(DbForm.__SERVER_FORM_NAME_KEY__);
						dbf = DbForm.findByFormId(db, id);
						DateFormat df = DateFormat.getDateInstance();
						if (dbf == null) {
							// it's not in the database
							dbf = new DbForm();
							dbf.setForm_name(json.getString(DbForm.__SERVER_FORM_NAME_KEY__));
							// this was changed the last day, form_id no longer exists in the server
							// to avoid excessive changes, we set form_id as the new server's name
							//dbf.setForm_id(json.getString(DbForm.__SERVER_FORM_ID_KEY__));
							dbf.setForm_id(json.getString(DbForm.__SERVER_FORM_NAME_KEY__));
							dbf.setForm_version(json.getInt(DbForm.__SERVER_FORM_VERSION_KEY__));
							dbf.setServer_state(DbForm.__FORM_SERVER_STATE_NEW__);
							dbf.setForm_file_path(null);
							dbf.setForm_description(json.getString(DbForm.__SERVER_FORM_DESCRIPTION_KEY__));
							try {
								dbf.setForm_date(df.parse(json.getString(DbForm.__SERVER_FORM_DATE_KEY__)));
							}
							catch (ParseException e) {
								Log.e(LOG_TAG, "Parsing " + json.getString(DbForm.__SERVER_FORM_DATE_KEY__) + " for form_id=" + dbf.getForm_id() + " failed. Filling up with null");
								dbf.setForm_date(null);
								e.printStackTrace();
							}
							dbf.save(db);
						}
						else {
							// erase form from map because it's in the server list
							map.remove(dbf.getForm_id());
							// check for update
							if (dbf.getForm_version() < json.getInt(DbForm.__SERVER_FORM_VERSION_KEY__)) {
								// if we're new, we set version to the latest
								if (dbf.getServer_state() == DbForm.__FORM_SERVER_STATE_NEW__)
									dbf.setForm_version(json.getInt(DbForm.__SERVER_FORM_VERSION_KEY__));
								else {
									// else, we tell the user there's a more recent version available keeping version 
									// intact until download, unless we store ours and the server's, of course
									dbf.setServer_state(DbForm.__FORM_SERVER_STATE_MORE_RECENT_AVAILABLE__);
								}
								dbf.setForm_name(json.getString(DbForm.__SERVER_FORM_NAME_KEY__));
								dbf.setForm_description(json.getString(DbForm.__SERVER_FORM_DESCRIPTION_KEY__));
								try {
									dbf.setForm_date(df.parse(json.getString(DbForm.__SERVER_FORM_DATE_KEY__)));
								}
								catch (ParseException e) {
									Log.e(LOG_TAG, "Parsing " + json.getString(DbForm.__SERVER_FORM_DATE_KEY__) + " for form_id=" + dbf.getForm_id() + " failed. Filling up with null");
									dbf.setForm_date(null);
									e.printStackTrace();
								}
							}
							// set as latest version if it was previously set as 'Not Found'
							if (dbf.getServer_state() == DbForm.__FORM_SERVER_STATE_NOT_FOUND__)
								dbf.setServer_state(DbForm.__FORM_SERVER_STATE_LATEST_VERSION__);
							dbf.save(db);
						}
					}
					if (!map.isEmpty()) {
						// there are forms in the database not found in the server, we need to update
						Iterator<Map.Entry<String, Boolean>> it = map.entrySet().iterator();
						Map.Entry<String, Boolean> e;
						while (it.hasNext()) {
							e = (Map.Entry<String, Boolean>)it.next();
							dbf = DbForm.findByFormId(db, e.getKey());
							if (dbf.getServer_state() >= DbForm.__FORM_STATE_LOCAL__) {
								Log.i(LOG_TAG, dbf.getForm_id() + " was not found in the server. Marking as such.");
								dbf.setServer_state(DbForm.__FORM_SERVER_STATE_NOT_FOUND__);
								dbf.save(db);
							}
							else {
								// if we don't have it locally, we might as well erase it from the database
								Log.i(LOG_TAG, dbf.getForm_id() + " was not found in the server and is not stored locally. Erasing from the database.");
								dbf.delete(db);
							}
						}
					}
				} catch (Exception e) {
					updateSuccesful = false;
					Log.e(LOG_TAG, "Exception ocurred while processing the server response list. Maybe server response was not the list and was an error message.");
					e.printStackTrace();
				}
			}
			onPostExecute(updateSuccesful);
			return updateSuccesful;
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			boolean ret = false;
			String url = params[0];
			SimpleHttpGet get = new SimpleHttpGet();
			String response = get.performHttpGetRequest(attempts, url, httpClient, null);
			ret = updateDatabase(response);
			if (ret)
				Log.i(LOG_TAG, "Update from URL: " + url + " finished succesfully.");
			else
				Log.e(LOG_TAG, "Update from URL: " + url + " encountered some kind of error.");
			if (ret) {
				Editor ed = prefs.edit();
				//DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
				//ed.putString(GBSharedPreferences.__LAST_SERVER_LIST_CHECK_KEY__, df.format(new Date()));
				Date d = new Date();
				ed.putString(GBSharedPreferences.__LAST_SERVER_LIST_CHECK_KEY__, d.toLocaleString());
				ed.commit();
			}
			broadcast = new Intent(BROADCAST_ACTION);
			broadcast.putExtra("result", ret);
			broadcast.putExtra("serverResponse", response);
			sendBroadcast(broadcast);
			return ret;
		}
		
	}
	
}
