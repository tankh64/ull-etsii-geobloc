/**
 * 
 */
package com.geobloc.shared;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.geobloc.db.DbFormInstance;
import com.geobloc.form.FormClass;
import com.geobloc.form.FormDataPage;
import com.geobloc.form.FormLocationPage;
import com.geobloc.form.FormPage;
import com.geobloc.form.FormPage.PageType;
import com.geobloc.prompt.ItemList;
import com.geobloc.prompt.QuestionPrompt;
import com.geobloc.shared.Utilities.QuestionType;
import com.geobloc.xml.FormTextField;
import com.geobloc.xml.IField;
import com.geobloc.xml.TextMultiField;
import com.geobloc.xml.TextXMLWriter;

/**
 * Class whose sole responsibility is to transform an {@link IInstanceDefinition} and {@link FormPage} Objects 
 * into an instance xml file.
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public class XMLBuilder {
	private static final String LOG_TAG = "XMLBuilder";
	
	private Context context;
	
	private DbFormInstance dbi;
	private FormClass form;
	private List<IField> myFields;
	TextMultiField instanceFields;
	
	private QuestionPrompt prompt;
	
	public XMLBuilder(Context context) {
		this.context = context;
	}
	
	private void addHeader() {
		if (dbi.getForm_definition() != null)
			instanceFields.addTag("gb_scheme", dbi.getForm_definition().getForm_id());
		else
			instanceFields.addTag("gb_scheme", "notAvailable");
		instanceFields.addTag("gb_device", Utilities.getDeviceID(context));
		instanceFields.addTag("gb_formId", dbi.getInstance_form_id());
		instanceFields.addTag("gb_formVersion", Long.toString(dbi.getInstance_form_version()));
		
		String complete = "";
		if (dbi.isComplete())
			complete = "TRUE";
		else
			complete = "FALSE";
		instanceFields.addTag("gb_complete", complete);
	}
	
	private void processDataPrompt(TextMultiField xml, QuestionPrompt prompt) {
		FormTextField field = null;
		// filter the labels
		if (prompt.getType() != QuestionType.GB_LABEL) {
			// if prompt is not a list
			if ((prompt.getType() != QuestionType.GB_SINGLE_LIST) && (prompt.getType() != QuestionType.GB_MULTIPLE_LIST)) {
				// we set the tag
				if (prompt.getType() == QuestionType.GB_DATAINPUT)
					field = new FormTextField("gb_field");
				if (prompt.getType() == QuestionType.GB_CHECKBOX)
					field = new FormTextField("gb_checkbox");
				if (prompt.getType() == QuestionType.GB_CHECKBOX_THREE)
					field = new FormTextField("gb_checkboxthree");
				// and add the fields; prompt interface makes it very easy
				field.addTag("id", prompt.getQuestionId());
				if (prompt.getAnswer() != null)
					field.addTag("value", (String)prompt.getAnswer());
				else
					field.addTag("value", "");
				// finally, we add it to the XML
				xml.addField(field);
			}
			else {
				if (prompt.getType() == QuestionType.GB_SINGLE_LIST) {
					field = new FormTextField("gb_singleChoiceList");
					field.addTag("id", prompt.getQuestionId());
					ItemList answer = (ItemList) prompt.getAnswer();
					field.addTag("selected_value", answer.getId());
					xml.addField(field);
				}
				else {
					// it's a multpile choice list
					TextMultiField multiField = new TextMultiField("gb_multipleChoiceList");
					multiField.addTag("id", prompt.getQuestionId());
					// inside the list the are multiple values
					TextMultiField valuesField = new TextMultiField("selected_values");
					multiField.addField(valuesField);
					List<ItemList> answer = (List<ItemList>) prompt.getAnswer();
					// for every value, we add a field
					for (ItemList item : answer) {
						field = new FormTextField("gb_listItem");
						field.addTag("id", item.getId());
						field.addTag("value", item.getValue());
						valuesField.addField(field);
					}
					// and then we add all
					xml.addField(multiField);
				}
			}
			
		}
	}
	
	private void processFormDataPage(FormDataPage page) {
		TextMultiField xml = new TextMultiField("gb_dataPage");		
		for (int i = 0; i < page.getNumQuestions(); i++) {
			prompt = page.getQuestion(i);
			processDataPrompt(xml, prompt);
		}
		instanceFields.addField(xml);
	}
	
	private void processFormBinaryPage(FormPage page) {
		FormTextField xml = null;
		if (page.getPageType() == PageType.PHOTO)
			xml = new FormTextField("gb_photoPage");
		if (page.getPageType() == PageType.VIDEO)
			xml = new FormTextField("gb_videoPage");
		if (page.getPageType() == PageType.AUDIO)
			xml = new FormTextField("gb_audioPage");
		// set id in xml
		// set binary in xml
		instanceFields.addField(xml);
	}
	
	private void processFormLocationPage(FormLocationPage page) {
		FormLocationPage locationPage = (FormLocationPage) page;
		TextMultiField xml = new TextMultiField("gb_locationPage");
		FormTextField text = new FormTextField("gb_location");
		//text.addTag("longitude", locationPage.getLongitude());
		//text.addTag("latitude", locationPage.getLatitude());
		instanceFields.addField(xml);
	}
	
	public String transformToXML(IInstanceDefinition instance, FormClass form) {
		dbi = (DbFormInstance) instance;
		this.form = form;
		// build XML file
		myFields = new ArrayList<IField>();
		instanceFields = new TextMultiField("gb_instance");
		
		addHeader();
		FormPage page;
		for (int i = 0; i < form.getNumPages(); i++) {
			page = form.getPage(i);
			if (page.getPageType() == PageType.DATA) {
				processFormDataPage((FormDataPage)page);
			}
			if ((page.getPageType() == PageType.AUDIO) || (page.getPageType() == PageType.PHOTO) || 
					(page.getPageType() == PageType.VIDEO)) {
				processFormBinaryPage(page);
			}
			if (page.getPageType() == PageType.LOCATION) {
				processFormLocationPage((FormLocationPage)page);
			}
		}
		myFields.add(instanceFields);
		
		// final transformation
		TextXMLWriter writer = new TextXMLWriter();
    	return writer.WriteXML(myFields);
	}
}
