/**
 * 
 */
package com.geobloc.xml;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

/**
 * A class which can hold several fields inside, used to represent fields of the same object to be represented.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class MultiField implements IField {

	private List<IField> fields;
	private String tag;
	
	public MultiField() {
		fields = new ArrayList<IField>();
		tag = "No tag";
	}
	
	public MultiField(String tag) {
		fields = new ArrayList<IField>();
		setFieldTag(tag);
	}
	
	public String getFieldTag() {
		return tag;
	}

	public void setFieldTag(String tag) {
		this.tag = tag;		
	}	
	
	public void addField(IField field) {
		fields.add(field);
	}
	
	public void removeField(IField field) {
		fields.remove(field);
	}
	
	public List<IField> getFields() {
		return fields;
	}

	public void setFields(List<IField> fields) {
		this.fields = fields;
	}

	public void toXML(XmlSerializer serializer) {
		try {
			serializer.startTag("", this.getFieldTag());
			//serializer.attribute("", "number", String.valueOf(fields.size()));
			serializer.text("\n");
			for (IField field : fields) {
				// NOTE: Each IField should indent according to depth
				serializer.text(IField.__IFIELD_IDENTATION__);
				field.toXML(serializer);
			}
			serializer.endTag("", this.getFieldTag());
			serializer.text("\n");
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
