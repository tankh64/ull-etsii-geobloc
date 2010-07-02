/**
 * 
 */
package com.geobloc.shared;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.geobloc.db.DbForm;
import com.geobloc.db.DbFormSQLiteHelper;

/**
 * Class implementing the {@link IJavaToDatabaseForm} to interface with the Parsing engine.
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class JavaForms implements IJavaToDatabaseForm {

	private SQLiteDatabase db;
	private DbForm dbf;
	
	public JavaForms(Context context) {
		db = (new DbFormSQLiteHelper(context)).getReadableDatabase();
	}
	
	/* (non-Javadoc)
	 * @see com.geobloc.shared.IJavaToDatabaseForm#getListOfLocalForms()
	 */
	@Override
	public List<IFormDefinition> getListOfLocalForms() throws Exception {
		ArrayList<IFormDefinition> list = new ArrayList<IFormDefinition>();
		Cursor c = DbForm.getAll(db);
		c.moveToFirst();
		
		while (!c.isAfterLast()) {
			dbf = new DbForm();
			dbf.loadFrom(c);
			if (dbf.getServer_state() >= DbForm.__FORM_STATE_LOCAL__) 
				list.add((IFormDefinition)dbf);
			c.moveToNext();
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see com.geobloc.shared.IJavaToDatabaseForm#getLocalFormDefinition(long)
	 */
	@Override
	public IFormDefinition getLocalFormDefinition(long formLocalId) {
		dbf = new DbForm();
		dbf = DbForm.loadFrom(db, formLocalId);
		return (IFormDefinition)dbf;
	}

	/* (non-Javadoc)
	 * @see com.geobloc.shared.IJavaToDatabaseForm#getPathLocalForm(long)
	 */
	@Override
	public String getPathLocalForm(long formLocalId) {
		dbf = new DbForm();
		dbf = DbForm.loadFrom(db, formLocalId);
		return dbf.getForm_file_path();
	}

}
