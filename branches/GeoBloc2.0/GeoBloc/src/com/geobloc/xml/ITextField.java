/**
 * 
 */
package com.geobloc.xml;

import org.xmlpull.v1.XmlSerializer;

/**
 * Interface for SimpleXMLParser fields
 * 
 * REBUILT for the new XML interface
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public interface ITextField extends IField {
	
	public String getTagi(int i);
	public void setTagi(String tag, int i);
	public String getFieldi(int i);
	public void setFieldi(String field, int i);
	
	public void toXML(XmlSerializer serializer);

}
