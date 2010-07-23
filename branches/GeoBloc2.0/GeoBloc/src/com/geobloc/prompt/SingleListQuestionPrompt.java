package com.geobloc.prompt;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import android.util.Log;

import com.geobloc.handlers.AttributeTag;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.QuestionType;

public class SingleListQuestionPrompt extends QuestionPrompt {
	
	private static final String TAG = "ListQuestionPrompt";
	
	private String title;
	private Attributes atts;
	
	/** items from the list */
	private List<ItemList> listItem;
	/** List Item selected */
	private int selected;
	
	// Deberá tener la lista de opciones ...
	
	public SingleListQuestionPrompt(String name, AttributeTag att) {
		
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
		type = QuestionType.GB_SINGLE_LIST;
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
	public void addItemToList (String label, String value, String idItem) {
		ItemList item = new ItemList (label, value, idItem);
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

	@Override
	public Object getAnswer() {
		if (selected != -1) 
			return listItem.get(selected);
		Log.i(TAG, "No existe ninguno del singleList seleccionado");
		return null;
	}

	/**
	 * Answer contiene el "ItemList" seleccionado
	 */
	@Override
	public void setAnswer(Object answer) {
		ItemList item = (ItemList)answer;
		String id = item.getId();
		if ((id == null) || (id.length() == 0)) {
			Log.e(TAG, "Id of sigleList NULL or empty");
			selected = -1;
			return;
		}
		for (int i=0; i<listItem.size(); i++) {
			if (id.equalsIgnoreCase(listItem.get(i).getId())) {
				selected = i;
				return;
			}
		}
		selected = -1;	// Ninguno seleccionado
		return;
	}

}
