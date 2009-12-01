package com.geobloc;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Actividad que representará un formulario. Cada formulario cargado
 * será un QuestionActivity.
 * @author Jorge Carballo
 *
 */
public class QuestionActivity extends Activity {
	private final String t = "QuestionActivity";
	
	/** Opciones del menu */
	private static final int MENU_SAVE = Menu.FIRST;
	private static final int MENU_SAVE_AND_SEND = MENU_SAVE+1;
	
	enum TextType{STRING, INT, FLOAT};

	private FormDef formDef;
	private FormHandler formHandler;
	
	private LinearLayout linearLayout;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (savedInstanceState != null) {
            Toast.makeText(getApplicationContext(),
            		"Regreso",
                    Toast.LENGTH_SHORT).show();
            		return;
        }
        
        setContentView(R.layout.question_form);
        setTitle(getString(R.string.app_name)+ " > " + getString(R.string.list_form));
    
        FillForm();
    }
	
	/**
	 * Rellena el formulario (por ahora directamete, sin utilizar ningún archivo)
	 */
	private void FillForm () {
        linearLayout = (LinearLayout) findViewById (R.id.LinearLayout01);
        
        if (linearLayout == null) {
            Toast.makeText(getApplicationContext(),
            		"linearLayout == null",
                    Toast.LENGTH_SHORT).show();
        }
        else {
        	/* Creamos el formulario */ 	
        	AddText("Uno");
        	AddButton("Boton");
        	AddText("Dos");
        	AddEditText(TextType.INT, "Número de Visita de perera");
        	AddEditText(TextType.STRING, "Nombre");
        	AddText("Tres");
        	AddButton("Otro Boton con Mas Texto del que cabe, o eso espero ... a ver q sale");
        }
	}
	
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, QuestionActivity.MENU_SAVE, 0,
				R.string.menu_question_view_save).setIcon(android.R.drawable.ic_menu_save);
		menu.add(0, QuestionActivity.MENU_SAVE_AND_SEND, 0,
				R.string.menu_question_view_save_and_send).setIcon(android.R.drawable.ic_menu_send);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected (int featureId, MenuItem item) {
		CharSequence toastText;
		switch (item.getItemId()) {
		case MENU_SAVE:
			toastText = "Guardado";
			break;
		case MENU_SAVE_AND_SEND:
			toastText = "Gardado y Enviado";
			break;
			default:
				toastText = "No se que hacer";
		}
        Toast.makeText(getApplicationContext(),
        		toastText,
                Toast.LENGTH_SHORT).show();
		return true;
	}
	
	/**
	 * Añade un TextView al formulario
	 * @param texto El texto que se añadirá
	 */
	private void AddText(String texto) {	
        TextView tv = new TextView(getApplicationContext());
        tv.setText(texto);
        tv.setPadding(5, 5, 0, 5);
      		
		linearLayout.addView(tv);
	}
	
	/**
	 * Añade un boton al formulario, y un evento, que al ser pulsado el botón,
	 * mostrará el texto que tiene en pantalla
	 * @param texto El texto que contendrá el botón
	 */
	private void AddButton (String texto) {
		final Button but = new Button(getApplicationContext());
		but.setText(texto);
		but.setPadding(3,3,3,3);
		
        but.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	Toast.makeText(getApplicationContext(),
            			but.getText(),
            			Toast.LENGTH_SHORT).show();
            }
        });
		
		linearLayout.addView(but);
	}
	
	/**
	 * Añade un campo para insertar texto en el formulario
	 * @param Tt Tipo de texto a insertar
	 * @param text Texto que acompañará al EditText
	 */
	private void AddEditText (TextType Tt, String text) {
		
		/* Contendrá el Texto y el EditText */
		LinearLayout mView = new LinearLayout(getApplicationContext());
		//mView.setLayoutParams (new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mView.setPadding(5, 5, 5, 5);
		mView.setOrientation(LinearLayout.HORIZONTAL);
		
		/* Texto antes del EditText */
		TextView Text = new TextView(getApplicationContext());
		Text.setText(text+":  ");
		//Text.setPadding(0, 15, 0, 0);
		Text.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 3));
		//Text.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 3));
		
		EditText ed = new EditText(getApplicationContext());
		ed.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 2));
		//ed.setLayoutParams (new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 2));
		
		switch (Tt) {
			case INT:
				ed.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
				break;
			case FLOAT:
				ed.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
	    				break;
			case STRING:
				ed.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
	    				break;
				default:
					break;
		}
		
		mView.addView(Text);
		mView.addView(ed);
	
		linearLayout.addView(mView);
	}
}
