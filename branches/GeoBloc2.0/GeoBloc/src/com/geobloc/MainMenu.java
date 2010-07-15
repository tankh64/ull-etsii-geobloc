package com.geobloc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geobloc.activities.FormActivity;
import com.geobloc.activities.FormsManager;
import com.geobloc.activities.InstanceManager;
import com.geobloc.activities.FormDefinitionList;
import com.geobloc.activities.SecondStaticFormPrototype;
import com.geobloc.activities.StaticFormPrototype;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.shared.Utilities;

/**
 * Main Menu class; used to connect parts of the app in development.
 * 
 * @author Jorge Carballo (jelcaf@gmail.com)
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */

public class MainMenu extends Activity {
	
	private static final String TAG = "MainMenu";
	
	// For result from Activity
	private static final int LIST_FORM = 0;					// List of FormDefinition
	private static final int QUESTION_FORM = LIST_FORM + 1;
	private static final int PARSING_XML = LIST_FORM + 2;
	
	private static final int SIMPLE_LIST_FORM = LIST_FORM + 9;
	
	// Buttons
	private Button mCreateFormButton;
	private Button mTestButton;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide the title bar
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        TextView tv = (TextView) findViewById(R.id.ProgramName);
        tv.setText(getString(R.string.app_name)+" v"+getString(R.string.version));
        
        // Create Form Button
        mCreateFormButton = (Button) findViewById(R.id.ButtonMainMenu1);
        mCreateFormButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent (getApplicationContext(), FormDefinitionList.class);
                startActivityForResult(i, LIST_FORM);
            }
            
 
        });
        
        // Create Test Button
        mTestButton = (Button) findViewById(R.id.ButtonMainMenu2);
        mTestButton.setText(getString(R.string.ButtonMainMenu2));
        mTestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent (getApplicationContext(), FormList.class);
                startActivityForResult(i, SIMPLE_LIST_FORM);
            }
        });
    }
    
    //@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	switch (requestCode) {
    	
    		case LIST_FORM:

    			/* Nos deberá devolver el "formLocalId" y el path completo del fichero
    			 * We will return the "formLocalId" and the full file path
    			 */
    			if(resultCode == RESULT_OK){
    				Bundle extras = data.getExtras();
    				
    				if (extras != null) {
    					if (extras.containsKey(FormDefinitionList.LOCAL_ID) || (extras.containsKey(FormDefinitionList.FILE_PATH))) { 
    						//String filename = extras.getString (FormList.FILE_NAME);
    						String filepath = extras.getString (FormList.FILE_PATH);
    						long localId = extras.getLong(FormDefinitionList.LOCAL_ID);
    				
    						//Log.i(TAG, "Filename es "+filename);
    						Log.i(TAG, "Filepath es "+filepath);
    						Log.i(TAG, "localId es "+localId);
    				
    						Intent i = new Intent (getApplicationContext(), FormActivity.class);
    						//i.putExtra(FormActivity.FILE_NAME, filename);
    						i.putExtra(FormActivity.FILE_PATH, filepath);
    						i.putExtra(FormActivity.LOCAL_ID, localId);
    						
    						startActivityForResult(i, PARSING_XML);
    					} else {
    						Log.e(TAG, "LIST_FORM no devuelve un nombre de fichero");
    					}
    				} else {
    					Log.e(TAG, "LIST_FORM no devuelve nada (null)");
    				}
    			}
    			break;
    		
    		case QUESTION_FORM:
    			if(resultCode == RESULT_OK){
    				Utilities.showToast(getApplicationContext(),
							"Actividad devuelve correctamente",
							Toast.LENGTH_SHORT);
    			}
    			else {
    				Utilities.showToast(getApplicationContext(),
							"Actividad NO devuelve correctamente",
							Toast.LENGTH_SHORT);
    			}
    			break;
    			
    		case PARSING_XML:
    			break;
    			
    		case SIMPLE_LIST_FORM:

    			if(resultCode == RESULT_OK){
    				Bundle extras = data.getExtras();
    				
    				if (extras != null) {
    					if (extras.containsKey(FormList.FILE_NAME) && (extras.containsKey(FormList.FILE_PATH))) {
    						String filename = extras.getString (FormList.FILE_NAME);
    						String filepath = extras.getString (FormList.FILE_PATH);
    				
    						Intent i = new Intent (getApplicationContext(),FormActivity.class);
    						i.putExtra(FormActivity.FILE_NAME, filename);
    						i.putExtra(FormActivity.FILE_PATH, filepath);
    						startActivityForResult(i, PARSING_XML);
    					} else {
    						Log.e(TAG, "SIMPLE_LIST_FORM no devuelve un nombre de fichero");
    					}
    				} else {
     					Log.e(TAG, "SIMPLE_LIST_FORM no devuelve nada (null)");
     				}
    			}
    			break;
    	}
    }
    
    public void mainMenuOnClickHandler(View target) {
    	Intent i = null;
    	switch (target.getId()) {
    		case R.id.mainMenu_formsManagerButton:
    			i = new Intent(this, FormsManager.class);
    			break;
    		case R.id.mainMenu_instancesManagerButton:
    			i = new Intent(this, InstanceManager.class);
    			break;
    		default:
    			break;
    	}
    	if (i != null)
    		startActivity(i);
    }
    
    /*
     * TESTING
     */
    
    public void staticFormStartButtonOnClickHandler(View target) {
    	Intent i = new Intent(this, StaticFormPrototype.class);
    	startActivity(i);
    }
    
    /* --> Menu <-- */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case (R.id.mainMenuSettings):
    		Intent i = new Intent(this, GBSharedPreferences.class);
			startActivity(i);
			break;
    	// More items go here (if any) ...
    	}
    	return false; 
    }
}