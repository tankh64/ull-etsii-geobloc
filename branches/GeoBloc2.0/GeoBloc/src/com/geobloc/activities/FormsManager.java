/**
 * 
 */
package com.geobloc.activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import org.apache.http.client.HttpClient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.geobloc.ApplicationEx;
import com.geobloc.R;
import com.geobloc.activities.InstanceManager.DbFormInstanceAdapter;
import com.geobloc.animations.FormsDownloaderListViewAnimation;
import com.geobloc.db.DbForm;
import com.geobloc.db.DbFormInstance;
import com.geobloc.db.DbFormSQLiteHelper;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.services.UpdateFormsDatabaseService;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.shared.Utilities;
import com.geobloc.tasks.GetListOfAvailableFormsTask;
import com.geobloc.widget.ProgressItem;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class FormsManager extends Activity {
	
	/*
	 * 
	 * CODE FOR ANIMATIONS (SWIPE AND BOTTOM LAYOUTS)
	 * 
	 */
	
	private static int __NUMBER_OF_PAGES__ = 2;
	
	private ViewFlipper flipper;
	private int currentPage = 1; // we start with the download page (to the right)
	
	private Animation bounceRight;
	private Animation bounceLeft;
	
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;	
	
	/*
	private boolean enableButtonSetAnimation = true;
	private RelativeLayout allInstancesButtonSet;
	private RelativeLayout completedInstancesButtonSet;
	*/
	
	class MyGestureDetector extends SimpleOnGestureListener {
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
                if (Math.abs(e1.getY() - e2.getY()) > GBSharedPreferences.SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > GBSharedPreferences.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > GBSharedPreferences.SWIPE_THRESHOLD_VELOCITY) {
                	if ((currentPage+1) == __NUMBER_OF_PAGES__) {
                		// bounce right
                		flipper.startAnimation(bounceRight);
                		return false;
                	}
                	flipper.setInAnimation(slideLeftIn);
                    flipper.setOutAnimation(slideLeftOut);
                	flipper.showNext();
                	currentPage++;
                }  else if ((e2.getX() - e1.getX() > GBSharedPreferences.SWIPE_MIN_DISTANCE) && 
                		(Math.abs(velocityX) > GBSharedPreferences.SWIPE_THRESHOLD_VELOCITY)) {
                	if (currentPage == 0) {
                		// bounce left
                		flipper.startAnimation(bounceLeft);
                		return false;
                	}
                	flipper.setInAnimation(slideRightIn);
                    flipper.setOutAnimation(slideRightOut);
                	flipper.showPrevious();
                	currentPage--;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;

		}
	}
	
	private void initConfig() {
		
		//Hide the title bar
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forms_manager);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.flipper = (ViewFlipper)findViewById(R.id.formsManager_viewFlipper);
		this.serverList = (ListView)findViewById(R.id.formsManager_downloadListView);
		gestureDetector = new GestureDetector(new MyGestureDetector());
		serverList.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
			}
		});

		this.connectivityListItem = new ProgressItem(getBaseContext());
		this.connectivityListItem.toggleBarVisibility();
		this.connectivityListItem.setText(getString(R.string.ready));
		serverList.addHeaderView(this.connectivityListItem);
		
		formsDb = (new DbFormSQLiteHelper(getBaseContext())).getWritableDatabase();
		formsCursor = DbForm.getAll(formsDb);
		formsCursor.moveToFirst();
		
		startManagingCursor(formsCursor);
		/*
		DbForm dbf = new DbForm();
		dbf.setForm_id("unknown");
		dbf.setForm_version(0);
		dbf.setForm_name("New");
		dbf.setForm_description("Found in the SD Card.");
		dbf.setForm_date(new Date());
		dbf.setForm_file_path("path");
		dbf.setServer_state(DbForm.__FORM_SERVER_STATE_LATEST_VERSION__);
		dbf.save(formsDb);
		*/
		
		formsAdapter = new DbFormAdapter(formsCursor);
		serverList.setAdapter(formsAdapter);
		
		bounceLeft = AnimationUtils.loadAnimation(this, R.anim.bounce_left);
		bounceRight = AnimationUtils.loadAnimation(this, R.anim.bounce_right);
		slideLeftIn  = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
		
		/*
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        */
		serviceIntent = new Intent(this, UpdateFormsDatabaseService.class); 
		bindService(serviceIntent,
				onService, Context.BIND_AUTO_CREATE);

	}
	
	/*
	 * 
	 * CODE FOR ACTIVITY LOGIC 
	 * 
	 */
	
	private SharedPreferences prefs;
	
	private SQLiteDatabase formsDb;
	private Cursor formsCursor;
	private DbFormAdapter formsAdapter;
	
	private ListView serverList;
	private static ArrayList<Long> serverCheckedItems;
	private IStandardTaskListener updateListener;
	private UpdateFormsDatabaseService updateService = null;
	private Intent serviceIntent;
	private ListView deleteList;
	
	private ProgressItem connectivityListItem;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConfig();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		formsCursor.close();
		formsDb.close();
		
		
		updateService.unbindService(onService);
		/*
		updateService.stopService(serviceIntent);
		*/
	}
	
	private ServiceConnection onService = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			updateService = ((UpdateFormsDatabaseService.LocalBinder)rawBinder).getService();
			updateServerList();
		}
		
		public void onServiceDisconnected(ComponentName className) {
			updateService = null;
		}
	};
	
	private void updateServerList() {
		if (Utilities.evaluateConnectivityAvailable(getBaseContext())) {
			this.connectivityListItem.getBar().setVisibility(View.VISIBLE);
			this.connectivityListItem.setText(getString(R.string.formsManager_donwloadingServerList));
			
	
			/*
			DateFormat df = DateFormat.getDateInstance();
			String s = df.format(new Date());
			try {
				Date d = df.parse(s);
				this.connectivityListItem.setText(d.toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			
			updateService.updateFormsDatabase();
			formsAdapter.notifyDataSetInvalidated();
			this.connectivityListItem.toggleBarVisibility();
			this.connectivityListItem.setText(getString(R.string.ready));
			this.connectivityListItem.setBackgroundColor(R.color.SoftGreen);
		}
		else {
			this.connectivityListItem.getBar().setVisibility(View.GONE);
			this.connectivityListItem.setText(getString(R.string.noConnectivity));
		}
	}
	
	private class FormsManager_downloadServerListTaskListener implements IStandardTaskListener {
		/*
		private Context callerContext;
		private Context appContext;
		
		public FormsManager_downloadServerListTaskListener(Context appContext, Context callerContext) {
			this.callerContext = callerContext;
			this.appContext = appContext;
		}
		*/
		
		@Override
		public void taskComplete(Object result) {
			/*
			if (data != null)
				data.clear();
			data = (Hashtable<String, String>)result;
			
			rebuildListView(appContext, callerContext);
			animateListView();
			*/
			connectivityListItem.setText(getString(R.string.formsManager_processingServerList));
		}

		/*		
		private void animateListView()     {
			listView.startAnimation(new FormsDownloaderListViewAnimation());
		}
		*/

		@Override
		public void progressUpdate(int progress, int total) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class DbFormAdapter extends CursorAdapter {
		
		DbFormAdapter (Cursor c) {
			super(FormsManager.this, c);
			int n = c.getCount();
			serverCheckedItems = new ArrayList<Long>(n);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			DbFormWrapper wrapper = (DbFormWrapper) view.getTag();
			wrapper.populateFrom(cursor);
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.forms_manager_list_item, parent, false);
			DbFormWrapper wrapper = new DbFormWrapper(view);
			
			view.setTag(wrapper);
			wrapper.populateFrom(cursor);
	
			return view;
		}
	}
	
	class DbFormWrapper {
		private DbForm dbf = null;
		private long id;
		private int version;
		private CheckBox cb;
    	private TextView formName;
    	private TextView formVersion;
    	private TextView status;
		private View row = null;
		private int state;
		
		public DbFormWrapper (View view) {
			row = view;
		}
		
		void populateFrom(Cursor c) {
			/*
			 * DUE TO PERFORMANCE ISSUES WE CANNOT USE THIS CODE
			if (dbf == null)
				dbf = new DbForm();
			dbf.loadFrom(c);
			*/
			id = c.getLong(c.getColumnIndex(DbForm.__LOCALFORMSDB_ID_KEY__));
			version = c.getInt(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_VERSION_KEY__));
			getFormName().setText(c.getString(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_NAME_KEY__)));
			getFormVersion().setText(c.getInt(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_VERSION_KEY__)) + ".0");
			state = c.getInt(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_SERVERSTATE_KEY__));
			switch (state) {
				case DbForm.__FORM_SERVER_STATE_NEW__:
					getStatus().setText(getString(R.string.formsManager_list_itemFormStatusNew));
					break;
				case DbForm.__FORM_SERVER_STATE_LATEST_VERSION__:
					getStatus().setText(getString(R.string.formsManager_list_itemFormStatusCurrent));
					break;
				case DbForm.__FORM_SERVER_STATE_MORE_RECENT_AVAILABLE__:
					getStatus().setText(getString(R.string.formsManager_list_itemFormStatusUpdate));
					break;
				default:
					getStatus().setText(getString(R.string.formsManager_list_itemFormStatusNotFound));
					break;
			}
		}

		public CheckBox getCb() {
			if (cb == null) {
				cb = (CheckBox) row.findViewById(R.id.forms_manager_list_itemCb);
				cb.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!serverCheckedItems.contains(id)) {
							serverCheckedItems.add(id);
							row.setBackgroundColor(Color.rgb(165, 42, 42));
							//row.setBackgroundColor(R.color.CheckedRed);
						}
						else {
							serverCheckedItems.remove(id);
							row.setBackgroundColor(R.color.Transparent);
						}
					}
				});
			}
			return cb;
		}

		public TextView getFormName() {
			if (formName == null)
				formName = (TextView) row.findViewById(R.id.forms_manager_list_itemFormNameTextView);
			return formName;
		}

		public TextView getFormVersion() {
			if (formVersion == null)
				formVersion = (TextView) row.findViewById(R.id.forms_manager_list_itemFormVersionNumberTextView);
			return formVersion;
		}

		public TextView getStatus() {
			if (status == null)
				status = (TextView) row.findViewById(R.id.forms_manager_list_itemFormStatusTextView);
			return status;
		}
		
		
	}
	
	/*
	 * This needs to be removed once we set the listener on the erase listview
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
	else
		return false;
	}
	
}
