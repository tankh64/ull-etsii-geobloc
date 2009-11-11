/**
 * 
 */
package dh.anddev.xml;

/**
 * Class which implements ITextField. Used by XMLParsers or XMLWriters
 * to store a simple String value such as a name, description, etc.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class FormTextField implements ITextField {
	
	private String name;
	private String contents;

	public FormTextField(){
		name = "No name";
		contents = "Empty.";
	}
	
	public FormTextField(String name, String contents) {
		setFieldName(name);
		setFieldValue(contents);
	}
	
	/* (non-Javadoc)
	 * @see dh.android.xml.ITextField#getFieldName()
	 */
	@Override
	public String getFieldName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see dh.android.xml.ITextField#getFieldValue()
	 */
	@Override
	public String getFieldValue() {
		return contents;
	}

	/* (non-Javadoc)
	 * @see dh.android.xml.ITextField#setFieldName(java.lang.String)
	 */
	@Override
	public void setFieldName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see dh.android.xml.ITextField#setFieldValue(java.lang.String)
	 */
	@Override
	public void setFieldValue(String value) {
		this.contents = value;
	}

}
