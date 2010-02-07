/**
 * 
 */
package com.geobloc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.geobloc.R;
import com.geobloc.widget.ThreeStateButton;
import com.geobloc.widget.ThreeStateCheckBox;

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
		LinearLayout myLinearLayout = (LinearLayout) findViewById(R.id.myLinearLayout);
		
		ThreeStateCheckBox boxOne = (ThreeStateCheckBox) findViewById(R.id.boxOne);
		boxOne.setText("Cool ThreeStateCheckBox");


		ThreeStateCheckBox threestate = new ThreeStateCheckBox(this);
		threestate.setState(ThreeStateButton.__STATE_PRESSED__);
		threestate.setText("Programmatically inflated.");
		threestate.setDrawShadowEnabled(true);
		threestate.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT));
		myLinearLayout.addView(threestate);
		threestate.setText("Testing with a very long text, and using the button underneath to check if the view takes the necessary space.");
		
		Button limit = new Button(this);
		limit.setText("Limit");
		myLinearLayout.addView(limit);
	}
}
