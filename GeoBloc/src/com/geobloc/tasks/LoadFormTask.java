package com.geobloc.tasks;

import java.io.File;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.geobloc.Cover;
import com.geobloc.handlers.XMLHandler;
import com.geobloc.form.FormClass;
import com.geobloc.listeners.IStandardTaskListener;
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
public final class LoadFormTask extends AsyncTask<String, Void, FormClass>{

	private Context context;
	private IStandardTaskListener listener;
	
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
	protected FormClass doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		/*** Filename of the form */
		String filename = params[0];
		File filexml = new File (filename);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			/* Creamos un SAXParser. */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			/* Creamos un nuevo ContentHandler */
			XMLHandler myXMLHandler = new XMLHandler();
			
			/** Parsing the file */
            sp.parse(new File(filename), myXMLHandler);
            
            num = myXMLHandler.getNumPages();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

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
	@Override
	protected void onPostExecute(FormClass result) {
		if (listener != null) {
			listener.taskComplete(result);
		}
		
		Utilities.showToast(context, num+" pages loaded", Toast.LENGTH_SHORT);
		
		super.onPostExecute(result);
    }

	
}
