/**
 * 
 */
package com.geobloc.xml;

import org.xmlpull.v1.XmlSerializer;

/**
 * Interface for SimpleXMLParser fields
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public interface ITextField extends IField {
	
	public String getFieldName();
	public String getFieldValue();
	public void setFieldName(String name);
	public void setFieldValue(String value);
	
	public void toXML(XmlSerializer serializer);

}
