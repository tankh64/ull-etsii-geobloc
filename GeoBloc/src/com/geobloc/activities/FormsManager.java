/**
 * 
 */
package com.geobloc.activities;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.Gravity;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.geobloc.R;
import com.geobloc.animations.DeleteItemsAnimation;
import com.geobloc.animations.UpdateFormsAnimation;
import com.geobloc.db.DbForm;
import com.geobloc.db.DbFormSQLiteHelper;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.services.DownloadFormsService;
import com.geobloc.services.UpdateFormsDatabaseService;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.shared.Utilities;
import com.geobloc.tasks.DeleteFormsTask;
import com.geobloc.widget.ExpandableTextView;
import com.geobloc.widget.ProgressItem;

/**
 * Activity which will allow the user to refresh the current data on the server, download or update new 
 * forms and erase forms.
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
	/**
	 * 0 == LEFT PAGE (Delete Forms List)
	 * 1 == RIGHT PAGE (Server List)
	 */
	private int currentPage = 1;
	
	private Animation bounceRight;
	private Animation bounceLeft;
	
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	
	private GestureDetector gestureDetector;
	
	private boolean enableButtonSetAnimation = false;
	private boolean enableBackgrounds = false;
	private boolean enableViewAnimations = false;
	private ViewGroup updateButtonSet;
	private ViewGroup deleteButtonSet;
	//private RelativeLayout completedInstancesButtonSet;
	
	class MyGestureDetector extends SimpleOnGestureListener {
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if ((enableBackgrounds) && (localFormsBitmap == null)) {
				localFormsBitmap = Utilities.scaleDownToContainer(res, R.drawable.local_forms, deleteView.getMeasuredHeight(), deleteView.getMeasuredWidth());
				bmp = new BitmapDrawable(res, localFormsBitmap);
				bmp.setGravity(Gravity.CENTER);
				deleteView.setBackgroundDrawable(bmp);
			}
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
		
        // load preferences and resources
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		res = this.getResources();
        
		// debug and gather options
		serverDebugInfo = "";
		enableButtonSetAnimation = prefs.getBoolean(GBSharedPreferences.__SLIDING_BUTTONS_ANIMATION_KEY__, 
				GBSharedPreferences.__DEFAULT_SLIDING_BUTTONS_ANIMATION__);
		enableBackgrounds = prefs.getBoolean(GBSharedPreferences. __ENABLE_ACTIVITY_BACKGROUNDS_KEY__, 
				GBSharedPreferences.__DEFAULT_ENABLE_ACTIVITY_BACKGROUNDS__);
		enableViewAnimations = prefs.getBoolean(GBSharedPreferences.__ENABLE_VIEW_ANIMATIONS_KEY__, 
        		GBSharedPreferences.__DEFAULT_ENABLE_VIEW_ANIMATIONS__);
		
		// load views
		this.flipper = (ViewFlipper)findViewById(R.id.formsManager_viewFlipper);
		this.downloadView = findViewById(R.id.formsManager_downloadFormsLayout);
		this.deleteView = findViewById(R.id.formsManager_deleteFormsLayout);
		this.serverList = (ListView)findViewById(R.id.formsManager_downloadListView);
		this.deleteFormsList = (ListView)findViewById(R.id.formsManager_deleteFormsListView);
		this.lastUpdateDateTextView = (TextView) findViewById(R.id.formsManager_downloadLastListDate);
		this.lastUpdateDateTextView.setText(getString(R.string.formsManager_downlaodLastListDate) + " " + prefs.getString(GBSharedPreferences.__LAST_SERVER_LIST_CHECK_KEY__, getString(R.string.notAvailable)));
		this.updateButtonSet = (ViewGroup) findViewById(R.id.formsManager_downloadFormsFooter);
		this.deleteButtonSet = (ViewGroup) findViewById(R.id.formsManager_deleteFormsFooter);
		
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
		deleteFormsList.setOnTouchListener(touchListener);
		
		// create and set header in server (right) list
		this.serverConnectivityListItem = new ProgressItem(getBaseContext());
		this.serverConnectivityListItem.toggleBarVisibility();
		this.serverConnectivityListItem.getProgressBar().setVisibility(View.GONE);
		this.serverConnectivityListItem.setText(getString(R.string.ready));
		this.serverConnectivityListItem.setFocusable(false);
		serverList.addHeaderView(this.serverConnectivityListItem);
		serverList.setBackgroundColor(Color.TRANSPARENT);
		
		// create and set header in erase (left) list
		this.deleteProgressListItem = new ProgressItem(getBaseContext());
		this.deleteProgressListItem.getText().setVisibility(View.GONE);
		this.deleteProgressListItem.getBar().setVisibility(View.GONE);
		this.deleteProgressListItem.getProgressBar().setVisibility(View.GONE);
		deleteFormsList.addHeaderView(this.deleteProgressListItem);
		deleteFormsList.setBackgroundColor(Color.TRANSPARENT);
		
		// connect lists with databases
		formsDb = (new DbFormSQLiteHelper(getBaseContext())).getWritableDatabase();
		
		formsCursor = DbForm.getAllForms(formsDb);
		formsCursor.moveToFirst();	
		startManagingCursor(formsCursor);
		formsAdapter = new DbFormAdapter(this, formsCursor, 0, updateButtonSet, enableButtonSetAnimation);
		serverList.setAdapter(formsAdapter);
		
		// left list
		deleteFormsCursor = DbForm.getAllLocalForms(formsDb);
		//deleteFormsCursor.moveToFirst();
		startManagingCursor(deleteFormsCursor);
		deleteFormsAdapter = new DbFormAdapter(this, deleteFormsCursor, 1, deleteButtonSet, enableButtonSetAnimation);
		deleteFormsList.setAdapter(deleteFormsAdapter);
		
		
		
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
        	deleteButtonSet.setVisibility(View.GONE);
        }
        else {
        	updateButtonSet.setVisibility(View.VISIBLE);
        	deleteButtonSet.setVisibility(View.VISIBLE);
        }
		
		// load services
		doBindServices();
		didItOnce = false;
		
	}
	
	/*
	 * 
	 * CODE FOR ACTIVITY LOGIC 
	 * 
	 */
	
	private SQLiteDatabase formsDb;
	private Cursor formsCursor;
	private Cursor deleteFormsCursor;
	private DbFormAdapter formsAdapter;
	private DbFormAdapter deleteFormsAdapter;
	
	private ListView serverList;
	private String serverDebugInfo;
	private TextView lastUpdateDateTextView;
	private UpdateFormsDatabaseService updateService = null;
	private UpdateFormsDatabaseService.LocalBinder lb;
	private DownloadFormsService downloadService = null;
	private boolean serverServiceBound = false;
	private ListView deleteFormsList;
	
	private ProgressItem serverConnectivityListItem;
	private ProgressItem deleteProgressListItem;
	
	private View downloadView;
	private View deleteView;
	private Bitmap connectivityBitmap;
	private Bitmap noConnectivityBitmap;
	private Bitmap localFormsBitmap;
	private BitmapDrawable bmp;
	
	private SharedPreferences prefs;
	private Resources res;
	
	private boolean didItOnce;
	
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
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (enableBackgrounds && hasFocus && !didItOnce) {
			this.canPerformInternetAction();
			this.serverConnectivityListItem.getText().setText(getString(R.string.ready));
			this.serverConnectivityListItem.getBar().setVisibility(View.GONE);
			didItOnce = true;
		}
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
	
	private void refreshLists() {
		formsCursor.requery();
		formsAdapter.notifyDataSetChanged();
		deleteFormsCursor.requery();
		deleteFormsAdapter.notifyDataSetChanged();
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
			serverConnectivityListItem.getBar().setVisibility(View.GONE);
			if (!success) {
				serverConnectivityListItem.setText(getString(R.string.formsManager_errorProcessingServerList));
				serverConnectivityListItem.setBackgroundColor(Color.RED);
			}
			else {
				lastUpdateDateTextView.setText(getString(R.string.formsManager_downlaodLastListDate) + " " + prefs.getString(GBSharedPreferences.__LAST_SERVER_LIST_CHECK_KEY__, ""));
				formsCursor.requery();
				formsAdapter.notifyDataSetChanged();
				serverConnectivityListItem.setText(getString(R.string.ready));
				serverConnectivityListItem.setBackgroundColor(res.getColor(R.color.TransparentGreen));
				if (enableViewAnimations) {
					animateServerList();
				}
			}		
		}
	};
	
	private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean update = intent.getBooleanExtra("update", false);
			boolean success = !intent.getBooleanExtra("result", false);
			if (update) {
				//serverConnectivityListItem.setText("Completado " + intent.getIntExtra("next", 0) + " de " + intent.getIntExtra("total", 0));
				serverConnectivityListItem.setText(getString(R.string.completedItems, intent.getIntExtra("next", 0), intent.getIntExtra("total", 0)));
				serverConnectivityListItem.setPtrogressBarProgress(intent.getIntExtra("next", 0), intent.getIntExtra("total", 0));
			}
			else {
				serverConnectivityListItem.getBar().setVisibility(View.GONE);
				serverConnectivityListItem.getProgressBar().setVisibility(View.GONE);
				serverDebugInfo = intent.getStringExtra("serverResponse");
				refreshLists();
				if (!success) {
					serverConnectivityListItem.setText(getString(R.string.formsManager_errosEncounteredDuringDownload));
					serverConnectivityListItem.setBackgroundColor(Color.RED);				
				}
				else {
					serverConnectivityListItem.setText(getString(R.string.ready));
					serverConnectivityListItem.setBackgroundColor(res.getColor(R.color.TransparentGreen));
				}
				Utilities.showTitleAndMessageDialog(context, getString(R.string.report), intent.getStringExtra("report"));
			}
		}
		
	};
	
	private boolean canPerformInternetAction() {
		this.serverConnectivityListItem.setBackgroundColor(Color.TRANSPARENT);
		
		if (connectivityBitmap == null) {
			connectivityBitmap = Utilities.scaleDownToContainer(getResources(), R.drawable.connectivity, downloadView);
		}
    	
		if (noConnectivityBitmap == null) {
			noConnectivityBitmap = Utilities.scaleDownToContainer(getResources(), R.drawable.no_connectivity, downloadView);
			
		}

		/*
		 * NEED TO IMPROVE THIS BY KEEPING TRACK OF CONNECTIVITY
		 */
		if (Utilities.evaluateConnectivityAvailable(getBaseContext())) {
			this.serverConnectivityListItem.getBar().setVisibility(View.VISIBLE);
			this.serverConnectivityListItem.setText(getString(R.string.working));
			if (enableBackgrounds) {
				bmp = new BitmapDrawable(res, connectivityBitmap);
				bmp.setGravity(Gravity.CENTER);
				downloadView.setBackgroundDrawable(bmp);
			}
			return true;
			
		}
		else {
			this.serverConnectivityListItem.getBar().setVisibility(View.GONE);
			this.serverConnectivityListItem.setText(getString(R.string.noConnectivity));
			this.serverConnectivityListItem.setBackgroundColor(Color.RED);
			if (enableBackgrounds) {
				bmp = new BitmapDrawable(res, noConnectivityBitmap);
				bmp.setGravity(Gravity.CENTER);
				downloadView.setBackgroundDrawable(bmp);
			}
			return false;
		}
	}
	
	private void updateServerList() {
		if (canPerformInternetAction()) {
			this.serverConnectivityListItem.setText(getString(R.string.formsManager_donwloadingServerList));
			updateService.updateFormsDatabase();
		}
	}
	
	private void downloadForms() {
		if (formsAdapter.areThereElementsSelected()) {
			if (canPerformInternetAction()) {
				this.serverConnectivityListItem.setText(getString(R.string.downloading));
				this.serverConnectivityListItem.getProgressBar().setVisibility(View.VISIBLE);
				this.serverConnectivityListItem.getProgressBar().setProgress(0);
				Long[] array = new Long[formsAdapter.getListOfCheckedIds().size()];
				for (int i = 0; i < array.length; i++)
					array[i] = formsAdapter.getListOfCheckedIds().get(i);
				//downloadService.downloadForms((Long[])formsAdapter.getListOfCheckedIds().toArray());
				this.serverConnectivityListItem.setPtrogressBarProgress(0, array.length);
				formsAdapter.uncheckAllItems();
				downloadService.downloadForms(array);
				serverList.scrollTo(0, 0);
			}
		}
	}
	
	private class DeleteFormsTaskListener implements IStandardTaskListener {

		private Context context;
		
		public DeleteFormsTaskListener (Context context) {
			this.context = context;
		}
		
		@Override
		public void progressUpdate(int progress, int total) {
			deleteProgressListItem.setText(getString(R.string.completedItems, progress, total));
			deleteProgressListItem.setPtrogressBarProgress(progress, total);
		}

		@Override
		public void taskComplete(final Object result) {
			deleteProgressListItem.getText().setVisibility(View.GONE);
			deleteProgressListItem.getBar().setVisibility(View.GONE);
			deleteProgressListItem.getProgressBar().setVisibility(View.GONE);
			
			//animateDeleteList();
			if (enableViewAnimations) {
				Animation delete = new DeleteItemsAnimation();
				delete.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {	
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					@Override
					public void onAnimationEnd(Animation animation) {
						refreshLists();
						Utilities.showTitleAndMessageDialog(context, getString(R.string.report), (String)result);
					}
				});
				deleteFormsList.startAnimation(delete);
			}
			else {
				refreshLists();
				Utilities.showTitleAndMessageDialog(context, getString(R.string.report), (String)result);
			}
		}
		
	}
	
	private void eraseForms() {
		if (deleteFormsAdapter.areThereElementsSelected()) {
			deleteProgressListItem.getText().setVisibility(View.VISIBLE);
			deleteProgressListItem.getText().setText(getString(R.string.deleting));
			deleteProgressListItem.getBar().setVisibility(View.VISIBLE);
			deleteProgressListItem.getProgressBar().setVisibility(View.VISIBLE);
			deleteProgressListItem.getProgressBar().setProgress(0);
			DeleteFormsTaskListener taskListener = new DeleteFormsTaskListener(this);
			DeleteFormsTask deleteTask = new DeleteFormsTask();
			deleteTask.setContext(this);
			deleteTask.setSQLiteDatabase(formsDb);
			deleteTask.setListener(taskListener);
			Long[] params = new Long[deleteFormsAdapter.getListOfCheckedIds().size()];
			for (int i = 0; i < params.length; i++)
				params[i] = deleteFormsAdapter.getListOfCheckedIds().get(i);
			deleteFormsAdapter.uncheckAllItems();
			deleteTask.execute(params);
			deleteFormsList.scrollTo(0, 0);
			
		}
	}
	
	private void animateServerList()     {
		serverList.startAnimation(new UpdateFormsAnimation());
	}
	
	public void formsManagerOnClickListener (View target) {
		switch (target.getId()) {
		case R.id.formsManager_downloadButton:
			downloadForms();
			break;
		case R.id.formsManager_deleteButton:
			eraseForms();
			break;
		default:
			break;
		}
	}
	
	public class DbFormAdapter extends CursorAdapter {
		
		private ArrayList<Long> checkedItems;
		private ArrayList<Long> openItems;
		/**
		 * Mode 0 => Server List
		 * Mode 1 => Erase Forms List
		 */
		private int mode;
		private ViewGroup buttonSet;
		private Context context;
		private boolean enableButtonSetAnimation;
		private Resources res;
		
		public DbFormAdapter (Context context, Cursor c, int mode, ViewGroup buttonSet, boolean enableSlidingButtons) {
			super(context, c);
			this.mode = mode;
			int n = c.getCount();
			this.buttonSet = buttonSet;
			this.context = context;
			this.enableButtonSetAnimation = enableSlidingButtons;
			checkedItems = new ArrayList<Long>();
			openItems = new ArrayList<Long>();
			res = ((Activity)context).getResources();
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			DbFormWrapper wrapper = (DbFormWrapper) view.getTag();
			wrapper.populateFrom(cursor);
			wrapperLogic(wrapper);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			View view = inflater.inflate(R.layout.forms_manager_list_item, parent, false);
			FormsManager.DbFormWrapper wrapper = new FormsManager.DbFormWrapper(view, this);// = new DbFormWrapper(view, this);
			
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
			return (checkedItems.size() > 0);
		}
		/**
		 * @return ArrayList with the database IDs of the selected list items.
		 */
		public ArrayList<Long> getListOfCheckedIds() {
			return checkedItems;
		}
		/**
		 * Unchecks all items from the list and triggers animations if necessary.
		 */
		public void uncheckAllItems() {
			checkedItems.clear();
			
			if (enableButtonSetAnimation) {
				if ((checkedItems.size() < 1) && (buttonSet.getVisibility() == View.VISIBLE))
					Utilities.toggleSlidingAnimation(buttonSet, false);
			}
			
			this.notifyDataSetChanged();
		}
		
		/**
		 * Closes all items descriptions.
		 */
		public void closeAllItems() {
			openItems.clear();
			this.notifyDataSetChanged();
		}
		
		/**
		 * Checks all selectable items from the list.
		 */
		public void checkAllItems() {
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
			this.notifyDataSetChanged();
		}
		/**
		 * Adds or removes items to the selected items list. It also triggers animations when 
		 * necessary.
		 * @param wrapper {@link DbFormWrapper} of the item to toggle.
		 */
		public void toggleSelected(DbFormWrapper wrapper) {
			switch (mode) {
				case 0:
					toggleSelectedMode0(wrapper);
					break;
				default:
					toggleSelectedMode1(wrapper);
					break;
			}
			if (enableButtonSetAnimation) {
				if ((checkedItems.size() > 0) && (buttonSet.getVisibility() == View.GONE))
					Utilities.toggleSlidingAnimation(buttonSet, true);
				if ((checkedItems.size() < 1) && (buttonSet.getVisibility() == View.VISIBLE))
					Utilities.toggleSlidingAnimation(buttonSet, false);
			}
		}
		
		private void toggleSelectedMode0(DbFormWrapper wrapper) {
			if (wrapper.getState() < DbForm.__FORM_SERVER_STATE_LATEST_VERSION__) {
				if (!checkedItems.contains(wrapper.getId())) {
					checkedItems.add(wrapper.getId());
					wrapper.getRow().setBackgroundColor(res.getColor(R.color.ULLChecked));
					wrapper.getCb().setChecked(true);
					//row.setBackgroundColor(R.color.CheckedRed);
				}
				else {
					checkedItems.remove(wrapper.getId());
					wrapper.getRow().setBackgroundColor(res.getColor(R.color.Transparent));
					wrapper.getCb().setChecked(false);
				}
			}

		}
		
		private void toggleSelectedMode1(DbFormWrapper wrapper) {
			if (!checkedItems.contains(wrapper.getId())) {
				checkedItems.add(wrapper.getId());
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.TransparentRed));
				wrapper.getCb().setChecked(true);
			}
			else {
				checkedItems.remove(wrapper.getId());
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.Transparent));
				wrapper.getCb().setChecked(false);
			}
		}
		
		private void toggleOpen (DbFormWrapper wrapper) {
			if (!openItems.contains(wrapper.getId())) {
				openItems.add(wrapper.getId());
				wrapper.getFormDescription().setOpen(true);
			}
			else {
				openItems.remove(wrapper.getId());
				wrapper.getFormDescription().setOpen(false);
			}
		}
		
		/**
		 * Handles state of the list items. Whether they should be enabled, disabled, background color, 
		 * checkbox checked, state information towards the user, etc.
		 * @param wrapper DbFormWrapper of the list item to handle.
		 */
		public void wrapperLogic (DbFormWrapper wrapper) {
			switch (mode) {
			case 0:
				wrapperLogicMode0(wrapper);
				break;
			default:
				wrapperLogicMode1(wrapper);
				break;
			}
		}
		
		private void wrapperLogicMode0(DbFormWrapper wrapper) {
			/*
			 * Selected / Unselected logic
			 */
			if (checkedItems.contains(wrapper.getId())) {
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.ULLChecked));
				wrapper.getCb().setChecked(true);
			}
			else {
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.Transparent));
				wrapper.getCb().setChecked(false);
			}

			/*
			 * Open / Closed logic
			 */
			if (openItems.contains(wrapper.getId())) {
				wrapper.getFormDescription().setOpen(true);
			}
			else
				wrapper.getFormDescription().setOpen(false);
			
			/*
			 * State logic
			 */
			wrapper.getFormName().setTextColor(Color.WHITE);
			wrapper.getFormVersion().setTextColor(Color.WHITE);
			wrapper.getFormVersionTitle().setTextColor(Color.WHITE);
			wrapper.getFormDescription().getTitle().setTextColor(Color.WHITE);
			wrapper.getFormDescription().getBody().setTextColor(Color.WHITE);
			wrapper.getFormDate().setTextColor(Color.WHITE);
			if (wrapper.getState() > DbForm.__FORM_SERVER_STATE_MORE_RECENT_AVAILABLE__) {
				wrapper.getCb().setEnabled(false);
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.TransparentGray));
				wrapper.getFormName().setTextColor(Color.BLACK);
				wrapper.getFormVersion().setTextColor(Color.BLACK);
				wrapper.getFormVersionTitle().setTextColor(Color.BLACK);
				wrapper.getFormDescription().getTitle().setTextColor(Color.BLACK);
				wrapper.getFormDescription().getBody().setTextColor(Color.BLACK);
				wrapper.getFormDate().setTextColor(Color.BLACK);
			}
			switch (wrapper.getState()) {
				case DbForm.__FORM_SERVER_STATE_NEW__:
					wrapper.getStatus().setText(context.getString(R.string.formsManager_list_itemFormStatusNew));
					wrapper.getStatus().setTextColor(Color.GREEN);
					break;
				case DbForm.__FORM_SERVER_STATE_LATEST_VERSION__:
					wrapper.getStatus().setText(context.getString(R.string.formsManager_list_itemFormStatusCurrent));
					wrapper.getStatus().setTextColor(Color.CYAN);
					break;
				case DbForm.__FORM_SERVER_STATE_MORE_RECENT_AVAILABLE__:
					wrapper.getStatus().setText(context.getString(R.string.formsManager_list_itemFormStatusUpdate));
					wrapper.getStatus().setTextColor(Color.YELLOW);
					break;
				case DbForm.__FORM_SERVER_STATE_NOT_FOUND__:
				default:
					wrapper.getStatus().setText(context.getString(R.string.formsManager_list_itemFormStatusNotFound));
					wrapper.getStatus().setTextColor(Color.RED);
					break;
			}
		}
		
		private void wrapperLogicMode1(DbFormWrapper wrapper) {
			/*
			 * Selected / Unselected logic
			 */
			wrapper.getFormDescription().setVisibility(View.GONE);
			if (checkedItems.contains(wrapper.getId())) {
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.TransparentRed));
				wrapper.getCb().setChecked(true);
			}
			else {
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.Transparent));
				wrapper.getCb().setChecked(false);
			}
			
			wrapper.getStatus().setVisibility(View.GONE);
		}
	}

    public class DbFormWrapper {
    	private DbForm dbf = null;
    	private long id;
    	private int version;
    	private String description;
    	private CheckBox cb;
    	private TextView formName;
    	private TextView formVersion;
    	private TextView status;
    	private TextView formVersionTitle;
    	private TextView formDate;
    	private ExpandableTextView formDescription;
    	private View row = null;
    	private DbFormAdapter adapter = null;
    	private int state;
    	private DbFormWrapper myself;

    	
    	public DbFormWrapper (View view, final DbFormAdapter adapter) {
    		this.adapter = adapter;
    		myself = this;
    		row = view;

    	}
    	
    	public void populateFrom(Cursor c) {
    		
    		//DUE TO PERFORMANCE ISSUES WE CANNOT USE THIS CODE
    		if (dbf == null)
    			dbf = new DbForm();
    		dbf.loadFrom(c);
    		
    		id = c.getLong(c.getColumnIndex(DbForm.__LOCALFORMSDB_ID_KEY__));
    		version = c.getInt(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_VERSION_KEY__));
    		getFormName().setText(c.getString(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_NAME_KEY__)));
    		getFormVersion().setText(c.getInt(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_VERSION_KEY__)) + ".0");
    		state = c.getInt(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_SERVERSTATE_KEY__));
    		getFormDescription().getBody().setText(c.getString(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_DESCRIPTION_KEY__)));
    		DateFormat df = DateFormat.getDateInstance();
    		try {
    			Date form_date = df.parse(c.getString(c.getColumnIndex(DbForm.__LOCALFORMSDB_FORM_DATE_KEY__)));
    			getFormDate().setText(getString(R.string.date) + ": " + df.format(form_date));
    		}
    		catch (Exception e) {
    			getFormDate().setText(getString(R.string.date) + ": " + getString(R.string.notAvailable));
    		}
    		
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
    			formName.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						adapter.toggleSelected(myself);	
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
    	
    	public TextView getFormVersionTitle() {
    		if (formVersionTitle == null)
    			formVersionTitle = (TextView) row.findViewById(R.id.forms_manager_list_itemFormVersionTextView);
    		return formVersionTitle;
    	}
    	
    	public ExpandableTextView getFormDescription() {
    		if (formDescription == null) {
    			formDescription = (ExpandableTextView) row.findViewById(R.id.forms_manager_list_itemFormDescriptionExpTextView);
    			formDescription.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						adapter.toggleOpen(myself);
					}
				});
    		}
    		return formDescription;
    	}
    	
    	public TextView getFormDate() {
    		if (formDate == null)
    			formDate = (TextView) row.findViewById(R.id.forms_manager_list_itemFormDate);
    		return formDate;
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
    		// debugging enabled
    		MenuItem item = menu.findItem(R.id.formsManager_menuShowDebug);
    		item.setVisible(true);
    	}
    	return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
    	MenuItem item = menu.findItem(R.id.formsManager_menuUpdate);
    	if (currentPage == 0) {
    		// left page
    		item.setVisible(false);
    	}
    	else
    		item.setVisible(true);
		return true;
    }
    
    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case (R.id.formsManager_menuUpdate):
    		updateServerList();
			break;
    	case (R.id.formsManager_menuSelectAll):
    		if (currentPage == 1)
    			formsAdapter.checkAllItems();
    		else
    			deleteFormsAdapter.checkAllItems();
    		break;
    	case (R.id.formsManager_menuShowDebug):
    		Utilities.showTitleAndMessageDialog(this, getString(R.string.debugging), serverDebugInfo);
    		break;
    	// More items go here (if any) ...
    	}
    	return false; 
    }

}


