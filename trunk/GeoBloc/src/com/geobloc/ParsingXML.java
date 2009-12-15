package com.geobloc;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.geobloc.shared.Utilities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ParsingXML extends Activity {
	
	public static final String FILE_NAME = "filename";
	
	private FormClass formulario;
	
	LinearLayout pageLayout;
	List<FormPage> listPages;
	
	private String filename;
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Aqui debemos conocer el nombre del fichero
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
        	filename = bundle.getString(ParsingXML.FILE_NAME);
        }
        else {
        	Utilities.showToast(getApplicationContext(),
            		"No se ha seleccionado fichero",
                    Toast.LENGTH_SHORT);
        	finish();
        }
		
        /* Create a new TextView to display the parsing result later. */
        TextView tv = new TextView(getApplicationContext());
        
        //LinearLayout linear = new LinearLayout (getApplicationContext());
		
		setContentView(R.layout.question_2_button);
		setTitle(getString(R.string.app_name)+ " > Parseado ");
        
        try {

        	/* Creamos un SAXParser. */
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            /* Creamos un nuevo ContentHandler */
            XMLHandler myXMLHandler = new XMLHandler();
            Context contexto = getApplicationContext();
            myXMLHandler.Initialize(contexto);
            
            sp.parse(new File(filename), myXMLHandler);
            /* Acaba el tratamiento. */
            
            // Obtenemos la lista de Paginas del formulario
            listPages = myXMLHandler.getListPages();

     //       linear = (LinearLayout) findViewById (R.id.LinearLayout02);
            
            /* Obtenemos el layout generado por el Handler */
     //       linear = myXMLHandler.getParsedData(contexto);
        	
            //setContentView(R.layout.question_2_button);  
            //setContentView(linear);
                   Utilities.showToast(getApplicationContext(),
        					"El formulario tiene "+listPages.size()+" paginas",
       				Toast.LENGTH_SHORT);
            
        } catch (Exception e) {
            /* Mostramos el Error. */
            tv.setText("Error: " + e.getMessage());
            Utilities.showToast(getApplicationContext(),
            		"Error de fichero " + filename + ": "+ e.getMessage(),
                    Toast.LENGTH_SHORT);

        	this.setContentView(tv);
        }

        // Debemos añadir las páginas al formulario para luego imprimirlas
        //printPage (0);

	}
	
	
	/**
	 * Dibuja la página del formulario 
	 * 
	 * @param page Página a mostrar en el formulario
	 */
	private void printPage (FormPage page) {
		try {
			/* Creamos un SAXParser. */
			//SAXParserFactory spf = SAXParserFactory.newInstance();
			//SAXParser sp = spf.newSAXParser();

			/* Creamos un nuevo ContentHandler */
			//PageHandler myPageHandler = new PageHandler();
			//Context contexto = getApplicationContext();
			//myPageHandler.Initialize(contexto);
        
			//sp.parse( new InputSource( new StringReader(page.getCodeXML())), myPageHandler);
			/* Acaba el tratamiento. */
			
			Utilities.showToast(getApplicationContext(),
	        		"Ahora mostramos la página",
	                Toast.LENGTH_SHORT);
			
			 /* Create a new TextView to display the parsing result later. */
	        //TextView tv = new TextView(getApplicationContext());
	        //tv.setText(page.getCodeXML());
	        //this.setContentView(tv);
	        
			//pageLayout = (LinearLayout) findViewById (R.id.LinearLayout02);
			//pageLayout = myPageHandler.getParsedData(contexto);
			
		} catch (Exception e) {
			/* Mostramos el Error. */
			Utilities.showToast(getApplicationContext(),
        		"Error de fichero " + filename + ": "+ e.getMessage(),
                Toast.LENGTH_SHORT);
		}
	}
	
	/**
	 * Dibuja la página del formulario 
	 * @param index Índice de la página a mostrar en el formulario
	 */
	private void printPage (int index) {
		if (index < formulario.getNumPages()) {
			printPage (formulario.getPage(index));
		}
		else {
			// Error
		}
	}

}
