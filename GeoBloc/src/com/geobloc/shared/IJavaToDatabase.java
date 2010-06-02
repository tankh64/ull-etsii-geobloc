/**
 * 
 */
package com.geobloc.shared;

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
public interface IJavaToDatabase {

	/*
	 * Form methods
	 */
	
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
	
	/*
	 * Instance methods
	 */
	
	/**
	 * Similarly to {@link getListOfLocalForms()}, this method returns us a list of all the stored instances in our database.
	 * @return List with {@link InstanceDefinition} Java Objects
	 * @throws Exception if something goes wrong, like not being able to read from the database.
	 */
	public List<IInstanceDefinition> getListOfLocalInstances() throws Exception;
	/**
	 * Allows us to create a new instance based on the given form
	 * @param form_local_id the unique identifier to the local form, usually provided by a {@link DbForm}
	 * @return {@link InstanceDefinition} representing the newly created instance.
	 * @throws Exception if something goes wrong.
	 */
	public IInstanceDefinition newInstance(long form_local_id) throws Exception;
	/**
	 * Basic operation to delete an instance.
	 * @param instance_local_id the unique identifier of the instance, generally provided by a {@link InstanceDefinition} Object.
	 * @throws Exception if the operation failed.
	 */
	public void deleteInstance(long instance_local_id) throws Exception;
	/**
	 * Perhaps the most complicated operation, its task is to transform the in-memory instance into the final instance.xml file.
	 * @param instance the {@link InstanceDefinition} Object representing the instance to be saved
	 * @param form the {@link FormClass} Object containing the necessary information.
	 * @throws Exception if the operation could not be performed.
	 */
	public void saveInstance(IInstanceDefinition instance, FormClass form) throws Exception;
}
