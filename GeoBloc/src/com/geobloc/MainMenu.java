package com.geobloc;

import com.geobloc.FormList;
import com.geobloc.activities.StaticFormPrototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainMenu extends Activity {
	
	// For result from Activity
	private static final int LIST_FORM = 0;
	
	// Buttons
	private Button mCreateFormButton;
	private Button mTestButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Create Form Button
        mCreateFormButton = (Button) findViewById(R.id.ButtonMainMenu1);
        mCreateFormButton.setText(getString(R.string.ButtonMainMenu1));
        mCreateFormButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                 
                /*Toast.makeText(getApplicationContext(),
                		getString(R.string.create_form),
                        Toast.LENGTH_SHORT).show();*/
                
            	Intent i = new Intent (getApplicationContext(), FormList.class);
                startActivity(i);
                //startActivity(new Intent (getApplicationContext(), FormList.class));
            	//startActivityForResult(i, LIST_FORM);
            }
        });
        
     // Create Test Button
        mTestButton = (Button) findViewById(R.id.ButtonMainMenu2);
        mTestButton.setText(getString(R.string.ButtonMainMenu2));
        mTestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                
            	Intent i = new Intent (getApplicationContext(), QuestionActivity.class);
                startActivity(i);

            }
        });

    }
    
    public void staticFormStartButtonOnClickHandler(View target)
    {
    	// start GeoBlocStaticForm Activity here
    	Intent i = new Intent(this, StaticFormPrototype.class);
    	startActivity(i);
    }
}