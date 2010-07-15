/**
 * 
 */
package com.geobloc.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.geobloc.R;
import com.geobloc.db.DbForm;
import com.geobloc.db.DbFormInstance;
import com.geobloc.db.DbFormInstanceSQLiteHelper;
import com.geobloc.form.FormClass;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.shared.IFormDefinition;
import com.geobloc.shared.IInstanceDefinition;
import com.geobloc.shared.IJavaToDatabaseForm;
import com.geobloc.shared.IJavaToDatabaseInstance;
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
		this.eraseButtonSet = (RelativeLayout) findViewById(R.id.allInstancesBottomButtons);
        this.sendButtonSet = (RelativeLayout) findViewById(R.id.completeInstancesBottomButtons);
        this.eraseList = (ListView) findViewById(R.id.allInstancesListView);
        this.sendList = (ListView) findViewById(R.id.completeInstancesListView);
		
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
		
		db2=(new DbFormInstanceSQLiteHelper(getBaseContext())).getWritableDatabase();
		if (prefs.getBoolean(GBSharedPreferences.__SEND_INCOMPLETE_KEY__, true))
			sendInstancesModel = DbFormInstance.getAll(db2);
		else
			sendInstancesModel = DbFormInstance.getAllCompleted(db2);
		startManagingCursor(sendInstancesModel);
		instancesAdapter = new DbFormInstanceAdapter2(sendInstancesModel, sendButtonSet);
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
		
		// make other changes upon options
		packagesPath =  prefs.getString(GBSharedPreferences.__PACKAGES_PATH_KEY__, 
			GBSharedPreferences.__DEFAULT_PACKAGES_PATH__);
		
		registerForContextMenu(sendList);
		
		// load services

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
	private String packagesPath;
	private ProgressItem connectivityListItem;
	
	private static final String LOG_TAG = "InstanceManager";
	private Cursor allInstancesModel = null;
	private Cursor sendInstancesModel = null;
	//private DbFormInstanceAdapter allInstancesAdapter = null;
	private DbFormInstanceAdapter2 instancesAdapter = null;
	private SQLiteDatabase db = null;
	private SQLiteDatabase db2 = null;
	
	private ListView eraseList;
	private ListView sendList;
	
	private ProgressDialog pd;
	
	/**
	 * 
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConfig(savedInstanceState);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//allInstancesModel.close();
		sendInstancesModel.close();
		//db.close();
		db2.close();
	}

	/*
	 * Method for all Button clicks in the Activity
	 * 
	 */
	public void allInstancesButtonOnClick(View target) {
		/*
		switch(target.getId()) {
			case (R.id.allInstancesEraseButton) :
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
				
				break;
			case (R.id.completeInstancesSendButton) :
				performSend();
				break;
			
			case (R.id.completeInstancesMarkIncompleteButton):
				performMarkIncomplete();
				break;
			default:
				break;
		}
		*/
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
	
	class DbFormInstanceAdapter2 extends CursorAdapter {
		
		private ArrayList<Long> checkedItems;
		private ViewGroup buttonSet;
		
		DbFormInstanceAdapter2 (Cursor c, ViewGroup buttonSet) {
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
		
		private void toggleSelected(DbFormInstanceWrapper wrapper) {
			if (!checkedItems.contains(wrapper.getId())) {
				checkedItems.add(wrapper.getId());
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.ULLGradientStart));
				wrapper.getCb().setChecked(true);
			}
			else {
				checkedItems.remove(wrapper.getId());
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.Transparent));
				wrapper.getCb().setChecked(false);
			}
		}
		
		private void wrapperLogic(DbFormInstanceWrapper wrapper) {
			if (checkedItems.contains(wrapper.getId())) {
				wrapper.getCb().setChecked(true);
				wrapper.getRow().setBackgroundColor(res.getColor(R.color.ULLGradientEnd));
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
		}
	}
		
	class DbFormInstanceWrapper {
		private DbFormInstanceWrapper myself = null;
		private DbFormInstanceAdapter2 adapter = null;
		private long id;
		private int position;
		private boolean complete;
		private CheckBox cb = null;
		private TextView name = null;
		private TextView createdDate = null;
		private TextView completedText = null;
		private TextView completed = null;
		private View row = null;
		
		// MODE 0: ERASE MODE
		// MODE 1: SEND MODE
		private int mode;
		
		DbFormInstanceWrapper (View view, DbFormInstanceAdapter2 adapter) {
			row = view;
			row.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					return false;
				}
			});
			myself = this;
			this.adapter = adapter;
			setMode(0);
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
			int col = c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_LABEL_KEY__);
			String text = c.getString(col);
			getName().setText(text);
			//getName().setText(dbi.getName());
			int completedAux = c.getInt(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_COMPLETED_KEY__));
			complete = (completedAux == 1);
			/*
			 * LIST ITEM DISPLAY LOGIC
			 */
			getCompletedText().setVisibility(View.VISIBLE);
			getCreatedDate().setText(getString(R.string.instanceManager_list_itemFormInstanceCreatedDateText) + " " + c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_CREATEDDATE_KEY__)));
			if (!isComplete())
				getCompleted().setText(R.string.instanceManager_list_itemFormInstanceCompleteNo);
			else
				getCompleted().setText(R.string.instanceManager_list_itemFormInstanceCompleteYes);

			
		}

		public int getMode() {
			return mode;
		}
		
		public void setMode(int mode) {
			this.mode = mode;
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
			if (name == null)
				name = (TextView) row.findViewById(R.id.instance_manager_list_itemFormInstanceNameTextView);
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
	
	private static final int MENU_ONE_ID = Menu.FIRST+1;
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) { 
		menu.setHeaderTitle("Sample Context Menu");
		menu.add(200, 200, 200, "item1"); 
		populateMenu(menu);
	}
	
	private void populateMenu(ContextMenu menu) {
		menu.setHeaderTitle("TITLE");
		menu.add("HELLO");
		menu.add(Menu.NONE, MENU_ONE_ID, Menu.NONE, getString(R.string.instanceManager_completeInstancesMarkIncomplete));
	}
	 
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return applyMenuChoice(item) || super.onContextItemSelected(item);
	}
	
	private boolean applyMenuChoice(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		switch (item.getItemId()) {
			case MENU_ONE_ID:
				Utilities.showToast(this, "Id: " + menuInfo.id, Toast.LENGTH_SHORT);
				return true;
			default:
				return false;
		}
	}
}
