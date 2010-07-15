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
	private ArrayList<String> tags = new ArrayList<String>();
	private ArrayList<String> fields = new ArrayList<String>();
	
	private List<IField> ifields;
	
	public TextMultiField() {
		ifields = new ArrayList<IField>();
		tags = new ArrayList<String>();
		fields = new ArrayList<String>();
	}
	
	public TextMultiField(String tag) {
		ifields = new ArrayList<IField>();
		this.tag = tag;
		tags = new ArrayList<String>();
		fields = new ArrayList<String>();

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
	
	public void addField(IField field) {
		ifields.add(field);
	}
	
	public void removeField(IField field) {
		ifields.remove(field);
	}
	
	public List<IField> getFields() {
		return ifields;
	}

	public void setFields(List<IField> fields) {
		this.ifields = fields;
	}
	

	/* (non-Javadoc)
	 * @see com.geobloc.xml.IField#toXML(org.xmlpull.v1.XmlSerializer)
	 */
	public void toXML(XmlSerializer serializer) {
		try {
			serializer.startTag("", this.getFieldTag());
			for (int i = 0; i < fields.size(); i++) {
				serializer.text("\n" + IField.__IFIELD_IDENTATION__ + IField.__IFIELD_IDENTATION__);
				serializer.startTag("", tags.get(i));
				serializer.text(fields.get(i));
				serializer.endTag("", tags.get(i));
			}

			serializer.text("\n");
			//serializer.attribute("", "number", String.valueOf(fields.size()));
			for (IField field : ifields) {
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
