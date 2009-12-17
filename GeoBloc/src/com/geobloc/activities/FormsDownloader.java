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

import com.geobloc.ApplicationEx;
import com.geobloc.R;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.tasks.GetListOfAvailableFormsTask;
import com.google.listeners.IStandardTaskListener;

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
        	return formsNameArrayList.size();
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
                //holder.text = (TextView) convertView.findViewById(R.id.text);
                //holder.icon = (ImageView) convertView.findViewById(R.id.icon);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.formName.setText(formsNameArrayList.get(position));
            holder.formVersion.setText("1");
            //holder.text.setText(DATA[position]);
            //holder.icon.setImageBitmap((position & 1) == 1 ? mIcon1 : mIcon2);

            return convertView;
        }

        static class ViewHolder {
        	CheckBox cb;
        	TextView formName;
        	TextView formVersion;
            //TextView text;
            //ImageView icon;
        }

    }
	
	private ListView listView;
	private Button eraseButton;
	private static List<String> formsNameArrayList = new ArrayList<String>();
	private static List<String> formsKeyArrayList = new ArrayList<String>();
	
	private ProgressDialog pd;
	
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
		
		// for testing
		/*
		formsNameArrayList.add("Form1.xml");
		formsNameArrayList.add("Forms2.xml");
		*/
		
		listView.setAdapter(new EfficientAdapter(getApplicationContext()));
		
		listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		pd = ProgressDialog.show(this, "Working", "Fetching files from server...");
		pd.setIndeterminate(false);
		pd.setCancelable(false);
		
		//Thread thread = new Thread(this);
		//thread.start();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String url = prefs.getString(GBSharedPreferences.__GET_AVAILABLE_FORMS_LIST_SERVLET_ADDRESS_KEY__, 
				GBSharedPreferences.__DEFAULT_GET_AVAILABLE_FORMS_LIST_SERVLET_ADDRESS);
		
		GetListOfAvailableFormsTask task = new GetListOfAvailableFormsTask();
		task.setContext(getApplicationContext());
		// get httpClient from ApplicationEx
		ApplicationEx app = (ApplicationEx)this.getApplication();
		HttpClient httpClient = app.getHttpClient();
		task.setHttpClient(httpClient);
		task.setListener(this);
		task.execute(url);
		
	}
	/*
	// thread code
	public void run() {
		// HttpGet
		
		
		// get servlet address from shared preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String url = prefs.getString(GBSharedPreferences.__GET_AVAILABLE_FORMS_LIST_SERVLET_ADDRESS_KEY__, 
				GBSharedPreferences.__DEFAULT_GET_AVAILABLE_FORMS_LIST_SERVLET_ADDRESS);
		
		SimpleHttpPost post;
		
		
		// boolean to check for Exception
		boolean exception = false;

		// boolean to exit loop (success or failrue)
		boolean done = false;
		
		Hashtable<String, String> response;
		
		for (int i = 1; (!exception && !done && (i <= 3)); i++) {
			post = new SimpleHttpPost();
			// tell the handler in which attempt we are
			//handler.sendEmptyMessage(i);
			try {
				// get httpClient from ApplicationEx
				ApplicationEx app = (ApplicationEx)this.getApplication();
				HttpClient httpClient = app.getHttpClient();
				//serverResponse = post.executeMultipartPost(formDirectory, "form.xml", url, httpClient);
				response = post.executeHttpPostAvailableForms(url, httpClient);
				
				// should be enough to check
				if (response != null)
					done = true;
			}
			catch (Exception e){
				// if exception, exit loop
				exception = true;
				e.printStackTrace();
				response = null;
			}
		}

		// tell the handler to dismiss the dialog; we're done
        handler.sendEmptyMessage(0);
    }
	*/
	/*
	// thread handler code (activated when thread is finished)
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	pd.dismiss();
        	
        }
    };
    */

	//@Override
	public void downloadingComplete(Object result) {
		pd.dismiss();
		Hashtable<String, String> data = (Hashtable<String, String>)result;
		Enumeration<String> filenames = data.keys();
		
		formsNameArrayList.clear();
		// build the List checking whether the filename is already in forms path
		while (filenames.hasMoreElements()) {
			formsNameArrayList.add(filenames.nextElement());
		}
		
		listView.setAdapter(new EfficientAdapter(getApplicationContext()));
    	/*
    	listView.setAdapter(new EfficientAdapter(getApplicationContext()));
		
		listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        */
	}

	//@Override
	public void progressUpdate(int progress, int total) {
		// TODO Auto-generated method stub
		
	}
}
