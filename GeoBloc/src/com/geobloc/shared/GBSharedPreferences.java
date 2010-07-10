/**
 * 
 */
package com.geobloc.shared;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;

import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.EditText;

import com.geobloc.R;

/**
 * @author Jorge Carballo (jelcaf@gmail.com)
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class GBSharedPreferences extends PreferenceActivity {
	
	private EditTextPreference baseServerAddress;
	private EditTextPreference uploadPackagesServletAddress;
	private EditTextPreference listOfAvailableFormsServletAddress;
	private EditTextPreference downloadFormsServletAddress;
	private EditTextPreference lastServletListCheck;
	private EditTextPreference packagesPath;
	private EditTextPreference formsPath;
	private EditTextPreference numberOfInternetAttempts;
	private CheckBoxPreference slidingButtonsAnimationEnabled;
	private CheckBoxPreference photoSizeBigEnable;
	private CheckBoxPreference sendIncompleteInstances;
	//private DialogPreference dropLocalFormsTable;
	private CheckBoxPreference userApplicationDebugging;
	private ListPreference formThemeColor;
	
	private EditText et;
	private SharedPreferences prefs;
	
	// internet preference keys
	public static String __BASE_SERVER_ADDRESS_KEY__ = "baseServerAddressKey";
	public static String __UPLOAD_PACKAGES_SERVLET_ADDRESS_KEY__ = "uploadPackagesServletAddress";
	public static String __DOWNLOAD_FORMS_SERVLET_ADDRESS_KEY__ = "downloadFormsServletAddress";
	public static String __GET_AVAILABLE_FORMS_LIST_SERVLET_ADDRESS_KEY__ = "getAvailableFormsListFromServletAddress";
	public static String __LAST_SERVER_LIST_CHECK_KEY__ = "lastTimeApplicationCheckedForANewListOfFormsInTheServer";
	public static String __NUMBER_OF_INTERNET_ATTEMPTS_KEY__ = "numberOfInternetConnectionAttempts";
	
	// persistance preference keys
	public static String __PACKAGES_PATH_KEY__ = "packagesPath";
	public static String __FORMS_PATH_KEY__ = "formsPath";
	
	// animation preferences
	public static String __SLIDING_BUTTONS_ANIMATION_KEY__ = "slidingButtonsAnimationKey";
	
	// advanced preferences
	public static String __DELETE_LOCALFORMS_TABLE_KEY__ = "dropLocalFormsTableKey";
	public static String __ENABLE_DEBUGGING_FEATURES_KEY__ = "enableOptionsForUserApplicationDebugging";
	
	// forms preferences
	public static String __FORM_THEME_COLOR__ = "formThemeColor";
	public static String __FORM_REQUIRED_COLOR__ = "colorRequired";
	public static String __FORM_PHOTO_SIZE_BIG__ = "formPhotoSize";
	
	// instance preferences
	public static String __SEND_INCOMPLETE_KEY__ = "allowSendingIncompleteInstances";
	
	// default internet addresses
	public static String __DEFAULT_BASE_SERVER_ADDRESS__ = "http://tomcat.etsii.ull.es/geobloc/";
	//public static String __DEFAULT_UPLOAD_PACKACGES_SERVLET_ADDRESS__ = "http://ull-etsii-geobloc.appspot.com/upload_basicpackageform";
	//public static String __DEFAULT_GET_AVAILABLE_FORMS_LIST_SERVLET_ADDRESS__ = "http://ull-etsii-geobloc.appspot.com/get_listofavailablebasicforms";
	//public static String __DEFAULT_DOWNLOAD_FORMS_SERVLET_ADRESS__ = "http://ull-etsii-geobloc.appspot.com/get_basicformfile";
	
	public static String __DEFAULT_UPLOAD_PACKACGES_SERVLET_ADDRESS__ = "notAvailable";
	public static String __DEFAULT_GET_AVAILABLE_FORMS_LIST_SERVLET_ADDRESS__ = "getFormsList";
	public static String __DEFAULT_DOWNLOAD_FORMS_SERVLET_ADRESS__ = "sendFormServlet";
	// default directory paths
	public static String __DEFAULT_FORMS_PATH__ = Environment.getExternalStorageDirectory()+"/GeoBloc/forms/";
	public static String __DEFAULT_PACKAGES_PATH__ = Environment.getExternalStorageDirectory()+"/GeoBloc/packages/";
	
	// default filenames
	public static String __DEFAULT_PACKAGE_MANIFEST_FILENAME__ = "manifest.xml";
	public static String __DEFAULT_FORM_FILENAME__ = "form.xml";
	
	// default animation states
	public static boolean __DEFAULT_SLIDING_BUTTONS_ANIMATION__ = true;
	
	// swipe
	public static final int SWIPE_MIN_DISTANCE = 70;
	public static final int SWIPE_MAX_OFF_PATH = 320;
	public static final int SWIPE_THRESHOLD_VELOCITY = 100;
	
	// default form
	public static final boolean __DEFAULT_FORM_PHOTO_SIZE_BIG__ = false;
	
	// other
	public static String __DEFAULT_NUMBER_OF_INTERNET_ATTEMPTS__ = "3";
	public final static int __DEFAULT__ANIMATION_TIME__ = 2500;
	public static String __OK_SIGNATURE__ = "ALLRITE";

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initConfig();
	}
	
	private void initConfig() {
		addPreferencesFromResource(R.layout.settings);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		baseServerAddress = (EditTextPreference) findPreference(GBSharedPreferences.__BASE_SERVER_ADDRESS_KEY__);
		uploadPackagesServletAddress = (EditTextPreference) findPreference(GBSharedPreferences.__UPLOAD_PACKAGES_SERVLET_ADDRESS_KEY__);
		listOfAvailableFormsServletAddress = (EditTextPreference) findPreference(GBSharedPreferences.__GET_AVAILABLE_FORMS_LIST_SERVLET_ADDRESS_KEY__);
		downloadFormsServletAddress = (EditTextPreference) findPreference(GBSharedPreferences.__DOWNLOAD_FORMS_SERVLET_ADDRESS_KEY__);
		lastServletListCheck = (EditTextPreference) findPreference(GBSharedPreferences.__LAST_SERVER_LIST_CHECK_KEY__);
		formsPath = (EditTextPreference) findPreference(GBSharedPreferences.__FORMS_PATH_KEY__);
		packagesPath = (EditTextPreference) findPreference(GBSharedPreferences.__PACKAGES_PATH_KEY__);
		numberOfInternetAttempts = (EditTextPreference) findPreference(GBSharedPreferences.__NUMBER_OF_INTERNET_ATTEMPTS_KEY__);
		slidingButtonsAnimationEnabled = (CheckBoxPreference) findPreference(GBSharedPreferences.__SLIDING_BUTTONS_ANIMATION_KEY__);
		photoSizeBigEnable = (CheckBoxPreference) findPreference(GBSharedPreferences.__FORM_PHOTO_SIZE_BIG__);
		userApplicationDebugging = (CheckBoxPreference) findPreference(GBSharedPreferences.__ENABLE_DEBUGGING_FEATURES_KEY__);
		sendIncompleteInstances = (CheckBoxPreference) findPreference(GBSharedPreferences.__SEND_INCOMPLETE_KEY__);
		//dropLocalFormsTable = (DialogPreference) findPreference (GBSharedPreferences.__DELETE_LOCALFORMS_TABLE_KEY__);
		formThemeColor = (ListPreference) findPreference (GBSharedPreferences.__FORM_THEME_COLOR__);
		
		// default baseServerAddress
		setEditTextDefaultConfig(baseServerAddress, 
				GBSharedPreferences.__DEFAULT_BASE_SERVER_ADDRESS__);
		
		// default uploadPackagesServletAddress
		setEditTextDefaultConfig(uploadPackagesServletAddress, 
				GBSharedPreferences.__DEFAULT_UPLOAD_PACKACGES_SERVLET_ADDRESS__);
		
		// default listOfAvailableFormsAddress
		setEditTextDefaultConfig(listOfAvailableFormsServletAddress, 
				GBSharedPreferences.__DEFAULT_GET_AVAILABLE_FORMS_LIST_SERVLET_ADDRESS__);
		
		// default downloadFormsServletAddress
		setEditTextDefaultConfig(downloadFormsServletAddress,
				GBSharedPreferences.__DEFAULT_DOWNLOAD_FORMS_SERVLET_ADRESS__);
		
		// default numberOfInternetAttempts
		setEditTextDefaultConfig(numberOfInternetAttempts, 
				GBSharedPreferences.__DEFAULT_NUMBER_OF_INTERNET_ATTEMPTS__);
		
		//lastServletListCheck.setSummary(lastServletListCheck.getText().toString());
		setEditTextDefaultConfig(lastServletListCheck, "");
		if (lastServletListCheck.getText() != null)
			lastServletListCheck.setSummary(lastServletListCheck.getText().toString());
		else
			lastServletListCheck.setSummary("");
		
		// default forms path
		setEditTextDefaultConfig(formsPath, GBSharedPreferences.__DEFAULT_FORMS_PATH__);
		
		
		// default packages path
		setEditTextDefaultConfig(packagesPath, GBSharedPreferences.__DEFAULT_PACKAGES_PATH__);
		
		// default animations setting
		//slidingButtonsAnimationEnabled.setDefaultValue(GBSharedPreferences.__DEFAULT_SLIDING_BUTTONS_ANIMATION__);
		slidingButtonsAnimationEnabled.setChecked(prefs.getBoolean(GBSharedPreferences.__SLIDING_BUTTONS_ANIMATION_KEY__, 
				GBSharedPreferences.__DEFAULT_SLIDING_BUTTONS_ANIMATION__));
		
		photoSizeBigEnable.setChecked(prefs.getBoolean(GBSharedPreferences.__FORM_PHOTO_SIZE_BIG__, 
				GBSharedPreferences.__DEFAULT_FORM_PHOTO_SIZE_BIG__));
		
		userApplicationDebugging.setDefaultValue(false);
		sendIncompleteInstances.setChecked(prefs.getBoolean(GBSharedPreferences.__SEND_INCOMPLETE_KEY__, true));
		
		if (formThemeColor.getEntry() == null)
			formThemeColor.setValueIndex(0);

	}
	
	private void setEditTextDefaultConfig(EditTextPreference edt, String defaultText) {
		if ((edt.getText() == null) || (edt.getText().length() == 0))
			edt.setText(defaultText);
	}
}
