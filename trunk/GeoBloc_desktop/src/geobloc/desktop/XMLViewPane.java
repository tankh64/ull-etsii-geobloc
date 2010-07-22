/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package geobloc.desktop;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

/**
 *
 * @author Usuario
 */
public class XMLViewPane extends JScrollPane {
    // The contents panel
    private JEditorPane editorPane;

    // Create the page
    public XMLViewPane() {
        super();
        setViewportView(editorPane = new JEditorPane());
    }

    public void setText(String text) {
        editorPane.setText(text);
    }
}
