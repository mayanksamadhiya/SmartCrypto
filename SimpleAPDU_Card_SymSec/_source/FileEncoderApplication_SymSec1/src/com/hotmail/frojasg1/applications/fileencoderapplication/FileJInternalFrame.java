/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.applications.fileencoderapplication;

import com.hotmail.frojasg1.applications.fileencoderapplication.simpleapdu.SimpleAPDU;
import com.hotmail.frojasg1.applications.common.components.internationalization.InternException;
import com.hotmail.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.hotmail.frojasg1.applications.common.components.internationalization.MapResizeRelocateComponentItem;
import com.hotmail.frojasg1.applications.common.components.internationalization.ResizeRelocateItem;
import com.hotmail.frojasg1.applications.common.configuration.ConfigurationException;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoderParameters;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.OperationCancellation;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.UpdatingProgress;
import com.hotmail.frojasg1.applications.fileencoder.configuration.EncryptingConfiguration;
import com.hotmail.frojasg1.applications.fileencoder.configuration.ListOfEncryptingConfigurations;
import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.ApplicationConfiguration;
import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.StringsConfiguration;
import com.hotmail.frojasg1.applications.fileencoderapplication.threads.DecryptFileThread;
import com.hotmail.frojasg1.applications.fileencoderapplication.threads.EncryptFileThread;
import com.hotmail.frojasg1.applications.fileencoderapplication.threads.OpenEncryptedFileThread;
import com.hotmail.frojasg1.applications.fileencoderapplication.threads.ParentThread;
import com.hotmail.frojasg1.applications.fileencoderapplication.utils.Utils;
import com.hotmail.frojasg1.general.StringFunctions;
import java.awt.Color;
import java.awt.Frame;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 *
 * @author Usuario
 */
public class FileJInternalFrame extends javax.swing.JInternalFrame  implements UpdatingProgress
{

    ParentThread a_actionThread = null;

	Frame a_parent = null;
	
	JDial_encryptingConfiguration a_jdec = null;

	Date a_beginningOfOperation = null;

	OperationCancellation a_operationCancellation = new OperationCancellation(false);
	
	protected JFrameInternationalization a_intern = null;

	protected String a_configurationBaseFileName = "FileJInternalFrame";
	protected Vector<JPopupMenu> a_vectorJpopupMenus = null;

	// the next parameter is used to know if the last encrypting configuration
	// was set from the parameters got from encrypted a file when decrypting.
	// if so, the parameter a_jdec must be updated in function
	// M_setEncoderParameters_toJDial_encriptingConfiguration_fromDecodingFileParameters
	// even though it is configured in the ApplicationConfiguration that manual
	// encryting configuration has priority over the encrypting configuration
	// got when decrypting a file.
	protected boolean a_wasLastEncryptingConfigurationParametersUpdatedFromDecodingFile = false;

	protected FileEncoderParameters a_fep_fromEncryptedFileParameters = null;
	
	/**
     * Creates new form FileJInternalFrame
     */
    public FileJInternalFrame( String title, Frame parent, String language )
	{
        super( title, false, true, true, false );

		a_parent = parent;
        initComponents();

		// internationalization and resizing behaviour of components.
		setWindowConfiguration( parent );

		try
		{
			M_changeLanguage( language );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		float factor = ApplicationConfiguration.M_getInstance().M_getApplicationFontSize();
		M_changeFontSize(factor);
		
        setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		
		repaint();
    }

	protected void setWindowConfiguration( Frame parent )
	{
		jta_history.setLineWrap( true );
		jta_history.setWrapStyleWord( true );

		setMaximizable(false);

		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );

			mapRRCI.putResizeRelocateComponentItem( jButton1, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jtf_fileName, ResizeRelocateItem.RESIZE_TO_RIGHT );

			mapRRCI.putResizeRelocateComponentItem( jScrollPane3, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jta_history, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );

		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		a_intern = new JFrameInternationalization(	ApplicationConfiguration.sa_MAIN_FOLDER,
													ApplicationConfiguration.sa_APPLICATION_NAME,
													ApplicationConfiguration.sa_CONFIGURATION_GROUP,
													ApplicationConfiguration.sa_PATH_PROPERTIES_IN_JAR,
													a_configurationBaseFileName,
													this,
													parent,
													a_vectorJpopupMenus,
													false,
													mapRRCI );
	}

	public void M_releaseResources()
	{
		a_intern=null;	// for the garbage collector to free the memory of the internationallization object and after the memory of this form
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        jPasswordField2 = new javax.swing.JPasswordField();
        jb_encrypt = new javax.swing.JButton();
        jb_decrypt = new javax.swing.JButton();
        jb_open = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane3 = new javax.swing.JScrollPane();
        jta_history = new javax.swing.JTextArea();
        jtf_fileName = new javax.swing.JTextField();
        jb_deleteFile = new javax.swing.JButton();
        jb_clearHistory = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jl_elapsedTime = new javax.swing.JLabel();
        jb_encryptingConfiguration = new javax.swing.JButton();
        jb_cancel = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setResizable(true);
        setMinimumSize(new java.awt.Dimension(855, 494));
        setName("jInternalFrame"); // NOI18N
        setPreferredSize(new java.awt.Dimension(855, 494));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        getContentPane().setLayout(null);

        jPanel1.setName("jPanel"); // NOI18N
        jPanel1.setLayout(null);

        jLabel1.setText("File :");
        jLabel1.setName("jl_file"); // NOI18N
        jPanel1.add(jLabel1);
        jLabel1.setBounds(10, 10, 80, 20);

        jButton1.setText("...");
        jButton1.setName("jb_chooseFile"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(810, 10, 20, 25);

        jLabel2.setText("Password :");
        jLabel2.setName("jf_password"); // NOI18N
        jPanel1.add(jLabel2);
        jLabel2.setBounds(10, 40, 180, 20);

        jPasswordField1.setName(""); // NOI18N
        jPasswordField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jPasswordField1FocusLost(evt);
            }
        });
        jPasswordField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordField1KeyReleased(evt);
            }
        });
        jPanel1.add(jPasswordField1);
        jPasswordField1.setBounds(190, 40, 460, 22);

        jLabel3.setText("Repeat Password :");
        jLabel3.setName("jl_repeatPassword"); // NOI18N
        jPanel1.add(jLabel3);
        jLabel3.setBounds(10, 70, 180, 20);

        jPasswordField2.setName(""); // NOI18N
        jPasswordField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jPasswordField2FocusLost(evt);
            }
        });
        jPasswordField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordField2KeyReleased(evt);
            }
        });
        jPanel1.add(jPasswordField2);
        jPasswordField2.setBounds(190, 70, 460, 22);

        jb_encrypt.setText("Encrypt");
        jb_encrypt.setName("jb_encrypt"); // NOI18N
        jb_encrypt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_encryptActionPerformed(evt);
            }
        });
        jPanel1.add(jb_encrypt);
        jb_encrypt.setBounds(10, 100, 170, 25);

        jb_decrypt.setText("Decrypt");
        jb_decrypt.setName("jb_decrypt"); // NOI18N
        jb_decrypt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_decryptActionPerformed(evt);
            }
        });
        jPanel1.add(jb_decrypt);
        jb_decrypt.setBounds(200, 100, 180, 25);

        jb_open.setText("Open encrypted file");
        jb_open.setName("jb_openEncryptedFile"); // NOI18N
        jb_open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_openActionPerformed(evt);
            }
        });
        jPanel1.add(jb_open);
        jb_open.setBounds(400, 130, 230, 25);

        jLabel4.setText("History :");
        jLabel4.setName("jl_history"); // NOI18N
        jPanel1.add(jLabel4);
        jLabel4.setBounds(10, 170, 110, 20);

        jSeparator1.setMinimumSize(new java.awt.Dimension(200, 2));
        jSeparator1.setPreferredSize(new java.awt.Dimension(200, 2));
        jPanel1.add(jSeparator1);
        jSeparator1.setBounds(10, 160, 820, 10);

        jta_history.setEditable(false);
        jta_history.setColumns(20);
        jta_history.setRows(5);
        jta_history.setName("jta_history"); // NOI18N
        jScrollPane3.setViewportView(jta_history);

        jPanel1.add(jScrollPane3);
        jScrollPane3.setBounds(10, 200, 830, 250);

        jtf_fileName.setName(""); // NOI18N
        jPanel1.add(jtf_fileName);
        jtf_fileName.setBounds(90, 10, 710, 22);

        jb_deleteFile.setText("Delete file");
        jb_deleteFile.setName("jb_deleteFile"); // NOI18N
        jb_deleteFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_deleteFileActionPerformed(evt);
            }
        });
        jPanel1.add(jb_deleteFile);
        jb_deleteFile.setBounds(650, 100, 180, 25);

        jb_clearHistory.setText("Clear History");
        jb_clearHistory.setName("jb_clearHistory"); // NOI18N
        jb_clearHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_clearHistoryActionPerformed(evt);
            }
        });
        jPanel1.add(jb_clearHistory);
        jb_clearHistory.setBounds(650, 130, 180, 25);

        jProgressBar1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0)));
        jProgressBar1.setName("jpb_progress"); // NOI18N
        jProgressBar1.setStringPainted(true);
        jPanel1.add(jProgressBar1);
        jProgressBar1.setBounds(350, 170, 230, 20);

        jl_elapsedTime.setMinimumSize(new java.awt.Dimension(100, 20));
        jl_elapsedTime.setName(""); // NOI18N
        jl_elapsedTime.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel1.add(jl_elapsedTime);
        jl_elapsedTime.setBounds(610, 170, 130, 20);

        jb_encryptingConfiguration.setText("Encrypting Configuration");
        jb_encryptingConfiguration.setName("jb_encryptingConfiguration"); // NOI18N
        jb_encryptingConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_encryptingConfigurationActionPerformed(evt);
            }
        });
        jPanel1.add(jb_encryptingConfiguration);
        jb_encryptingConfiguration.setBounds(400, 100, 230, 25);

        jb_cancel.setText("Cancel");
        jb_cancel.setName("jb_cancel"); // NOI18N
        jb_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_cancelActionPerformed(evt);
            }
        });
        jPanel1.add(jb_cancel);
        jb_cancel.setBounds(190, 170, 150, 25);

        jButton2.setText("Encrypt Using Card");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);
        jButton2.setBounds(10, 130, 170, 25);

        jButton3.setText("Decrypt Using Card");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3);
        jButton3.setBounds(200, 130, 180, 25);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 840, 460);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        String path = ApplicationConfiguration.M_getInstance().
        M_getStrParamConfiguration(ApplicationConfiguration.CONF_LAST_DIRECTORY);

        JFileChooser chooser=new JFileChooser(path);
        FileNameExtensionFilter fnef =
        new FileNameExtensionFilter(	StringsConfiguration.M_getInstance().
            M_getStrParamConfiguration(StringsConfiguration.CONF_ENCRYPTED_FILE_JFE),
            "jfe" );
        chooser.addChoosableFileFilter(fnef);
        Utils.M_changeFontToApplicationFontSize_forComponent( chooser );

        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            path=chooser.getSelectedFile().getParentFile().getAbsolutePath();
            jtf_fileName.setText( chooser.getSelectedFile().toString() );

            ApplicationConfiguration.M_getInstance().M_setStrParamConfiguration(
                ApplicationConfiguration.CONF_LAST_DIRECTORY,
                ( path ) );
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jPasswordField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPasswordField1FocusLost
        // TODO add your handling code here:
        M_checkPasswords();
    }//GEN-LAST:event_jPasswordField1FocusLost

    private void jPasswordField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPasswordField2FocusLost
        // TODO add your handling code here:
        M_checkPasswords();
    }//GEN-LAST:event_jPasswordField2FocusLost

    private void jb_encryptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_encryptActionPerformed
        // TODO add your handling code here:

        a_operationCancellation.M_setHasToCancel(false);

        if( (a_actionThread==null) || a_actionThread.M_getHasEnded() )
        {
            if( M_arePasswordsEqual() )
            {
                a_actionThread = new EncryptFileThread( this,
                    jtf_fileName.getText(),
                    jPasswordField1.getPassword(),
                    a_operationCancellation );
                a_actionThread.start();
            }
            else
            {
                Utils.showMessageDialog(this,
                    StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ENCRYPTION_TWO_PASSWORDS_MUST_BE_EQUAL),
                    StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_PASSWORD_ERROR),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            Utils.showMessageDialog(this,
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ONGOING_ACTION),
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ONGOING_ACTION),
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jb_encryptActionPerformed

    private void jb_decryptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_decryptActionPerformed
        // TODO add your handling code here:

        a_operationCancellation.M_setHasToCancel(false);

        if( (a_actionThread==null) || a_actionThread.M_getHasEnded() )
        {
            if( M_arePasswordsEqual() )
            {
                a_actionThread = new DecryptFileThread( this,
                    jtf_fileName.getText(),
                    jPasswordField1.getPassword(),
                    a_operationCancellation );
                a_actionThread.start();
            }
            else
            {
                Utils.showMessageDialog(this,
                    StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_DECRYPTION_TWO_PASSWORDS_MUST_BE_EQUAL),
                    StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_PASSWORD_ERROR),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            Utils.showMessageDialog(this,
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ONGOING_ACTION),
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ONGOING_ACTION),
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jb_decryptActionPerformed

    private void jb_openActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_openActionPerformed
        // TODO add your handling code here:

        a_operationCancellation.M_setHasToCancel(false);

        if( (a_actionThread==null) || a_actionThread.M_getHasEnded() )
        {
            if( M_arePasswordsEqual() )
            {
                a_actionThread = new OpenEncryptedFileThread( this,
                    jtf_fileName.getText(),
                    jPasswordField1.getPassword(),
                    a_operationCancellation );
                a_actionThread.start();
            }
            else
            {
                Utils.showMessageDialog(this,
                    StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_OPEN_TWO_PASSWORDS_MUST_BE_EQUAL),
                    StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_PASSWORD_ERROR),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            Utils.showMessageDialog(this,
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ONGOING_ACTION),
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ONGOING_ACTION),
                JOptionPane.ERROR_MESSAGE);

        }
    }//GEN-LAST:event_jb_openActionPerformed

    private void jb_deleteFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_deleteFileActionPerformed
        // TODO add your handling code here:
        if( (a_actionThread==null) || a_actionThread.M_getHasEnded() )
        {
            //Custom button text
            Object[] options = {StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_YES),
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_NO)};
            int nn = Utils.showOptionDialog(this,
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_SURE_DELETE_FILE) +
                jtf_fileName.getText() + " ?",
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ATENTION),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

            if( nn == 0 )
            {
                ParentThread.M_secureEraseFile( jtf_fileName.getText(), this );
            }
        }
        else
        {
            Utils.showMessageDialog(this,
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ONGOING_ACTION),
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ONGOING_ACTION),
                JOptionPane.ERROR_MESSAGE);

        }
    }//GEN-LAST:event_jb_deleteFileActionPerformed

    private void jb_clearHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_clearHistoryActionPerformed
        // TODO add your handling code here:

        M_clearHistory();
    }//GEN-LAST:event_jb_clearHistoryActionPerformed

    private void jb_encryptingConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_encryptingConfigurationActionPerformed
        // TODO add your handling code here:
        JDial_encryptingConfiguration jdec = null;
        if( a_jdec == null )
        {
            File file = new File( M_getFileName() );
            long size = -1;
            if( file.exists() ) size = file.length();
            EncryptingConfiguration ec = ListOfEncryptingConfigurations.M_getInstance().M_getEncryptingConfigurationForAParticularFileSize(size);

            jdec = new JDial_encryptingConfiguration( this, true, M_getLanguage() );
            if( ec != null ) jdec.M_setEncryptingConfiguration(ec);
        }
        else
        jdec = new JDial_encryptingConfiguration( a_jdec, M_getLanguage() );

        jdec.setVisible(true);
        jdec.M_releaseResources();	// to release the resources of the form
    }//GEN-LAST:event_jb_encryptingConfigurationActionPerformed

    private void jb_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_cancelActionPerformed
        // TODO add your handling code here:
        a_operationCancellation.M_setHasToCancel(true);
    }//GEN-LAST:event_jb_cancelActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:

		if( a_intern != null )
		{
			try
			{
				a_intern.M_saveGeneralConfiguration();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
    }//GEN-LAST:event_formComponentResized

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        // TODO add your handling code here:

        if( M_hasPendingTasks() )
        {
            Utils.showMessageDialog(this,
                    StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_FORM_HAS_PENDING_TASKS) + " \n" +
                    jtf_fileName.getText(),
                    StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_PENDING_TASKS),
                    JOptionPane.WARNING_MESSAGE);
        }
        else
        {
			try
			{
				a_intern.saveConfiguration();
			}
			catch( InternException ex )
			{
				ex.printStackTrace();
			}

			M_releaseResources();
            dispose();
        }
    }//GEN-LAST:event_formInternalFrameClosing

    private void jPasswordField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField1KeyReleased
        // TODO add your handling code here:

        M_checkPasswords();

    }//GEN-LAST:event_jPasswordField1KeyReleased

    private void jPasswordField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField2KeyReleased
        // TODO add your handling code here:

        M_checkPasswords();

    }//GEN-LAST:event_jPasswordField2KeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        
        String strArray[] = {"Test1", "Test2"};
        String passwordFromCard = SimpleAPDU.main(strArray);
        jPasswordField1.setText(passwordFromCard);
        jPasswordField2.setText(passwordFromCard);
        
        
         a_operationCancellation.M_setHasToCancel(false);

        if( (a_actionThread==null) || a_actionThread.M_getHasEnded() )
        {
            if( M_arePasswordsEqual() )
            {
                a_actionThread = new DecryptFileThread( this,
                    jtf_fileName.getText(),
                    jPasswordField1.getPassword(),
                    a_operationCancellation );
                a_actionThread.start();
            }
            else
            {
                Utils.showMessageDialog(this,
                    StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_DECRYPTION_TWO_PASSWORDS_MUST_BE_EQUAL),
                    StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_PASSWORD_ERROR),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            Utils.showMessageDialog(this,
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ONGOING_ACTION),
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ONGOING_ACTION),
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        
        long startTime = System.currentTimeMillis();
        String strArray[] = {"Test1", "Test2"};
        String passwordFromCard = SimpleAPDU.main(strArray);
        jPasswordField1.setText(passwordFromCard);
        jPasswordField2.setText(passwordFromCard);

        //Utils.showMessageDialog(this, passwordFromCard);
        
        a_operationCancellation.M_setHasToCancel(false);

        if( (a_actionThread==null) || a_actionThread.M_getHasEnded() )
        {
            if( M_arePasswordsEqual() )
            {
                a_actionThread = new EncryptFileThread( this,
                    jtf_fileName.getText(),
                    jPasswordField1.getPassword(),
                    a_operationCancellation );
                a_actionThread.start();
            }
            else
            {
                Utils.showMessageDialog(this,
                    StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ENCRYPTION_TWO_PASSWORDS_MUST_BE_EQUAL),
                    StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_PASSWORD_ERROR),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            Utils.showMessageDialog(this,
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ONGOING_ACTION),
                StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ONGOING_ACTION),
                JOptionPane.ERROR_MESSAGE);
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        File timeFile = new File("D:\\Masaryk\\SEM2\\PV204 Security Technologies\\PC_Application.v1.0\\_source\\FileEncoderApplication_SymSec1\\time.txt");
                try {
                    BufferedWriter br = new BufferedWriter(new FileWriter(timeFile));
                    br.write(totalTime + "\n");
                    br.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton jb_cancel;
    private javax.swing.JButton jb_clearHistory;
    private javax.swing.JButton jb_decrypt;
    private javax.swing.JButton jb_deleteFile;
    private javax.swing.JButton jb_encrypt;
    private javax.swing.JButton jb_encryptingConfiguration;
    private javax.swing.JButton jb_open;
    private javax.swing.JLabel jl_elapsedTime;
    private javax.swing.JTextArea jta_history;
    private javax.swing.JTextField jtf_fileName;
    // End of variables declaration//GEN-END:variables


    protected boolean M_arePasswordsEqual()
    {
/*        return( ( new String( jPasswordField1.getPassword())).
                equals( new String( jPasswordField2.getPassword() )) );
*/
		return( StringFunctions.M_compare( jPasswordField1.getPassword(), jPasswordField2.getPassword() ) == 0 );
    }
    
    protected void M_checkPasswords()
    {
        if( ! M_arePasswordsEqual() )
        {
            M_showDifferentPasswords_RED();
        }
        else
        {
            M_showEqualPasswords_BLACK();
        }
    }
    
    protected void M_showDifferentPasswords_RED()
    {
        M_showColorPasswords(Color.RED);
    }

    protected void M_showEqualPasswords_BLACK()
    {
        M_showColorPasswords(Color.BLACK);
    }

    protected void M_showColorPasswords( Color color )
    {
        jPasswordField1.setForeground(color);
        jPasswordField2.setForeground(color);
    }

    public synchronized void M_addStatusInformation( String text )
    {
        jta_history.append( text + "\n\n" );
		int length = jta_history.getText().length();
		jta_history.setCaretPosition(length);
	}

    public synchronized void M_clearHistory()
    {
        jta_history.setText( "" );
    }
    
    public boolean M_hasPendingTasks()
    {
        boolean result = false;
        
        if( a_actionThread != null )
        {
            result = ! a_actionThread.M_getHasEnded();
        }
        
        return( result );
    }
	
	public Frame M_getParent()
	{
		return( a_parent );
	}
	
	public void M_setJDial_encriptingConfigurationFromManualConfigurationApplied( JDial_encryptingConfiguration jfec )
	{
		a_wasLastEncryptingConfigurationParametersUpdatedFromDecodingFile=false;
		a_jdec = jfec;
	}

	protected void M_setProgressValue( int progress )
	{
		jProgressBar1.setValue(progress);
		Date now = new Date();
		String textForElapsedTime = String.valueOf( now.getTime() - a_beginningOfOperation.getTime() ) + " ms";
		jl_elapsedTime.setText( textForElapsedTime );
	}
	
	public void beginProgress()
	{
		a_beginningOfOperation = new Date();
		updateProgress(0);
	}

	public void updateProgress(int completedPercentage)
	{
		M_setProgressValue( completedPercentage );
	}

	public void endProgress()
	{
		M_setProgressValue( 100 );
	}
	
	protected String[] getAdditionalArgsForEncryptingFromEncryptedFile()
	{
		String result[] = null;
		if( a_fep_fromEncryptedFileParameters != null )
		{
			result = new String[12];

			result[0] = FileEncoderParameters.STR_FILE_ENCODER_TYPE_TAG;
			result[1] = String.valueOf(a_fep_fromEncryptedFileParameters.M_getFileEncoderType());
			result[2] = FileEncoderParameters.STR_NUM_BYTES_FILE_SLICE_TAG;
			result[3] = String.valueOf(a_fep_fromEncryptedFileParameters.M_getNumberOfBytesOfFileSlice());
			result[4] = FileEncoderParameters.STR_SIZE_OF_NUMBERS_SIMPLE_ENCODER_TAG;
			result[5] = String.valueOf(a_fep_fromEncryptedFileParameters.M_getSizeOfNumbersSimpleEncoder());
			result[6] = FileEncoderParameters.STR_SIZE_OF_NUMBERS_REORDERER_ENCODER_TAG;
			result[7] = String.valueOf(a_fep_fromEncryptedFileParameters.M_getSizeOfNumbersReordererEncoder());
			result[8] = FileEncoderParameters.STR_NUMBER_OF_BITS_PER_ITERATION_SIMPLE_ENCODER_TAG;
			result[9] = String.valueOf(a_fep_fromEncryptedFileParameters.M_getNumberOfBitsPerIterationSimpleEncoder());
			result[10] = FileEncoderParameters.STR_NUMBER_OF_BITS_PER_ITERATION_REORDERER_ENCODER_TAG;
			result[11] = String.valueOf(a_fep_fromEncryptedFileParameters.M_getNumberOfBitsPerIterationReordererEncoder());
		}
		return( result );
	}
	
	public String[] M_getAdditionalArgsForEncrypting()
	{
		String result[] = null;
		
		ApplicationConfiguration conf = ApplicationConfiguration.M_getInstance();
		long priorityConfFromFileSize = conf.M_getIntParamConfiguration(ApplicationConfiguration.CONF_GET_ENCRYPTING_CONFIGURATION_FROM_FILE_SIZE_PRIORITY);
		long priorityFromEncryptedFile = conf.M_getIntParamConfiguration(ApplicationConfiguration.CONF_GET_ENCRYPTING_CONFIGURATION_FROM_ENCRYPTED_FILE_FOR_NEXT_ENCRYPTIONS_PRIORITY);
		long priorityFromManualConfiguration = conf.M_getIntParamConfiguration(ApplicationConfiguration.CONF_MANUAL_ENCRYPTING_CONFIGURATION_PRIORITY);
		
		if(	( priorityConfFromFileSize == 1 )
			||
			( priorityConfFromFileSize == 2 )
			&&
			(
				( priorityFromEncryptedFile == 1 )	&&	(a_fep_fromEncryptedFileParameters == null)
				||
				( priorityFromManualConfiguration == 1 ) && ( (a_jdec == null) || a_wasLastEncryptingConfigurationParametersUpdatedFromDecodingFile )
			)
			||
			( priorityConfFromFileSize == 3 ) && (a_fep_fromEncryptedFileParameters == null) && (a_jdec == null)
		   )
		{
			result = new String[1];
			result[0]=FileEncoderParameters.STR_USE_FILE_SIZE_FOR_ENCRYPTING_PARAMS_TAG;
		}
		else if( ( priorityFromEncryptedFile < priorityFromManualConfiguration ) && (a_fep_fromEncryptedFileParameters != null) )
		{
			result = getAdditionalArgsForEncryptingFromEncryptedFile();
		}
		else
		{
			result = ( (a_jdec==null) ? null : a_jdec.M_getArgs() );
		}
		return( result );
	}
	
	public void M_setEncoderParameters_toJDial_encriptingConfiguration_fromDecodingFileParameters( FileEncoderParameters fep )
	{
		a_fep_fromEncryptedFileParameters = fep;

		ApplicationConfiguration conf = ApplicationConfiguration.M_getInstance();
		if( ( conf.M_getIntParamConfiguration(ApplicationConfiguration.CONF_GET_ENCRYPTING_CONFIGURATION_FROM_ENCRYPTED_FILE_FOR_NEXT_ENCRYPTIONS_PRIORITY) <
			conf.M_getIntParamConfiguration(ApplicationConfiguration.CONF_MANUAL_ENCRYPTING_CONFIGURATION_PRIORITY) )
			||
			(a_jdec==null)
			||
			a_wasLastEncryptingConfigurationParametersUpdatedFromDecodingFile
			)
		{
			a_wasLastEncryptingConfigurationParametersUpdatedFromDecodingFile=true;
			if( a_jdec == null ) a_jdec = new JDial_encryptingConfiguration( this, true, M_getLanguage() );
			a_jdec.M_setFileEncoderParameters(fep);
		}
	}
	
	public String M_getFileName()
	{
		return( jtf_fileName.getText() );
	}

	public void M_changeLanguage( String language ) throws ConfigurationException, InternException
	{
		if( a_intern != null )
		{
			a_intern.M_changeLanguage( language );
		}
	}
	
	public String M_getLanguage()
	{
		String result = ( (a_intern!=null) ? a_intern.M_getLanguage() : null );
		return( result );
	}

	public EncryptingConfiguration M_getEncrytingConfigurationFromFileSize()
	{
		EncryptingConfiguration result = ListOfEncryptingConfigurations.M_getInstance().M_getEncrytingConfigurationFromFileSize(M_getFileName());

		return( result );
	}
	
	public boolean M_getWasLastEncryptingConfigurationParametersUpdatedFromDecodingFile()
	{
		return( a_wasLastEncryptingConfigurationParametersUpdatedFromDecodingFile );
	}

	public void M_changeFontSize( float factor )
	{
		if( a_intern != null )
		{
			a_intern.M_changeFontSize(factor);
		}
	}

}
