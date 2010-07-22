/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * OptionPane.java
 *
 * Created on 26-ene-2010, 17:03:25
 */

package geobloc.desktop.dialogs;

/**
 *
 * @author Usuario
 */
public class OptionPane extends javax.swing.JPanel {

    /** Creates new form OptionPane */
    public OptionPane() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelTextField = new javax.swing.JTextField();
        valueTextField = new javax.swing.JTextField();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(geobloc.desktop.FormDesignerApp.class).getContext().getResourceMap(OptionPane.class);
        labelTextField.setText(resourceMap.getString("labelTextField.text")); // NOI18N
        labelTextField.setName("labelTextField"); // NOI18N

        valueTextField.setText(resourceMap.getString("valueTextField.text")); // NOI18N
        valueTextField.setName("valueTextField"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(labelTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(valueTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(labelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(valueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public String getLabelText() { return labelTextField.getText(); }
    public String getValueText() { return valueTextField.getText(); }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField labelTextField;
    private javax.swing.JTextField valueTextField;
    // End of variables declaration//GEN-END:variables

}
