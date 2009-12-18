/**
 * 
 */
package com.geobloc.activities;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.client.HttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geobloc.ApplicationEx;
import com.geobloc.R;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.shared.Utilities;
import com.geobloc.tasks.DownloadFormsTask;
import com.geobloc.tasks.GetListOfAvailableFormsTask;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
//public class FormsDownloader extends Activity implements Runnable {
public class FormsDownloader extends Activity implements IStandardTaskListener {
	
	@SuppressWarnings("unused")
	private static class EfficientAdapter extends BaseAdapter {
		// from ApiDemos/Views/List14.java for Efficient ListView Adapters
		// and from ApiDemos/Views/List11.java for Multi-Choice ListViews
		
        private LayoutInflater mInflater;

        public EfficientAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);

        }

        /**
         * The number of items in the list is determined by the number of speeches
         * in our array.
         *
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
            //return DATA.length;
        	return formFilenames.size();
        }

        /**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex data
         * structure, we would return whatever object represents one row in the
         * list.
         *
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position) {
            return position;
        }

        /**
         * Use the array index as a unique id.
         *
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a view to hold each row.
         *
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.forms_downloader_list_item, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.cb = (CheckBox) convertView.findViewById(R.id.forms_downloader_list_itemCb);
                holder.formName = (TextView) convertView.findViewById(R.id.forms_downloader_list_itemFormNameTextView);
                holder.formVersion = (TextView) convertView.findViewById(R.id.forms_downloader_list_itemFormVersionNumberTextView);
                holder.status = (TextView) convertView.findViewById(R.id.forms_downloader_list_itemFormStatusInfoTextView);
                holder.background = (ViewGroup) convertView.findViewById(R.id.forms_downloader_list_itemParentView);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.formName.setText(formFilenames.get(position));
            holder.formVersion.setText("1");
            
            // depending on form's state
            if (downloadedFormFilenames.contains(formFilenames.get(position))) {
            	// form has been downloaded
            	// disable (currently only support downloaded or new)
            	holder.cb.setEnabled(false);
            	holder.status.setText(R.string.forms_downloader_list_itemFormStatusCurrent);
            	holder.status.setTextColor(Color.CYAN);
            	holder.background.setBackgroundColor(Color.DKGRAY);
            }
            else {
            	// new form
            	holder.status.setText(R.string.forms_downloader_list_itemFormStatusNew);
            	holder.status.setTextColor(Color.GREEN);
            }

            return convertView;
        }

        static class ViewHolder {
        	CheckBox cb;
        	TextView formName;
        	TextView formVersion;
        	TextView status;
        	ViewGroup background;
        }
    }
	
	private static class FormsDownloader_DownloadFormsTaskListener implements IStandardTaskListener {

		private Context callerContext;
		private Context appContext;
		
		public FormsDownloader_DownloadFormsTaskListener(Context appContext, Context callerContext) {
			this.callerContext = callerContext;
			this.appContext = appContext;
		}
		
		@Override
		public void downloadingComplete(Object result) {
			// TODO Auto-generated method stub
			Enumeration<String> filenames = data.keys();
			formFilenames.clear();
			downloadedFormFilenames.clear();
			GeoBlocPackageManager pm = new GeoBlocPackageManager();
			pm.openOrBuildPackage(formsPath);
			if (pm.OK()) {
				downloadedFormFilenames = pm.getAllFilenames();
				while (filenames.hasMoreElements()) {
					formFilenames.add(filenames.nextElement());
				}
				listView.setAdapter(new EfficientAdapter(appContext));
				pd.dismiss();
				String res = (String)result;
				Utilities.showTitleAndMessageDialog(callerContext, "Report", res);
			}
			else {
				pd.dismiss();
				Utilities.showToast(callerContext, "Error! Could not I/O from SDCard.", Toast.LENGTH_LONG);
			}			
		}

		@Override
		public void progressUpdate(int progress, int total) {
			// TODO Auto-generated method stub
			
		}
    	
    }
	
	private static ListView listView;
	private Button eraseButton;
	private static List<String> formFilenames = new ArrayList<String>();
	private static List<String> downloadedFormFilenames = new ArrayList<String>();
	private static List<String> formKeys = new ArrayList<String>();
	
	private SharedPreferences prefs;
	private static String formsPath;
	private static ProgressDialog pd;
	
	private static Hashtable<String, String> data;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forms_downloader);
        initConfig();
	}
	
	private void initConfig() {
		listView = (ListView) findViewById(R.id.forms_downloaderListView);
		
		// for now (behaviour will be added later to eraseButton)
		eraseButton = (Button) findViewById(R.id.forms_downloaderEraseButton);
		eraseButton.setEnabled(false);

		
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		formsPath = prefs.getString(GBSharedPreferences.__FORMS_PATH_KEY__, 
				GBSharedPreferences.__DEFAULT_FORMS_PATH__);
		
		listView.setAdapter(new EfficientAdapter(getApplicationContext()));
		
		listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		pd = ProgressDialog.show(this, "Working", "Fetching filelist from server...");
		pd.setIndeterminate(false);
		pd.setCancelable(false);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String url = prefs.getString(GBSharedPreferences.__GET_AVAILABLE_FORMS_LIST_SERVLET_ADDRESS_KEY__, 
				GBSharedPreferences.__DEFAULT_GET_AVAILABLE_FORMS_LIST_SERVLET_ADDRESS__);
		
		GetListOfAvailableFormsTask task = new GetListOfAvailableFormsTask();
		task.setContext(getApplicationContext());
		// get httpClient from ApplicationEx
		ApplicationEx app = (ApplicationEx)this.getApplication();
		HttpClient httpClient = app.getHttpClient();
		task.setHttpClient(httpClient);
		task.setListener(this);
		task.execute(url);
		
	}
	
	public void forms_downloaderDownloadButtonOnClickHandler(View target) {
		
		// this is supposed to be more efficient, but how?? it returns null
		//SparseBooleanArray sba = listView.getCheckedItemPositions();
		
		if (data != null) {
			pd = ProgressDialog.show(this, "Working", "Preparing operation...");
			pd.setIndeterminate(false);
			pd.setCancelable(false);
			
			//store key of files in toDownload
			List<String> toDownload = new ArrayList<String>();
			String report = "";
			int listItemCount = listView.getChildCount();
			// alternative to getCheckedItemPositions()
			// go through each and every View of the listView and check if the checkbox is checked
			for( int i=0;i<listItemCount;i++ ) { 
				TextView tv = (TextView) ((View)listView.getChildAt(i).findViewById
						(R.id.forms_downloader_list_itemFormNameTextView));
		    	CheckBox cbox = (CheckBox) ((View)listView.getChildAt(i)).findViewById 
		    			(R.id.forms_downloader_list_itemCb); 
		    	if( cbox.isChecked()) {
		    		toDownload.add(data.get(tv.getText().toString()));
		    		toDownload.add(tv.getText().toString());
		    		report += tv.getText().toString() + " " + data.get(tv.getText().toString()) + "\n";
		    	}
			}
			if (toDownload.size() > 0) {
				pd.setMessage("Downloading files...");
				String[] taskParam = new String[toDownload.size()];
				for (int i = 0 ; i < toDownload.size(); i++)
					taskParam[i] = toDownload.get(i);
				DownloadFormsTask task = new DownloadFormsTask();
				task.setContext(getApplicationContext());
				// get httpClient from ApplicationEx
				ApplicationEx app = (ApplicationEx)this.getApplication();
				HttpClient httpClient = app.getHttpClient();
				task.setHttpClient(httpClient);
				task.setListener(new FormsDownloader_DownloadFormsTaskListener(getApplicationContext(), this));
				task.execute(taskParam);
			}
			else {
				pd.dismiss();
				Utilities.showToast(this, "Could not perform operation. No forms have been selected.", Toast.LENGTH_SHORT);
			}
		}
		else
			Utilities.showToast(this, "Could not perform operation. Internal Error", Toast.LENGTH_LONG);
	}
	

	//@Override
	public void downloadingComplete(Object result) {
		
		if (data != null)
			data.clear();
		data = (Hashtable<String, String>)result;
		Enumeration<String> filenames = data.keys();
		
		formFilenames.clear();
		downloadedFormFilenames.clear();
		GeoBlocPackageManager pm = new GeoBlocPackageManager();
		pm.openOrBuildPackage(formsPath);
		if (pm.OK()) {
			downloadedFormFilenames = pm.getAllFilenames();
			while (filenames.hasMoreElements()) {
				formFilenames.add(filenames.nextElement());
			}
			listView.setAdapter(new EfficientAdapter(getApplicationContext()));
			pd.dismiss();
		}
		else {
			pd.dismiss();
			Utilities.showToast(this, "Error! Could not I/O from SDCard.", Toast.LENGTH_LONG);
		}		
	}

	//@Override
	public void progressUpdate(int progress, int total) {
		// TODO Auto-generated method stub
		
	}
}
