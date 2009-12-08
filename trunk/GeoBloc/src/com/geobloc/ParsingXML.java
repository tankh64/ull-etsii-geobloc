package com.geobloc;

import java.io.File;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ParsingXML extends Activity {
	
	public static final String FILE_NAME = "filename";
	
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
        	Toast.makeText(getApplicationContext(),
            		"No se ha seleccionado fichero",
                    Toast.LENGTH_SHORT).show();
        	finish();
        }
		
		
        /* Create a new TextView to display the parsing result later. */
        TextView tv = new TextView(this);
        
        LinearLayout linear = new LinearLayout (getApplicationContext());
		
		setContentView(R.layout.question_form);
		setTitle(getString(R.string.app_name)+ " > Parseado ");
        
        try {

        	/* Creamos un SAXParser. */
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            /* Cogemos el XMLReader del SAXParser que hemos creado. */
            XMLReader xr = sp.getXMLReader();
            /* Creamos un nuevo ContentHandler y lo aplicamos al XML-Reader*/
            XMLHandler myXMLHandler = new XMLHandler();
            Context contexto = getApplicationContext();
            myXMLHandler.Initialize(contexto);
            xr.setContentHandler(myXMLHandler);
            
            sp.parse(new File(filename), myXMLHandler);
            /* Acaba el tratamiento. */

            /* Ponemos los datos por pantalla con el XMLHandler. */
            linear = myXMLHandler.getParsedData(contexto);
            
			Toast.makeText(getApplicationContext(),
			getString(R.string.parsed_file_ok, filename),
            Toast.LENGTH_SHORT).show();
        	
            setContentView(linear);
            
        } catch (Exception e) {
            /* Mostramos el Error. */
            tv.setText("Error: " + e.getMessage());
        	Toast.makeText(getApplicationContext(),
            		"Error de fichero " + filename + ": "+ e.getMessage(),
                    Toast.LENGTH_SHORT).show();

        	this.setContentView(tv);
        }


	}

}
