package com.geobloc.prompt;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import android.util.Log;

import com.geobloc.handlers.AttributeTag;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.QuestionType;

public class ListQuestionPrompt extends QuestionPrompt {
	
	private static final String TAG = "ListQuestionPrompt";
	
	private String title;
	private Attributes atts;
	
	/** items from the list */
	private List<ItemList> listItem;
	
	// Deberá tener la lista de opciones ...
	
	public ListQuestionPrompt(String name, AttributeTag att) {
		
		Log.i(TAG, "Constructor"+TAG);
		// ID
		if (att.attMap.containsKey(Utilities.ATTR_ID)) {
			this.setQuestionId(att.attMap.get(Utilities.ATTR_ID));
		} else {
			Log.e(TAG, "<"+name+"> has not ID");
		}
		
		if (att.isRequired())
			this.setRequired();
			
		this.setQuestionTitle(name);
		this.setType();
		
		listItem = new ArrayList();
	}


	@Override
	public void setType () {
		type = QuestionType.GB_LIST;
	}
	
	/**
	 * Sets the text on the label
	 * @param name
	 */
	public void setQuestionTitle (String name) {
		title = name;
	}
	
	public String getQuestionTitle () {
		return title;
	}
	
	/**
	 * Adds the pair (label, value), an object item to the list.
	 * @param label
	 * @param value
	 */
	public void addItemToList (String label, String value) {
		ItemList item = new ItemList (label, value);
		listItem.add(item);
	}
	/**
	 * Add an item to the list.
	 * @param item
	 */
	public void addItemToList (ItemList item) {
		listItem.add(item);
	}
	
	public int getSizeOfList () {
		return listItem.size();
	}
	
	public ItemList getItem (int pos) {
		return listItem.get(pos);
	}

}
