package com.geobloc.db;

import java.util.Date;

import com.geobloc.shared.IFormDefinition;

/**
 * Class to represent the basic info of a form to be used by different classes.

 * 
 * @author Jorge Carballo
 * @author Dinesh Harjani
 *
 */
public class FormDefinition implements IFormDefinition {
	
	private String form_name;
	private String form_id; // id in server
	private int form_version; // version in server
	private String form_description;
	private String form_date;
	private long form_local_id;
	
	
	public FormDefinition(String formName, String formId, int formVersion,
			String formDescription, String formDate, long formLocalId) {
		super();
		form_name = formName;
		form_id = formId;
		form_version = formVersion;
		form_description = formDescription;
		form_date = formDate;
		form_local_id = formLocalId;
	}
	
	public String getForm_name() {
		return form_name;
	}
	public void setForm_name(String formName) {
		form_name = formName;
	}
	public String getForm_id() {
		return form_id;
	}
	public void setForm_id(String formId) {
		form_id = formId;
	}
	public int getForm_version() {
		return form_version;
	}
	public void setForm_version(int formVersion) {
		form_version = formVersion;
	}
	public String getForm_description() {
		return form_description;
	}
	public void setForm_description(String formDescription) {
		form_description = formDescription;
	}
	public String getForm_date() {
		return form_date;
	}
	public void setForm_date(String formDate) {
		form_date = formDate;
	}
	public long getForm_local_id() {
		return form_local_id;
	}
	public void setForm_local_id(long formLocalId) {
		form_local_id = formLocalId;
	}
		

}
