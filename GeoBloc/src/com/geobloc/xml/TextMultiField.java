/**
 * 
 */
package com.geobloc.xml;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

/**
 * Very similar to the MultiField, the TextMultiField contains an Embedded TextField besides other fields 
 * it might be containing.
 * 
 * Structure:
 * <tag>
 *  <tagA> fieldA </tagA>
 *  <tagB> fieldB </tagB>
 *  other IFields...
 * <tag>
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class TextMultiField implements IField, ITextField {

	private String tag;
	private String tagA;
	private String tagB;
	private String fieldA;
	private String fieldB;
	
	private List<IField> fields;
	
	public TextMultiField() {
		fields = new ArrayList<IField>();
		tag = "tag";
		tagA = "tagA";
		tagB = "tagB";
		fieldA = "fieldA";
		fieldB = "fieldB";
	}
	
	public TextMultiField(String tag, String tagA, String tagB, String fieldA, String fieldB) {
		fields = new ArrayList<IField>();
		this.tag = tag;
		this.tagA = tagA;
		this.tagB = tagB;
		this.fieldA = fieldA;
		this.fieldB = fieldB;
	}
	
	/* (non-Javadoc)
	 * @see com.geobloc.xml.IField#getFieldTag()
	 */
	@Override
	public String getFieldTag() {
		return tag;
	}

	/* (non-Javadoc)
	 * @see com.geobloc.xml.IField#setFieldTag(java.lang.String)
	 */
	@Override
	public void setFieldTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String getFieldA() {
		return fieldA;
	}

	@Override
	public String getFieldB() {
		return fieldB;
	}

	@Override
	public void setFieldA(String a) {
		fieldA = a;		
	}

	@Override
	public void setFieldB(String b) {
		fieldB = b;
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

	/* (non-Javadoc)
	 * @see com.geobloc.xml.IField#toXML(org.xmlpull.v1.XmlSerializer)
	 */
	public void toXML(XmlSerializer serializer) {
		try {
			serializer.startTag("", this.getFieldTag());
			serializer.text("\n" + IField.__IFIELD_IDENTATION__ + IField.__IFIELD_IDENTATION__);
			serializer.startTag("", getTagA());
			serializer.text(getFieldA());
			serializer.endTag("", getTagA());
			serializer.text("\n" + IField.__IFIELD_IDENTATION__+ IField.__IFIELD_IDENTATION__);
			serializer.startTag("", getTagB());
			serializer.text(getFieldB());
			serializer.endTag("", getTagB());
			serializer.text("\n");
			//serializer.attribute("", "number", String.valueOf(fields.size()));
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
