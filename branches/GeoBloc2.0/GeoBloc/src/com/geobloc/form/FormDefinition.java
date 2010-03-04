package com.geobloc.form;

import java.util.Date;

/**
 * Class to represent the basic info of a form to be used by different classes.

 * 
 * @author Jorge Carballo
 *
 */
public class FormDefinition {
	
	private String nameForm;
	private String descriptionForm;
	private String versionForm;
	private String dateForm;
	
	public FormDefinition (String name, String desc, String version, String date) {
		
	}
	
	public void setNameForm (String name) {
		this.nameForm = name;
	}
	public void setDescriptionForm (String desc) {
		this.descriptionForm = desc;
	}
	public void setVersionForm (String version) {
		this.versionForm = version;
	}
	public void setDateForm (String date) {
		this.dateForm = date;
	}
	public String getNameForm () {
		return this.nameForm;
	}
	public String getDescriptionForm () {
		return this.descriptionForm;
	}
	public String getVersionForm () {
		return this.versionForm;
	}
	public String getDateForm () {
		return this.dateForm;
	}

}
