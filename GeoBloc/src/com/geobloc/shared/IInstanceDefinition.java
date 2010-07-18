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

	/**
	 * Provides access to the instance's key in the local database.
	 * @return A long with the instance's key.
	 */
	public long getInstance_local_id();
	/**
	 * Mechanism to modify or set the instance's database key. Never used.
	 * @param instanceLocalId New instance local key.
	 */
	public void setInstance_local_id(long instanceLocalId) ;
	
	/**
	 * Returns the latest information on the attached form to this instance. However, its information 
	 * might not be consistent with the actual instance's form since the app might update or 
	 * delete the form. This has been designed to ensure instances are usable no matter what happens 
	 * with their attached forms locally.
	 * @return A {@link IFormDefinition} Object representing the latest information on the attached form. Can be null.
	 */
	public IFormDefinition getForm_definition();
	/**
	 * Method which allows us to set the form attached to this instance. However, it does not alter 
	 * the attached instance's version (that must be done with the appropriate method) nor replaces the 
	 * form.xml included in every instance.
	 * @param formDefinition
	 */
	public void setForm_definition(IFormDefinition formDefinition);
	/**
	 * Because the current representation of the instance's form might be altered during the existence of 
	 * an instance, the instance must now, on its own, the form it belongs to.
	 * @return
	 */
	public String getInstance_form_id();
	/**
	 * @return the attached form's version when the instance was created.
	 */
	public long getInstance_form_version();
	/**
	 * 
	 * @param formVersion The form attached to this instance's version when the instance was created.
	 */
	public void setInstance_form_version(long formVersion);
	
	/**
	 * @return Full path where the instance's files are stored.
	 */
	public String getPackage_path();
	/**
	 * Method to set or modify the instance's folder location.
	 * @param packagePath Path to the instance's folder.
	 */
	public void setPackage_path(String packagePath);
	/**
	 * @return The date in which the form was declared complete or was last saved.
	 */
	public Date getDate();
	/**
	 * Allows us to set the most recent date in which the instance was modified.
	 * @param date
	 */
	public void setDate(Date date);
	/**
	 * Allows us to know whether an instance has been flagged as complete or not. However, it does not 
	 * affect the workflow of the application unless the setting allowing the user to send incomplete 
	 * instances is unchecked.
	 * @return True if the instance was flagged as complete. 
	 */
	public boolean isComplete();
	/**
	 * @param complete A setting for the complete flag of the instance.
	 */
	public void setComplete(boolean complete);
	/**
	 * To give instances a meaning, we have added a local label, which will not be sent to the server.
	 * @return A label defining the instance's contents.
	 */
	public String getLabel();
	/**
	 * 
	 * @param label A string representing the instance's contents.
	 */
	public void setLabel(String label);
	
}
