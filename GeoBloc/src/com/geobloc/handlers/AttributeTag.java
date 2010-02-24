package com.geobloc.handlers;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import com.geobloc.shared.Utilities;

public class AttributeTag {

	public Map<String, String> attMap;
	
	public AttributeTag (Attributes atts) {
		attMap = new HashMap<String, String>();
		
		for (int i=0;i<atts.getLength(); i++) {
			attMap.put(atts.getLocalName(i), atts.getValue(i));
		}
	}
	
	public void put (String key, String value) {
		attMap.put(key, value);
	}
	
	public boolean isRequired () {
		return (attMap.get(Utilities.ATTR_IS_REQUIRED) == "true");
	}
}
