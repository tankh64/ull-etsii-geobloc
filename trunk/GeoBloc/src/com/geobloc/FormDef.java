package com.geobloc;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;


/**
 * Clase que representará una definición de formulario
 * 
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public class FormDef {

	private File defFile;	// Definition Form
	
	/** Proveedor de intérpretes */
	SAXParserFactory factory;
	/** Objeto Parser */
	SAXParser parser;
	 

	public FormDef (String file) {
		defFile = new File(file);
		
		try {
			factory = SAXParserFactory.newInstance();
			parser = factory.newSAXParser();
			
			parser.parse (defFile, new DefaultHandler());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

