/**
 * 
 */
package com.geobloc.tasks;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.geobloc.R;
import com.geobloc.db.DbForm;
import com.geobloc.listeners.IStandardTaskListener;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class DeleteFormsTask extends AsyncTask<Long, Integer, String> {

	private static final String LOG_TAG = "DeleteFormsTask";
	
	private Context context;
	private SQLiteDatabase db;
	
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
	
	@Override
	protected String doInBackground(Long... params) {
		DbForm dbf;
		String report = "";
		File f;
		int i = 0;
		for (long id : params) {
			// load form
			dbf = DbForm.loadFrom(db, id);
			f = new File(dbf.getForm_file_path());
			if (!f.exists())
				Log.e(LOG_TAG, "Form with id='" + dbf.getForm_id() + "' could not be found in the filesystem.");
			else {
				if (f.delete()) {
					// file deleted successfully
					dbf.setForm_file_path(null);
					if (dbf.getServer_state() == DbForm.__FORM_SERVER_STATE_NOT_FOUND__) {
						// form not found in the server, better delete it from the database too
						dbf.delete(db);
						Log.i(LOG_TAG, "Form with id='" + dbf.getForm_id() + "' was deleted from the database due to its 'Not Found' status..");
					}
					else {
						dbf.setServer_state(DbForm.__FORM_SERVER_STATE_NEW__);
						dbf.save(db);
					}
					report += " - " + dbf.getForm_name() + " " + context.getString(R.string.formsManager_itemDeletedSuccessfully) + "\n\n";
					Log.i(LOG_TAG, "Form with id='" + dbf.getForm_id() + "' was deleted successfully.");
				}
				else {
					// could not delete
					report += " - " + dbf.getForm_name() + " " + context.getString(R.string.formsManager_itemDeleteEncounteredErrors) + "\n\n";
					Log.e(LOG_TAG, "Form with id='" + dbf.getForm_id() + "' could not be deleted.");
				}
			}
			i++;
			publishProgress(i, params.length);
		}
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
