/**
 * 
 */
package com.geobloc.xml;

import java.util.List;

/**
 * Interface which specifies a method to take ITextFields and save them
 * as a XML string.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com
 *
 */
public interface IXMLWriter {

	public String WriteXML(List<IField> fields);
}
