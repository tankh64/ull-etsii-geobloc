/**
 * 
 */
package com.geobloc.xml;

import org.xmlpull.v1.XmlSerializer;

/**
 * Interface for XML file fields; each field should know how to add itself to the XmlSerializer.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public interface IField {
	
	public static String __IFIELD_IDENTATION__ = "   ";
	
	public String getFieldTag();
	public void setFieldTag(String tag);
	public void toXML(XmlSerializer serializer);
}
