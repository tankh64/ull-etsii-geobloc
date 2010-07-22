/**
 * 
 */
package com.geobloc.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.geobloc.ApplicationEx;
import com.geobloc.R;
import com.geobloc.db.DbForm;
import com.geobloc.db.DbFormSQLiteHelper;
import com.geobloc.internet.SimpleHttpGet;
import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.shared.GBSharedPreferences;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 * select * from forms
 * where id = 'paramID' and version = (select max (version) from forms
 * 										where id = 'paramID'
 * 										group by id);
 *
 */
public class DownloadFormsService extends Service {
	private static String LOG_TAG = "DownloadFormsService";
	private static String extension = ".xml";
	
	private HttpClient httpClient;
	private int attempts;
	private SharedPreferences prefs;
	private String url;
	private DateFormat df;
	private GeoBlocPackageManager pm;
	private boolean errorsEncountered;
	
	private final LocalBinder binder = new LocalBinder();
	
	private SQLiteDatabase db;
	private DbForm dbf;
	private String report;
	private String serverResponses;
	private HashMap<String, String> headers;
	
	public static final String BROADCAST_ACTION=
		"com.geobloc.services.DownloadFormsServiceEvent";
	private Intent broadcast = new Intent(BROADCAST_ACTION);
	
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
		url += prefs.getString(GBSharedPreferences.__DOWNLOAD_FORMS_SERVLET_ADDRESS_KEY__, GBSharedPreferences.__DEFAULT_DOWNLOAD_FORMS_SERVLET_ADRESS__);
		
		db = (new DbFormSQLiteHelper(getBaseContext())).getWritableDatabase();
		pm = new GeoBlocPackageManager();
		pm.openPackage(prefs.getString(GBSharedPreferences.__FORMS_PATH_KEY__, GBSharedPreferences.__DEFAULT_FORMS_PATH__));
		df = DateFormat.getInstance();
		Log.i(LOG_TAG, "DownloadFormsService initialized.");
	}
	
	public void onDestroy() {
		if (db != null) {
			db.close();
			Log.i(LOG_TAG, "Forms database closed.");
		}
		super.onDestroy();
	}
	
	public void downloadForms(Long[] ids) {
		new PerformDownloadForms().execute(ids);
	}
	
	public class LocalBinder extends Binder {
		
		public DownloadFormsService getService() {
			return DownloadFormsService.this;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	class PerformDownloadForms extends AsyncTask<Long, Integer, String> {
		
		private HashMap<String, Long> map;
		
		@Override
		public void onPreExecute() 	{
			super.onPreExecute();
			map = DbForm.getFormIdLocalHashMap(db);
		}
		
		private boolean processAndSave(String response, DbForm dbf) {
			boolean succesful = true;
			String filename;
			try {
				JSONObject json = new JSONObject(response);
				//dbf.setForm_name(json.getString(DbForm.__SERVER_FORM_NAME_KEY__));
				dbf.setForm_version(json.getInt(DbForm.__SERVER_FORM_VERSION_KEY__));
				dbf.setServer_state(DbForm.__FORM_SERVER_STATE_LATEST_VERSION__);
				dbf.setForm_description(json.getString(DbForm.__SERVER_FORM_DESCRIPTION_KEY__));
				try {
					dbf.setForm_date(df.parse(json.getString(DbForm.__SERVER_FORM_DATE_KEY__)));
				}
				catch (ParseException e) {
					Log.e(LOG_TAG, "Parsing " + json.getString(DbForm.__SERVER_FORM_DATE_KEY__) + " for form_id=" + dbf.getForm_id() + " failed. Filling up with null");
					dbf.setForm_date(null);
					e.printStackTrace();
				}
				// detect previously used filenames
				// This could be useful when switching from one server to another
				filename = dbf.getForm_id().toLowerCase().replaceAll(" ", "_");
				while (map.containsKey(filename)) {
					filename += "A";
				}
				// we're just using map to make this faster, we don't really care about the value
				map.put(filename, (long)-1);
				filename += extension;
				dbf.setForm_file_path(pm.getPackageFullpath()+filename);
				pm.addFile(dbf.getForm_file_name(), json.getString(DbForm.__SERVER_FORM_XML_KEY__));
				dbf.save(db);
			}
			catch (Exception e) {
				succesful = false;
				e.printStackTrace();
			}
			return succesful;
		}
		
		@Override
		protected String doInBackground(Long... ids) {
			errorsEncountered = false;
			report = "";
			serverResponses = "";
			DbForm dbf;
			String response;
			String requestUrl;
			SimpleHttpGet get = new SimpleHttpGet();
			int i = 0;
			for (Long id :ids) {
				try {
					// dbf should be guaranteed to NOT be null
					// nevertheless, we're safeguarded by the try/catch
					dbf = DbForm.loadFrom(db, id);
					if (dbf.getServer_state() >= DbForm.__FORM_STATE_LOCAL__)
						pm.eraseFile(dbf.getForm_file_name());
					requestUrl = url;
					headers = new HashMap<String, String>();
					headers.put("gb_form_name", dbf.getForm_id());
					//requestUrl += "?id=" + dbf.getForm_id();
					response = get.performHttpGetRequest(attempts, requestUrl, httpClient, headers);
					serverResponses += "Response for item with id=" + dbf.getForm_id() + ":\n";
					serverResponses += response + "\n\n";
					report += " - " + dbf.getForm_name() + " ";
					if (!processAndSave(response, dbf)) {
						errorsEncountered = true;
						report += getString(R.string.formsManager_itemDownloadEncounteredErrors) + "\n\n";
					}
					else
						report += getString(R.string.formsManager_itemDownloadedSuccessfully) + "\n\n";
					i++;
					publishProgress(i, ids.length);
				}	
				catch (Exception e) {
					e.printStackTrace();
					Log.e(LOG_TAG, "An error ocurred while attempting to download form with database id = " + id);
				}
			}
			return report;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			broadcast = new Intent(BROADCAST_ACTION);
			broadcast.putExtra("update", true);
			broadcast.putExtra("next", progress[0]);
			broadcast.putExtra("total", progress[1]);
			sendBroadcast(broadcast);
		}
		
		@Override
		protected void onPostExecute(String result) {
			broadcast = new Intent(BROADCAST_ACTION);
			broadcast.putExtra("update", false);
			broadcast.putExtra("result", errorsEncountered);
			broadcast.putExtra("report", report);
			broadcast.putExtra("serverResponse", serverResponses);
			sendBroadcast(broadcast);
		}
	}
	
}
