package com.geobloc.prompt;

public class ItemList {
	
	private String label;
	private String value;
	
	ItemList (String mLabel, String mValue) {
		setLabel(mLabel);
		setValue(mValue);
	}

	private void setLabel (String mLabel) {
		label = mLabel;
	}
	private void setValue (String mValue) {
		value = mValue;
	}
	public String getLabel () {
		return label;
	}
	public String getValue () {
		return value;
	}
	public int getIntValue () {
		return Integer.parseInt(value);
	}
}
