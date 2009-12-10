/**
 * 
 */
package com.geobloc.shared;

import android.content.Context;
import android.widget.Toast;

/**
 * Common methods we keep calling again, again, and again all over our code.
 * Best if we keep it all here.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class Utilities {

	/* 
	 * Displays a Toast. The context parameter is filled with getApplicationContext() from the Activity 
	 * you're calling this from. Duration is with Toast.LENGTH_SHORT or Toast.LENGTH_LONG
	*/
	public static void showToast(Context context, String message, int duration) {
		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
	}

}
