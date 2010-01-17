/**
 * 
 */
package com.geobloc.shared;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.shared.Utilities.WidgetType;
import com.geobloc.tasks.UploadPackageTask;
import com.geobloc.xml.FormTextField;
import com.geobloc.xml.IField;
import com.geobloc.xml.MultiField;
import com.geobloc.xml.TextXMLWriter;

/**
 * Class built from decoupling the task of managing the packages for sending them to the server.
 * MISSING: manifestBuilder (& manifest.xml)
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class UploadPackageHelper implements IStandardTaskListener {

	private Context callerContext;
	private HttpClient httpClient;
	private GeoBlocPackageManager pm;
	private ViewGroup vg;
	private List<WidgetType> list;
	
	private ProgressDialog pd;
	
	/**
	 * Class constructor.
	 * @param callerContext The context of this class' creator. Normally it's an Activity, and the keyword 
	 * 		"this" is more than enough.
	 * @param httpClient Since this helper handles the UploadPackageTask, it requires an httpClient.
	 * @param pm An initialized GeoBlocPackageManager, used to make the form.xml file. NOTE: It's the caller's 
	 * 		responsability to check if the GeoBlocPackageManager is not OK.
	 * @param vg A ViewGroup(Layout) containing all the GeoBloc Widgets to be transformed into an xml file
	 * @param list A list of WidgetType enums, where child i of vg (vg[i]] must be of 
	 * 		WidgetType list[i], or an exception will be thrown.
	 */
	public UploadPackageHelper(Context callerContext, HttpClient httpClient, GeoBlocPackageManager pm, ViewGroup vg, List<WidgetType> list) {
		this.callerContext = callerContext;
		this.httpClient = httpClient;
		this.pm = pm;
		this.vg = vg;
		this.list = list;
	}
	
	/**
	 * This method has the responsability of starting the chain of events which lead the user to at least try 
	 * to upload a package. For now, it always returns true, since the sending goes through NewTextReader. 
	 * For now. An Exception can be thrown if list[i] is not of type WidgetTyoe
	 *
	 */
	public boolean packAndSend() throws Exception {
		TextXMLWriter writer = new TextXMLWriter();
    	String xml = writer.WriteXML(this.getFields());
    	
    	// add form.xml
    	boolean xmlOk = pm.addFile(GBSharedPreferences.__DEFAULT_FORM_FILENAME__, xml);
    	
    	pd = ProgressDialog.show(callerContext, "Working", "Uploading package to Server...");
		pd.setIndeterminate(false);
		pd.setCancelable(false);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//pd.setMax(100);
		//pd.setProgress(0);
		
		UploadPackageTask task = new UploadPackageTask();
		task.setContext(callerContext.getApplicationContext());
		/*
		// get httpClient from ApplicationEx
		ApplicationEx app = (ApplicationEx)this.getApplication();
		HttpClient httpClient = app.getHttpClient();
		*/
		task.setHttpClient(httpClient);
		task.setListener(this);
		task.execute(pm.getPackageFullpath());
    	
    	// for now, always return true
		return true;
	}
	
	/**
	 * Method responsible of transforming the GeoBloc Widgets into fields which will be written into a xml file.
	 *
	 */
	private List<IField> getFields() throws Exception {
		List<IField> myFields = new ArrayList<IField>();
		FormTextField field;
    	MultiField fields = new MultiField("form-fields");
		// now, for every widget in ViewGroup
    	int enumCount = list.size();
    	int childFlag = 0;
    	int childCount = vg.getChildCount();
    	String name = "Widget not found";
    	String value = "Widget not found";
    	for (int i = 0; ((i < enumCount) && (childFlag < childCount)); i++) {
    		if (list.get(i) == WidgetType.LABEL) {
    			TextView tv = (TextView) vg.getChildAt(childFlag);
    			name = tv.toString();
    			value = tv.getText().toString();
    		}
    		if ((list.get(i) == WidgetType.INT) || (list.get(i) == WidgetType.STRING)) {
    			TextView tv = (TextView) vg.getChildAt(childFlag);
    			childFlag++; // These two WidgetTypes combine an EditText & a TextView
    			EditText et = (EditText) vg.getChildAt(childFlag);
    			
    			name = tv.getText().toString();
    			value = et.getText().toString();
    		}
    		if (list.get(i) == WidgetType.CHECKBOX) {
    			CheckBox cb = (CheckBox) vg.getChildAt(childFlag);
    			name = cb.toString();
    			if (cb.isChecked())
    				value = "TRUE";
    			else
    				value = "FALSE";
    		}
    		field = new FormTextField("widget", name, value);
    		fields.addField(field);
    		childFlag++;
    	}
    	// add MultiField
    	myFields.add(fields);
		return myFields;
	}

	@Override
	public void taskComplete(Object result) {
		if (pd != null)
			pd.dismiss();
		
		String res = (String) result;
		Utilities.showTitleAndMessageDialog(callerContext, "Package Report", res);		
	}

	@Override
	public void progressUpdate(int progress, int total) {
		// TODO Auto-generated method stub
		
	}
}
