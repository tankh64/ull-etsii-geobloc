package com.geobloc;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un Formulario
 * 
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public class FormClass {
	
	private String nameForm;
	
	/** Lista de páginas del formulario */
	private List<FormPage> listPages;
	
	public FormClass () {
		listPages = new ArrayList<FormPage>();
	}
	
	public FormClass (List<FormPage> initList) {
		listPages = initList;
	}

	public FormClass (String name, List<FormPage> initList) {
		nameForm = name;
		listPages = initList;
	}
	
	public void addPage (FormPage page) {
		listPages.add(page);
	}
	
	public FormPage getPage (int index) {
		return listPages.get(index);
	}
	
	public String getName () {
		return nameForm;
	}
	
	public int getNumPages () {
		return listPages.size();
	}

}
