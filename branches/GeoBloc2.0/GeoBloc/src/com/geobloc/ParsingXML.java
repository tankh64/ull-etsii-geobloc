package com.geobloc;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.HttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.shared.UploadPackageHelper;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.WidgetType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Most important activity; it renders the parsed file and allows the user to send the package.
 * 
 * @author Jorge Carballo
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */

public class ParsingXML extends Activity {
	
	public static final String FILE_NAME = "filename";
	
	private static final int MENU_SEND = Menu.FIRST;
	
	private FormClass formulario;
	
	ScrollView  pageLayout;
	List<FormPage> listPages;
	
	private int pageActual;
	
	private String filename;
	
	List<Utilities.WidgetType> dataType;
	GeoBlocPackageManager formPackage;
	HttpClient httpClient;
	ApplicationEx app;
	LinearLayout pageToSend;
	UploadPackageHelper uploadHelper;
	
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
            
            //////////////////////////////////////////////////////
            // Para el envío del formulario
			// initialize UploadPackageHelper
			// get httpClient from ApplicationEx
			app = (ApplicationEx)this.getApplication();
			httpClient = app.getHttpClient();
			////////////////////////////////////////////////////////

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
	        
	        
			/**
			 * If this page is the first page of the form, return the data necessary
			 * to send the completed form to the server.
			 */
			if (index == 0) {
				/** NOTA: En la variable "linear" (como un LinearLayout)
				 */
				//dataType = new ArrayList<WidgetType>();
				dataType = myPageHandler.getInfoToSend();
				
				//pageToSend = linear;
				pageToSend = myPageHandler.getPageToSend();
				
				Utilities.showToast(getApplicationContext(),
		        		"Campos = "+dataType.size()+"\n"+pageToSend,
		                Toast.LENGTH_SHORT);
				
				// build package
		    	Calendar cal = Calendar.getInstance();
		    	String date = cal.get(Calendar.DATE) + "-" + (cal.get(Calendar.MONTH)+1) 
					+ "-" + cal.get(Calendar.YEAR);
		    	String packagename = "geobloc_pk_" + date + "_" + cal.get(Calendar.HOUR_OF_DAY) 
		    		+ "-" + cal.get(Calendar.MINUTE) 
					+ "-" + cal.get(Calendar.SECOND)+"/";
				formPackage = new GeoBlocPackageManager(getApplicationContext(), packagename);
				uploadHelper = new UploadPackageHelper(this, httpClient,
						      formPackage,
						      (ViewGroup) linear,
						      dataType);
				
				

			}
			/** Evidentemente ésto no va a ir aqui, es solo un "parche" para mostrarlo
			 * en la gerencia
			 */
	        
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
	
	
	//////////////// MENU
	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, ParsingXML.MENU_SEND, 0,
				R.string.menu_send_form).setIcon(android.R.drawable.ic_menu_upload);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected (int featureId, MenuItem item) {
		CharSequence toastText;
		switch (item.getItemId()) {
		case MENU_SEND:
			toastText = "Guardado";
			try {
				uploadHelper.packAndSend();
			} catch (Exception e) {
				toastText = e.toString();
			}
			break;
		default:
			toastText = "No se que hacer";
		}
        Toast.makeText(getApplicationContext(),
        		toastText,
                Toast.LENGTH_LONG).show();
		return true;
	}

}
