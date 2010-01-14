package com.geobloc;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una página del Formulario cargado.
 * Contiene el string (XML) que generó dicha página y la lista de valores que contienen
 * sus campos.
 * 
 * @author Jorge Carballo
 *
 */
public class FormPage {
	
	private String codeXML = "";
	
	private List<String> clave;		// Nombre del Campo
	private List<String> valor;		// Valor del Campo
	
	public FormPage () {
		clave = new ArrayList<String>();
		valor = new ArrayList<String>();
	}
	
	
	public void setCodeXML (String code) {
		codeXML = code;
	}
	
	/** 
	 * Devuelve el XML desde el cual se genera la página
	 * @return
	 */
	public String getCodeXML () {
		return codeXML;
	}
	
	/**
	 * Añade la clave y el valor de un campo a la lista de campos
	 * @param key Nombre del campo
	 * @param value Valor del campo
	 */
	public void add (String key, String value) {
		clave.add(key);
		valor.add(value);
	}

	/**
	 * 
	 * @return Número de campos de la página
	 */
	public int getNumFields () {
		if (clave.size() == valor.size())
			return clave.size();
		return 0;
	}

}
