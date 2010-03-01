/**
 * 
 */
package com.geobloc.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.geobloc.R;
import com.geobloc.db.DbFormInstance;
import com.geobloc.db.DbFormInstanceSQLiteHelper;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.shared.GBSharedPreferences;
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
	 * CODE FOR SWIPE ANIMATION
	 * 
	 */
	
	private ViewFlipper flipper;
	
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;	
	
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
	
	private void initConfig() {
		setContentView(R.layout.instance_manager);
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
        
        //SQLiteOpenHelper helper = new DbFormInstanceSQLiteHelper(this);
        //db = helper.getWritableDatabase();
        
        //db = openOrCreateDatabase(DbFormInstanceSQLiteHelper.__LOCALPACKAGESDB_TABLE_NAME__, 2, null);
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
		
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		packagesPath =  prefs.getString(GBSharedPreferences.__PACKAGES_PATH_KEY__, 
			GBSharedPreferences.__DEFAULT_PACKAGES_PATH__);
		
		// TESTING
		//DbFormInstanceSQLiteHelper.autoAdd(db);
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
        initConfig();
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
				break;
			case (R.id.completeInstancesSendButton) :
				
				break;
			default:
				break;
		}
	}
	
	
	/*
	 * DATABASE RELATED
	 * 
	 */
	
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
	}
		
	class DbFormInstanceWrapper {
		private DbFormInstance dbi = null;
		private long id;
		private int position;
		private CheckBox cb = null;
		private TextView name = null;
		private TextView createdDate = null;
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
			if (getMode() == 0)
				getCreatedDate().setText(c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_CREATEDDATE_KEY__)));
			else {
				// in mode 1, we're guaranteed to have a completed date
				getCreatedDate().setText(c.getString(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_COMPLETEDDATE_KEY__)));
			}
			int completedAux = c.getInt(c.getColumnIndex(DbFormInstance.__LOCALPACKAGESDB_COMPLETED_KEY__));
			if (completedAux == 0)
				getCompleted().setText(R.string.instance_manager_list_itemFormInstanceCompleteNo);
			else
				getCompleted().setText(R.string.instance_manager_list_itemFormInstanceCompleteYes);
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
	}

	@Override
	public void progressUpdate(int progress, int total) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void taskComplete(Object result) {
		allInstancesModel.requery();
		completedInstancesModel.requery();
		// erase list of checked items
		allInstancesCheckedItems.clear();
		allInstancesCheckedItems = new ArrayList<Long>(allInstancesModel.getCount());
		completedInstancesCheckedItems.clear();
		completedInstancesCheckedItems = new ArrayList<Long> (completedInstancesModel.getCount());
		pd.dismiss();
		String res = (String) result;
		Utilities.showTitleAndMessageDialog(this, "Resultado", res);
	}
	
}
