package com.geobloc.form;

public class FormPage {
	/**
	 * 
	 * @author Administrador
	 *
	 */
	public enum PageType {DATA, PHOTO, VIDEO, AUDIO, LOCATION};
	
	private String namePage;
	private PageType type;
	
	public FormPage () {
		
	}
	
	public FormPage (String name) {
		this.setNamePage(name);
	}
	
	/**
	 * Sets the name of the page
	 * @param name The name under which we will refer to page
	 */
	public void setNamePage (String name) {
		this.namePage = name;
	}
	public String getNamePage () {
		return this.namePage;
	}
	
	/**
	 * Returns the type of page
	 */
	public PageType getPageType () {
		return this.type;
	}
	
	public void setPageType (PageType mtype) {
		this.type = mtype;
	}

}
