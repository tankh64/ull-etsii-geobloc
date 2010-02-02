package com.geobloc.tasks;

import java.util.ArrayList;

import com.geobloc.Cover;
import com.geobloc.form.FormClass;
import com.geobloc.shared.Utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Task that loads a form
 * 
 * @author Jorge Carballo (jelcaf@gmail.com
 *
 */
public class LoadFormTask extends AsyncTask<String, Void, FormClass>{

	private Context context;
	
	public void setContext (Context context) {
		this.context = context;
	}
	
	/* 
	 * Runs on GUI Thread
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
	}
	
	@Override
	protected FormClass doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		//Utilities.showToast(context, "LoadFormTask", Toast.LENGTH_SHORT);
		
		return null;
	}
	
	/*
	 * Runs on UI Thread
	 */
	/*@Override
	protected void onProgressUpdate(Void... params) {

    }*/
	
	/*
	 * Runs on UI Thread
	 */
	/*@Override
	protected void onPostExecute(FormClass result) {

    }*/

	
}
