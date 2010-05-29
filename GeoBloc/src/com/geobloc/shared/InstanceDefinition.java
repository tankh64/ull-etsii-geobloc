/**
 * 
 */
package com.geobloc.shared;

import java.util.Date;

/**
 * @author Jorge Carballo
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class InstanceDefinition {
	
	private long instance_local_id;
	private FormDefinition form_definition;
	private String package_path; // instance path on SD-Card
	private Date date;
	private boolean complete;
	private String label; // this label only exists in Android Client
	
		
	public InstanceDefinition(long instanceLocalId,
			FormDefinition formDefinition, String packagePath, Date date,
			boolean complete, String label) {
		super();
		instance_local_id = instanceLocalId;
		form_definition = formDefinition;
		package_path = packagePath;
		this.date = date;
		this.complete = complete;
		this.label = label;
	}
	
	public long getInstance_local_id() {
		return instance_local_id;
	}
	public void setInstance_local_id(long instanceLocalId) {
		instance_local_id = instanceLocalId;
	}
	public FormDefinition getForm_definition() {
		return form_definition;
	}
	public void setForm_definition(FormDefinition formDefinition) {
		form_definition = formDefinition;
	}
	public String getPackage_path() {
		return package_path;
	}
	public void setPackage_path(String packagePath) {
		package_path = packagePath;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	
}
