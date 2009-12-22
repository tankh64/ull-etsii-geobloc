/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package formdesigner;

import javax.swing.*;

/**
 *
 * @author Usuario
 */
public class PageViewPane extends JScrollPane {

    private JList itemsList;

    public PageViewPane() {
        super();
        itemsList = new JList(new DefaultListModel());

    }

    public void addLabel(String labelText) {
        JLabel label = new JLabel(labelText);
        itemsList.add(label);
    }
}
