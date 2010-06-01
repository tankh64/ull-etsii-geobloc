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
public interface IInstanceDefinition {

	public long getInstance_local_id();	
	public void setInstance_local_id(long instanceLocalId) ;
	
	public IFormDefinition getForm_definition();
	public void setForm_definition(IFormDefinition formDefinition);
	
	public String getPackage_path();
	public void setPackage_path(String packagePath);
	
	public Date getDate();
	public void setDate(Date date);
	
	public boolean isComplete();
	public void setComplete(boolean complete);
	
	public String getLabel();
	public void setLabel(String label);
	
}
