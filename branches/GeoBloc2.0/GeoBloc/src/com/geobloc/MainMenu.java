package com.geobloc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.geobloc.activities.FormActivity;
import com.geobloc.activities.FormsDownloader;
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
	
	// For result from Activity
	private static final int LIST_FORM = 0;
	private static final int QUESTION_FORM = LIST_FORM + 1;
	private static final int PARSING_XML = LIST_FORM + 2;
	
	private static final int TEST = LIST_FORM + 9;
	
	// Buttons
	private Button mCreateFormButton;
	private Button mTestButton;
	
	// Spinner
	
	//
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Create Form Button
        mCreateFormButton = (Button) findViewById(R.id.ButtonMainMenu1);
        mCreateFormButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                
            	Intent i = new Intent (getApplicationContext(), FormList.class);
                startActivityForResult(i, LIST_FORM);
            }
            
 
        });
        
     // Create Test Button
        mTestButton = (Button) findViewById(R.id.ButtonMainMenu2);
        mTestButton.setText(getString(R.string.ButtonMainMenu2));
        mTestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                
            	Intent i = new Intent (getApplicationContext(), FormList.class);
                startActivityForResult(i, TEST);
            }
        });
    }
    
    //@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	switch (requestCode) {
    	
    		case LIST_FORM:

    			if(resultCode == RESULT_OK){
    				Bundle extras = data.getExtras();
    				String filename = extras.getString (FormList.FILE_NAME);
    				String filepath = extras.getString (FormList.FILE_PATH);
    				
    				Intent i = new Intent (getApplicationContext(), ParsingXML.class);
    				i.putExtra(ParsingXML.FILE_NAME, filepath);
    				startActivityForResult(i, PARSING_XML);
    			}
    			else {
    				Utilities.showToast(getApplicationContext(),
    							"Actividad NO devuelve correctamente",
    							Toast.LENGTH_SHORT);
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
    			
    		case TEST:

    			if(resultCode == RESULT_OK){
    				Bundle extras = data.getExtras();
    				String filename = extras.getString (FormList.FILE_NAME);
    				String filepath = extras.getString (FormList.FILE_PATH);
    				
    				Intent i = new Intent (getApplicationContext(),FormActivity.class);
    				i.putExtra(FormActivity.FILE_NAME, filepath);
    				startActivityForResult(i, PARSING_XML);
    			}
    			else {
    				Utilities.showToast(getApplicationContext(),
    							"Actividad NO devuelve correctamente",
    							Toast.LENGTH_SHORT);
    			}
    			break;
    	}
    }

    
    public void formsDownloaderButtonOnClickHandler(View target)
    {
    	Intent i = new Intent(this, FormsDownloader.class);
    	startActivity(i);
    }
    
    public void formsSenderButtonOnClickHandler(View target) {
    	Utilities.showToast(this, "Merry XMas!!", Toast.LENGTH_LONG);
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