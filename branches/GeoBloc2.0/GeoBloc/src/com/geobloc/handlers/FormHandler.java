package com.geobloc.handlers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.geobloc.form.FormClass;
import com.geobloc.shared.Utilities;


/**
 * Form handler
 *  
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public class FormHandler {
	
	private FormClass defForm;
	/** Llevará el numero de "campo" por el que va en el formulario */
	private int index;
	
	public FormHandler (FormClass form) {
		defForm = form;
		index = 0;
	}
	
	public int getNextData () {
		index++;
		return index;
	}
	
	public int getIndex () {
		return index;
	}
	
	public int getNumPages () {
		return defForm.getNumPages();
	}

	public String getNameForm () {
		return defForm.getNameForm();
	}
	
	public void setDescription (String desc) {
		defForm.setDescription(desc);
	}
	
	public String getDescription () {
		return defForm.getDescription();
	}
	
	/** Método que devolverá un ViewGroup del formulario */
	public ViewGroup getLayout(Context context) {
		
		LinearLayout view = new LinearLayout(context);
		
		if (defForm == null)
			return null;
		
		for (int i=0; i < defForm.getNumPages(); i++) {
			view.addView(defForm.getPage(i).getLayoutPage(context));
		}
		
		return (ViewGroup)view;
	}

}
