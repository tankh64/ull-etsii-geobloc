/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package formdesigner;

import org.jdom.*;
import org.jdom.output.*;

/**
 *
 * @author Usuario
 */
public class Form {

    // Basic properties
    private String name;
    private String outputFile;

    // The JDOM Form
    private Document form;
    private Element gbForm;

    // Form pages management
    private Element actualPage;
    private int pagesCounter;

    public Form(String formName) {
        this.name = formName;
        form = new Document(gbForm = new Element("gb_form"));
        gbForm.addContent(new Element("gb_name").addContent(name));
        gbForm.addContent(actualPage = new Element("gb_page"));
        pagesCounter = 1;
    }

    public void addLabel(String labelText) {
        actualPage.addContent(new Element("gb_label").addContent(labelText));
    }

    public void addButton(String buttonText) {
        actualPage.addContent(new Element("gb_button").addContent(buttonText));
    }

    public void writeOutputFile() {
        try {
            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
            out.output(form, System.out);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
