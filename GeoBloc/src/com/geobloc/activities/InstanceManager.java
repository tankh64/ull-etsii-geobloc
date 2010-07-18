/**
 * 
 */
package com.geobloc.activities;

import java.io.File;
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
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.geobloc.R;
import com.geobloc.db.DbFormInstance;
import com.geobloc.db.DbFormInstanceSQLiteHelper;
import com.geobloc.db.DbFormSQLiteHelper;
import com.geobloc.persistance.SDFilePersistance;
import com.geobloc.services.UploadInstancesService;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.shared.Utilities;
import com.geobloc.widget.ProgressItem;

/**
 * New Activity which will allow the user to upload completed instances of forms or erase them.
 * User will also be able to see which instances are pending of being marked as "complete".
 * In other words, the user will know about all the instances he's been working on, completed and not completed.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class InstanceManager extends Activity {

	/*
	 * 
	 * CODE FOR ANIMATIONS (SWIPE AND BOTTOM LAYOUTS)
	 * 
	 */
	private static int __NUMBER_OF_PAGES__ = 2;
	
	private ViewFlipper flipper;
	private int currentPage = 1; // we start with the upload page (to the right)
	
	private Animation bounceRight;
	private Animation bounceLeft;
	
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;	
	
	private boolean enableButtonSetAnimation = true;
	private RelativeLayout eraseButtonSet;
	private RelativeLayout sendButtonSet;
	
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
                }  else if (e2.getX() - e1.getX() > GBSharedPreferences.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > GBSharedPreferences.SWIPE_THRESHOLD_VELOCITY) {
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
                // nothing
            }
            return false;
		}
	}
	
	private void initConfig(Bundle savedInstanceState) {
		//Hide the title bar
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forms_manager);
		
        // set content view, get preferences and resources
		setContentView(R.layout.instance_manager);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		res = this.getResources();
		
		// get views
		this.flipper = (ViewFlipper)findViewById(R.id.instanceManager_viewFlipper);
		this.eraseButtonSet = (RelativeLayout) findViewById(R.id.instanceManager_deleteInstancesFooter);
        this.sendButtonSet = (RelativeLayout) findViewById(R.id.instanceManager_sendInstancesFooter);
        this.eraseList = (ListView) findViewById(R.id.instanceManager_sendInstancesListView);
        this.sendList = (ListView) findViewById(R.id.instanceManager_sendInstancesListView);
		
		gestureDetector = new GestureDetector(new MyGestureDetector());
		
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };     
        
        // set on touch listeners
        eraseList.setOnTouchListener(gestureListener);
		sendList.setOnTouchListener(gestureListener);
		
		serverDebugInfo = "";
		
		// create and add header to internet dependent list
		this.connectivityListItem = new ProgressItem(getBaseContext());
		this.connectivityListItem.toggleBarVisibility();
		this.connectivityListItem.getProgressBar().setVisibility(View.GONE);
		this.connectivityListItem.setText(getString(R.string.ready));
		this.connectivityListItem.setFocusable(false);
		sendList.addHeaderView(this.connectivityListItem);
		sendList.setBackgroundColor(Color.TRANSPARENT);
		
		// connect lists with databases
		/*
		db=(new DbFormInstanceSQLiteHelper(getBaseContext())).getWritableDatabase();
		allInstancesModel = DbFormInstance.getAll(db);
		startManagingCursor(allInstancesModel);
		*/
		formsDb = (new DbFormSQLiteHelper(getBaseContext())).getReadableDatabase();
		instancesDb=(new DbFormInstanceSQLiteHelper(getBaseContext())).getWritableDatabase();
		if (prefs.getBoolean(GBSharedPreferences.__SEND_INCOMPLETE_KEY__, true))
			sendCursor = DbFormInstance.getAll(instancesDb);
		else
			sendCursor = DbFormInstance.getAllCompleted(instancesDb);
		startManagingCursor(sendCursor);
		instancesAdapter = new DbFormInstanceAdapter(sendCursor, sendButtonSet);
		sendList.setAdapter(instancesAdapter);
		
        enableButtonSetAnimation = prefs.getBoolean(GBSharedPreferences.__SLIDING_BUTTONS_ANIMATION_KEY__, 
				GBSharedPreferences.__DEFAULT_SLIDING_BUTTONS_ANIMATION__);
        ;
        if (enableButtonSetAnimation) {
        	eraseButtonSet.setVisibility(View.GONE);
        	sendButtonSet.setVisibility(View.GONE);
        }
        else {
        	eraseButtonSet.setVisibility(View.VISIBLE);
        	sendButtonSet.setVisibility(View.VISIBLE);
        }
		
		// get all animations
		bounceLeft = AnimationUtils.loadAnimation(this, R.anim.bounce_left);
		bounceRight = AnimationUtils.loadAnimation(this, R.anim.bounce_right);
		slideLeftIn  = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
		
		registerForContextMenu(sendList);
		
		// load services
		doBindServices();
		didItOnce = false;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
	else
		return false;
	}
	
	
	/*
	 * 
	 * CODE FOR ACTIVITY LOGIC 
	 * 
	 */
	
	private SharedPreferences prefs;
	private Resources res;
	private ProgressItem connectivityListItem;
	
	private static final String LOG_TAG = "InstanceManager";
	private String serverDebugInfo;
	private Cursor sendCursor = null;
	//private DbFormInstanceAdapter allInstancesAdapter = null;
	private DbFormInstanceAdapter instancesAdapter = null;
	private SQLiteDatabase formsDb = null;
	private SQLiteDatabase instancesDb = null;
	
	private UploadInstancesService uploadService = null;
	private boolean servicesBound = false;
	
	private boolean didItOnce;
	
	private ListView eraseList;
	private ListView sendList;
	
	
	/**
	 * 
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConfig(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		registerReceiver(receiver,
				new IntentFilter(UploadInstancesService.BROADCAST_ACTION));
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && !didItOnce) {
			this.canPerformInternetAction();
			this.connectivityListItem.getText().setText(getString(R.string.ready));
			this.connectivityListItem.getBar().setVisibility(View.GONE);
			didItOnce = true;
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		unregisterReceiver(receiver);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//allInstancesModel.close();
		sendCursor.close();
		formsDb.close();
		instancesDb.close();
		doUnbindServices();
	}
	
	private ServiceConnection onUploadService = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			uploadService = ((UploadInstancesService.LocalBinder)rawBinder).getService();

		}
		
		public void onServiceDisconnected(ComponentName className) {
			uploadService = null;
		}
	};
	
	void doBindServices() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
	    bindService(new Intent(InstanceManager.this, 
	            UploadInstancesService.class), onUploadService, Context.BIND_AUTO_CREATE);
	    servicesBound = true;
	}
	
	void doUnbindServices() {
	    if (servicesBound) {
	        // Detach our existing connection.
	        unbindService(onUploadService);
	        servicesBound = false;
	    }
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean update = intent.getBooleanExtra("update", false);
			boolean success = !intent.getBooleanExtra("result", false);
			if (update) {
				connectivityListItem.setText(getString(R.string.completedItems, intent.getIntExtra("next", 0), intent.getIntExtra("total", 0)));
				connectivityListItem.setPtrogressBarProgress(intent.getIntExtra("next", 0), intent.getIntExtra("total", 0));
			}
			else {
				connectivityListItem.getBar().setVisibility(View.GONE);
				connectivityListItem.getProgressBar().setVisibility(View.GONE);
				
				serverDebugInfo = intent.getStringExtra("serverResponse");
				instancesAdapter.uncheckAllItems();
				sendCursor.requery();
				instancesAdapter.notifyDataSetChanged();
				/*
				deleteFormsCursor.requery();
				deleteFormsAdapter.notifyDataSetChanged();
				*/
				if (!success) {
					connectivityListItem.setText(getString(R.string.formsManager_errosEncounteredDuringDownload));
					connectivityListItem.setBackgroundColor(Color.RED);				
				}
				else {
					connectivityListItem.setText(getString(R.string.ready));
					connectivityListItem.setBackgroundColor(res.getColor(R.color.TransparentGreen));
				}
				Utilities.showTitleAndMessageDialog(context, getString(R.string.report), intent.getStringExtra("report"));
			}
		}
		
	};
	
	private boolean canPerformInternetAction() {
		this.connectivityListItem.setBackgroundColor(Color.TRANSPARENT);
		/*
		if (connectivityBitmap == null) {
			connectivityBitmap = Utilities.scaleDownToContainer(getResources(), R.drawable.connectivity, downloadView);
		}
    	
		if (noConnectivityBitmap == null) {
			noConnectivityBitmap = Utilities.scaleDownToContainer(getResources(), R.drawable.no_connectivity, downloadView);
			
		}
		*/
		
		/*
		 * NEED TO IMPROVE THIS BY KEEPING TRACK OF CONNECTIVITY
		 */
		if (Utilities.evaluateConnectivityAvailable(getBaseContext())) {
			this.connectivityListItem.getBar().setVisibility(View.VISIBLE);
			this.connectivityListItem.setText(getString(R.string.working));
			/*
			bmp = new BitmapDrawable(res, connectivityBitmap);
			bmp.setGravity(Gravity.CENTER);
			downloadView.setBackgroundDrawable(bmp);
			*/
			return true;
			
		}
		else {
			connectivityListItem.getBar().setVisibility(View.GONE);
			connectivityListItem.setText(getString(R.string.noConnectivity));
			connectivityListItem.setBackgroundColor(Color.RED);
			/*
			bmp = new BitmapDrawable(res, noConnectivityBitmap);
			bmp.setGravity(Gravity.CENTER);
			downloadView.setBackgroundDrawable(bmp);
			*/
			return false;
		}
	}
	
	/*
	 * Method for all Button clicks in the Activity
	 * 
	 */
	public void allInstancesButtonOnClick(View target) {
		switch(target.getId()) {
			case (R.id.instanceManager_deleteInstancesButton) :
				/*
				boolean ok = false;
				AlertDialog.Builder alert = new AlertDialog.Builder(this);  
				alert.setTitle("¿Está seguro?");  
				alert.setMessage("Borrará " + allInstancesCheckedItems.size() + " paquete(s)");
				alert.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//ok = true;
						performDelete();
					}
				});
				alert.setNegativeButton("No", null);
				alert.show();
				*/
				break;
			case (R.id.instanceManager_sendInstancesButton) :
				sendInstances();
				break;
			
			case (R.id.instanceManager_markIncompleteButton):
				/*
				performMarkIncomplete();
				*/
				break;
			default:
				break;
		}
		
	}
	
	
	/*
	 * DATABASE RELATED
	 * 
	 */
	/*
	private void updateAllInstances() {
		allInstancesModel.requery();
		// erase list of checked items
		allInstancesCheckedItems.clear();
		allInstancesCheckedItems = new ArrayList<Long>(allInstancesModel.getCount());
		if (enableButtonSetAnimation)
			allInstancesButtonSet.setVisibility(View.GONE);
	}
	
	private void updateCompletedInstances() {
		completedInstancesModel.requery();
		// erase list of checked items
		completedInstancesCheckedItems.clear();
		completedInstancesCheckedItems = new ArrayList<Long> (completedInstancesModel.getCount());
		if (enableButtonSetAnimation)
			completedInstancesButtonSet.setVisibility(View.GONE);
	}
	*/
	/*
	private void performDelete() {
		if (allInstancesCheckedItems.size() > 0) {
			pd = ProgressDialog.show(this, "Trabajando", "Realizando la operación...");
			pd.setIndeterminate(false);
			pd.setCancelable(false);
			
			DeletePackagesTask deleteTask = new DeletePackagesTask();
			deleteTask.setContext(this);
			deleteTask.setListener(this);
			deleteTask.setSQLiteDatabase(db);
			Long[] taskArray = new Long[allInstancesCheckedItems.size()];
			int i = 0;
			for (Long id : allInstancesCheckedItems) {
				taskArray[i] = id;
				i++;
			}
			deleteTask.execute(taskArray);
			//
			// REQUERY CANNOT BE PERFORMED HERE SINCE WE MUST WAIT FOR THE DELETETASK TO FINISH
			//
		}
		else
			Utilities.showToast(this, "No se puede realizar la operación. No hay elementos seleccionados.", Toast.LENGTH_LONG);
	}
	*/
	/*
	private void performSend() {
		if (completedInstancesCheckedItems.size() > 0) {
			DbFormInstance dbi;
			//Long[] taskArray = new Long[completedInstancesCheckedItems.size()];
			//int i = 0;
			for (Long id : completedInstancesCheckedItems) {
				//taskArray[i] = id;
				//i++;
				dbi = DbFormInstance.loadFrom(db, id);
				GeoBlocPackageManager pm = new GeoBlocPackageManager();
				pm.openPackage(dbi.getPackage_path());
				UploadPackageHelper.preparePackage(dbi, db, pm);
			}
			
			
		}
		else 
			Utilities.showToast(this, "No se puede realizar la operación. No hay elementos seleccionados.", Toast.LENGTH_LONG);
	}
	*/
	/*
	private void performMarkIncomplete() {
		if (completedInstancesCheckedItems.size() > 0) {
			pd = ProgressDialog.show(this, "Trabajando", "Realizando la operación...");
			pd.setIndeterminate(false);
			pd.setCancelable(false);
			DbFormInstance dbi;
			for (Long id : completedInstancesCheckedItems) {
				dbi = DbFormInstance.loadFrom(db, id);
				dbi.setCompleted(false);
				dbi.save(db);
			}
			updateAllInstances();
			updateCompletedInstances();
			pd.dismiss();
		}
		else 
			Utilities.showToast(this, "No se puede realizar la operación. No hay elementos seleccionados.", Toast.LENGTH_LONG);

	}
	*/
	/*
	class DbFormInstanceAdapter extends CursorAdapter {
		
		DbFormInstanceAdapter (Cursor c) {
			super(InstanceManager.this, c);
			int n = c.getCount();
			allInstancesCheckedItems = new ArrayList<Long>(n);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			DbFormInstanceWrapper wrapper = (DbFormInstanceWrapper) view.getTag();
			wrapper.populateFrom(cursor);
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.instance_manager_list_item, parent, false);
			DbFormInstanceWrapper wrapper = new DbFormInstanceWrapper(view);
			
			view.setTag(wrapper);
			wrapper.populateFrom(cursor);
	
			return view;
		}
	}
	*/
	
	private void sendInstances() {
		if (canPerformInternetAction() && this.instancesAdapter.areThereElementsSelected()) {
			serverDebugInfo = "";
			//Long[] taskArray = new Long[completedInstancesCheckedItems.size()];
			//int i = 0;
			this.connectivityListItem.getProgressBar().setVisibility(View.VISIBLE);
			Long[] array = new Long[instancesAdapter.getListOfCheckedIds().size()];
			for (int i = 0; i < array.length; i++)
				array[i] = instancesAdapter.getListOfCheckedIds().get(i);
			uploadService.uploadInstances(array);
		}
	}
	
	class DbFormInstanceAdapter extends CursorAdapter {
		
		private ArrayList<Long> checkedItems;
		private ViewGroup buttonSet;
		
		DbFormInstanceAdapter (Cursor c, ViewGroup buttonSet) {
			super(InstanceManager.this, c);
			this.buttonSet = buttonSet;
			checkedItems = new ArrayList<Long>();
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			DbFormInstanceWrapper wrapper = (DbFormInstanceWrapper) view.getTag();
			//wrapper.setMode(1);
			wrapper.populateFrom(cursor);
			wrapperLogic(wrapper);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.instance_manager_list_item, parent, false);
			DbFormInstanceWrapper wrapper = new DbFormInstanceWrapper(view, this);
			view.setTag(wrapper);
			wrapper.populateFrom(cursor);
			wrapperLogic(wrapper);
			return view;
		}
		
		public boolean areThereElementsSelected() {
			return (checkedItems.size() > 0);
		}
		
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
		 * Checks all selectable items from the list.
		 */
		public void checkAllItems() {
			DbFormInstanceWrapper wrapper;
			View v = null;
			// let's be good; we first load the View
			if (this.getCount() > 0) {
				v = getView(0, null, null);
				wrapper = (DbFormInstanceWrapper) v.getTag();
				if (!wrapper.getCb().isChecked())
					this.toggleSelected(wrapper);
			}
			// and then we reuse it
			for (int i = 1; i < this.getCount(); i++) {
				wrapper = (DbFormInstanceWrapper) this.getView(i, v, null).getTag();
				if (!wrapper.getCb().isChecked())
					this.toggleSelected(wrapper);
			}
			this.notifyDataSetChanged();
		}
		
		private void toggleSelected(DbFormInstanceWrapper wrapper) {
			if (!checkedItems.contains(wrapper.getId())) {
				checkedItems.add(wrapper.getId());
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.TransparentGreen));
				wrapper.getCb().setChecked(true);
			}
			else {
				checkedItems.remove(wrapper.getId());
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.Transparent));
				wrapper.getCb().setChecked(false);
			}
			if (enableButtonSetAnimation) {
				if ((checkedItems.size() > 0) && (buttonSet.getVisibility() == View.GONE))
					Utilities.toggleSlidingAnimation(buttonSet, true);
				if ((checkedItems.size() < 1) && (buttonSet.getVisibility() == View.VISIBLE))
					Utilities.toggleSlidingAnimation(buttonSet, false);
			}
		}
		
		private void wrapperLogic(DbFormInstanceWrapper wrapper) {
			if (checkedItems.contains(wrapper.getId())) {
				wrapper.getCb().setChecked(true);
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.TransparentGreen));
			}
			else {
				wrapper.getCb().setChecked(false);
				wrapper.getRow().setBackgroundColor(R.color.Transparent);
			}
			
			if (enableButtonSetAnimation) {
				if ((checkedItems.size() > 0) && (eraseButtonSet.getVisibility() == View.GONE))
					Utilities.toggleSlidingAnimation(buttonSet, true);
				if ((checkedItems.size() < 1) && (eraseButtonSet.getVisibility() == View.VISIBLE))
					Utilities.toggleSlidingAnimation(buttonSet, false);
			}
			
			/*
			 * State logic
			 */
			if (!wrapper.isComplete())
				wrapper.getCompleted().setText(R.string.instanceManager_list_itemFormInstanceCompleteNo);
			else
				wrapper.getCompleted().setText(R.string.instanceManager_list_itemFormInstanceCompleteYes);

		}
	}
		
	class DbFormInstanceWrapper {
		private DbFormInstanceWrapper myself = null;
		private DbFormInstanceAdapter adapter = null;
		private long id;
		private int position;
		private String packagePath;
		private boolean complete;
		private CheckBox cb = null;
		private TextView name = null;
		private TextView createdDate = null;
		private TextView completedText = null;
		private TextView completed = null;
		private View row = null;
		
		DbFormInstanceWrapper (final View view, DbFormInstanceAdapter adapter) {
			row = view;
			row.setLongClickable(true);
			myself = this;
			this.adapter = adapter;
		}
		
		void populateFrom(Cursor c) {
			/*
			 * DUE TO PERFORMANCE ISSUES WE CANNOT USE THIS CODE
			if (dbi == null)
				dbi = new DbFormInstance();
			dbi.loadFrom(c);
			*/
			position = c.getPosition();
			id = c.getLong(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_ID__));
			getName().setText(c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_LABEL_KEY__)));
			packagePath = c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_PATH_KEY__));
			int completedAux = c.getInt(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_COMPLETED_KEY__));
			complete = (completedAux == 1);
			/*
			 * LIST ITEM DISPLAY LOGIC
			 */
			getCompletedText().setVisibility(View.VISIBLE);
			getCreatedDate().setText(getString(R.string.instanceManager_list_itemFormInstanceCreatedDateText) + " " + c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_CREATEDDATE_KEY__)));
			
			
		}

		public String getInstance_package_path() {
			return packagePath;
		}
		
		public boolean isComplete() {
			return complete;
		}
		
		public long getId() {
			return id;
		}
		
		public View getRow() {
			return row;
		}
		
		public CheckBox getCb() {
			if (cb == null) {
				cb = (CheckBox) row.findViewById(R.id.instance_manager_list_itemCb);
				cb.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						adapter.toggleSelected(myself);
					}
				});
			}
			return cb;
		}
		
		public TextView getName() {
			if (name == null)  {
				name = (TextView) row.findViewById(R.id.instance_manager_list_itemFormInstanceNameTextView);
			}
			return name;
		}

		public TextView getCreatedDate() {
			if (createdDate == null)
				createdDate = (TextView) row.findViewById(R.id.instance_manager_list_itemFormInstanceCreated);
			return createdDate;
		}

		public TextView getCompleted() {
			if (completed == null)
				completed = (TextView) row.findViewById(R.id.instance_managerlist_itemFormInstanceCompletedInfoTextView);
			return completed;
		}

		public TextView getCompletedText() {
			if (completedText == null)
				completedText = (TextView) row.findViewById(R.id.instance_manager_list_itemFormInstanceCompletedTextView);
			return completedText;
		}
		
		
	}
	
	/*
	 * MENUS & CONTEXT MENUS
	 */
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.instance_manager_menu, menu);
    	
    	if (prefs.getBoolean(GBSharedPreferences.__ENABLE_DEBUGGING_FEATURES_KEY__, false)) {
    		// debugging enabled
    		MenuItem item = menu.findItem(R.id.instanceManager_menuShowDebug);
    		item.setVisible(true);
    	}
    	return true;
    }
    
    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	
    	case (R.id.instanceManager_menuSelectAll):
    		if (currentPage == 1)
    			instancesAdapter.checkAllItems();
    		/*
    		else
    			deleteFormsAdapter.checkAllItems();
    		*/
    		break;
    	case (R.id.instanceManager_menuShowDebug):
    		Utilities.showTitleAndMessageDialog(this, getString(R.string.debugging), serverDebugInfo);
    		break;
    	// More items go here (if any) ...
    	}
    	return false; 
    }
	
	/**
	 * If the instance has been marked as complete, then we mark it as incomplete.
	 */
    private static final int MENU_ONE_ID = Menu.FIRST+1;
	/**
	 * Debug only option to show instance.xml file
	 */
	private static final int MENU_TWO_ID = Menu.FIRST+2;
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) { 
		AdapterView.AdapterContextMenuInfo myMenuInfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
		DbFormInstanceWrapper wrapper = (DbFormInstanceWrapper)myMenuInfo.targetView.getTag();
		menu.setHeaderTitle(wrapper.getName().getText().toString());
		populateMenu(menu, wrapper);
	}
	
	private void populateMenu(ContextMenu menu, DbFormInstanceWrapper wrapper) {
		
		
		//menu.add(Menu.NONE, MENU_ONE_ID, Menu.NONE, getString(R.string.instanceManager_completeInstancesMarkIncomplete));
		
		if (wrapper.isComplete()) {
			menu.add(Menu.NONE, MENU_ONE_ID, Menu.NONE, getString(R.string.instanceManager_markIncomplete));
		}
		
		if (prefs.getBoolean(GBSharedPreferences.__ENABLE_DEBUGGING_FEATURES_KEY__, false)) {
			menu.add(Menu.NONE, MENU_TWO_ID, Menu.NONE, getString(R.string.debugging));
		}
	}
	 
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return applyMenuChoice(item) || super.onContextItemSelected(item);
	}
	
	private boolean applyMenuChoice(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		DbFormInstanceWrapper wrapper = (DbFormInstanceWrapper)menuInfo.targetView.getTag();
		DbFormInstance dbi = DbFormInstance.loadFrom(instancesDb, formsDb, wrapper.getId());
		switch (item.getItemId()) {
			case MENU_ONE_ID:
				dbi.setComplete(false);
				dbi.save(instancesDb);
				this.sendCursor.requery();
				instancesAdapter.notifyDataSetChanged();
				return true;
			case MENU_TWO_ID:
				File f = new File(dbi.getPackage_path()+"/instance.xml");
				if (f.canRead()) {
					Utilities.showTitleAndMessageDialog(this, getString(R.string.debugging), SDFilePersistance.getContents(f));
				}
				else
					Utilities.showTitleAndMessageDialog(this, getString(R.string.debugging), getString(R.string.notAvailable));
			default:
				return false;
		}
	}
	
}
