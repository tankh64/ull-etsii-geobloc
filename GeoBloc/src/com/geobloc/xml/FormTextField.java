/**
 * 
 */
package com.geobloc.xml;

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
 * 	<tagA> fieldA </tagA>
 *  <tagB> fieldB </tagB>
 * </tag>
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class FormTextField implements ITextField {
	
	private String tag;
	private String tagA;
	private String tagB;
	private String fieldA;
	private String fieldB;

	public FormTextField(){
		tag = "No tag";
		fieldA = "Empty.";
		fieldB = "Empty.";
	}
	
	public FormTextField(String tag, String tagA, String tagB, String name, String contents) {
		setFieldTag(tag);
		setTagA(tagA);
		setTagB(tagB);
		setFieldA(name);
		setFieldB(contents);
	}
		
	
	public String getFieldTag() {
		return tag;
	}

	public void setFieldTag(String tag) {
		this.tag = tag;
	}
	
	public void setTagA(String tagA) {
		this.tagA = tagA;
	}

	public String getTagA() {
		return tagA;
	}

	public void setTagB(String tagB) {
		this.tagB = tagB;
	}

	public String getTagB() {
		return tagB;
	}

	/* (non-Javadoc)
	 * @see dh.android.xml.ITextField#getFieldName()
	 */
	//@Override
	public String getFieldA() {
		return fieldA;
	}

	/* (non-Javadoc)
	 * @see dh.android.xml.ITextField#getFieldValue()
	 */
	//@Override
	public String getFieldB() {
		return fieldB;
	}

	/* (non-Javadoc)
	 * @see dh.android.xml.ITextField#setFieldName(java.lang.String)
	 */
	//@Override
	public void setFieldA(String a) {
		this.fieldA = a;
	}

	/* (non-Javadoc)
	 * @see dh.android.xml.ITextField#setFieldValue(java.lang.String)
	 */
	//@Override
	public void setFieldB(String b) {
		this.fieldB = b;
	}
	
	public void toXML(XmlSerializer serializer) {
		try {
			serializer.startTag("", getFieldTag());
			serializer.text("\n" + IField.__IFIELD_IDENTATION__+ IField.__IFIELD_IDENTATION__);
			serializer.startTag("", getTagA());
			serializer.text(getFieldA());
			serializer.endTag("", getTagA());
			serializer.text("\n" + IField.__IFIELD_IDENTATION__+ IField.__IFIELD_IDENTATION__);
			serializer.startTag("", getTagB());
			serializer.text(getFieldB());
			serializer.endTag("", getTagB());
			serializer.text("\n" + IField.__IFIELD_IDENTATION__);
    		serializer.endTag("", getFieldTag());
    		serializer.text("\n");
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

}
