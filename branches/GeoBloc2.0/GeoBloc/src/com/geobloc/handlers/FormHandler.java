package com.geobloc.handlers;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.geobloc.form.FormClass;
import com.geobloc.form.FormDataPage;
import com.geobloc.form.FormPage;
import com.geobloc.prompt.QuestionPrompt;
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
	
	private LinearLayout view;
	private RelativeLayout viewR;
	
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
	
	
	public String getVersionForm () {
		return defForm.getVersionForm();
	}
	
	public void setVersionForm (String ver) {
		defForm.setVersionForm(ver);
	}
	
	/**
	 * 
	 * @param numPage 0 to ...
	 * @return
	 */
	public int getNumQuestionOfPage (int numPage) {
		return defForm.getNumQuestionsOfPage(numPage);
	}
	
	public QuestionPrompt getQuestionOfPage (int question, int page) {
		return ((FormDataPage) defForm.getPage(page)).getQuestion(question);
	}
	
	/**
	 * Returns a page of a form
	 * @param page number of the page to return
	 * @return
	 */
	public FormPage getPage (int page) {
		return defForm.getPage(page);
	}
	
	public String getNamePage (int page) {
		return defForm.getNamePage(page);
	}
	
	/**
	 * Returns all the pages names
	 * @return
	 */
	public ArrayList<String> getAllNamesOfPages () {
		ArrayList<String> nameList = new ArrayList<String>();
		
		nameList.add("Inicio");
		for (int i=0; i<defForm.getNumPages(); i++) {
			nameList.add(defForm.getNamePage(i));
		}
		nameList.add("Fin");
		
		return nameList;
	}
	
	/**
	 * Devuelve el formulario
	 */
	public FormClass getForm() {
		return defForm;
	}
}
