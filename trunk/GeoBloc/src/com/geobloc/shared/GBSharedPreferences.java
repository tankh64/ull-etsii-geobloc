/**
 * 
 */
package com.geobloc.shared;


import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.widget.EditText;

import com.geobloc.R;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class GBSharedPreferences extends PreferenceActivity {
	
	private EditTextPreference uploadPackagesServletAddress;
	private EditTextPreference packagesPath;
	private EditTextPreference formsPath;
	
	private EditText et;
	private SharedPreferences prefs;
	
	public static String __UPLOAD_PACKAGES_SERVLET_ADDRESS_KEY__ = "uploadPackagesServletAddress";
	public static String __PACKAGES_PATH_KEY__ = "packagesPath";
	public static String __FORMS_PATH_KEY__ = "formsPath";
	
	// default internet addresses
	public static String __DEFAULT_UPLOAD_PACKACGES_SERVLET_ADDRESS__ = "http://ull-etsii-geobloc.appspot.com/upload_basicpackageform";
	// default directory paths
	public static String __DEFAULT_FORMS_PATH__ = Environment.getExternalStorageDirectory()+"/GeoBloc/forms/";
	public static String __DEFAULT_PACKAGES_PATH__ = Environment.getExternalStorageDirectory()+"/GeoBloc/packages/";
	
	// default filenames
	public static String __DEFAULT_PACKAGE_MANIFEST_FILENAME__ = "manifest.xml";
	public static String __DEFAULT_FORM_FILENAME__ = "form.xml";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initConfig();
	}
	
	private void initConfig() {
		addPreferencesFromResource(R.layout.settings);
		
		uploadPackagesServletAddress = (EditTextPreference) findPreference(GBSharedPreferences.__UPLOAD_PACKAGES_SERVLET_ADDRESS_KEY__);
		formsPath = (EditTextPreference) findPreference(GBSharedPreferences.__FORMS_PATH_KEY__);
		packagesPath = (EditTextPreference) findPreference(GBSharedPreferences.__PACKAGES_PATH_KEY__);
		
		// default uploadPackagesServletAddress
		if ((uploadPackagesServletAddress.getText() == null) || (uploadPackagesServletAddress.getText() == ""))
			uploadPackagesServletAddress.setText(GBSharedPreferences.__DEFAULT_UPLOAD_PACKACGES_SERVLET_ADDRESS__);
		
		// default forms path
		if ((formsPath.getText() == null) || (formsPath.getText().length() == 0))
			formsPath.setText(GBSharedPreferences.__DEFAULT_FORMS_PATH__);
		
		
		// default packages path
		if ((packagesPath.getText() == null) || (packagesPath.getText().length() == 0))
			packagesPath.setText(GBSharedPreferences.__DEFAULT_PACKAGES_PATH__);

	}
}
