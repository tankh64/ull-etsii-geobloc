package com.geobloc.form;

import java.util.Date;

/**
 * Class to represent the basic info of a form to be used by different classes.

 * 
 * @author Jorge Carballo
 *
 */
public class FormDefinition {
	
	private String filename;
	private String nameForm;
	private String descriptionForm;
	private String versionForm;
	private String dateForm;
	
	public FormDefinition (String filename, String name, String desc, String version, String date) {
		setFilename (filename);
		setNameForm (name);
		setDescriptionForm(desc);
		setVersionForm (version);
		setDateForm (date);
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
	public void setFilename (String name) {
		this.filename = name;
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
	public String getFilename () {
		return this.filename;
	}

}
