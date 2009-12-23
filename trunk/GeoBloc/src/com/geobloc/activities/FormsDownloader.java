/**
 * 
 */
package com.geobloc.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.client.HttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.geobloc.ApplicationEx;
import com.geobloc.R;
import com.geobloc.animations.FormsDownloaderListViewAnimation;
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
public class FormsDownloader extends Activity implements android.content.DialogInterface.OnClickListener {
	
	@SuppressWarnings("unused")
	private static class EfficientAdapter extends BaseAdapter {
		// from ApiDemos/Views/List14.java for Efficient ListView Adapters
		// and from ApiDemos/Views/List11.java for Multi-Choice ListViews
		
		private LayoutInflater mInflater;

        public EfficientAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);
            checkedItems = new boolean[getCount()];
        }

        /**
         * The number of items in the list is determined by the mode we're on.
         *
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
        	if (eraseMode)
        		return downloadedFormFilenames.size();
        	else
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
            //return position;
        	View v = listView.getChildAt(position);
        	ViewHolder holder = (ViewHolder) v.getTag();
        	holder.position = position;
        	holder.cb.setChecked(checkedItems[position]);
        	return (ViewHolder) v.getTag();
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
        	/*
        	// new views, they won't be selected
        	while (checkedItems.size() < (position+1))
        		checkedItems.add(false);
        	*/
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
                holder.position = position;
                holder.cb = (CheckBox) convertView.findViewById(R.id.forms_downloader_list_itemCb);
                holder.cb.setOnClickListener(holder);
                holder.formName = (TextView) convertView.findViewById(R.id.forms_downloader_list_itemFormNameTextView);
                holder.formVersion = (TextView) convertView.findViewById(R.id.forms_downloader_list_itemFormVersionNumberTextView);
                holder.status = (TextView) convertView.findViewById(R.id.forms_downloader_list_itemFormStatusInfoTextView);
                holder.background = (ViewGroup) convertView.findViewById(R.id.forms_downloader_list_itemParentView);

                convertView.setTag(holder);
                
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
                holder.position = position;
				holder.cb.setChecked(checkedItems[position]);
            }

			
			
            //handleDownloadMode(position, parent, holder);
            
            if (!eraseMode) {
            	handleDownloadMode(position, parent, holder);
            }
            else {
            	handleEraseMode(position, parent, holder);
            }
            
            
            return convertView;
        }

        static class ViewHolder implements OnClickListener {
        	int position;
        	CheckBox cb;
        	TextView formName;
        	TextView formVersion;
        	TextView status;
        	ViewGroup background;
			
        	//@Override
			public void onClick(View v) {
				checkedItems[position] = cb.isChecked();
				// erase mode behaviour
				if (eraseMode) { 
					if (checkedItems[position])
						background.setBackgroundColor(Color.rgb(165, 42, 42));
					else
						background.setBackgroundColor(Color.DKGRAY);
				}
			}
        }// end of ViewHolder
        
		/**
		 * @param position
		 * @param parent
		 * @param holder
		 */
		private void handleEraseMode(int position, ViewGroup parent,
				ViewHolder holder) {
			/* ERASE MODE */
			// Bind the data efficiently with the holder.
			holder.formName.setText(downloadedFormFilenames.get(position));
			holder.formVersion.setText("1");
			holder.background.setBackgroundColor(Color.BLACK);
			holder.status.setText(R.string.forms_downloader_list_itemFormStatusCurrent);
			holder.status.setTextColor(Color.CYAN);
			if (checkedItems[position]) {
				holder.background.setBackgroundColor(Color.rgb(165, 42, 42));
			}
			else
				holder.background.setBackgroundColor(Color.DKGRAY);
		}

		/**
		 * @param position
		 * @param parent
		 * @param holder
		 */
		private void handleDownloadMode(int position, ViewGroup parent,
				ViewHolder holder) {
			/* DOWNLOAD MODE */
			// Bind the data efficiently with the holder.
			holder.formName.setText(formFilenames.get(position));
			holder.formVersion.setText("1");
			holder.background.setBackgroundColor(Color.BLACK);
			if (downloadedFormFilenames.contains(formFilenames.get(position))) {
				// form has been downloaded
				// disable (currently only support downloaded or new)
				holder.status.setText(R.string.forms_downloader_list_itemFormStatusCurrent);
				holder.status.setTextColor(Color.CYAN);
				holder.background.setBackgroundColor(Color.DKGRAY);
				holder.cb.setEnabled(false);
			}
			else {
				// new form
				holder.status.setText(R.string.forms_downloader_list_itemFormStatusNew);
				holder.status.setTextColor(Color.GREEN);
				holder.cb.setEnabled(true);
			}
		}
    }
	
	private static class FormsDownloader_GetListOfAvailableFormsTaskListener implements IStandardTaskListener {

		private Context callerContext;
		private Context appContext;
		
		public FormsDownloader_GetListOfAvailableFormsTaskListener(Context appContext, Context callerContext) {
			this.callerContext = callerContext;
			this.appContext = appContext;
		}
		
		@Override
		public void taskComplete(Object result) {
			if (data != null)
				data.clear();
			data = (Hashtable<String, String>)result;
			
			rebuildListView(appContext, callerContext);
			animateListView();
		}

		
		
		private void animateListView()     {
			listView.startAnimation(new FormsDownloaderListViewAnimation());
		}

		@Override
		public void progressUpdate(int progress, int total) {
			// TODO Auto-generated method stub
			
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
		public void taskComplete(Object result) {

			
			rebuildListView(appContext, callerContext);
			String res = (String)result;
			Utilities.showTitleAndMessageDialog(callerContext, "Report", res);
		}

		@Override
		public void progressUpdate(int progress, int total) {
			// TODO Auto-generated method stub
			
		}
    	
    }
	
	/**
	 * 
	 */
	private static boolean rebuildListView(Context appContext, Context callerContext) {
		newForms = false;
		Enumeration<String> filenames = data.keys();		
		formFilenames.clear();
		downloadedFormFilenames.clear();
		GeoBlocPackageManager pm = new GeoBlocPackageManager();
		pm.openOrBuildPackage(formsPath);
		if (pm.OK()) {
			downloadedFormFilenames = pm.getAllFilenames();
			while (filenames.hasMoreElements()) {
				String next = filenames.nextElement();
				// for now, if all elements of filenames are in downloadedFormFilenames, 
				// then they've already been downloaded (remember no Update option yet)
				if ((!newForms) && (!downloadedFormFilenames.contains(next)))
					newForms = true;
				formFilenames.add(next);
			}
			listView.setAdapter(new EfficientAdapter(appContext));
			pd.dismiss();
			
			
			if ((!eraseMode && (!newForms)) || (eraseMode && (downloadedFormFilenames.size() == 0))) {
				actionButton.setEnabled(false);
				Utilities.showToast(callerContext, "There are no items for this operation", Toast.LENGTH_LONG);
			}
			else
				actionButton.setEnabled(true);
			// refreshing the list went well
			return true;
		}
		else {
			pd.dismiss();
			Utilities.showToast(callerContext, "Error! Could not I/O from SDCard.", Toast.LENGTH_LONG);
			return false;
		}
		
	}
	
	private static ListView listView;
	private static Button actionButton;
	private static ToggleButton eraseModeButton;
	
	private static boolean[] checkedItems;
	private static List<String> formFilenames = new ArrayList<String>();
	private static List<String> downloadedFormFilenames = new ArrayList<String>();
	private static List<String> formKeys = new ArrayList<String>();
	
	private SharedPreferences prefs;
	private static String formsPath;
	private static ProgressDialog pd;
	
	private static Hashtable<String, String> data;
	
	public static boolean newForms;
	public static boolean eraseMode = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forms_downloader);
        initConfig();
	}
	
	private void initConfig() {
		listView = (ListView) findViewById(R.id.forms_downloaderListView);
		actionButton = (Button) findViewById(R.id.forms_downloaderActionButton);


		eraseModeButton = (ToggleButton) findViewById(R.id.forms_downloaderEraseModeButton);
		//eraseModeButton.setEnabled(false);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		formsPath = prefs.getString(GBSharedPreferences.__FORMS_PATH_KEY__, 
				GBSharedPreferences.__DEFAULT_FORMS_PATH__);
		
		//listView.setAdapter(new EfficientAdapter(getApplicationContext()));
		
		listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		if (!eraseMode) {
			/* DOWNLOAD MODE */
			if ((data == null) || (listView.getChildCount() < 1))
				updateListOfAvailableForms();
			else
				rebuildListView(getApplicationContext(), this);
			actionButton.setText(R.string.forms_downloaderActionDLButtonText);
			eraseModeButton.setChecked(false);
		}
		else {
			/* ERASE MODE*/
			actionButton.setText(R.string.forms_downloaderActionERButtonText);
			eraseModeButton.setChecked(true);
			rebuildListView(getApplicationContext(), this);
		}
		
		
	}

	private void updateListOfAvailableForms() {
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
		task.setListener(new FormsDownloader_GetListOfAvailableFormsTaskListener(getApplicationContext(), this));
		task.execute(url);
	}
	
	/**
	 * @return
	 */
	private List<String> getListOfSelectedItemsForDownload() {
		// this is supposed to be more efficient, but how?? it returns null
		//SparseBooleanArray sba = listView.getCheckedItemPositions();
		//store key of files in toDownload
		List<String> toDownload = new ArrayList<String>();
		for (int i = 0; i < checkedItems.length; i++) {
			if (checkedItems[i]) {
				toDownload.add(data.get(formFilenames.get(i)));
				toDownload.add(formFilenames.get(i));
			}
		}
		return toDownload;
	}
	
	private boolean eraseAssociatedCheckedItems() {
		boolean eraseResult = true;
		GeoBlocPackageManager pm = new GeoBlocPackageManager();
		pm.openOrBuildPackage(formsPath);
		if (pm.OK()) {
			for (int i = 0; i < checkedItems.length; i++)
				if (checkedItems[i])
					eraseResult = pm.eraseFile(downloadedFormFilenames.get(i));
		}
		else
			eraseResult = false;
		return eraseResult;
	}
	
	public void forms_downloaderActionButtonOnClickHandler(View target) {
		if (!eraseMode) {
			/* DOWNLOAD MODE */
			if (data != null) {
				pd = ProgressDialog.show(this, "Working", "Preparing operation...");
				pd.setIndeterminate(false);
				pd.setCancelable(false);
			
				List<String> toDownload = getListOfSelectedItemsForDownload();
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
		else {
			/* ERASE MODE */
			AlertDialog.Builder alert = new AlertDialog.Builder(this);  
			alert.setTitle("Are you sure?");
			String message = "Do you want to erase the following items?\n\n";
			for (int i = 0; i < checkedItems.length; i++) {
				if (checkedItems[i])
					message += downloadedFormFilenames.get(i) + "\n";
			}
			alert.setMessage(message);
			alert.setPositiveButton("Yes", this);
			alert.setNegativeButton("No", null);
			alert.show();
		}
	}


	
	public void forms_downloaderEraseModeButtonOnClickHandler(View target) {
		if (eraseModeButton.isChecked()) {;
			// go into erase mode
			eraseMode = true;
			actionButton.setText(R.string.forms_downloaderActionERButtonText);
			//listView.startAnimation(new ????Animation());
		}
		else {
			// back to download mode
			eraseMode = false;
			actionButton.setText(R.string.forms_downloaderActionDLButtonText);
		}
		rebuildListView(getApplicationContext(), this);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		boolean eraseResult = eraseAssociatedCheckedItems();
		if (eraseResult)
			Utilities.showToast(this, "Operation completed succesfully.", Toast.LENGTH_LONG);
		else
			Utilities.showToast(this, "Operation could not be completed.", Toast.LENGTH_LONG);
		rebuildListView(getApplicationContext(), this);
	}
}
