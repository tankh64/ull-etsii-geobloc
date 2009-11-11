/**
 * 
 */
package dh.anddev.xml;

import com.thoughtworks.xstream.XStream;

/**
 * A wrapper class for the XStream which does not implement IXMLWriter but returns 
 * an XML representation of a Java Object.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class XStreamXMLWriter {
	public static String WriteXML (Object obj) {
		String xml = "";
		XStream xStream = new XStream();
		xStream.alias(obj.toString(), obj.getClass());
		xml = xStream.toXML(obj);
		return xml;
	}
}
