package com.geobloc.form;

import java.util.ArrayList;
import java.util.List;

/**
 * represents a form
 * 
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public class FormClass {
	
	/** TODO
	 * Make the mapping from the names of the pages to the position on the form
	 */
	
	private String nameForm;
	private String versionForm;
	private String description;
	
	private List<FormPage> listPages;
	
	/// Constructs
	public FormClass () {
		listPages = new ArrayList<FormPage>();
	}
	
	public FormClass (String name) {
		this.setNameForm(name);
		
		listPages = new ArrayList<FormPage>();
	}
	
	public FormClass (List<FormPage> initList) {
		listPages = initList;
	}

	public FormClass (String name, List<FormPage> initList) {
		this.setNameForm(name);
		listPages = initList;
	}
	
	
	/// Methods
	public void setNameForm (String name) {
		this.nameForm = name;
	}
	
	public String getNameForm () {
		return nameForm;
	}
	
	public void setVersionForm (String version) {
		this.versionForm = version;
	}
	
	public String getVersionForm () {
		return versionForm;
	}
	
	public void setDescription (String desc) {
		this.description = desc;
	}
	
	public String getDescription () {
		return this.description;
	}
	
	/**
	 * Add a page to form
	 * @param page New page to add to the form
	 */
	public void addPage (FormPage page) {
		listPages.add(page);
	}
	
	public FormPage getPage (int index) {
		return (listPages.get(index));
	}

	public String getNamePage (int page) {
		return (listPages.get(page)).getNamePage();
	}
	
	/**
	 * @return Number of pages on the form
	 */
	public int getNumPages () {
		return listPages.size();
	}
	
	public int getNumQuestionsOfPage (int numPage) {
		return ((FormDataPage)listPages.get(numPage)).getNumQuestions();
	}

}
