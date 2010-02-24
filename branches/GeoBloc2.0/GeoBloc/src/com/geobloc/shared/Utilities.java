/**
 * 
 */
package com.geobloc.shared;

import android.app.AlertDialog;
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
	 * Attributes for the Tags and the questions
	 */
	public static String ATTR_ID = "id";
	public static String ATTR_IS_REQUIRED = "isRequired";
	
	/*
	 * Type for the "Widget"
	 */
	public enum WidgetType {LABEL, STRING, INT, BUTTON, CHECKBOX};
	
	/**
	 * Enumerated type indicating the type of question
	 * @author Jorge Carballo (jelcaf@gmail.com)
	 *
	 */
	public enum QuestionType {GB_DATAINPUT, GB_LABEL, GB_BUTTON};
	
	/* 
	 * Displays a Toast. The context parameter is filled with getApplicationContext() from the Activity 
	 * you're calling this from. Duration is with Toast.LENGTH_SHORT or Toast.LENGTH_LONG
	*/
	public static void showToast(Context context, String message, int duration) {
		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
	}
	
	/*
	 * Displays a simple Dialog with an OK button. Used fot the common task of giving some information to the 
	 * user without switching to another Activity. Needs Activity context, ApplicationContext will make 
	 * the caller crash.
	 */
	public static void showTitleAndMessageDialog(Context context, String title, String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);  
		alert.setTitle(title);  
		alert.setMessage(message);
		alert.setPositiveButton("OK", null);
		alert.show();
	}

}
