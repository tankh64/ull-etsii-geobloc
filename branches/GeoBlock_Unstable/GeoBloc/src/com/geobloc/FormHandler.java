package com.geobloc;


/**
 *  Manejador de formularios
 *  
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public class FormHandler {
	
	private FormDef defForm;	// Formulario (ya veremos como hacerlo)
	
	/** Llevará el numero de "campo" por el que va en el formulario */
	private int index;
	
	
	public FormHandler (FormDef form) {
		defForm = form;
		index = 0;
	}
	
	public int getNextData () {
		index++;
		return index;
	}
	
	public int getIndex () {
		return index;
	}

}
