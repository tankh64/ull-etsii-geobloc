/**
 * 
 */
package com.geobloc.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

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
import com.geobloc.db.DbFormInstance;
import com.geobloc.db.DbFormInstanceSQLiteHelper;
import com.geobloc.db.DbFormSQLiteHelper;
import com.geobloc.internet.HttpFileMultipartPost;
import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.shared.Utilities;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class UploadInstancesService extends Service {
	private static final String LOG_TAG = "UploadInstancesService";
	private static final String extension = ".zip";
	
	private HttpClient httpClient;
	private int attempts;
	private SharedPreferences prefs;
	private String url;
	
	private boolean errorsEncountered;
	
	private final LocalBinder binder = new LocalBinder();
	
	private SQLiteDatabase formsDb;
	private SQLiteDatabase instancesDb;
	private DbFormInstance dbi;
	private String report;
	private String serverResponses;
	private String packagesPath;
	
	private GeoBlocPackageManager pm;
	
	public static final String BROADCAST_ACTION=
		"com.geobloc.services.UploadInstancesServiceEvent";
	private Intent broadcast = new Intent(BROADCAST_ACTION);

	@Override
	public void onCreate() {
		super.onCreate();
		initConfig();
	}
	
	private void initConfig() {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		attempts = Integer.parseInt(prefs.getString(GBSharedPreferences.__NUMBER_OF_INTERNET_ATTEMPTS_KEY__, 
				GBSharedPreferences.__DEFAULT_NUMBER_OF_INTERNET_ATTEMPTS__));
		ApplicationEx app = (ApplicationEx)this.getApplication();
		httpClient = app.getHttpClient();
		url = prefs.getString(GBSharedPreferences.__BASE_SERVER_ADDRESS_KEY__, GBSharedPreferences.__DEFAULT_BASE_SERVER_ADDRESS__);
		url += prefs.getString(GBSharedPreferences.__UPLOAD_PACKAGES_SERVLET_ADDRESS_KEY__, GBSharedPreferences.__DEFAULT_UPLOAD_PACKAGES_SERVLET_ADDRESS__);
		packagesPath = prefs.getString(GBSharedPreferences.__PACKAGES_PATH_KEY__, GBSharedPreferences.__DEFAULT_PACKAGES_PATH__);
		formsDb = (new DbFormSQLiteHelper(getBaseContext())).getReadableDatabase();
		instancesDb = (new DbFormInstanceSQLiteHelper(getBaseContext())).getWritableDatabase();
		pm = new GeoBlocPackageManager();

		Log.i(LOG_TAG, "UploadInstancesService initialized.");
	}
	
	public void onDestroy() {
		if (formsDb != null) {
			formsDb.close();
			Log.i(LOG_TAG, "Forms database closed.");
		}
		if (instancesDb != null) {
			instancesDb.close();
			Log.i(LOG_TAG, "Instances database closed.");
		}
		super.onDestroy();
	}
	
	public void uploadInstances(Long[] ids) {
		new PerformUploadInstances().execute(ids);
	}
	
	public class LocalBinder extends Binder {
		
		public UploadInstancesService getService() {
			return UploadInstancesService.this;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}
	
	class PerformUploadInstances extends AsyncTask<Long, Integer, String> {

		/**
		 * 	Builds the package file to be sent.
		 * @return True if the instance was packaged. False if something went wrong.
		*/
		private boolean packInstance(DbFormInstance dbi) {
			if (dbi.getCompressedPackageFileLocation() != null) {
				Log.d(LOG_TAG, "Erasing previous package of instance with localId='" + dbi.getInstance_local_id() + "'.");
				File f = new File(dbi.getCompressedPackageFileLocation());
				if (!f.delete()) {
					Log.e(LOG_TAG, "Could not erase old package. Upload cancelled.");
					return false;
				}
			}
			pm.openPackage(dbi.getPackage_path());
			dbi.setCompressedPackageFileLocation(dbi.getPackage_path() + dbi.getPackage_name() + extension);
			if (pm.buildZIPfromPackage(dbi.getCompressedPackageFileLocation())) {
				Log.i(LOG_TAG, "Package for instance with localId='" + dbi.getInstance_local_id() + "' built successfully.");
				return true;
			}
			else {
				Log.e(LOG_TAG, "Could not build package. Upload cancelled.");
				return false;
			}
		}
		
		/**
		 * Performs the final upload operation. If the operation is successful, it erases the instance 
		 * from both local storage and the database.
		 * @param dbi
		 * @param post
		 * @return
		 */
		private boolean sendInstance(DbFormInstance dbi, HttpFileMultipartPost post) {
			List<File> files = new ArrayList<File>();
			files.add(new File(dbi.getCompressedPackageFileLocation()));
			String myUrl = url;
			myUrl += "?id=" + dbi.getInstance_form_id() + "&version=" + dbi.getInstance_form_version();
			
			myUrl += "&device=" + Utilities.getDeviceID(getBaseContext());
			if (dbi.isComplete())
				myUrl += "&complete=1";
			else
				myUrl += "&complete=0";
			
			try {
				Log.d(LOG_TAG, "Sending instance with localId='" + dbi.getInstance_local_id() + "'.");
				HttpResponse response = post.executeMultipartPackagePost(dbi, files, myUrl, httpClient);
				// check response error code
				if (response == null) {
					throw new Exception();
				}
				else {
					serverResponses += "- Response for instance " + dbi.getLabel() + ":\n";
					serverResponses += response.getStatusLine();
					serverResponses += "\n\n";
					if ((response.getStatusLine().getStatusCode() >= GBSharedPreferences.__SERVER_RESPONSE_SUCCESS_L__) && 
						(response.getStatusLine().getStatusCode() <= GBSharedPreferences.__SERVER_RESPONSE_SUCCESS_U__))	{
						Log.i(LOG_TAG, "Server responded upload successful for instance with localId='" + dbi.getInstance_local_id() + "'.");
						// erase package/instance
						pm.openPackage(packagesPath);
						if (pm.eraseDirectory(dbi.getPackage_name())) {
							dbi.delete(instancesDb);
						}
						else {
							// there was an error, but we move on
							errorsEncountered = true;
							Log.e(LOG_TAG, "Could not delete package from external storage. Since upload was successful, proceeding to delete from database.");
							dbi.delete(instancesDb);
						}
					}
					else {
						throw new Exception();
					}
				}
			} catch (Exception e) {
				// all false returns go through here
				errorsEncountered = true;
				dbi.save(instancesDb);
				Log.e(LOG_TAG, "An exception ocurred while executing Http Post operation. Upload unsuccessful.");
				e.printStackTrace();
				return false;
			}
			
			// if we get here, upload was successful
			return true;
		}
		
		@Override
		protected String doInBackground(Long... ids) {
			errorsEncountered = false;
			report = "";
			serverResponses = "";
			int i = 0;
			DbFormInstance dbi;
			HttpFileMultipartPost post = new HttpFileMultipartPost();
			for (Long id : ids) {
				dbi = DbFormInstance.loadFrom(instancesDb, formsDb, id);
				report += " - " + dbi.getLabel() + " ";
				if (packInstance(dbi)) {
					Log.d(LOG_TAG, "Instace with localId='" + dbi.getInstance_local_id() + "' was packaged succesfully.");
					if (sendInstance(dbi, post)) {
						report += getString(R.string.instanceManager_itemUploadedSuccessfully) + "\n\n";
					}
					else {
						errorsEncountered = true;
						report += getString(R.string.instanceManager_itemUploadEncounteredErrors) + "\n\n";
						Log.e(LOG_TAG, "Instace with localId='" + dbi.getInstance_local_id() + "' could not be sent. Upload cancelled.");
					}
				}
				else {
					errorsEncountered = true;
					report += getString(R.string.instanceManager_itemUploadEncounteredErrors) + "\n\n";
					Log.e(LOG_TAG, "Instace with localId='" + dbi.getInstance_local_id() + "' could not be packaged. Upload cancelled.");
				}
				i++;
				publishProgress(i, ids.length);
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
