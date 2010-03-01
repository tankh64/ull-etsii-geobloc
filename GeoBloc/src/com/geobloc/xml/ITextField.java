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
	
	public String getFieldA();
	public String getFieldB();
	public void setFieldA(String a);
	public void setFieldB(String b);
	
	public void toXML(XmlSerializer serializer);

}
