package com.geobloc;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


/**
 * Cover of the program
 * 
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public class Cover extends Activity {

	final Handler handler = new Handler();
	Timer t;
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.cover);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
        t = new Timer(); 
        t.schedule(new TimerTask() { 
        	public void run() { 
        		handler.post(new Runnable() { 
        			public void run() {
        		        Intent i = new Intent (getApplicationContext(), MainMenu.class);
        		        startActivity(i);
        		        
        		        finish();
        			} 
        		}); 
        	} 
        }, 3000);
	}
	
	@Override
	public void onPause () {
		super.onPause();
		if (t != null) {
			t.cancel();
		}
	}
}
