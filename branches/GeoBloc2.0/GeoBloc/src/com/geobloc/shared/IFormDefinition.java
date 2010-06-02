package com.geobloc.shared;

import java.util.Date;

import com.geobloc.db.DbForm;

/**
 * Class to represent the basic info of a form to be used by different classes.

 * 
 * @author Jorge Carballo
 * @author Dinesh Harjani
 *
 */
/*
 * 			DbForm form1 = new DbForm("Fresas", "serverform11212", 0, null, "Formulario de fresas", new Date(), -1, 0);
        	DbForm form2 = new DbForm("Naranjas", "serverform11213", 1, null, "Formulario de naranjas", new Date(), -1, 0);
        	DbForm form3 = new DbForm("Tomates", "serverform11214", 2, null, "Formulario de tomates", new Date(), -1, 0);
        	DbForm form4 = new DbForm("Papas", "serverform11215", 2, null, "Formulario de papitas negras", new Date(), -1, 0);
			
			
        	listForm.add(form1);
        	listForm.add(form2);
        	listForm.add(form3);
        	listForm.add(form4);
 */

public interface IFormDefinition {

	public String getForm_name();	
	public void setForm_name(String formName);
	
	public String getForm_id();	
	public void setForm_id(String formId);
	
	public int getForm_version();	
	public void setForm_version(int formVersion);
	
	public String getForm_description();	
	public void setForm_description(String formDescription);
	
	public Date getForm_date();	
	public void setForm_date(Date formDate);
	
	public long getForm_local_id();	
	public void setForm_local_id(long formLocalId);
	
}
