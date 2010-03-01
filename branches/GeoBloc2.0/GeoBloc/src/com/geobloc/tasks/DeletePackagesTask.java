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

import com.geobloc.db.DbFormInstance;
import com.geobloc.db.DbFormInstanceSQLiteHelper;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.shared.GBSharedPreferences;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class DeletePackagesTask extends AsyncTask<Long, String, String> {

	private static final String LOG_TAG = "DeletePackagesTask";
	
	private Context context;
	private SQLiteDatabase db;
	private GeoBlocPackageManager pm;
	private SharedPreferences prefs;
	private String packagesPath;
	private int erased;
	
	private IStandardTaskListener listener;
	
	public void setListener(IStandardTaskListener listener) {
		this.listener = listener;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public void setSQLiteDatabase(SQLiteDatabase db) {
		this.db = db;
	}
	
	private void initConfig() {
	
		erased = 0;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		packagesPath =  prefs.getString(GBSharedPreferences.__PACKAGES_PATH_KEY__, 
			GBSharedPreferences.__DEFAULT_PACKAGES_PATH__);
		
		pm = new GeoBlocPackageManager();
		pm.openOrBuildPackage(packagesPath);
	}
	
	@Override
	protected String doInBackground(Long... params) {
		initConfig();
		int count = params.length;
		for (Long id : params) {
			if (pm.OK()) {
				if (deletePackage(id))
					erased++;
			}
		}
		String res = "";
		res += erased + " paquete(s) fueron borrado(s). \n";
		if (erased < params.length)
			res += (params.length - erased) + " paquete(s) no pudieron ser borrado(s). \n";
		return res;
	}
	
	private boolean deletePackage(Long id) {
		DbFormInstance dbi = DbFormInstance.loadFrom(db, id);
		boolean found = false;
		boolean deleted = false;
		if (dbi != null) {
			List<File> packages = pm.getAllDirectories();
			int i = 0;
			while ((!found) && (i < packages.size())) {
				if (dbi.getPackageLocation().contains(packages.get(i).getAbsolutePath())) {
					found = true;
					deleted = pm.eraseDirectory(packages.get(i).getName());
					if (deleted)
						dbi.delete(db);
				}
				i++;
			}
		}
		return deleted;
	}
	
	@Override
	protected void onPostExecute(String result) {
		if (listener != null) {
			listener.taskComplete(result);
		}
    }

}
