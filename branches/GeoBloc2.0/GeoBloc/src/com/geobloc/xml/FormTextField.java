/**
 * 
 */
package com.geobloc.xml;

import org.xmlpull.v1.XmlSerializer;

/**
 * Class which implements ITextField. Used by XMLParsers or XMLWriters
 * to store a simple String value such as a name, description, etc.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class FormTextField implements ITextField {
	
	private String tag;
	private String name;
	private String contents;

	public FormTextField(){
		tag = "No tag";
		name = "No name";
		contents = "Empty.";
	}
	
	public FormTextField(String tag, String name, String contents) {
		setFieldTag(tag);
		setFieldName(name);
		setFieldValue(contents);
	}
		
	
	public String getFieldTag() {
		return tag;
	}

	public void setFieldTag(String tag) {
		this.tag = tag;
	}

	/* (non-Javadoc)
	 * @see dh.android.xml.ITextField#getFieldName()
	 */
	//@Override
	public String getFieldName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see dh.android.xml.ITextField#getFieldValue()
	 */
	//@Override
	public String getFieldValue() {
		return contents;
	}

	/* (non-Javadoc)
	 * @see dh.android.xml.ITextField#setFieldName(java.lang.String)
	 */
	//@Override
	public void setFieldName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see dh.android.xml.ITextField#setFieldValue(java.lang.String)
	 */
	//@Override
	public void setFieldValue(String value) {
		this.contents = value;
	}
	
	public void toXML(XmlSerializer serializer) {
		try {
			serializer.startTag("", getFieldTag());
    		serializer.attribute("", "name", getFieldName());
    		serializer.attribute("", "value", getFieldValue());
    		serializer.endTag("", getFieldTag());
    		serializer.text("\n");
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

}
