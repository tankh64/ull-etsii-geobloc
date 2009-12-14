package com.geobloc;

import java.io.File;
import java.net.URL;

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
        
        LinearLayout linear = new LinearLayout (getApplicationContext());
		
		setContentView(R.layout.question_form);
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

            /* Obtenemos el layout generado por el Handler */
            linear = myXMLHandler.getParsedData(contexto);
            
            Utilities.showToast(getApplicationContext(),
					getString(R.string.parsed_file_ok, filename),
					Toast.LENGTH_SHORT);
        	
            setContentView(linear);
            
        } catch (Exception e) {
            /* Mostramos el Error. */
            tv.setText("Error: " + e.getMessage());
            Utilities.showToast(getApplicationContext(),
            		"Error de fichero " + filename + ": "+ e.getMessage(),
                    Toast.LENGTH_SHORT);

        	this.setContentView(tv);
        }


	}

}
