/*
 * FormDesignerView.java
 */

package geobloc.desktop;

import geobloc.desktop.dialogs.ListDialogPane;
import geobloc.desktop.dialogs.FieldDialogPane;
import com.toedter.calendar.JDateChooser;
import geobloc.desktop.dialogs.ImportFormDialogPane;
import geobloc.desktop.dialogs.NewFormDialogPane;
import geobloc.desktop.dialogs.LabelDialogPane;
import geobloc.desktop.dialogs.PageDialogPane;
import geobloc.desktop.dialogs.PhotoPanel;
import java.io.IOException;
import java.net.URISyntaxException;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jdom.JDOMException;
import org.json.*;
/**
 * The application's main frame.
 */
public class FormDesignerView extends FrameView {

    public FormDesignerView(SingleFrameApplication app) {
        super(app);

        initComponents();

        /** Start of our inizialization tasks **/

        // At the begining hide the design and xml view areas
        designPanel.setVisible(false);
        xmlFormViewPanel.setVisible(false);

        // Disable resizing of the frame and instanciate the file chooser
        this.getFrame().setResizable(false);
        // change this!!!
        fChooser = new JFileChooser(new File("D:\\Clases\\Proyecto\\GeoBloc_desktop\\src\\dtd"));

        // Create the tab change Listener
        tabChangeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane tabbedPane = (JTabbedPane) changeEvent.getSource();
                if (tabbedPane.getSelectedIndex() != -1) {
                    form.setActualPage(tabbedPane.getSelectedIndex());
                    actualPage = (PageViewPane) tabbedPane.getSelectedComponent();
                } else
                    actualPage = null;
            }};

        // Stablish de XML files filter
        fChooser.setAcceptAllFileFilterUsed(false);
        fChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML files (*.xml)", "xml"));
        /** End of our inizialization tasks **/

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = FormDesignerApp.getApplication().getMainFrame();
            aboutBox = new FormDesignerAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        FormDesignerApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    mainPanel = new javax.swing.JPanel();
    palettePanel = new javax.swing.JPanel();
    addPageButton = new javax.swing.JButton();
    deletePageButton = new javax.swing.JButton();
    paletteSeparator1 = new javax.swing.JSeparator();
    addLabelButton = new javax.swing.JButton();
    addInputFieldButton = new javax.swing.JButton();
    addSingleChoiceButton = new javax.swing.JButton();
    addMultipleChoiceButton = new javax.swing.JButton();
    addComboBoxButton = new javax.swing.JButton();
    designPanel = new javax.swing.JPanel();
    formNameLabel = new javax.swing.JLabel();
    formNameTextField = new javax.swing.JTextField();
    formViewPane = new javax.swing.JTabbedPane();
    formDateLabel = new javax.swing.JLabel();
    formDateChooser = new com.toedter.calendar.JDateChooser();
    formVersionLabel = new javax.swing.JLabel();
    formVersionTextField = new javax.swing.JFormattedTextField();
    xmlFormViewPanel = new javax.swing.JPanel();
    xmlViewScrollPane = new javax.swing.JScrollPane();
    xmlView = new javax.swing.JEditorPane();
    updateButton = new javax.swing.JButton();
    viewButton = new javax.swing.JButton();
    menuBar = new javax.swing.JMenuBar();
    javax.swing.JMenu fileMenu = new javax.swing.JMenu();
    newFormMenuItem = new javax.swing.JMenuItem();
    openFormMenuItem = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JPopupMenu.Separator();
    saveMenuItem = new javax.swing.JMenuItem();
    saveAsMenuItem = new javax.swing.JMenuItem();
    jSeparator1 = new javax.swing.JPopupMenu.Separator();
    importMenuItem = new javax.swing.JMenuItem();
    exportMenuItem = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JPopupMenu.Separator();
    javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
    javax.swing.JMenu helpMenu = new javax.swing.JMenu();
    javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
    statusPanel = new javax.swing.JPanel();
    javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
    statusMessageLabel = new javax.swing.JLabel();
    statusAnimationLabel = new javax.swing.JLabel();
    progressBar = new javax.swing.JProgressBar();

    mainPanel.setName("mainPanel"); // NOI18N

    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(geobloc.desktop.FormDesignerApp.class).getContext().getResourceMap(FormDesignerView.class);
    palettePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("palettePanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("palettePanel.border.titleFont"))); // NOI18N
    palettePanel.setName("palettePanel"); // NOI18N

    addPageButton.setText(resourceMap.getString("addPageButton.text")); // NOI18N
    addPageButton.setEnabled(false);
    addPageButton.setName("addPageButton"); // NOI18N
    addPageButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addPageButtonActionPerformed(evt);
      }
    });

    deletePageButton.setText(resourceMap.getString("deletePageButton.text")); // NOI18N
    deletePageButton.setEnabled(false);
    deletePageButton.setName("deletePageButton"); // NOI18N
    deletePageButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deletePageButtonActionPerformed(evt);
      }
    });

    paletteSeparator1.setName("paletteSeparator1"); // NOI18N

    addLabelButton.setText(resourceMap.getString("addLabelButton.text")); // NOI18N
    addLabelButton.setEnabled(false);
    addLabelButton.setName("addLabelButton"); // NOI18N
    addLabelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addLabelButtonActionPerformed(evt);
      }
    });

    addInputFieldButton.setText(resourceMap.getString("addInputFieldButton.text")); // NOI18N
    addInputFieldButton.setEnabled(false);
    addInputFieldButton.setName("addInputFieldButton"); // NOI18N
    addInputFieldButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addInputFieldButtonActionPerformed(evt);
      }
    });

    addSingleChoiceButton.setText(resourceMap.getString("addSingleChoiceButton.text")); // NOI18N
    addSingleChoiceButton.setEnabled(false);
    addSingleChoiceButton.setName("addSingleChoiceButton"); // NOI18N
    addSingleChoiceButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addSingleChoiceButtonActionPerformed(evt);
      }
    });

    addMultipleChoiceButton.setText(resourceMap.getString("addMultipleChoiceButton.text")); // NOI18N
    addMultipleChoiceButton.setEnabled(false);
    addMultipleChoiceButton.setName("addMultipleChoiceButton"); // NOI18N
    addMultipleChoiceButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addMultipleChoiceButtonActionPerformed(evt);
      }
    });

    addComboBoxButton.setText(resourceMap.getString("addComboBoxButton.text")); // NOI18N
    addComboBoxButton.setEnabled(false);
    addComboBoxButton.setName("addComboBoxButton"); // NOI18N
    addComboBoxButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addComboBoxButtonActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout palettePanelLayout = new javax.swing.GroupLayout(palettePanel);
    palettePanel.setLayout(palettePanelLayout);
    palettePanelLayout.setHorizontalGroup(
      palettePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(palettePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(palettePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(addPageButton, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
          .addComponent(deletePageButton, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
          .addComponent(paletteSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
          .addComponent(addLabelButton, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
          .addComponent(addInputFieldButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
          .addComponent(addSingleChoiceButton, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
          .addComponent(addMultipleChoiceButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(addComboBoxButton, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
        .addContainerGap())
    );
    palettePanelLayout.setVerticalGroup(
      palettePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(palettePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(addPageButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(deletePageButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(paletteSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(addLabelButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(addInputFieldButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(addSingleChoiceButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(addMultipleChoiceButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(addComboBoxButton)
        .addContainerGap(150, Short.MAX_VALUE))
    );

    designPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("designPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("designPanel.border.titleFont"))); // NOI18N
    designPanel.setName("designPanel"); // NOI18N

    formNameLabel.setText(resourceMap.getString("formNameLabel.text")); // NOI18N
    formNameLabel.setName("formNameLabel"); // NOI18N

    formNameTextField.setText(resourceMap.getString("formNameTextField.text")); // NOI18N
    formNameTextField.setName("formNameTextField"); // NOI18N
    formNameTextField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        formNameTextFieldActionPerformed(evt);
      }
    });

    formViewPane.setName("formViewPane"); // NOI18N

    formDateLabel.setText(resourceMap.getString("formDateLabel.text")); // NOI18N
    formDateLabel.setName("formDateLabel"); // NOI18N

    formDateChooser.setDateFormatString(resourceMap.getString("formDateChooser.dateFormatString")); // NOI18N
    formDateChooser.setName("formDateChooser"); // NOI18N
    formDateChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(java.beans.PropertyChangeEvent evt) {
        formDateChooserPropertyChange(evt);
      }
    });

    formVersionLabel.setText(resourceMap.getString("formVersionLabel.text")); // NOI18N
    formVersionLabel.setName("formVersionLabel"); // NOI18N

    formVersionTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
    formVersionTextField.setText(resourceMap.getString("formVersionTextField.text")); // NOI18N
    formVersionTextField.setName("formVersionTextField"); // NOI18N

    javax.swing.GroupLayout designPanelLayout = new javax.swing.GroupLayout(designPanel);
    designPanel.setLayout(designPanelLayout);
    designPanelLayout.setHorizontalGroup(
      designPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(designPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(designPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(formViewPane, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
          .addGroup(designPanelLayout.createSequentialGroup()
            .addComponent(formNameLabel)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(formNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(formDateLabel)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(formDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(formVersionLabel)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(formVersionTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)))
        .addContainerGap())
    );
    designPanelLayout.setVerticalGroup(
      designPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(designPanelLayout.createSequentialGroup()
        .addGroup(designPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
          .addComponent(formNameLabel)
          .addComponent(formNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(formDateLabel)
          .addComponent(formDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(formVersionLabel)
          .addComponent(formVersionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(formViewPane, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
        .addContainerGap())
    );

    xmlFormViewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("xmlFormViewPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("xmlFormViewPanel.border.titleFont"))); // NOI18N
    xmlFormViewPanel.setName("xmlFormViewPanel"); // NOI18N

    xmlViewScrollPane.setName("xmlViewScrollPane"); // NOI18N

    xmlView.setContentType(resourceMap.getString("xmlView.contentType")); // NOI18N
    xmlView.setFont(resourceMap.getFont("xmlView.font")); // NOI18N
    xmlView.setName("xmlView"); // NOI18N
    xmlViewScrollPane.setViewportView(xmlView);

    updateButton.setText(resourceMap.getString("updateButton.text")); // NOI18N
    updateButton.setName("updateButton"); // NOI18N
    updateButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        updateButtonActionPerformed(evt);
      }
    });

    viewButton.setText(resourceMap.getString("viewButton.text")); // NOI18N
    viewButton.setName("viewButton"); // NOI18N
    viewButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        viewButtonActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout xmlFormViewPanelLayout = new javax.swing.GroupLayout(xmlFormViewPanel);
    xmlFormViewPanel.setLayout(xmlFormViewPanelLayout);
    xmlFormViewPanelLayout.setHorizontalGroup(
      xmlFormViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(xmlFormViewPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(xmlFormViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(xmlViewScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
          .addGroup(xmlFormViewPanelLayout.createSequentialGroup()
            .addComponent(viewButton)
            .addGap(18, 18, 18)
            .addComponent(updateButton)))
        .addContainerGap())
    );
    xmlFormViewPanelLayout.setVerticalGroup(
      xmlFormViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, xmlFormViewPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(xmlViewScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(xmlFormViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(viewButton)
          .addComponent(updateButton))
        .addContainerGap())
    );

    javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
    mainPanel.setLayout(mainPanelLayout);
    mainPanelLayout.setHorizontalGroup(
      mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(mainPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(palettePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(designPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(xmlFormViewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );
    mainPanelLayout.setVerticalGroup(
      mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(xmlFormViewPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(designPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(palettePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );

    menuBar.setName("menuBar"); // NOI18N

    fileMenu.setIcon(resourceMap.getIcon("fileMenu.icon")); // NOI18N
    fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
    fileMenu.setName("fileMenu"); // NOI18N

    newFormMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
    newFormMenuItem.setText(resourceMap.getString("newFromMenuItem.text")); // NOI18N
    newFormMenuItem.setName("newFromMenuItem"); // NOI18N
    newFormMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        newFormMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(newFormMenuItem);

    openFormMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
    openFormMenuItem.setText(resourceMap.getString("openFormMenuItem.text")); // NOI18N
    openFormMenuItem.setToolTipText(resourceMap.getString("openFormMenuItem.toolTipText")); // NOI18N
    openFormMenuItem.setName("openFormMenuItem"); // NOI18N
    openFormMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        openFormMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(openFormMenuItem);

    jSeparator3.setName("jSeparator3"); // NOI18N
    fileMenu.add(jSeparator3);

    saveMenuItem.setText(resourceMap.getString("saveMenuItem.text")); // NOI18N
    saveMenuItem.setToolTipText(resourceMap.getString("saveMenuItem.toolTipText")); // NOI18N
    saveMenuItem.setEnabled(false);
    saveMenuItem.setName("saveMenuItem"); // NOI18N
    saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(saveMenuItem);

    saveAsMenuItem.setText(resourceMap.getString("saveAsMenuItem.text")); // NOI18N
    saveAsMenuItem.setToolTipText(resourceMap.getString("saveAsMenuItem.toolTipText")); // NOI18N
    saveAsMenuItem.setEnabled(false);
    saveAsMenuItem.setName("saveAsMenuItem"); // NOI18N
    saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveAsMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(saveAsMenuItem);

    jSeparator1.setName("jSeparator1"); // NOI18N
    fileMenu.add(jSeparator1);

    importMenuItem.setText(resourceMap.getString("importMenuItem.text")); // NOI18N
    importMenuItem.setName("importMenuItem"); // NOI18N
    importMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        importMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(importMenuItem);

    exportMenuItem.setText(resourceMap.getString("exportMenuItem.text")); // NOI18N
    exportMenuItem.setName("exportMenuItem"); // NOI18N
    exportMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        exportMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(exportMenuItem);

    jSeparator2.setName("jSeparator2"); // NOI18N
    fileMenu.add(jSeparator2);

    javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(geobloc.desktop.FormDesignerApp.class).getContext().getActionMap(FormDesignerView.class, this);
    exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
    exitMenuItem.setName("exitMenuItem"); // NOI18N
    fileMenu.add(exitMenuItem);

    menuBar.add(fileMenu);

    helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
    helpMenu.setName("helpMenu"); // NOI18N

    aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
    aboutMenuItem.setName("aboutMenuItem"); // NOI18N
    helpMenu.add(aboutMenuItem);

    menuBar.add(helpMenu);

    statusPanel.setName("statusPanel"); // NOI18N

    statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

    statusMessageLabel.setName("statusMessageLabel"); // NOI18N

    statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

    progressBar.setName("progressBar"); // NOI18N

    javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
    statusPanel.setLayout(statusPanelLayout);
    statusPanelLayout.setHorizontalGroup(
      statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1086, Short.MAX_VALUE)
      .addGroup(statusPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(statusMessageLabel)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 916, Short.MAX_VALUE)
        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(statusAnimationLabel)
        .addContainerGap())
    );
    statusPanelLayout.setVerticalGroup(
      statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(statusPanelLayout.createSequentialGroup()
        .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(statusMessageLabel)
          .addComponent(statusAnimationLabel)
          .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(3, 3, 3))
    );

    setComponent(mainPanel);
    setMenuBar(menuBar);
    setStatusBar(statusPanel);
  }// </editor-fold>//GEN-END:initComponents

    private void newFormMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newFormMenuItemActionPerformed
        // Create the new form dialog pane and the parameters vector
        NewFormDialogPane newFormDialogPane = new NewFormDialogPane();
        Vector<String> parameters = new Vector<String>();
        // Show the dialog and get the parameters
        if (JOptionPane.showConfirmDialog(getFrame(),newFormDialogPane,"New form",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
            parameters = newFormDialogPane.getResults();
        else
            return;

        // Enable palette buttons
        enablePaletteButtons();

        // Create the form JDOM object
        form = new Form(parameters.get(0),parameters.get(1));

        // Stablish the form name and the first version
        formNameTextField.setText(parameters.get(0));
        formVersionTextField.setText("1");

        // Stabilsh the actual date as default
        formDateChooser.setCalendar(Calendar.getInstance());

        // Display the design area
        designPanel.setVisible(true);

        // Create and display the xml view areas
        xmlFormViewPanel.setVisible(true);

        // Clean the tabbed pane and create the pages vector
        formViewPane.removeChangeListener(tabChangeListener);
        formViewPane.removeAll();
        pagesList = new Vector<PageViewPane>();

        // Add the tab change listener
        formViewPane.addChangeListener(tabChangeListener);
        
        // Add the first page
        addPageButtonActionPerformed(evt);
    }//GEN-LAST:event_newFormMenuItemActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        // Check if user has previously choosed a save file
        if (form.getOutputFile() == null)
            saveAsMenuItemActionPerformed(evt);
        else
            form.writeOutputFile();
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void addLabelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLabelButtonActionPerformed
        // Create the label dialog pane and the parameters vector
        LabelDialogPane labelDialogPane = new LabelDialogPane();
        Vector<String> parameters = new Vector<String>();
        // Show the dialog and get the parameters
        if (JOptionPane.showConfirmDialog(getFrame(),labelDialogPane,"New label",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
            parameters = labelDialogPane.getResults();
        else
            return;
        // Add the element to JDOM form
        form.addLabel(parameters);
        // Display the label on the form view
        actualPage.addLabel(parameters);
    }//GEN-LAST:event_addLabelButtonActionPerformed

    private void addPageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPageButtonActionPerformed
        // Create the page dialog pane and the parameters vector
        PageDialogPane pageDialogPane = new PageDialogPane();
        Vector<String> parameters = new Vector<String>();
        // Show the dialog and get the parameters
        if (JOptionPane.showConfirmDialog(getFrame(),pageDialogPane,"New Page", JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
            parameters = pageDialogPane.getResults();
        else if (pagesList.size() == 0) { // Create the default page if is a new form
            parameters.add("page1");
            parameters.add("Page 1");
            parameters.add("data");
        } else
            return;


        // Create the new page and add it to the pages vector
        PageViewPane newPage;
        if (parameters.get(2).equals("photo"))
          newPage = new PageViewPane("photo");
        else
          newPage = new PageViewPane();
        pagesList.add(newPage);

        // Add the new page to the JDOM form
        form.addPage(parameters);

        // Add and display the new page on the form view
        formViewPane.add(newPage, parameters.get(1));
        formViewPane.setSelectedComponent(newPage);
    }//GEN-LAST:event_addPageButtonActionPerformed

    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed
        // Show the save dialog
        while (fChooser.showSaveDialog(FormDesignerApp.getApplication().getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fChooser.getSelectedFile();
            // Add by default .xml extension
            if (!selectedFile.getName().matches(".*\\.(:?xml|XML)"))
                selectedFile = new File(selectedFile.getPath()+".xml");
            // Check if the file exist to popup an want overwrite message
            if (!selectedFile.exists() || 
                    (selectedFile.exists() && JOptionPane.showConfirmDialog(fChooser, "File already exist. Overwrite?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION)) {
                form.writeOutputFile(selectedFile);
                break;
            }
        }
    }//GEN-LAST:event_saveAsMenuItemActionPerformed

    private void openFormMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFormMenuItemActionPerformed
        // Display the chooser
        while (fChooser.showOpenDialog(FormDesignerApp.getApplication().getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            File formFile = fChooser.getSelectedFile();
            if (!formFile.exists())
                // Show error dialog "File doesn't exist"
                JOptionPane.showMessageDialog(fChooser, "File doesn't exist", "Invalid file",JOptionPane.ERROR_MESSAGE);
            else if (!formFile.getName().matches(".*\\.(:?xml|XML)"))
                // Show error dialog "Not a valid .xml file"
                JOptionPane.showMessageDialog(fChooser, "Not a .xml file", "Invalid file",JOptionPane.ERROR_MESSAGE);
            else {
                // Create the JDOM form from file and the pages vector
                form = new Form();
                // Check if the JDOM form was created
                try {
                  form.buildFromFile(formFile);
                } catch (Exception ex) {
                  JOptionPane.showMessageDialog(fChooser, "File is not a valid XML GeoBloc Form or is corrupted. Please check syntax and DTD specifications\n"+"Error: "+ex.getMessage(), "Invalid file content",JOptionPane.ERROR_MESSAGE);
                  return;
                }
                pagesList = new Vector<PageViewPane>();
                // Draw the opened form in the design area
                drawForm(form);
                break;
            }
        }
}//GEN-LAST:event_openFormMenuItemActionPerformed

    private void formNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formNameTextFieldActionPerformed
        String newName = ((JTextField)evt.getSource()).getText();
        form.setName(newName);
    }//GEN-LAST:event_formNameTextFieldActionPerformed

    private void addInputFieldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addInputFieldButtonActionPerformed
        FieldDialogPane fieldDialog = new FieldDialogPane();
        Vector<String> parameters;
        // Show a dialog box asking for the field label
        if (JOptionPane.showConfirmDialog(getFrame(),fieldDialog,"New input field",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            parameters = fieldDialog.getResults();
        } else
            return;
        // Add the element to JDOM form (on the actual page)
        form.addInputField(parameters);
        // Display the field on the form view
        actualPage.addField(parameters);
    }//GEN-LAST:event_addInputFieldButtonActionPerformed

    private void deletePageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePageButtonActionPerformed
        // Check that deleted page isnt the last one
        if (pagesList.size() == 1){
            // Show a dialog box reporting the action
            JOptionPane.showMessageDialog(getFrame(),"El formulario debe contener al menos una página");
        } else {
            // Remove the gb_page element from the JDOM form
            form.removePage(formViewPane.getSelectedIndex());
            // Remove the tab and component from the form view pane and the pages vector
            formViewPane.remove(formViewPane.getSelectedIndex());
            pagesList.remove(formViewPane.getSelectedIndex());
            // Set the new actual page
            form.setActualPage(formViewPane.getSelectedIndex());
            actualPage = (PageViewPane)formViewPane.getSelectedComponent();
        }
    }//GEN-LAST:event_deletePageButtonActionPerformed

    private void addSingleChoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSingleChoiceButtonActionPerformed
        ListDialogPane listDialogPane = new ListDialogPane();
        Vector<String> parameters = new Vector<String>();
        // Show a dialog box asking for the option list parameters
        if (JOptionPane.showConfirmDialog(getFrame(),listDialogPane,"New single choice list",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            parameters = listDialogPane.getResults("single");
        } else
            return;
        // Add the element to JDOM form (on the actual page)
        form.addList(parameters); // INCLUIR EN PARAMETERS
        // Display the field on the form view
        actualPage.addSingleChoiceList(parameters);
    }//GEN-LAST:event_addSingleChoiceButtonActionPerformed

    private void addMultipleChoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMultipleChoiceButtonActionPerformed
        ListDialogPane listDialogPane = new ListDialogPane();
        Vector<String> parameters = new Vector<String>();
        // Show a dialog box asking for the option list parameters
        if (JOptionPane.showConfirmDialog(getFrame(),listDialogPane,"New multiple choice list",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            parameters = listDialogPane.getResults("multiple");
        } else
            return;
        // Add the element to JDOM form (on the actual page)
        form.addList(parameters);
        // Display the field on the form view
        actualPage.addMultipleChoiceList(parameters);
    }//GEN-LAST:event_addMultipleChoiceButtonActionPerformed

    private void formDateChooserPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_formDateChooserPropertyChange
        // Not a date change
        if (!evt.getPropertyName().equals("date"))
            return;
        // Get the date
        Calendar newDate = ((JDateChooser)evt.getSource()).getCalendar();
        Vector<String> parameters = new Vector<String>(3);
        // Parse as separate values for day, month and year
        parameters.add(Integer.toString(newDate.get(Calendar.DATE)));
        parameters.add(Integer.toString(newDate.get(Calendar.MONTH)+1));
        parameters.add(Integer.toString(newDate.get(Calendar.YEAR)));
        form.setDate(parameters);
}//GEN-LAST:event_formDateChooserPropertyChange

    private void addComboBoxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addComboBoxButtonActionPerformed
        ListDialogPane listDialogPane = new ListDialogPane();
        Vector<String> parameters = new Vector<String>();
        // Show a dialog box asking for the option list parameters
        if (JOptionPane.showConfirmDialog(getFrame(),listDialogPane,"New combo box list",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            parameters = listDialogPane.getResults("combo");
        } else
            return;
        // Add the element to JDOM form (on the actual page)
        form.addList(parameters);
        // Display the field on the form view
        actualPage.addComboBox(parameters);
    }//GEN-LAST:event_addComboBoxButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        // Create a new form
        Form newForm = new Form();
        // Get the updated text
        String code = xmlView.getText();

        //if (!newForm.buildFromStream(new ByteArrayInputStream(code.getBytes())))
        //    return ;
        try {
          newForm.buildFromStream(new ByteArrayInputStream(code.getBytes()));
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(getFrame(),"XML parsing error. File is not a valid XML GeoBloc form\n"+"Error: "+ex.getMessage());
          return;
        }
        // System.out.println("Updated form:\n"+form.printOutputFile());
        form = newForm;
        drawForm(form);
    }//GEN-LAST:event_updateButtonActionPerformed

    private void importMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importMenuItemActionPerformed
      // Obtain the list of forms from the servlet
      Vector<String> formsList = new Vector();
      JSONArray jsonArray;
      try {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://tomcat.etsii.ull.es/geobloc/getFormsList");
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
          String jsonText = EntityUtils.toString(entity);
          jsonArray = new JSONArray(jsonText);
          for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            formsList.add(jsonObject.getString("gb_name")+"[Version: "+jsonObject.getString("gb_form_version")+"]");
          }
        }
      } catch (JSONException ex) {
        ex.printStackTrace();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      // Create the import form dialog pane and the parameters vector
      ImportFormDialogPane importFormDialogPane = new ImportFormDialogPane(formsList);
      String formSelection = new String();
      // Show the dialog and get the choosen form to open
      if (JOptionPane.showConfirmDialog(getFrame(),importFormDialogPane,"New form",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
        formSelection = importFormDialogPane.getResults();
      else
        return;

      // Create the parameters from selection
      String formName = formSelection.substring(0,formSelection.indexOf("["));
      String formVersion = formSelection.substring(formSelection.indexOf(": ")+2,formSelection.indexOf("]"));

      // Retrieve the selected form from the database
      String xmlForm = null;
      try {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://tomcat.etsii.ull.es/geobloc/getForm");
        httpget.setHeader("gb_form_name", formName);
        httpget.setHeader("gb_form_version", formVersion);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
          String jsonText = EntityUtils.toString(entity);
          System.out.println(jsonText);
          JSONObject jsonObject = new JSONObject(jsonText);
          xmlForm = jsonObject.getString("gb_xml");
        }
        // Build and display the form
        form = new Form();
        form.buildFromStream(new ByteArrayInputStream(xmlForm.getBytes()));
        pagesList = new Vector<PageViewPane>();
        drawForm(form);
      } catch (JDOMException ex) {
        ex.printStackTrace();
      } catch (JSONException ex) {
        ex.printStackTrace();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }//GEN-LAST:event_importMenuItemActionPerformed

    private void viewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewButtonActionPerformed
        xmlView.setText(form.printOutputFile());
        System.out.println(form.printOutputFile());
    }//GEN-LAST:event_viewButtonActionPerformed

    private void exportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportMenuItemActionPerformed
      // Ask for the form id
      String formID = JOptionPane.showInputDialog(getFrame(), "Form ID", "Save form on DataBase", JOptionPane.PLAIN_MESSAGE);
      if (formID == null)
        return;
      else if (formID.isEmpty())
        return;

      // Send the form to the servlet
      HttpClient httpclient = new DefaultHttpClient();
      List<NameValuePair> qparams = new ArrayList<NameValuePair>();
      qparams.add(new BasicNameValuePair("formId", formID));
      qparams.add(new BasicNameValuePair("name", form.getName()));
      qparams.add(new BasicNameValuePair("version", form.getVersion()));
      java.sql.Date sqlDate = new java.sql.Date(form.getDate().getTimeInMillis());
      qparams.add(new BasicNameValuePair("date", sqlDate.toString()));
      qparams.add(new BasicNameValuePair("xml", form.printOutputFile()));
      qparams.add(new BasicNameValuePair("description", form.getFormDescription()));
      try {
        URI uri = URIUtils.createURI("http", "tomcat.etsii.ull.es", -1, "/geobloc/putForm", URLEncodedUtils.format(qparams, "UTF-8"), null);
        HttpPost httppost = new HttpPost(uri);
        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == 201)
          JOptionPane.showMessageDialog(getFrame(), "Operación realizada con éxito");
        else
          System.out.println(EntityUtils.toString(response.getEntity()));
      } catch (URISyntaxException ex) {
        ex.printStackTrace();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }//GEN-LAST:event_exportMenuItemActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton addComboBoxButton;
  private javax.swing.JButton addInputFieldButton;
  private javax.swing.JButton addLabelButton;
  private javax.swing.JButton addMultipleChoiceButton;
  private javax.swing.JButton addPageButton;
  private javax.swing.JButton addSingleChoiceButton;
  private javax.swing.JButton deletePageButton;
  private javax.swing.JPanel designPanel;
  private javax.swing.JMenuItem exportMenuItem;
  private com.toedter.calendar.JDateChooser formDateChooser;
  private javax.swing.JLabel formDateLabel;
  private javax.swing.JLabel formNameLabel;
  private javax.swing.JTextField formNameTextField;
  private javax.swing.JLabel formVersionLabel;
  private javax.swing.JFormattedTextField formVersionTextField;
  private javax.swing.JTabbedPane formViewPane;
  private javax.swing.JMenuItem importMenuItem;
  private javax.swing.JPopupMenu.Separator jSeparator1;
  private javax.swing.JPopupMenu.Separator jSeparator2;
  private javax.swing.JPopupMenu.Separator jSeparator3;
  private javax.swing.JPanel mainPanel;
  private javax.swing.JMenuBar menuBar;
  private javax.swing.JMenuItem newFormMenuItem;
  private javax.swing.JMenuItem openFormMenuItem;
  private javax.swing.JPanel palettePanel;
  private javax.swing.JSeparator paletteSeparator1;
  private javax.swing.JProgressBar progressBar;
  private javax.swing.JMenuItem saveAsMenuItem;
  private javax.swing.JMenuItem saveMenuItem;
  private javax.swing.JLabel statusAnimationLabel;
  private javax.swing.JLabel statusMessageLabel;
  private javax.swing.JPanel statusPanel;
  private javax.swing.JButton updateButton;
  private javax.swing.JButton viewButton;
  private javax.swing.JPanel xmlFormViewPanel;
  private javax.swing.JEditorPane xmlView;
  private javax.swing.JScrollPane xmlViewScrollPane;
  // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;

    // Tab change listener
    private ChangeListener tabChangeListener;

    // File Chooser
    private JFileChooser fChooser;

    // XML code form object
    private Form form;

    // Form pages
    private Vector<PageViewPane> pagesList;
    private PageViewPane actualPage;


    // Rest of methods
    void enablePaletteButtons() {
        for (int i = 0; i < palettePanel.getComponentCount(); i++)
            palettePanel.getComponent(i).setEnabled(true);
        saveMenuItem.setEnabled(true);
        saveAsMenuItem.setEnabled(true);
    }

    private void drawForm(Form form) {
        // Clean the tabbed pane
        formViewPane.removeChangeListener(tabChangeListener);
        formViewPane.removeAll();

        // Set the form name
        formNameTextField.setText(form.getName());
        // Set the form version
        formVersionTextField.setText(form.getVersion());
        // Set the form date
        formDateChooser.setCalendar(form.getDate());

        // Draw each of the form pages on the design area
        Iterator<org.jdom.Element> pagesIterator = form.getPagesIterator();
        while (pagesIterator.hasNext()) {
            Iterator<org.jdom.Element> elementsIterator = form.getElementsIterator(pagesIterator.next());
            // Add the page with the first element specifing its name
            PageViewPane nextPage = new PageViewPane();
            pagesList.add(nextPage);
            // Add the page on the form view
            formViewPane.add(nextPage, elementsIterator.next().getText());
            // Add the rest of the elements to the design page
            while (elementsIterator.hasNext()) {
                org.jdom.Element element = elementsIterator.next();
                Vector<String> parameters = Form.getElementParameters(element);
                String elementType = element.getName();
                if (elementType.equals("gb_label")) {
                    nextPage.addLabel(parameters);
                } else if (elementType.equals("gb_field")) {
                    nextPage.addField(parameters);
                } else if (elementType.equals("gb_list")) {
                    String listType = element.getAttributeValue("type");
                    if (listType.equals("single")) {
                        nextPage.addSingleChoiceList(parameters);
                    } else if (listType.equals("multiple")) {
                        nextPage.addMultipleChoiceList(parameters);
                    } else if (listType.equals("combo")) {
                        nextPage.addComboBox(parameters);
                    } else {
                        System.out.println("\tUnrecognized list type: "+listType);
                    }
                } else {
                    System.out.println("\tUnrecognized element: "+element.getName());
                }
            }
        }

        // Stablish the first page as the actual one
        actualPage = (PageViewPane)formViewPane.getComponentAt(0);

        // Add the tab change listener
        formViewPane.addChangeListener(tabChangeListener);
        
        // If no form has been opened before, display the design area and enable the palette buttons
        if (!designPanel.isVisible()) {
            designPanel.setVisible(true);
            xmlFormViewPanel.setVisible(true);
            enablePaletteButtons();
        }
        
        // Write the xml code on the xml view
        xmlView.setText(form.printOutputFile());
    }

    // Create a temp file with the text, and write the code
    public File createTempFile(String code) {
        try {
            File tempFile = File.createTempFile("GBForm", null);
            System.out.println(tempFile.getAbsolutePath());
            BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
            out.write(code);
            out.close();
            return tempFile;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
