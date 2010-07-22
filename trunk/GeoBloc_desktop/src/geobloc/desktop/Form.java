/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package geobloc.desktop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

/**
 *
 * @author Usuario
 */
public class Form {

    // Basic properties
    private File outputFile;

    // The JDOM Form
    private Document form;
    private Element gbForm;
    private Element gbName;
    private Element gbVersion;
    private Element gbDate;
    private Element gbFormDescription;

    // Form pages management
    private Element actualPage;
    private Vector<Element> pagesList;

    // No argument constructor
    public Form() {
        pagesList = new Vector<Element>();
    }

    // Create an empty new form
    public Form(String formName, String formDescription) {
        // Create the root element and the document
        form = new Document(gbForm = new Element("gb_form"));
        // Create the name element
        gbForm.addContent(gbName = new Element("gb_name").addContent(formName));
        // Create the version element
        gbForm.addContent(gbVersion = new Element("gb_version").addContent("1"));
        // Create the date element
        gbDate = new Element("gb_date");
        gbDate.addContent(new Element("gb_dateDay"));
        gbDate.addContent(new Element("gb_dateMonth"));
        gbDate.addContent(new Element("gb_dateYear"));
        gbForm.addContent(gbDate);
        // Create the description element
        gbForm.addContent(gbFormDescription = new Element("gb_formDescription").addContent(formDescription));
        // Stablish the DTD
        form.setDocType(new DocType("gb_form","http://ull-etsii-geobloc.googlecode.com/files/GeoBlocFormsDTDv1.4.dtd"));

        // Initiate the page management system and set no output file path
        pagesList = new Vector<Element>();
        outputFile = null;
    }

    // Create a form object from a previously saved outputed form
    public boolean buildFromFile(File formFile) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder(true);
        form = saxBuilder.build(formFile);
        init();
        outputFile = formFile;
        return true;
    }
    
    public boolean buildFromStream(InputStream input) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder(true);
        form = saxBuilder.build(input);
        init();
        return true;
    }

    public void init() {
        // Bind the first level elements
        gbForm = form.getRootElement();
        gbName = gbForm.getChild("gb_name");
        gbVersion = gbForm.getChild("gb_version");
        gbDate = gbForm.getChild("gb_date");
        gbFormDescription = gbForm.getChild("gb_formDescription");
        
        // Create the list of pages and bind the first page as the actual
        List<Element> children = gbForm.getChildren();
        pagesList = new Vector<Element>(children.subList(4, children.size()));
        actualPage = pagesList.firstElement();
    }

    public String getName() {
        return gbName.getText();
    }

    public void setName(String newName) {
        gbName.setText(newName);
    }

    public String getVersion() {
        return gbVersion.getText();
    }

    public void setVersion(String newVersion) {
        gbVersion.setText(newVersion);
    }

    public Calendar getDate() {
        Calendar date = Calendar.getInstance();
        date.set(Integer.parseInt(gbDate.getChildText("gb_dateYear")), Integer.parseInt(gbDate.getChildText("gb_dateMonth"))-1, Integer.parseInt(gbDate.getChildText("gb_dateDay")));
        return date;
    }

    public void setDate(Vector<String> parameters) {
        gbDate.getChild("gb_dateDay").setText(parameters.get(0));
        gbDate.getChild("gb_dateMonth").setText(parameters.get(1));
        gbDate.getChild("gb_dateYear").setText(parameters.get(2));
    }

    public String getFormDescription() {
        return gbFormDescription.getText();
    }

    public void addPage(Vector<String> parameters) {
        // Create the page element depending on its type
        if (parameters.get(2).equals("data"))
            actualPage = new Element("gb_dataPage");
        else if (parameters.get(2).equals("photo"))
            actualPage = new Element("gb_photoPage");
        else if (parameters.get(2).equals("video"))
            actualPage = new Element("gb_videoPage");
        else if (parameters.get(2).equals("audio"))
            actualPage = new Element("gb_audioPage");
        else if (parameters.get(2).equals("location"))
            actualPage = new Element("gb_locationPage");
        else {
            System.out.println("Unknown page type");
            return;
        }
        actualPage.setAttribute("id",parameters.get(0));
        actualPage.addContent(new Element("gb_pageName").addContent(parameters.get(1)));
        if (!parameters.get(2).equals("data"))
            actualPage.setAttribute(new Attribute("isRequired",parameters.get(3)));
        gbForm.addContent(actualPage);
        pagesList.add(actualPage);
    }

    public void setActualPage(int pageIndex) {
        actualPage = pagesList.get(pageIndex);
    }

    public void removePage(int pageIndex) {
        gbForm.removeContent(actualPage);
        pagesList.remove(actualPage);        
    }

    // Creates a new GeoBlocElement and adds the ID and the description
    private Element createGBElement(String elementKind, String id, String description) {
        Element gbElement = new Element(elementKind);
        gbElement.setAttribute("id",id);
        if (description != null)
            gbElement.addContent(new Element("gb_description").addContent(description));
        return gbElement;
    }

    public void addLabel(Vector<String> parameters) {
        // Create the label element
        Element gbLabel = createGBElement("gb_label",parameters.get(0),parameters.get(1));
        // Add the label text
        gbLabel.addContent(new Element("gb_labelText").addContent(parameters.get(2)));
        actualPage.addContent(gbLabel);
    }

    void addInputField(Vector<String> parameters) {
        // Create the field element
        Element gbField = createGBElement("gb_field",parameters.get(0),parameters.get(1));
        // Add the field label
        gbField.addContent(new Element("gb_fieldLabel").addContent(parameters.get(2)));
        // If there's a default field value, add it
        if (!parameters.get(3).isEmpty())
            gbField.addContent(new Element("gb_fieldDefaultValue").addContent(parameters.get(3)));
        // Set the type attribute
        gbField.setAttribute(new Attribute("type",parameters.get(4)));
        // Set the lines number attribute
        gbField.setAttribute(new Attribute("linesNumber",parameters.get(5)));
        // Set the required attribute
        gbField.setAttribute(new Attribute("isRequired",parameters.get(6)));
        actualPage.addContent(gbField);
    }

    void addList(Vector<String> parameters) {
         // Create the list element
        Element gbList = createGBElement("gb_list",parameters.get(0),parameters.get(1));
        // Add the list label
        gbList.addContent(new Element("gb_listLabel").addContent(parameters.get(2)));
        // Set the type attribute
        gbList.setAttribute(new Attribute("type",parameters.get(3)));
        // Set the required attribute
        gbList.setAttribute(new Attribute("isRequired",parameters.get(4)));
        // Add the list elements
        for (int i = 5; i < parameters.size(); i+=2) {
            Element gbListItem = new Element("gb_listItem");
            gbListItem.addContent(new Element("gb_listItemLabel").addContent(parameters.get(i)));
            gbListItem.addContent(new Element("gb_listItemValue").addContent(parameters.get(i+1)));
            gbList.addContent(gbListItem);
        }
        actualPage.addContent(gbList);
    }

    public String printOutputFile() {
        StringWriter output = new StringWriter();
        try {
            // Write the form on the system console
            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
            out.output(form, output);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return output.toString();
    }

    public void writeOutputFile() {
        try {
            XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
            xmlOutput.output(form, new FileOutputStream(outputFile));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeOutputFile(File saveFile) {
        outputFile = saveFile;
        writeOutputFile();
    }

    public File getOutputFile() {
        return outputFile;
    }

    public Iterator<Element> getPagesIterator() {
        return pagesList.listIterator();
    }

    public Iterator<Element> getElementsIterator(Element page) {
        return page.getChildren().listIterator();
    }

    // Returns a vector containing the parameters needed for drawing each element
    public static Vector<String> getElementParameters(Element element) {
        Vector<String> parameters = new Vector<String>();
        String elementName = element.getName();
        // First parameter is the ID
        parameters.add(element.getAttributeValue("id"));
        // Then goes the description
        if (element.getChild("gb_description") != null)
            parameters.add(element.getChildText("gb_description"));
        else
            parameters.add("");
        // Add the rest of the parameters depending on the element type
        if (elementName.equals("gb_label")) {
            parameters.add(element.getChildText("gb_labelText"));
        } else if (elementName.equals("gb_field")) {
            parameters.add(element.getChildText("gb_fieldLabel"));
            parameters.add(element.getChildText("gb_fieldDefaultValue"));
            parameters.add(element.getAttributeValue("type"));
            parameters.add(element.getAttributeValue("linesNumber"));
            parameters.add(element.getAttributeValue("isRequired"));
        } else if (elementName.equals("gb_list")) {
            parameters.add(element.getChildText("gb_listLabel"));
            parameters.add(element.getAttributeValue("type"));
            parameters.add(element.getAttributeValue("isRequired"));
            Iterator<Element> itemsIterator = element.getChildren("gb_listItem").iterator();
            while (itemsIterator.hasNext()) {
                Element item = itemsIterator.next();
                parameters.add(item.getChildText("gb_listItemLabel"));
                parameters.add(item.getChildText("gb_listItemValue"));
            }
        } else {
            System.out.println("ERROR! no such element: "+elementName);
        }
        return parameters;
    }

}
