/**
 * 
 */
package com.geobloc.shared;

import java.util.ArrayList;
import java.util.List;

import com.geobloc.db.DbForm;
import com.geobloc.db.InstanceDefinition;
import com.geobloc.form.FormClass;

/**
 * This interface must be implemented by a class to abstract the Parser side of the app from the 
 * SQLiteDatabase and its underpinnings. All operations must be atomic, so if an Exception is thrown, 
 * there should be no evidence of the failed operation, as if it was never called. If no Exception is thrown, 
 * we assume the operation was completed successfully. 
 * 
 * @author Jorge Carballo
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public interface IJavaToDatabaseForm {
	/**
	 * This method must return a list with all the forms stored locally, which will later be used to create 
	 * a new instance from.
	 * @return List<FormDefinition> of {@link DbForm} Objects
	 * @throws Exception if this and other related operations cannot be performed, such as reading from the SD-Card.
	 */
	public List<IFormDefinition> getListOfLocalForms() throws Exception;
	/**
	 * This method allows us to get a Java object representation of a form stored locally.
	 * @param form_local_id The unique identifier (primary key) in the form local database
	 * @return {@link DbForm} a Java POJO representing a local form.
	 */
	public IFormDefinition getLocalFormDefinition(long form_local_id);
	/**
	 * Allows us to get the full path to the form.xml file associated with this local form. 
	 * @param form_local_id
	 * @return String 
	 */
	public String getPathLocalForm(long form_local_id);
}
