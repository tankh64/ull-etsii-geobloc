/**
 * 
 */
package com.geobloc.shared;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.view.View;
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
	
	public static String SINGLE_LIST_TYPE = "single";
	public static String MULTIPLE_LIST_TYPE = "multiple";
	public static String COMBO_LIST_TYPE = "combo";
	
	public static Color CBackground;
	// Deprecated public static int background = Color.WHITE;
	// Deprecated public static int fontColor = Color.DKGRAY;
	public static int requiredColor = Color.rgb(255,200,0);
	public static boolean photoSizeBigEnable = false;
	
	/*
	 * Type for the "Widget"
	 */
	public enum WidgetType {FIELD, CHECKBOX, CHECKBOXTHREE, LABEL, SINGLELIST,
		MULTIPLELIST, PHOTO, VIDEO, LOCATION, STRING, INT, BUTTON};
	
	/*
	 * Types for the FIELD
	 */
	public enum FieldType {STRING, INT, FLOAT};
	
	/**
	 * Enumerated type indicating the type of question
	 * @author Jorge Carballo (jelcaf@gmail.com)
	 *
	 */
	public enum QuestionType {GB_DATAINPUT, GB_LABEL, GB_MEDIA, GB_CHECKBOX, GB_CHECKBOX_THREE, GB_SINGLE_LIST, GB_MULTIPLE_LIST};
	
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

	public static String getDateAndTimeString() {
		Calendar cal = Calendar.getInstance();
    	String date = cal.get(Calendar.DATE) + "-" + (cal.get(Calendar.MONTH)+1) 
			+ "-" + cal.get(Calendar.YEAR);
    	String time = cal.get(Calendar.HOUR_OF_DAY) 
    		+ "-" + cal.get(Calendar.MINUTE) 
			+ "-" + cal.get(Calendar.SECOND)+"/";
		return "_" + date + "_" + time;
	}
	
	/**
	 * Builds a new package's name which follows this structure: formName + Date&Time.
	 * Identifier of the device is included inside the instance XML file.
	 * 
	 */
	public static String buildPackageName(Context context, String formName) {
		String name = formName;
		name += "_";
		
    	name += getDateAndTimeString();
		return name;
	}
	
	/**
	 * This method provides an easy way to detect whether Internet connectivity is available or not.
	 * It will check that, if we're on a mobile network, we're not doing roaming.
	 * @param context Necessary to get access to system information.
	 * @return true if there is connectivity and it is recommended (no roaming), false otherwise.
	 */
	public static boolean evaluateConnectivityAvailable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
		
        if (info == null)
        	return false; // no connectivity at all
        
		int netType = info.getType();
		TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (netType == ConnectivityManager.TYPE_WIFI) {
			return info.isConnected();
		}
		else
			if (netType == ConnectivityManager.TYPE_MOBILE
				&& !mTelephony.isNetworkRoaming()) {
				return info.isConnected();
			} else {
				return false;
			}
	}
	/**
	 * Scales a given photo from resource to the specified View's size. It takes care of keeping aspect ratio, so if the 
	 * so for example if the photo is wider than the View, it will scale it down until it fits. Note that if 
	 * the View is not inflated (height and width are zero) it can produce an Exception/force close the app. 
	 * @param res Access to the context's resources.
	 * @param resource The photo to scale.
	 * @param container The view to which the photo must be scaled down to.
	 * @return
	 */
	public static Bitmap scaleToContainer(Resources res, int resource, View container) {
		//connectivityBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.connectivity);
		Bitmap mySrc = BitmapFactory.decodeResource(res, resource);
		mySrc = Bitmap.createBitmap(mySrc);
		//connectivityBitmap = Bitmap.createScaledBitmap(connectivityBitmap, downloadView.getWidth(), downloadView.getHeight(), true);
		int bmpHeight = mySrc.getHeight();
		int bmpWidth = mySrc.getWidth();
		
		int containerWidth = container.getWidth();
		int containerHeight = container.getHeight();
		if((bmpWidth>bmpHeight)&&(bmpWidth>containerWidth))
		{
		    double ratio = ( (double)containerWidth/(double)bmpWidth );
		    bmpWidth=(int)containerWidth;
		    bmpHeight=(int)(ratio*bmpHeight);
		}
		else if((bmpHeight>bmpWidth)&&(bmpHeight>containerHeight))
		{
		    double ratio = ( (double)containerHeight/(double)bmpHeight );
		    bmpHeight=(int)containerHeight;
		    bmpWidth=(int)(ratio*bmpWidth);
		}

		//onnectivityBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.connectivity);
		mySrc = BitmapFactory.decodeResource(res, resource);
		//return Bitmap.createScaledBitmap(mySrc, bmpWidth, bmpHeight, true);
		return Bitmap.createBitmap(mySrc, 0, 0, bmpWidth, bmpHeight);
	}
	
	public static void setThemeForActivity (Activity ac, int idTheme) {
		switch (idTheme) {
		// Oscuro
		case 0: ac.setTheme(android.R.style.Theme);
				break;
		case 1: ac.setTheme(android.R.style.Theme_Light);
				break;
				default: ac.setTheme(android.R.style.Theme);
		}
	}
}
