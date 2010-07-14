package com.geobloc.prompt;

import android.util.Log;

import com.geobloc.handlers.AttributeTag;
import com.geobloc.shared.Utilities;

public class ItemList {
	private static final String TAG = "ItemList";
	
	private String label;
	private String value;
	private String id;
	private Boolean required;
	
	public ItemList (String mLabel, String mValue) {
		setLabel(mLabel);
		setValue(mValue);
	}
	
	public ItemList (String mLabel, String mValue, String itemId) {
		if (itemId.equalsIgnoreCase("")) {
			Log.e(TAG, "single list item <"+mLabel+","+mValue+"> has not ID");
		}
		setLabel(mLabel);
		setValue(mValue);
		setId(itemId);
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
	
	public String getId () {
		return id;
	}

	public void setId (String name) {
		id = name;
	}
	
	public void setRequired () {
		required = true;
	}
	
	public void unsetRequired () {
		required = false;
	}
	
	public boolean isRequired () {
		return required;
	}
}
