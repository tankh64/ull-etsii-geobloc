package com.geobloc.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geobloc.R;
import com.geobloc.db.DbFormInstance;
import com.geobloc.db.DbFormInstanceSQLiteHelper;
import com.geobloc.persistance.GeoBlocPackageManager;
import com.geobloc.persistance.GeoBlocPackageManifestBuilder;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.shared.Utilities;
import com.geobloc.xml.FormTextField;
import com.geobloc.xml.IField;
import com.geobloc.xml.IXMLWriter;
import com.geobloc.xml.TextMultiField;
import com.geobloc.xml.TextXMLWriter;

public class StaticFormPrototype extends Activity {
	
	private EditText numForm;
	private EditText inspector;
	private EditText numVisita;
	private EditText observaciones;
	private Button enviar;
	
	private String packageName;
	private GeoBlocPackageManager formPackage;
	private GeoBlocPackageManifestBuilder manifestBuilder;
	
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
    	packageName = Utilities.buildPackageName(this, "static");
    	formPackage = new GeoBlocPackageManager(getApplicationContext(), packageName);
    	
    	// build manifest
    	manifestBuilder = new GeoBlocPackageManifestBuilder(packageName, "GeoBloc Package", 
    			"static/1", "es", "application/octet", (new Date()).toLocaleString());
    	
    	if (!formPackage.OK()) {
    		enviar.setEnabled(false);
    		Utilities.showToast(getApplicationContext(), "Error! Could not build package", Toast.LENGTH_SHORT);
    	}
    	else {
    		// we add the default form file
    		manifestBuilder.addFile(GBSharedPreferences.__DEFAULT_FORM_FILENAME__, "text/xml");
    	}
    }
    
    public void enviarOnClickHandler (View target) {
    	//XMLExample1();
    	if (formPackage.OK()) {
    		packAndSend();
    	}
    }
    
    private List<IField> getFields() {
    	List<IField> myFields = new ArrayList<IField>();
    	FormTextField field;
    	TextMultiField fields = new TextMultiField("fields", "field1", "field2", "field1Content", "field2Content");
    	
    	// numForm
    	field = new FormTextField("field", "field1", "field2", "numForm", numForm.getText().toString());
    	fields.addField(field);
    	// inspector
    	field = new FormTextField("field", "field1", "field2", "inspector", inspector.getText().toString());
    	fields.addField(field);
    	// numVisita
    	field = new FormTextField("field", "field1", "field2", "numVisita", numVisita.getText().toString());
    	fields.addField(field);
    	// observaciones
    	field = new FormTextField("field", "field1", "field2", "observaciones", observaciones.getText().toString());
    	fields.addField(field);
    	
    	
    	// add MultiField
    	myFields.add(fields);
    	return myFields;
    }
    
    private void XMLExample1() {
    	
    	IXMLWriter writer = new TextXMLWriter();
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
    	boolean xmlOk = formPackage.addFile(GBSharedPreferences.__DEFAULT_FORM_FILENAME__, xml);
    	
    	// add manifest.xml
    	String manifestXml = manifestBuilder.toXml();
    	xmlOk = formPackage.addFile(GBSharedPreferences.__DEFAULT_PACKAGE_MANIFEST_FILENAME__, manifestXml);
    	
    	// add form to database (completed)
    	DbFormInstance dbi = new DbFormInstance();
    	dbi.setName(formPackage.getPackageName());
    	dbi.setCreatedDate(new Date());
    	dbi.setPackageLocation(formPackage.getPackageFullpath());
    	dbi.setCompletedDate(new Date());
    	dbi.setCompressedPackageFileLocation(null);
    	dbi.setCompleted(true);
    	
    	SQLiteDatabase db =(new DbFormInstanceSQLiteHelper(getBaseContext())).getWritableDatabase();
    	dbi.save(db);
    	db.close();
    	
    	Intent i = new Intent(this, NewTextReader.class);
    	i.putExtra(NewTextReader.__TEXT_READER_TEXT__, xml);
    	i.putExtra(NewTextReader.__TEXT_READER_PACKAGE_LOCATION__, formPackage.getPackageFullpath());
    	startActivity(i);    	
    }
    
}


