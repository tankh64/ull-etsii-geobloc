/**
 * 
 */
package com.geobloc.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.geobloc.R;
import com.geobloc.animations.FormsDownloaderListViewAnimation;
import com.geobloc.db.DbForm;
import com.geobloc.db.DbFormSQLiteHelper;
import com.geobloc.services.DownloadFormsService;
import com.geobloc.services.UpdateFormsDatabaseService;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.shared.IFormDefinition;
import com.geobloc.shared.IJavaToDatabaseForm;
import com.geobloc.shared.Utilities;
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
	
	private boolean enableButtonSetAnimation = true;
	private ViewGroup updateButtonSet;
	//private RelativeLayout completedInstancesButtonSet;
	
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
	
	private void toggleButtonSetAnimation(final int mode) {
		Animation anim;
		boolean slideIn = false;
		if ((mode == 0) && (updateButtonSet.getVisibility() == View.GONE) || ((mode == 1) && updateButtonSet.getVisibility() == View.GONE))
			slideIn = true;
		if (slideIn) {
			//
			anim = new TranslateAnimation(0.0f, 0.0f, updateButtonSet.getLayoutParams().height, 0.0f);
			anim.setInterpolator(new AccelerateInterpolator(0.3f));
			anim.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					// Not needed	
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// Not needed
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					if (mode == 0)
						updateButtonSet.setVisibility(View.VISIBLE);
					/*
					else
						completedInstancesButtonSet.setVisibility(View.VISIBLE);
					*/
				}
			});
		}
		else {
			anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, updateButtonSet.getLayoutParams().height);
			anim.setInterpolator(new AccelerateInterpolator(0.5f));
			anim.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					// Not needed
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// Not needed	
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					if (mode == 0)
						updateButtonSet.setVisibility(View.GONE);
					/*
					else
						completedInstancesButtonSet.setVisibility(View.GONE);
					*/
				}
			});
		}
		if (mode == 0)
			updateButtonSet.startAnimation(anim);
		/*
		else
			completedInstancesButtonSet.startAnimation(anim);
		*/
	}
	
	private void initConfig() {
		
		//Hide the title bar
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forms_manager);
		
        // load preferences and resources
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		res = this.getResources();
        
		// debug and gather options
		serverDebugInfo = "";
		enableButtonSetAnimation = prefs.getBoolean(GBSharedPreferences.__SLIDING_BUTTONS_ANIMATION_KEY__, 
				GBSharedPreferences.__DEFAULT_SLIDING_BUTTONS_ANIMATION__);
		
		// load views
		this.flipper = (ViewFlipper)findViewById(R.id.formsManager_viewFlipper);
		this.downloadView = findViewById(R.id.formsManager_downloadFormsLayout);
		this.serverList = (ListView)findViewById(R.id.formsManager_downloadListView);
		this.lastUpdateDateTextView = (TextView) findViewById(R.id.formsManager_downloadLastListDate);
		this.lastUpdateDateTextView.setText(getString(R.string.formsManager_downlaodLastListDate) + " " + prefs.getString(GBSharedPreferences.__LAST_SERVER_LIST_CHECK_KEY__, ""));
		this.updateButtonSet = (ViewGroup) findViewById(R.id.formsManager_downloadFormsBottom);
		
		// load flick gesture
		gestureDetector = new GestureDetector(new MyGestureDetector());
		OnTouchListener touchListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
			}
		};
		serverList.setOnTouchListener(touchListener);
		
		// create and set header in internet dependent list
		this.connectivityListItem = new ProgressItem(getBaseContext());
		this.connectivityListItem.toggleBarVisibility();
		this.connectivityListItem.getProgressBar().setVisibility(View.GONE);
		this.connectivityListItem.setText(getString(R.string.ready));
		this.connectivityListItem.setFocusable(false);
		serverList.addHeaderView(this.connectivityListItem);
		serverList.setBackgroundColor(Color.TRANSPARENT);
		
		// connect lists with databases
		formsDb = (new DbFormSQLiteHelper(getBaseContext())).getWritableDatabase();
		formsCursor = DbForm.getAll(formsDb);
		formsCursor.moveToFirst();	
		startManagingCursor(formsCursor);
		formsAdapter = new DbFormAdapter(formsCursor);
		serverList.setAdapter(formsAdapter);
		
		// load all animations
		bounceLeft = AnimationUtils.loadAnimation(this, R.anim.bounce_left);
		bounceRight = AnimationUtils.loadAnimation(this, R.anim.bounce_right);
		slideLeftIn  = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
		
		// make other changes upon options
		if (enableButtonSetAnimation) {
        	updateButtonSet.setVisibility(View.GONE);
        	//completedInstancesButtonSet.setVisibility(View.GONE);
        }
        else {
        	updateButtonSet.setVisibility(View.VISIBLE);
        	//completedInstancesButtonSet.setVisibility(View.VISIBLE);
        }
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
		/*
		serviceIntent = new Intent(this, UpdateFormsDatabaseService.class); 
		bindService(serviceIntent,
				onService, Context.BIND_AUTO_CREATE);
		*/
		
		// load services
		doBindServices();

		
	}
	
	/*
	 * 
	 * CODE FOR ACTIVITY LOGIC 
	 * 
	 */
	
	private SQLiteDatabase formsDb;
	private Cursor formsCursor;
	private DbFormAdapter formsAdapter;
	
	private ListView serverList;
	private String serverDebugInfo;
	private TextView lastUpdateDateTextView;
	private UpdateFormsDatabaseService updateService = null;
	private UpdateFormsDatabaseService.LocalBinder lb;
	private DownloadFormsService downloadService = null;
	private boolean serverServiceBound = false;
	private ListView deleteList;
	
	private ProgressItem connectivityListItem;
	
	private View downloadView;
	private Bitmap connectivityBitmap;
	private Bitmap noConnectivityBitmap;
	
	private SharedPreferences prefs;
	private Resources res;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConfig();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		registerReceiver(receiver,
				new IntentFilter(UpdateFormsDatabaseService.BROADCAST_ACTION));
		registerReceiver(downloadReceiver,
				new IntentFilter(DownloadFormsService.BROADCAST_ACTION));
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		unregisterReceiver(receiver);
		unregisterReceiver(downloadReceiver);
	}
	
		
	@Override
	public void onDestroy() {
		super.onDestroy();
		formsCursor.close();
		formsDb.close();
		
		doUnbindServices();

	}
	
	private ServiceConnection onUpdateService = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			updateService = ((UpdateFormsDatabaseService.LocalBinder)rawBinder).getService();
			lb = (UpdateFormsDatabaseService.LocalBinder) rawBinder;

		}
		
		public void onServiceDisconnected(ComponentName className) {
			updateService = null;
		}
	};
	
	private ServiceConnection onDownloadService = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder rawBinder) {
			downloadService = ((DownloadFormsService.LocalBinder)rawBinder).getService();			
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			downloadService = null;			
		}
		
	};
	
	
	void doBindServices() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
	    bindService(new Intent(FormsManager.this, 
	            UpdateFormsDatabaseService.class), onUpdateService, Context.BIND_AUTO_CREATE);
	    bindService(new Intent(FormsManager.this,
	    		DownloadFormsService.class), onDownloadService, Context.BIND_AUTO_CREATE);
	    serverServiceBound = true;
	}

	void doUnbindServices() {
	    if (serverServiceBound) {
	        // Detach our existing connection.
	        unbindService(onUpdateService);
	        unbindService(onDownloadService);
	        serverServiceBound = false;
	    }
	}

	
	private BroadcastReceiver receiver=new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			serverDebugInfo = intent.getStringExtra("serverResponse");
			boolean success = intent.getBooleanExtra("result", false);
			connectivityListItem.getBar().setVisibility(View.GONE);
			if (!success) {
				connectivityListItem.setText(getString(R.string.formsManager_errorProcessingServerList));
				connectivityListItem.setBackgroundColor(Color.RED);
			}
			else {
				/*
				// I wonder why this doesn't work... none of them do, and they should
				formsAdapter.notifyDataSetInvalidated();
				formsAdapter.notifyDataSetChanged();
				*/
				lastUpdateDateTextView.setText(getString(R.string.formsManager_downlaodLastListDate) + " " + prefs.getString(GBSharedPreferences.__LAST_SERVER_LIST_CHECK_KEY__, ""));
				formsCursor.requery();
				formsAdapter.notifyDataSetChanged();
				/*
				formsAdapter = new DbFormAdapter(formsCursor);
				serverList.setAdapter(formsAdapter);
				*/
				connectivityListItem.setText(getString(R.string.ready));
				connectivityListItem.setBackgroundColor(Color.GREEN);
				animateServerList();
			}		
		}
	};
	
	private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean update = intent.getBooleanExtra("update", false);
			boolean success = !intent.getBooleanExtra("result", false);
			if (update) {
				connectivityListItem.setText("Completado " + intent.getIntExtra("next", 0) + " de " + intent.getIntExtra("total", 0));
				connectivityListItem.setPtrogressBarProgress(intent.getIntExtra("next", 0), intent.getIntExtra("total", 0));
			}
			else {
				connectivityListItem.getBar().setVisibility(View.GONE);
				connectivityListItem.getProgressBar().setVisibility(View.GONE);
				serverDebugInfo = intent.getStringExtra("serverResponse");
				formsCursor.requery();
				formsAdapter.notifyDataSetChanged();
				if (!success) {
					connectivityListItem.setText(getString(R.string.formsManager_errosEncounteredDuringDownload));
					connectivityListItem.setBackgroundColor(Color.RED);				
				}
				else {
					connectivityListItem.setText(getString(R.string.ready));
					connectivityListItem.setBackgroundColor(Color.GREEN);
				}
				Utilities.showTitleAndMessageDialog(context, getString(R.string.report), intent.getStringExtra("report"));
			}
		}
		
	};
	
	private boolean canPerformInternetAction() {
		this.connectivityListItem.setBackgroundColor(Color.TRANSPARENT);
		
		if (connectivityBitmap == null) {
			connectivityBitmap = Utilities.scaleToContainer(getResources(), R.drawable.connectivity, downloadView);
		}
    	
		if (noConnectivityBitmap == null) {
			noConnectivityBitmap = Utilities.scaleToContainer(getResources(), R.drawable.no_connectivity, downloadView);
			
		}

		/*
		 * NEED TO IMPROVE THIS BY KEEPING TRACK OF CONNECTIVITY
		 */
		if (Utilities.evaluateConnectivityAvailable(getBaseContext())) {
			this.connectivityListItem.getBar().setVisibility(View.VISIBLE);
			this.connectivityListItem.setText(getString(R.string.working));
			downloadView.setBackgroundDrawable(new BitmapDrawable(connectivityBitmap));
			return true;
			
		}
		else {
			this.connectivityListItem.getBar().setVisibility(View.GONE);
			this.connectivityListItem.setText(getString(R.string.noConnectivity));
			this.connectivityListItem.setBackgroundColor(Color.RED);
			downloadView.setBackgroundDrawable(new BitmapDrawable(noConnectivityBitmap));
			return false;
		}
	}
	
	private void updateServerList() {
		if (canPerformInternetAction()) {
			this.connectivityListItem.setText(getString(R.string.formsManager_donwloadingServerList));
			updateService.updateFormsDatabase();
		}
	}
	
	private void downloadForms() {
		if (formsAdapter.areThereElementsSelected()) {
			if (canPerformInternetAction()) {
				this.connectivityListItem.setText(getString(R.string.downloading));
				this.connectivityListItem.getProgressBar().setVisibility(View.VISIBLE);
				Long[] array = new Long[formsAdapter.getListOfCheckedIds().size()];
				for (int i = 0; i < array.length; i++)
					array[i] = formsAdapter.getListOfCheckedIds().get(i);
				//downloadService.downloadForms((Long[])formsAdapter.getListOfCheckedIds().toArray());
				this.connectivityListItem.setPtrogressBarProgress(0, array.length);
				formsAdapter.uncheckAllItems();
				downloadService.downloadForms(array);
			}
		}
	}
	
	
	private void animateServerList()     {
		serverList.startAnimation(new FormsDownloaderListViewAnimation());
	}
	
	public void formsManagerOnClickListener (View target) {
		downloadForms();
	}
	
	class DbFormAdapter extends CursorAdapter {
		
		private ArrayList<Long> serverCheckedItems;
		
		DbFormAdapter (Cursor c) {
			super(FormsManager.this, c);
			int n = c.getCount();
			serverCheckedItems = new ArrayList<Long>(n);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			DbFormWrapper wrapper = (DbFormWrapper) view.getTag();
			wrapper.populateFrom(cursor);
			wrapperLogic(wrapper);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.forms_manager_list_item, parent, false);
			DbFormWrapper wrapper = new DbFormWrapper(view, this);
			
			view.setTag(wrapper);
			wrapper.populateFrom(cursor);
			wrapperLogic(wrapper);
			return view;
		}
		/**
		 * Checks whether there are elements selected.
		 * @return True if there is at least one element selected from the list.
		 */
		public boolean areThereElementsSelected() {
			return (serverCheckedItems.size() > 0);
		}
		/**
		 * @return ArrayList with the database IDs of the selected list items.
		 */
		public ArrayList<Long> getListOfCheckedIds() {
			return serverCheckedItems;
		}
		/**
		 * Unchecks all items from the list and triggers animations if necessary,
		 */
		public void uncheckAllItems() {
			serverCheckedItems.clear();
			if (enableButtonSetAnimation) {
				if ((serverCheckedItems.size() < 1) && (updateButtonSet.getVisibility() == View.VISIBLE))
					toggleButtonSetAnimation(0);
			}
		}
		/**
		 * Checks all selectable items from the list.
		 */
		private void checkAllItems() {
			DbFormWrapper wrapper;
			View v = null;
			// let's be good; we first load the View
			if (this.getCount() > 0) {
				v = getView(0, null, null);
				wrapper = (DbFormWrapper) v.getTag();
				if (!wrapper.getCb().isChecked())
					this.toggleSelected(wrapper);
			}
			// and then we reuse it
			for (int i = 1; i < this.getCount(); i++) {
				wrapper = (DbFormWrapper) this.getView(i, v, null).getTag();
				if (!wrapper.getCb().isChecked())
					this.toggleSelected(wrapper);
			}
		}
		/**
		 * Adds or removes items to the selected items list. It also triggers animations when 
		 * necessary.
		 * @param wrapper DbFormWrapper of the item to toggle.
		 */
		private void toggleSelected(DbFormWrapper wrapper) {
			if (wrapper.getState() < DbForm.__FORM_SERVER_STATE_LATEST_VERSION__) {
				if (!serverCheckedItems.contains(wrapper.getId())) {
					serverCheckedItems.add(wrapper.getId());
					wrapper.getRow().setBackgroundColor(res.getColor(R.color.ULLGradientStart));
					wrapper.getCb().setChecked(true);
					//row.setBackgroundColor(R.color.CheckedRed);
				}
				else {
					serverCheckedItems.remove(wrapper.getId());
					wrapper.getRow().setBackgroundColor(res.getColor(R.color.Transparent));
					wrapper.getCb().setChecked(false);
				}
			}
			if (enableButtonSetAnimation) {
				if ((serverCheckedItems.size() > 0) && (updateButtonSet.getVisibility() == View.GONE))
					toggleButtonSetAnimation(0);
				if ((serverCheckedItems.size() < 1) && (updateButtonSet.getVisibility() == View.VISIBLE))
					toggleButtonSetAnimation(0);
			}
		}
		/**
		 * Handles state of the list item's. Whether they should be enabled, disabled, background color, 
		 * checkbox checked, state information towards the user, etc.
		 * @param wrapper DbFormWrapper of the list item to handle.
		 */
		private void wrapperLogic (DbFormWrapper wrapper) {
			/*
			 * Selected / Unselected logic
			 */
			if (serverCheckedItems.contains(wrapper.getId())) {
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.ULLGradientEnd));
				wrapper.getCb().setChecked(true);
			}
			else {
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.Transparent));
				wrapper.getCb().setChecked(false);
			}

			/*
			 * State logic
			 */
			if (wrapper.getState() > DbForm.__FORM_SERVER_STATE_MORE_RECENT_AVAILABLE__) {
				wrapper.getCb().setEnabled(false);
				wrapper.getRow().setBackgroundColor(Color.GRAY);
			}
			switch (wrapper.getState()) {
				case DbForm.__FORM_SERVER_STATE_NEW__:
					wrapper.getStatus().setText(getString(R.string.formsManager_list_itemFormStatusNew));
					wrapper.getStatus().setTextColor(Color.GREEN);
					break;
				case DbForm.__FORM_SERVER_STATE_LATEST_VERSION__:
					wrapper.getStatus().setText(getString(R.string.formsManager_list_itemFormStatusCurrent));
					wrapper.getStatus().setTextColor(Color.CYAN);
					break;
				case DbForm.__FORM_SERVER_STATE_MORE_RECENT_AVAILABLE__:
					wrapper.getStatus().setText(getString(R.string.formsManager_list_itemFormStatusUpdate));
					wrapper.getStatus().setTextColor(Color.YELLOW);
					break;
				case DbForm.__FORM_SERVER_STATE_NOT_FOUND__:
				default:
					wrapper.getStatus().setText(getString(R.string.formsManager_list_itemFormStatusNotFound));
					wrapper.getStatus().setTextColor(Color.RED);
					break;
			}
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
		private DbFormAdapter adapter = null;
		private int state;
		private DbFormWrapper myself;
		
		public DbFormWrapper (View view, DbFormAdapter adapter) {
			row = view;
			this.adapter = adapter;
			myself = this;
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
			
			getCb().setEnabled(true);
			getRow().setBackgroundColor(Color.TRANSPARENT);
			
		}

		public View getRow() {
			return row;
		}
		
		public int getState() {
			return state;
		}
		
		public long getId() {
			return id;
		}
		
		public CheckBox getCb() {
			if (cb == null) {
				cb = (CheckBox) row.findViewById(R.id.forms_manager_list_itemCb);
				cb.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						adapter.toggleSelected(myself);
					}
				});
			}
			return cb;
		}

		public TextView getFormName() {
			if (formName == null) {
				formName = (TextView) row.findViewById(R.id.forms_manager_list_itemFormNameTextView);
				formName.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						adapter.toggleSelected(myself);
						return false;
					}
				});
			}
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
	
	/* 
	 * Menu
	 *  
	*/
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.forms_manager_menu, menu);
    	if (prefs.getBoolean(GBSharedPreferences.__ENABLE_DEBUGGING_FEATURES_KEY__, false)) {
    		MenuItem item = menu.findItem(R.id.formsManager_menuShowDebug);
    		item.setVisible(true);
    	}
    	return true;
    }
    
    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case (R.id.formsManager_menuUpdate):
    		updateServerList();
			break;
    	case (R.id.formsManager_menuSelectAll):
    		formsAdapter.checkAllItems();
    		break;
    	case (R.id.formsManager_menuShowDebug):
    		Utilities.showTitleAndMessageDialog(this, getString(R.string.debugging), serverDebugInfo);
    		break;
    	// More items go here (if any) ...
    	}
    	return false; 
    }
	
}
