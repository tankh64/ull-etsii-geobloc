/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package geobloc.desktop;

import geobloc.desktop.dialogs.PhotoPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Vector;
import javax.swing.*;

/**
 *
 * @author Usuario
 */
public class PageViewPane extends JScrollPane {

    // The contents panel
    private JPanel itemsPane;
    private PhotoPanel photoPane;

    // Create the page
    public PageViewPane() {
        super();
        setViewportView(itemsPane = new JPanel());
        itemsPane.setLayout(new BoxLayout(this.itemsPane,BoxLayout.Y_AXIS));
        itemsPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    public PageViewPane(String action) {
        super();
        setViewportView(itemsPane = new PhotoPanel());
        itemsPane.setLayout(new BoxLayout(this.itemsPane,BoxLayout.Y_AXIS));
        itemsPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    // Adds a label to the page
    public void addLabel(Vector<String> parameters) {
        JLabel label = new JLabel(parameters.get(2));
        itemsPane.add(label);
        itemsPane.add(Box.createRigidArea(new Dimension(0,10)));
        itemsPane.revalidate();
        itemsPane.repaint();
    }

    // Adds a button to the page
    public void addButton(Vector<String> parameters) {
        JButton button = new JButton(parameters.get(2));
        itemsPane.add(button);
        itemsPane.add(Box.createRigidArea(new Dimension(0,10)));
        itemsPane.revalidate();
        itemsPane.repaint();
    }

    public void addField(Vector<String> parameters) {
        JLabel fieldLabel = new JLabel(parameters.get(2));
        if (parameters.get(6).equals("true"))
            fieldLabel.setText("(*) "+fieldLabel.getText());
        JTextArea fieldTextArea = new JTextArea();
        fieldTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldTextArea.setRows(Integer.parseInt(parameters.get(5)));
        fieldTextArea.setBorder(BorderFactory.createCompoundBorder(
                   BorderFactory.createLineBorder(Color.black),
                   BorderFactory.createLineBorder(Color.gray)));
        fieldTextArea.setMaximumSize(new Dimension(Short.MAX_VALUE,fieldTextArea.getPreferredSize().height));
        fieldTextArea.setEditable(false);
        itemsPane.add(fieldLabel);
        itemsPane.add(fieldTextArea);
        itemsPane.add(Box.createRigidArea(new Dimension(0,10)));
        itemsPane.revalidate();
        itemsPane.repaint();
    }

    /* LIST PARAMETERS VECTOR
    * parameters[0] - ID
    * parameters[1] - Description
    * parameters[2] - List label
    * parameters[3] - List type
    * parameters[4] - Is required
    * parameters[5,6] - First list option label and value
    * parameters[7,8] - Second list option label and value
    * ...
    */
    void addSingleChoiceList(Vector<String> parameters) {
        JLabel listLabel = new JLabel(parameters.get(2));
        if (parameters.get(3).equals("true"))
            listLabel.setText("(*) "+listLabel.getText());
        itemsPane.add(listLabel);
        ButtonGroup listGroup = new ButtonGroup();
        for (int i = 5; i < parameters.size(); i+=2) {
            JRadioButton radioButton = new JRadioButton(parameters.get(i)+" ["+parameters.get(i+1)+"]");
            listGroup.add(radioButton);
            itemsPane.add(radioButton);
        }
        itemsPane.add(Box.createRigidArea(new Dimension(0,10)));
        itemsPane.revalidate();
        itemsPane.repaint();
    }

    void addMultipleChoiceList(Vector<String> parameters) {
        JLabel listLabel = new JLabel(parameters.get(2));
        if (parameters.get(3).equals("true"))
            listLabel.setText("(*) "+listLabel.getText());
        itemsPane.add(listLabel);
        for (int i = 5; i < parameters.size(); i+=2) {
            JCheckBox checkBox = new JCheckBox(parameters.get(i)+" ["+parameters.get(i+1)+"]");
            itemsPane.add(checkBox);
        }
        itemsPane.add(Box.createRigidArea(new Dimension(0,10)));
        itemsPane.revalidate();
        itemsPane.repaint();
    }

    void addComboBox(Vector<String> parameters) {
        JLabel listLabel = new JLabel(parameters.get(2));
        if (parameters.get(3).equals("true"))
            listLabel.setText("(*) "+listLabel.getText());
        itemsPane.add(listLabel);
        JComboBox comboBox = new JComboBox();
        for (int i = 5; i < parameters.size(); i+=2)
            comboBox.addItem(parameters.get(i)+" ["+parameters.get(i+1)+"]");
        // Stablish left alignment and maximun height
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboBox.setMaximumSize(new Dimension(Short.MAX_VALUE,comboBox.getPreferredSize().height));
        itemsPane.add(comboBox);
        itemsPane.add(Box.createRigidArea(new Dimension(0,10)));
        itemsPane.revalidate();
        itemsPane.repaint();
    }
}
