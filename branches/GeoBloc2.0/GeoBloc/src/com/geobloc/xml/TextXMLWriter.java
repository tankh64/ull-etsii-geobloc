/**
 * 
 */
package com.geobloc.xml;

import java.io.StringWriter;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

/**
 * Simple class which builds an XML string from a list of ITextFields which can be saved 
 * to a file.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class TextXMLWriter implements IXMLWriter {

	//@Override
	public String WriteXML(List<IField> fields) {
		XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        serializer.text("\n");
	        /*
	        try {
	        	serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", '\t');
	        }
	        catch (IllegalStateException e) {
	        	Log.e("IllegalStateException", "Could not recognise serializer property");
	        }
	        */
	        //serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true); 
	              
	        for (IField field: fields){
	        	field.toXML(serializer);
	        }
	        serializer.endDocument();
	        serializer.flush();
	        return writer.toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    } 

	}

}
