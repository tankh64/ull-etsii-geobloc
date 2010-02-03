/**
 * 
 */
package com.geobloc.activities;

import com.geobloc.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * New Activity which will allow the user to upload completed instances of forms or erase them.
 * User will also be able to see which instances are pending of being marked as "complete".
 * In other words, the user will know about all the instances he's been working on, completed and not completed.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class InstanceManager extends Activity {

	/**
	 * 
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instance_manager);
        initConfig();
	}
	
	private void initConfig() {
		
	
	}
}
