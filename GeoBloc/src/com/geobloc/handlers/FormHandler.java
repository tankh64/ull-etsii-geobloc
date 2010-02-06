package com.geobloc.handlers;

import android.widget.LinearLayout;

import com.geobloc.form.FormClass;


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
	
	
	public LinearLayout getLayout() {
		return null;
	}

}
