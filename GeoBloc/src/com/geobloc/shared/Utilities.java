/**
 * 
 */
package com.geobloc.shared;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.telephony.TelephonyManager;
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
	public static String ATTR_LINES_NUMBER = "linesNumber";
	public static String ATTR_TYPE = "type";
	public static String ATTR_ACTION = "action";
	
	public static int background = Color.WHITE;
	public static int fontColor = Color.DKGRAY;
	public static int requiredColor = Color.rgb(255,200,0);
	
	/*
	 * Type for the "Widget"
	 */
	public enum WidgetType {LABEL, STRING, INT, BUTTON, CHECKBOX};
	
	/*
	 * Types for the FIELD
	 */
	public enum FieldType {STRING, INT, FLOAT};
	
	/**
	 * Enumerated type indicating the type of question
	 * @author Jorge Carballo (jelcaf@gmail.com)
	 *
	 */
	public enum QuestionType {GB_DATAINPUT, GB_LABEL, GB_MEDIA, GB_CHECKBOX, GB_CHECKBOX_THREE};
	
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

	/*
	 * Builds a new package's name which follows this structure: formName + phoneID + Date&Time
	 * phoneID can be the phone's IMEI if it is a GSM device, MEID if it is a CDMA device, etc.
	 * 
	 */
	public static String buildPackageName(Context context, String formName) {
		String name = formName;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String id = tm.getDeviceId();
		// some devices do not have ID (MIDs, tablets, etc.)
		if (id != null)
			name += "_" + id;
		
    	Calendar cal = Calendar.getInstance();
    	String date = cal.get(Calendar.DATE) + "-" + (cal.get(Calendar.MONTH)+1) 
			+ "-" + cal.get(Calendar.YEAR);
    	String time = cal.get(Calendar.HOUR_OF_DAY) 
    		+ "-" + cal.get(Calendar.MINUTE) 
			+ "-" + cal.get(Calendar.SECOND)+"/";
		
    	name += "_" + date + "_" + time;
		return name;
	}
}
