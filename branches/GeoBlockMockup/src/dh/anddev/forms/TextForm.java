/**
 * 
 */
package dh.anddev.forms;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import dh.anddev.xml.ITextField;

/**
 * A simple text form made up of TextFields for use as XStream example.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class TextForm {
	
	private List<ITextField> textFields;
	
	public TextForm() {
		textFields = new ArrayList<ITextField>();
	}
	
	public void addField(ITextField field) {
		textFields.add(field);
	}
	
	public void removeField(ITextField field) {
		textFields.remove(field);
	}
	
	public int numberOfFields() {
		return textFields.size();
	}
	
	public void setFields (List<ITextField> fields) {
		textFields = null; // destroy previous fields
		textFields = fields;
	}
	
	public List<ITextField> getFields() {
		return textFields;
	}
	
	@Override
	public String toString() {
		return "TextForm";
	}
	
	public String ToXML() {
		String xml = "";
		XStream xStream = new XStream();
		xStream.alias("textForm", this.getClass());
		xStream.alias("TextFormField", dh.anddev.xml.FormTextField.class);
		xml = xStream.toXML(this);
		return xml;
	}
}
