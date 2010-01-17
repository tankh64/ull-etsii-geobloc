/**
 * 
 */
package com.geobloc.persistance;

import java.util.ArrayList;
import java.util.List;

import com.geobloc.xml.FormTextField;
import com.geobloc.xml.IField;
import com.geobloc.xml.MultiField;
import com.geobloc.xml.TextXMLWriter;

/**
 * A class designed to return an XML containing metadata about the package. Some information will be filled 
 * upon creation, but other data must be added by the class in charge of building the package.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class GeoBlocPackageManifestBuilder {

	// MultiField representing the package
	private MultiField packageMultiField;
		// MultiField representing package header (name, date, author...)
		private MultiField pk_metadata;
	
	public GeoBlocPackageManifestBuilder(String title, 
			String description, String form_Schema, String language, String type, String date) {
		packageMultiField = new MultiField();
		packageMultiField.setFieldTag("package");
		
		pk_metadata = new MultiField();
		pk_metadata.setFieldTag("pk-metadata");
		//pk_metadata is inside packageMultiField
		packageMultiField.addField(pk_metadata);
		
		// fill in packageMultiField
		FormTextField field;
		field = new FormTextField("field", "Title", title);
		pk_metadata.addField(field);
		field = new FormTextField("field", "Description", description);
		pk_metadata.addField(field);
		field = new FormTextField("field", "Form Schema", form_Schema);
		pk_metadata.addField(field);
		field = new FormTextField("field", "Language", language);
		pk_metadata.addField(field);
		field = new FormTextField("field", "type", type);
		pk_metadata.addField(field);
		field = new FormTextField("field", "date", date);
		pk_metadata.addField(field);
	}
	
	public void addFile(String filename, String filetype) {
		// each file is represented in a MultiField with two fields, filename and filetype
		MultiField f_metadata = new MultiField();
		f_metadata.setFieldTag("f-metadata");
		FormTextField field;
		
		field = new FormTextField("field", "file-name", filename);
		f_metadata.addField(field);
		field = new FormTextField("field", "file-type", filetype);
		f_metadata.addField(field);
		// we add the new file to the packageMultiField
		packageMultiField.addField(f_metadata);		
	}
	
	public String toXml() {
		List<IField> fields = new ArrayList<IField>();
		fields.add(packageMultiField);
		TextXMLWriter writer = new TextXMLWriter();
		return writer.WriteXML(fields);
	}
}
