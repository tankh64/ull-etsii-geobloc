/**
 * 
 */
package com.geobloc.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnCreateContextMenuListener;
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
import com.geobloc.db.DbFormInstance;
import com.geobloc.db.DbFormInstanceSQLiteHelper;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.shared.UploadPackageHelper;
import com.geobloc.shared.Utilities;
import com.geobloc.tasks.DeletePackagesTask;

/**
 * New Activity which will allow the user to upload completed instances of forms or erase them.
 * User will also be able to see which instances are pending of being marked as "complete".
 * In other words, the user will know about all the instances he's been working on, completed and not completed.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class InstanceManager extends Activity implements IStandardTaskListener {

	/*
	 * 
	 * CODE FOR ANIMATIONS (SWIPE AND BOTTOM LAYOUTS)
	 * 
	 */
	
	private ViewFlipper flipper;
	
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;	
	
	private boolean enableButtonSetAnimation = true;
	private RelativeLayout allInstancesButtonSet;
	private RelativeLayout completedInstancesButtonSet;
	
	class MyGestureDetector extends SimpleOnGestureListener {
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
                if (Math.abs(e1.getY() - e2.getY()) > GBSharedPreferences.SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > GBSharedPreferences.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > GBSharedPreferences.SWIPE_THRESHOLD_VELOCITY) {
                	flipper.setInAnimation(slideLeftIn);
                    flipper.setOutAnimation(slideLeftOut);
                	flipper.showNext();
                }  else if (e2.getX() - e1.getX() > GBSharedPreferences.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > GBSharedPreferences.SWIPE_THRESHOLD_VELOCITY) {
                	flipper.setInAnimation(slideRightIn);
                    flipper.setOutAnimation(slideRightOut);
                	flipper.showPrevious();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;

		}
	}
	
	private void toggleButtonSetAnimation(final int mode) {
		Animation anim;
		boolean slideIn = false;
		if ((mode == 0) && (allInstancesButtonSet.getVisibility() == View.GONE) || ((mode == 1) && completedInstancesButtonSet.getVisibility() == View.GONE))
			slideIn = true;
		if (slideIn) {
			//
			anim = new TranslateAnimation(0.0f, 0.0f, allInstancesButtonSet.getLayoutParams().height, 0.0f);
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
						allInstancesButtonSet.setVisibility(View.VISIBLE);
					else
						completedInstancesButtonSet.setVisibility(View.VISIBLE);
				}
			});
		}
		else {
			anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, allInstancesButtonSet.getLayoutParams().height);
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
						allInstancesButtonSet.setVisibility(View.GONE);
					else
						completedInstancesButtonSet.setVisibility(View.GONE);
				}
			});
		}
		if (mode == 0)
			allInstancesButtonSet.startAnimation(anim);
		else
			completedInstancesButtonSet.startAnimation(anim);
	}
	
	private void initConfig(Bundle savedInstanceState) {
		setContentView(R.layout.instance_manager);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.flipper = (ViewFlipper)findViewById(R.id.myInstancesViewFlipper);
		
		slideLeftIn  = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
		
		gestureDetector = new GestureDetector(new MyGestureDetector());
		
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };     
        
        enableButtonSetAnimation = prefs.getBoolean(GBSharedPreferences.__SLIDING_BUTTONS_ANIMATION_KEY__, 
				GBSharedPreferences.__DEFAULT_SLIDING_BUTTONS_ANIMATION__);
        allInstancesButtonSet = (RelativeLayout) findViewById(R.id.allInstancesBottomButtons);
        completedInstancesButtonSet = (RelativeLayout) findViewById(R.id.completeInstancesBottomButtons);
        if (enableButtonSetAnimation) {
        	allInstancesButtonSet.setVisibility(View.GONE);
        	completedInstancesButtonSet.setVisibility(View.GONE);
        }
        else {
        	allInstancesButtonSet.setVisibility(View.VISIBLE);
        	completedInstancesButtonSet.setVisibility(View.VISIBLE);
        }

		allInstances = (ListView) findViewById(R.id.allInstancesListView);
		allInstances.setOnTouchListener(gestureListener);
		completedInstances = (ListView) findViewById(R.id.completeInstancesListView);
		completedInstances.setOnTouchListener(gestureListener);
		
		
		db=(new DbFormInstanceSQLiteHelper(getBaseContext())).getWritableDatabase();
		allInstancesModel = DbFormInstance.getAll(db);
		startManagingCursor(allInstancesModel);
		allInstancesAdapter = new DbFormInstanceAdapter(allInstancesModel);
		allInstances.setAdapter(allInstancesAdapter);
		
		db2=(new DbFormInstanceSQLiteHelper(getBaseContext())).getWritableDatabase();
		completedInstancesModel = DbFormInstance.getAllCompleted(db2);
		startManagingCursor(completedInstancesModel);
		completedInstancesAdapter = new DbFormInstanceAdapter2(completedInstancesModel);
		completedInstances.setAdapter(completedInstancesAdapter);
		
		
		packagesPath =  prefs.getString(GBSharedPreferences.__PACKAGES_PATH_KEY__, 
			GBSharedPreferences.__DEFAULT_PACKAGES_PATH__);
		
		registerForContextMenu(completedInstances);
		/*
		completedInstances.setFocusableInTouchMode(true);
		*/
		/*
		completedInstances.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				//ContextMenu menu;
				Utilities.showToast(getBaseContext(), "Long click!", Toast.LENGTH_SHORT);
				completedInstances.showContextMenu();
				return false;
			}
		});
		
		completedInstances.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            	
            	menu.setHeaderTitle("aaaaaaaaa");
            	populateMenu(menu);
            	//menuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
                //long id = completedInstances.getAdapter().getItemId(menuInfo.);

            }
          });
		*/
		// recover from screen rotation
		if (savedInstanceState != null) {
			long[] longArray;
			// recover allInstancesCheckedItems
			longArray = savedInstanceState.getLongArray("allInstancesCheckedItems");
			for (int i = 0; i < longArray.length; i++)
				allInstancesCheckedItems.add(longArray[i]);
			
			// recover completedInstancesCheckedItems
			longArray = savedInstanceState.getLongArray("completedInstancesCheckedItems");
			for (int i = 0; i < longArray.length; i++)
				completedInstancesCheckedItems.add(longArray[i]);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
	else
		return false;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState); 
		long[] longArray;
		// save allInstancesCheckedItems
		longArray = new long[allInstancesCheckedItems.size()];
		for (int i = 0; i < allInstancesCheckedItems.size(); i++)
			longArray[i] = allInstancesCheckedItems.get(i);
		savedInstanceState.putLongArray("allInstancesCheckedItems", longArray);
		// save completedInstancesCheckedItems
		longArray = new long[completedInstancesCheckedItems.size()];
		for (int i = 0; i < completedInstancesCheckedItems.size(); i++)
			longArray[i] = completedInstancesCheckedItems.get(i);
		savedInstanceState.putLongArray("completedInstancesCheckedItems", longArray);
	}
	
	/*
	 * 
	 * CODE FOR ACTIVITY LOGIC 
	 * 
	 */
	
	private SharedPreferences prefs;
	private String packagesPath;
	
	private static final String LOG_TAG = "InstanceManager";
	private Cursor allInstancesModel = null;
	private Cursor completedInstancesModel = null;
	private DbFormInstanceAdapter allInstancesAdapter = null;
	private DbFormInstanceAdapter2 completedInstancesAdapter = null;
	private SQLiteDatabase db = null;
	private SQLiteDatabase db2 = null;
	
	private ListView allInstances;
	private static ArrayList<Long> allInstancesCheckedItems = null;
	private ListView completedInstances;
	private List<Long> completedInstancesCheckedItems = null;
	
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
		allInstancesModel.close();
		completedInstancesModel.close();
		db.close();
		db2.close();
		super.onDestroy();
	}

	/*
	 * Method for all Button clicks in the Activity
	 * 
	 */
	public void allInstancesButtonOnClick(View target) {
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
	}
	
	
	/*
	 * DATABASE RELATED
	 * 
	 */
	
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
			/*
			 * REQUERY CANNOT BE PERFORMED HERE SINCE WE MUST WAIT FOR THE DELETETASK TO FINISH
			 */
		}
		else
			Utilities.showToast(this, "No se puede realizar la operación. No hay elementos seleccionados.", Toast.LENGTH_LONG);
	}
	
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
				pm.openPackage(dbi.getPackageLocation());
				UploadPackageHelper.preparePackage(dbi, db, pm);
			}
			
			
		}
		else 
			Utilities.showToast(this, "No se puede realizar la operación. No hay elementos seleccionados.", Toast.LENGTH_LONG);
	}
	
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
	
	class DbFormInstanceAdapter2 extends CursorAdapter {
		
		DbFormInstanceAdapter2 (Cursor c) {
			super(InstanceManager.this, c);
			int n = c.getCount();
			completedInstancesCheckedItems = new ArrayList<Long>(n);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			DbFormInstanceWrapper wrapper = (DbFormInstanceWrapper) view.getTag();
			//wrapper.setMode(1);
			wrapper.populateFrom(cursor);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.instance_manager_list_item, parent, false);
			DbFormInstanceWrapper wrapper = new DbFormInstanceWrapper(view);
			wrapper.setMode(1);
			view.setTag(wrapper);
			wrapper.populateFrom(cursor);
	
			return view;
		}
		
		/*
		public long getItemId(int position) {
			DbFormInstanceWrapper wrapper = (DbFormInstanceWrapper) this.getItem(position);
			return wrapper.getId();
	    }
		*/
		
		/*
		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}
		*/
	}
		
	class DbFormInstanceWrapper {
		private DbFormInstance dbi = null;
		private long id;
		private int position;
		private CheckBox cb = null;
		private TextView name = null;
		private TextView createdDate = null;
		private TextView completedText = null;
		private TextView completed = null;
		private View row = null;
		
		// MODE 0: ERASE MODE
		// MODE 1: SEND MODE
		private int mode;
		
		DbFormInstanceWrapper (View view) {
			row = view;
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
			int col = c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_NAME_KEY__);
			String text = c.getString(col);
			getName().setText(text);
			//getName().setText(dbi.getName());
			int completedAux = c.getInt(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_COMPLETED_KEY__));
			
			/*
			 * LIST ITEM DISPLAY LOGIC
			 */
			if (getMode() == 0) {
				getCompletedText().setVisibility(View.VISIBLE);
				getCreatedDate().setText(getString(R.string.instance_manager_list_itemFormInstanceCreatedDateText) + " " + c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_CREATEDDATE_KEY__)));
				if (completedAux == 0)
					getCompleted().setText(R.string.instance_manager_list_itemFormInstanceCompleteNo);
				else
					getCompleted().setText(R.string.instance_manager_list_itemFormInstanceCompleteYes);
			}
			else {
				// all packages to be sent are completed, no need to tell
				getCompleted().setText("");
				getCompletedText().setVisibility(View.INVISIBLE);
				// in mode 1, we're guaranteed to have a completed date
				getCreatedDate().setText(getString(R.string.instance_manager_list_itemFormInstanceCompletedTextViewText) + " " + c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_COMPLETEDDATE_KEY__)));
			}
			
			
			/*
			 * List LOGIC
			 */
			if (getMode() == 0) {
				// all instances mode
				if (allInstancesCheckedItems.contains(id)) {
					getCb().setChecked(true);
					row.setBackgroundColor(Color.rgb(165, 42, 42));
				}
				else {
					getCb().setChecked(false);
					row.setBackgroundColor(R.color.Transparent);
				}
			}
			if (getMode() == 1) {
				// completed instances mode
				if (completedInstancesCheckedItems.contains(id)) {
					getCb().setChecked(true);
					row.setBackgroundColor(Color.rgb(0,160,0));
				}
				else {
					getCb().setChecked(false);
					row.setBackgroundColor(R.color.Transparent);
				}
			}
		}

		public int getMode() {
			return mode;
		}
		
		public void setMode(int mode) {
			this.mode = mode;
		}
		
		public long getId() {
			return id;
		}
		
		public CheckBox getCb() {
			if (cb == null) {
				cb = (CheckBox) row.findViewById(R.id.instance_manager_list_itemCb);
				cb.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (getMode() == 0) {
							// all instances mode
							if (!allInstancesCheckedItems.contains(id)) {
								allInstancesCheckedItems.add(id);
								row.setBackgroundColor(Color.rgb(165, 42, 42));
								//row.setBackgroundColor(R.color.CheckedRed);
							}
							else {
								allInstancesCheckedItems.remove(id);
								row.setBackgroundColor(R.color.Transparent);
							}
							if (enableButtonSetAnimation) {
								if ((allInstancesCheckedItems.size() > 0) && (allInstancesButtonSet.getVisibility() == View.GONE))
									toggleButtonSetAnimation(0);
								if ((allInstancesCheckedItems.size() < 1) && (allInstancesButtonSet.getVisibility() == View.VISIBLE))
									toggleButtonSetAnimation(0);
							}
						}
						if (getMode() == 1) {
							// completed instances mode
							if (!completedInstancesCheckedItems.contains(id)) {
								completedInstancesCheckedItems.add(id);
								row.setBackgroundColor(Color.rgb(0,160,0));
								//row.setBackgroundColor(R.color.CheckedRed);
							}
							else {
								completedInstancesCheckedItems.remove(id);
								row.setBackgroundColor(R.color.Transparent);
							}
							if (enableButtonSetAnimation) {
								if ((completedInstancesCheckedItems.size() > 0) && (completedInstancesButtonSet.getVisibility() == View.GONE))
									toggleButtonSetAnimation(1);
								if ((completedInstancesCheckedItems.size() < 1) && (completedInstancesButtonSet.getVisibility() == View.VISIBLE))
									toggleButtonSetAnimation(1);
							}
						}
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

	@Override
	public void progressUpdate(int progress, int total) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void taskComplete(Object result) {
		updateAllInstances();
		updateCompletedInstances();
		pd.dismiss();
		String res = (String) result;
		Utilities.showTitleAndMessageDialog(this, "Resultado", res);
	}
	
	/*
	 * MENUS & CONTEXT MENUS
	 */
	
	private static final int MENU_ONE_ID = Menu.FIRST+1;
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) { 
		menu.setHeaderTitle("Sample Context Menu");
		menu.add(200, 200, 200, "item1"); 
	}
	
	private void populateMenu(ContextMenu menu) {
		menu.setHeaderTitle("TITLE");
		menu.add("HELLO");
		menu.add(Menu.NONE, MENU_ONE_ID, Menu.NONE, getString(R.string.instance_manager_completeInstancesMarkIncomplete));
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
