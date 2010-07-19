/**
 * 
 */
package com.geobloc.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.geobloc.R;
import com.geobloc.db.DbFormInstance;
import com.geobloc.db.DbFormInstanceSQLiteHelper;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.shared.IJavaToDatabaseInstance;
import com.geobloc.shared.JavaInstances;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class DeleteInstancesTask extends AsyncTask<Long, Integer, String> {

	private static final String LOG_TAG = "DeletePackagesTask";
	
	private Context context;
	private IJavaToDatabaseInstance instanceInterface;
	
	private SQLiteDatabase instancesDb;
	private SQLiteDatabase formsDb;
	/*
	private GeoBlocPackageManager pm;
	private SharedPreferences prefs;
	private String packagesPath;
	private int erased;
	*/
	private IStandardTaskListener listener;
	
	public void setListener(IStandardTaskListener listener) {
		this.listener = listener;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	
	public void setInstanceSQLiteDatabase(SQLiteDatabase db) {
		this.instancesDb = db;
	}
	
	public void setFormsSQLiteDatabase(SQLiteDatabase db) {
		this.formsDb = db;
	}
	
	@Override
	protected void onPreExecute() {
		instanceInterface = DbFormInstance.getParserInterfaceInstance(context);
	}
	
	@Override
	protected String doInBackground(Long... params) {
		String report = "";
		DbFormInstance dbi;
		int i = 0;
		for (Long id : params) {
			dbi = DbFormInstance.loadFrom(instancesDb, formsDb, id);
			try {
				instanceInterface.deleteInstance(id);
				report += " - " + dbi.getLabel() + " " + context.getString(R.string.formsManager_itemDeletedSuccessfully) + "\n\n";
			}
			catch (Exception e) {
				report += " - " + dbi.getLabel() + " " + context.getString(R.string.formsManager_itemDeleteEncounteredErrors) + "\n\n";
				e.printStackTrace();
			}
			i++;
			publishProgress(i, params.length);
		}
		instanceInterface.close();
		return report;
	}
	
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
		if (listener != null) {
			listener.progressUpdate(progress[0], progress[1]);
		}
	}
	
	@Override
	protected void onPostExecute(String result) {
		if (listener != null) {
			listener.taskComplete(result);
		}
    }

}
