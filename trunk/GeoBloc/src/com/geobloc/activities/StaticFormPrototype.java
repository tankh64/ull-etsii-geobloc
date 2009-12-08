package com.geobloc.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geobloc.R;
import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.xml.FormTextField;
import com.geobloc.xml.ITextField;
import com.geobloc.xml.TextXMLWriter;

public class StaticFormPrototype extends Activity {
	
	private EditText numForm;
	private EditText inspector;
	private EditText numVisita;
	private EditText observaciones;
	private Button enviar;
	
	private String packageName;
	private GeoBlocPackageManager formPackage;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.static_form_prototype);
        
        initialConfig();
    }
    
    private void initialConfig() {
    	numForm = (EditText) findViewById(R.id.EditText01);
    	inspector = (EditText) findViewById(R.id.EditText02);
    	numVisita = (EditText) findViewById(R.id.EditText03);
    	observaciones = (EditText) findViewById(R.id.EditText04);
    	enviar = (Button) findViewById(R.id.Button04);
    	
    	// build package
    	Calendar cal = Calendar.getInstance();
    	packageName = "geobloc_pk_" + cal.get(Calendar.DATE) + "-" 
    		+ (cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.YEAR) 
			+ "_" + cal.get(Calendar.HOUR_OF_DAY) + "-" + cal.get(Calendar.MINUTE) 
			+ "-" + cal.get(Calendar.SECOND)+"/";
    	formPackage = new GeoBlocPackageManager(packageName);
    	if (!formPackage.OK()) {
    		enviar.setEnabled(false);
    		CharSequence toastText;
    		toastText = "Error! Could not build package";
    		int duration = Toast.LENGTH_SHORT;
    		Toast toast = Toast.makeText(getApplicationContext(), toastText, duration);
    		toast.show();
    	}
    }
    
    public void enviarOnClickHandler (View target) {
    	//XMLExample1();
    	if (formPackage.OK()) {
    		packAndSend();
    	}
    }
    
    private List<ITextField> getFields() {
    	List<ITextField> fields = new ArrayList<ITextField>();
    	FormTextField field;
    	
    	// numForm
    	field = new FormTextField();
    	field.setFieldName("numForm");
    	field.setFieldValue(numForm.getText().toString());
    	fields.add(field);
    	// inspector
    	field = new FormTextField();
    	field.setFieldName("inspector");
    	field.setFieldValue(inspector.getText().toString());
    	fields.add(field);
    	// numVisita
    	field = new FormTextField();
    	field.setFieldName("numVisita");
    	field.setFieldValue(numVisita.getText().toString());
    	fields.add(field);
    	// observaciones
    	field = new FormTextField();
    	field.setFieldName("observaciones");
    	field.setFieldValue(observaciones.getText().toString());
    	fields.add(field);
    	
    	return fields;
    }
    
    private void XMLExample1() {
    	
    	TextXMLWriter writer = new TextXMLWriter();
    	String xml = writer.WriteXML(this.getFields());
    	
    	//TextForm tf = new TextForm();
    	//tf.setFields(this.getFields());
    	
    	//String xml = XStreamXMLWriter.WriteXML(tf);
    	//String xml = tf.ToXML();
    	
    	Intent i = new Intent(this, NewTextReader.class);
    	i.putExtra(NewTextReader.__TEXT_READER_TEXT__, xml);
    	startActivity(i);
    	
    }
    
    private void packAndSend() {
    	TextXMLWriter writer = new TextXMLWriter();
    	String xml = writer.WriteXML(this.getFields());
    	
    	// add form.xml
    	boolean xmlOk = formPackage.addFile(GeoBlocPackageManager.__DEFAULT_FORM_FILENAME__, xml);
    	
    	Intent i = new Intent(this, NewTextReader.class);
    	i.putExtra(NewTextReader.__TEXT_READER_TEXT__, xml);
    	i.putExtra(NewTextReader.__TEXT_READER_PACKAGE_LOCATION__, formPackage.getPackageFullpath());
    	startActivity(i);    	
    }
    
}

