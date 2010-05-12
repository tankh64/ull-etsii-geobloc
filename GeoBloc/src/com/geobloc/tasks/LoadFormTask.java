package com.geobloc.tasks;

import java.io.File;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.geobloc.Cover;
import com.geobloc.handlers.FormHandler;
import com.geobloc.handlers.XMLHandler;
import com.geobloc.form.FormClass;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.shared.Utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Task that loads a form
 * 
 * @author Jorge Carballo (jelcaf@gmail.com
 *
 */
public final class LoadFormTask extends AsyncTask<String, Void, FormHandler>{
	
	private static final String TAG = "LoadFormTask";

	private Context context;
	private IStandardTaskListener listener;
	
	public String message;
	private boolean trowsOk;
	
	private FormClass formC;
	private FormHandler formH;
	
	/** Auxiliar for debug */
	int num;
	/***********************/
	public void setContext (Context context) {
		this.context = context;
	}
	
	public void setListener(IStandardTaskListener listener) {
		this.listener = listener;
	}
	
	/* 
	 * Runs on GUI Thread
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected FormHandler doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		/*** Filename of the form */
		String filename = params[0];
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			/* Creamos un SAXParser. */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			
			/** To validate the file against a DTD*/
			//spf.setValidating(true);
			//spf.setFeature("http://xml.org/sax/features/validation", true);
			
			SAXParser sp = spf.newSAXParser();

			XMLHandler myXMLHandler = new XMLHandler();
			
			/** Parsing the file */
            sp.parse(new File(filename), myXMLHandler);
          
            trowsOk = true;
            
            formC = new FormClass();
            formC = myXMLHandler.getForm();
            formH = new FormHandler (formC);
            return formH;
		}
		catch (Exception e) {
			e.printStackTrace();
			
			Log.w(TAG, "Error al parsear: "+e.getMessage());
			
			trowsOk = false;
			message = e.getMessage();
			return null;
		}
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
	@Override
	protected void onPostExecute(FormHandler result) {
		if (listener != null) {
			listener.taskComplete(result);
		}
		
		if (trowsOk == true) {
			message = formH.getNumPages()+" pages loaded of the form \""+formH.getNameForm()+"\"";
		}
		
		super.onPostExecute(result);
    }

	
}
