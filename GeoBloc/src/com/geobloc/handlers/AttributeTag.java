package com.geobloc.handlers;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import android.util.Log;

import com.geobloc.shared.Utilities;

public class AttributeTag {
	
	private static String TAG = "AttributeTag";

	public Map<String, String> attMap;
	
	public AttributeTag (Attributes atts) {
		attMap = new HashMap<String, String>();
		
		for (int i=0;i<atts.getLength(); i++) {
			attMap.put(atts.getLocalName(i), atts.getValue(i));
			Log.v(TAG, "["+atts.getLocalName(i)+"] -> "+atts.getValue(i));
		}
	}
	
	public void put (String key, String value) {
		attMap.put(key, value);
	}
	
	public boolean isRequired () {
		Log.v(TAG, "isRequired()");
		if (attMap.containsKey((Utilities.ATTR_IS_REQUIRED))) {
			Log.v(TAG, "Map -> "+attMap.get(Utilities.ATTR_IS_REQUIRED));
			return (attMap.get(Utilities.ATTR_IS_REQUIRED).equalsIgnoreCase("true"));
		}
		return false;
	}
}
