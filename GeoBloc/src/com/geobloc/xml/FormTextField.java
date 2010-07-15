/**
 * 
 */
package com.geobloc.xml;

import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

/**
 * Class which implements ITextField. Used by XMLParsers or XMLWriters
 * to store a simple String value such as a name, description, etc.
 * 
 * REBUILT:
 * Old Structure
 * <tag name="nameVariable" value="contentsVariable/>
 * 
 * New structure:
 * <tag>
 * 	<tag0> field0 </tag0>
 *  <tag1> field1 </tag1>
 *  <tagi> fieldi </tagi>
 * </tag>
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class FormTextField implements ITextField {
	
	private String tag;
	private ArrayList<String> tags;
	private ArrayList<String> fields;

	public FormTextField(){
		tag = "No tag";
		tags = new ArrayList<String>();
		fields = new ArrayList<String>();
	}
	
	public FormTextField(String tag) {
		setFieldTag(tag);
		tags = new ArrayList<String>();
		fields = new ArrayList<String>();
	}
		
	
	public String getFieldTag() {
		return tag;
	}

	public void setFieldTag(String tag) {
		this.tag = tag;
	}

	public void addTag(String tag, String field) {
		tags.add(tag);
		fields.add(field);
	}
	
	@Override
	public String getFieldi(int i) {
		if (i < fields.size())
			return fields.get(i);
		return null;
	}

	@Override
	public String getTagi(int i) {
		if (i < tags.size())
			return tags.get(i);
		return null;
	}

	@Override
	public void setFieldi(String field, int i) {
		fields.add(i, field);
	}

	@Override
	public void setTagi(String tag, int i) {
		tags.add(i, tag);
	}
	
	public void toXML(XmlSerializer serializer) {
		try {
			serializer.startTag("", getFieldTag());
			for (int i = 0; i < fields.size(); i++) {
				serializer.text("\n" + IField.__IFIELD_IDENTATION__+ IField.__IFIELD_IDENTATION__);
				serializer.startTag("", tags.get(i));
				serializer.text(fields.get(i));
				serializer.endTag("", tags.get(i));
			}
			serializer.text("\n" + IField.__IFIELD_IDENTATION__);
    		serializer.endTag("", getFieldTag());
    		serializer.text("\n");
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

}
