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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ParsingXML extends Activity {
	
	public static final String FILE_NAME = "filename";
	
	private FormClass formulario;
	
	ScrollView  pageLayout;
	List<FormPage> listPages;
	
	private int pageActual;
	
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

        setContentView(R.layout.form_question);
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
            
            formulario = new FormClass ("Formulario", listPages);

            Utilities.showToast(getApplicationContext(),
        				"El formulario tiene "+listPages.size()+" paginas",
        				Toast.LENGTH_SHORT);

         // Establecemos la funcionalidad a los botones (Atrás y Adelante)
            initButtons ();
            pageActual = 0;
            printPage (pageActual);
            
        } catch (Exception e) {
            /* Mostramos el Error. */
            tv.setText("Error: " + e.getMessage());
            Utilities.showToast(getApplicationContext(),
            		"Error de fichero " + filename + ": "+ e.getMessage(),
                    Toast.LENGTH_SHORT);

        	this.setContentView(tv);
        }

        

	}
	
	private void initButtons () {
		
		Button button_back = (Button) findViewById (R.id.Button01);

        button_back.setText(getString(R.string.button_back));
        button_back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if (pageActual > 0) {
            		pageLayout.removeAllViewsInLayout();
            		
            		pageActual--;
            		printPage (pageActual);
            	}
            	else {
            		// Avisar que no existen paginas anteriores
            	}
            }
        });
        
		Button button_next = (Button) findViewById (R.id.Button02);

        button_next.setText(getString(R.string.button_next));
        button_next.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if (pageActual < (formulario.getNumPages()-1)) {
            		pageLayout.removeAllViewsInLayout();
            		
            		pageActual++;
            		printPage (pageActual);
            	}
            	else {
            		// Avisar que no existen paginas anteriores
            	}
            }
        });
	}
	
	/**
	 * Dibuja la página del formulario 
	 * 
	 * @param page Página a mostrar en el formulario
	 */
	private void printPage (FormPage page, int index) {
		try {
			/* Creamos un SAXParser. */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			/* Creamos un nuevo ContentHandler */
			PageHandler myPageHandler = new PageHandler();
			Context contexto = getApplicationContext();
			myPageHandler.Initialize(contexto);
        
			sp.parse( new InputSource( new StringReader(page.getCodeXML())), myPageHandler);
			/* Acaba el tratamiento. */		
			
			pageLayout = (ScrollView) findViewById (R.id.ScrollView01);
			
			LinearLayout linear = myPageHandler.getParsedData(contexto);

			 /* Create a new TextView to display the parsing result later. */
	        TextView tv = new TextView(getApplicationContext());
	        tv.setText(page.getCodeXML());
	        
	        
	        
	        pageLayout.addView(linear);
	        
		} catch (Exception e) {
			/* Mostramos el Error. */
			Utilities.showToast(getApplicationContext(),
        		"Error al parsear PAGINA: "+ index+": "+e.getMessage(),
                Toast.LENGTH_SHORT);
			TextView tv = new TextView(getApplicationContext());
			tv.setText("---"+index+":\n"+page.getCodeXML()+"\n----\n"+e.getMessage());
			setContentView (tv);
		}
	}
	
	
	/**
	 * Dibuja la página del formulario 
	 * @param index Índice de la página a mostrar en el formulario
	 */
	private void printPage (int index) {
		if (index < formulario.getNumPages()) {
			FormPage myPage = formulario.getPage(index);
			printPage (myPage, index);
		}
		else {
			// Error
		}
	}

}
