/**
 * 
 */
package com.geobloc.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.geobloc.R;
import com.geobloc.shared.GBSharedPreferences;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class FormsManager extends Activity {
	
	/*
	 * 
	 * CODE FOR ANIMATIONS (SWIPE AND BOTTOM LAYOUTS)
	 * 
	 */
	
	private static int __NUMBER_OF_PAGES__ = 2;
	
	private ViewFlipper flipper;
	private int currentPage = 1; // we start with the download page (to the right)
	
	private Animation bounceRight;
	private Animation bounceLeft;
	
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;	
	
	/*
	private boolean enableButtonSetAnimation = true;
	private RelativeLayout allInstancesButtonSet;
	private RelativeLayout completedInstancesButtonSet;
	*/
	
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
                }  else if ((e2.getX() - e1.getX() > GBSharedPreferences.SWIPE_MIN_DISTANCE) && 
                		(Math.abs(velocityX) > GBSharedPreferences.SWIPE_THRESHOLD_VELOCITY)) {
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
                e.printStackTrace();
            }
            return false;

		}
	}
	
	private void initConfig() {
		//Hide the title bar
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forms_manager);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.flipper = (ViewFlipper)findViewById(R.id.formsManager_viewFlipper);
		this.serverList = (ListView)findViewById(R.id.formsManager_downloadListView);
		
		bounceLeft = AnimationUtils.loadAnimation(this, R.anim.bounce_left);
		bounceRight = AnimationUtils.loadAnimation(this, R.anim.bounce_right);
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
        
        connMgr = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        
	}
	
	/*
	 * 
	 * CODE FOR ACTIVITY LOGIC 
	 * 
	 */
	
	private SharedPreferences prefs;
	private ConnectivityManager connMgr;
	
	private ListView serverList;
	private ListView deleteList;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConfig();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
	else
		return false;
	}
}
