package com.geobloc.prompt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;

import android.util.Log;

import com.geobloc.handlers.AttributeTag;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.QuestionType;

public class MultipleListQuestionPrompt extends QuestionPrompt {
	
	private static final String TAG = "ListQuestionPrompt";
	
	private String title;
	private Attributes atts;
	
	/** items from the list */
	private List<ItemList> listItem;
	/* Lista de la seleccion de los Items (0,1,2) */
	private ArrayList<Integer> vSelected;
	
	// Deberá tener la lista de opciones ...
	
	public MultipleListQuestionPrompt(String name, AttributeTag att) {
		
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
		vSelected = new ArrayList<Integer>();
	}


	@Override
	public void setType () {
		type = QuestionType.GB_MULTIPLE_LIST;
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
		vSelected.add(0);	// Añadimos como no seleccionado
	}
	
	public int getSizeOfList () {
		return listItem.size();
	}
	
	public ItemList getItem (int pos) {
		return listItem.get(pos);
	}


	@Override
	public Object getAnswer() {
		List<ItemList> selectedItems = new ArrayList<ItemList>();

		/*for (int i=0; i<vSelected.size(); i++) {
			// Si el ID está seleccionado, lo insertamos en la lista de seleccionados
			if (vSelected.get(i) != 0) {
				selectedItems.add(listItem.get(i));
				Log.v(TAG, "Está seleccionado -"+listItem.get(i).getId()+
						"- <"+listItem.get(i).getLabel()+
						">"+listItem.get(i).getValue());
			}
			else {
				Log.v(TAG, "NO Está seleccionado -"+listItem.get(i).getId()+
						"- <"+listItem.get(i).getLabel()+
						">"+listItem.get(i).getValue());
			}
		}*/
		return selectedItems;
	}


	@Override
	public void setAnswer(Object answer) {
		// Answer será una Lista de ItemList
		List<ItemList> lista = (List<ItemList>) answer;
		
		
	}

}
