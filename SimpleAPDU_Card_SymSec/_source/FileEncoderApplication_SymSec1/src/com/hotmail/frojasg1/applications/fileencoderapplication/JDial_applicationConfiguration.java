/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.applications.fileencoderapplication;

import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.StringsConfiguration;
import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.ApplicationConfiguration;
import com.hotmail.frojasg1.applications.common.components.internationalization.InternException;
import com.hotmail.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.hotmail.frojasg1.applications.common.configuration.ConfigurationException;
import com.hotmail.frojasg1.applications.common.configuration.ConfigurationParent;
import com.hotmail.frojasg1.applications.fileencoderapplication.utils.Utils;
import com.wordpress.tips4java.libtablecolumnadjuster.TableColumnAdjuster;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Usuario
 */
public class JDial_applicationConfiguration extends javax.swing.JDialog
{

	protected static final float SA_FACTOR_LARGE_FONT_SIZE = 1.4F;
	protected static final float SA_FACTOR_NORMAL_FONT_SIZE = 1.0F;
	
	protected boolean a_initializing = true;
	protected boolean a_userHasPressedOK = false;
	protected String a_languageInConstructor = null;
	protected JFrameInternationalization a_intern = null;
	protected final static String a_configurationBaseFileName = "JDial_applicationConfiguration";

	protected EncryptingConfigurationPriority[] a_encryptingConfigurationPriorityArray = null;

	protected static final int SA_MANUAL_CONFIGURATION = 0;
	protected static final int SA_FILE_SIZE_CONFIGURATION = 1;
	protected static final int SA_ENCRYPTED_FILE_CONFIGURATION = 2;
	
	/**
	 * Creates new form JDial_applicationConfiguration
	 */
	public JDial_applicationConfiguration(java.awt.Frame parent,
												String language, boolean modal) {
		super(parent, modal);
		initComponents();

		a_languageInConstructor = language;
		initInternationalization( language, parent );

		M_initializeComponentContents();

		M_refreshJTable();

		float factor = ApplicationConfiguration.M_getInstance().M_getApplicationFontSize();
		M_changeFontSize(factor);
	}


	protected void initInternationalization( String language, Component parent )
	{
		a_intern = new JFrameInternationalization(	ApplicationConfiguration.sa_MAIN_FOLDER,
													ApplicationConfiguration.sa_APPLICATION_NAME,
													ApplicationConfiguration.sa_CONFIGURATION_GROUP,
													ApplicationConfiguration.sa_PATH_PROPERTIES_IN_JAR,
													a_configurationBaseFileName,
													this,
													parent,
													null,
													true,
													null );
		try
		{
			a_intern.M_changeLanguage( language );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	protected class EncryptingConfigurationPriority
	{
		protected int a_id;
		protected String a_stringsConfigurationParamName;
		protected String a_applicationConfigurationParamName;

		public EncryptingConfigurationPriority( int id,
												String applicationConfigurationParamName,
												String stringsConfigurationParamName )
		{
			a_id = id;
			a_applicationConfigurationParamName = applicationConfigurationParamName;
			a_stringsConfigurationParamName = stringsConfigurationParamName;
		}

		public int M_getId()									{ return( a_id );	}
		public String M_getApplicationConfigurationParamName()	{ return( a_applicationConfigurationParamName );	}

		@Override
		public String toString()
		{
			return( StringsConfiguration.M_getInstance().M_getStrParamConfiguration(a_stringsConfigurationParamName) );
		}
	}
	
	protected void M_putEncryptingConfigurationPriority(	EncryptingConfigurationPriority[] array,
															EncryptingConfigurationPriority element
														)
	{
		int priority = (int) ApplicationConfiguration.M_getInstance().M_getIntParamConfiguration( element.M_getApplicationConfigurationParamName() );
		int position = priority - 1;
		if( (position>=0) && (position<array.length) )
		{
			array[position]=element;
		}
	}
	
	protected EncryptingConfigurationPriority[] M_getEncryptingConfigurationPriorityIntArrayFromApplicationConfiguration()
	{
		EncryptingConfigurationPriority result[] = new EncryptingConfigurationPriority[3];
		for( int ii=0; ii<3; ii++ )	result[ii]=null;

		ApplicationConfiguration appConf = ApplicationConfiguration.M_getInstance();
		
		EncryptingConfigurationPriority ecp_manual =
			new EncryptingConfigurationPriority(	SA_MANUAL_CONFIGURATION,
													ApplicationConfiguration.CONF_MANUAL_ENCRYPTING_CONFIGURATION_PRIORITY,
													StringsConfiguration.CONF_MANUAL_ENCRYPTING_CONFIGURATION
												);

		EncryptingConfigurationPriority ecp_fileSize =
			new EncryptingConfigurationPriority(	SA_FILE_SIZE_CONFIGURATION,
													ApplicationConfiguration.CONF_GET_ENCRYPTING_CONFIGURATION_FROM_FILE_SIZE_PRIORITY,
													StringsConfiguration.CONF_FILE_SIZE_ENCRYPTING_CONFIGURATION
												);

		EncryptingConfigurationPriority ecp_fromEncryptedFile =
			new EncryptingConfigurationPriority(	SA_ENCRYPTED_FILE_CONFIGURATION,
													ApplicationConfiguration.CONF_GET_ENCRYPTING_CONFIGURATION_FROM_ENCRYPTED_FILE_FOR_NEXT_ENCRYPTIONS_PRIORITY,
													StringsConfiguration.CONF_ENCRYPTED_FILE_CONFIGURATION
												);

		M_putEncryptingConfigurationPriority( result, ecp_manual );
		M_putEncryptingConfigurationPriority( result, ecp_fileSize );
		M_putEncryptingConfigurationPriority( result, ecp_fromEncryptedFile );

		if( (result[0] == null) || (result[1]==null) || (result[2]==null) )
		{
			result[0]=ecp_manual;
			result[1]=ecp_fileSize;
			result[2]=ecp_fromEncryptedFile;
		}

		return( result );
	}
	
	protected void M_initializeComponentContents()
	{
		a_initializing = true;
		ApplicationConfiguration appConf = ApplicationConfiguration.M_getInstance();
		
		a_encryptingConfigurationPriorityArray = M_getEncryptingConfigurationPriorityIntArrayFromApplicationConfiguration();
		
		jTf_additionalLanguage.setText( appConf.M_getStrParamConfiguration( ApplicationConfiguration.CONF_ADDITIONAL_LANGUAGE ) );

		jCb_eraseDecryptedFileAfterEncrypting.setSelected( appConf.M_getIntParamConfiguration( ApplicationConfiguration.CONF_ERASE_DECRYPTED_FILE_AFTER_ENCRIPTING ) != 0 );
		jCb_renameEncryptedFileToOldAfterDecrypting.setSelected( appConf.M_getIntParamConfiguration( ApplicationConfiguration.CONF_HAS_TO_RENAME_PRESENT_ENCRYPTED_FILE_TO_OLD_AFTER_DECRYPTING ) != 0 );
		jCb_askToOverwriteOldEncryptedFile.setSelected( appConf.M_getIntParamConfiguration( ApplicationConfiguration.CONF_ASK_TO_OVERWRITE_OLD_ENCRYPTED_FILE_WHEN_IT_EXISTED_PREVIOUSLY ) != 0 );
		
		M_fillLanguageComboBox();
		
		M_fillFontSizeComboBox();
		
		a_encryptingConfigurationPriorityArray = M_getEncryptingConfigurationPriorityIntArrayFromApplicationConfiguration();
		
		jTf_additionalLanguageFolder.setText( M_getAdditionalLanguageFolder( jTf_additionalLanguage.getText() ) );
		
		a_initializing=false;
	}

	protected String M_getAdditionalLanguageFolder( String language )
	{
		String result = System.getProperty("user.home") + ConfigurationParent.sa_dirSeparator  + 
						ApplicationConfiguration.sa_MAIN_FOLDER + ConfigurationParent.sa_dirSeparator +
						ApplicationConfiguration.sa_APPLICATION_NAME + ConfigurationParent.sa_dirSeparator +
						ApplicationConfiguration.sa_CONFIGURATION_GROUP + ConfigurationParent.sa_dirSeparator +
						language;
		return( result );
	}
	
	protected void M_fillLanguageComboBox()
	{
		if( (!a_initializing) && ( jCb_language.getItemCount() == 3 ) &&
			( ((String) jCb_language.getItemAt(2)).compareTo(jTf_additionalLanguage.getText()) == 0 )
		   )
		{}
		else
		{
			int selectedIndex = -1;

			if( a_initializing )
			{
				String language = ApplicationConfiguration.M_getInstance().M_getStrParamConfiguration( ApplicationConfiguration.CONF_LANGUAGE );
				if( language != null )
				{
					if( language.equals( "EN" ) )	selectedIndex = 0;
					else if( language.equals( "ES" ) )	selectedIndex = 1;
					else if( language.equals( jTf_additionalLanguage.getText() ) )	selectedIndex = 2;
				}
			}
			else if( jCb_language.getSelectedIndex() > 0 )
			{
				if( jCb_language.getSelectedIndex() == 2 )
				{
					if( ((String) jCb_language.getItemAt(2)).compareTo(jTf_additionalLanguage.getText()) == 0 )
					{
						selectedIndex = 2;
					}
					else selectedIndex = 0;
				}
				else selectedIndex = jCb_language.getSelectedIndex();
			}
			jCb_language.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "EN", "ES", jTf_additionalLanguage.getText() }));

			if( selectedIndex >= 0 ) jCb_language.setSelectedIndex(selectedIndex);
		}
	}

	protected void M_fillFontSizeComboBox()
	{
		float configuredFactor = ApplicationConfiguration.M_getInstance().M_getApplicationFontSize();
		
		int selectedIndex = -1;
		if( configuredFactor == SA_FACTOR_NORMAL_FONT_SIZE )	selectedIndex = 0;
		else if( configuredFactor == SA_FACTOR_LARGE_FONT_SIZE ) selectedIndex = 1;

		M_refreshCbFontSize(selectedIndex);
	}
	
	
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLbl_EncryptingConfigurationPriority = new javax.swing.JLabel();
        jCb_language = new javax.swing.JComboBox();
        jLbl_language = new javax.swing.JLabel();
        jTf_additionalLanguage = new javax.swing.JTextField();
        jLbl_additionalLanguage1 = new javax.swing.JLabel();
        jCb_eraseDecryptedFileAfterEncrypting = new javax.swing.JCheckBox();
        jLbl_eraseDecryptedFileAfterEncrypting = new javax.swing.JLabel();
        jCb_askToOverwriteOldEncryptedFile = new javax.swing.JCheckBox();
        jCb_renameEncryptedFileToOldAfterDecrypting = new javax.swing.JCheckBox();
        jLbl_renameEncryptedFileToOldAfterDecrypting = new javax.swing.JLabel();
        jLbl_askToOverwriteOldEncryptedFile1 = new javax.swing.JLabel();
        jBtn_up = new javax.swing.JButton();
        jBtn_down = new javax.swing.JButton();
        jb_Ok = new javax.swing.JButton();
        jb_Cancel = new javax.swing.JButton();
        jT_priorityForEncryptingConfiguration = new javax.swing.JTable();
        jLbl_additionalLanguageFolder = new javax.swing.JLabel();
        jTf_additionalLanguageFolder = new javax.swing.JTextField();
        jLbl_EncryptingConfigurationPriority1 = new javax.swing.JLabel();
        jCb_fontSize = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(724, 460));
        setPreferredSize(new java.awt.Dimension(724, 460));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Application configuration");
        jLabel1.setName("jLbl_title"); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(130, 10, 580, 30);

        jLbl_EncryptingConfigurationPriority.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLbl_EncryptingConfigurationPriority.setText("Application Font Size");
        jLbl_EncryptingConfigurationPriority.setName("JDial_applicationFontSize"); // NOI18N
        getContentPane().add(jLbl_EncryptingConfigurationPriority);
        jLbl_EncryptingConfigurationPriority.setBounds(130, 260, 590, 20);

        jCb_language.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jCb_language.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCb_languageActionPerformed(evt);
            }
        });
        getContentPane().add(jCb_language);
        jCb_language.setBounds(40, 60, 80, 20);

        jLbl_language.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLbl_language.setText("Language");
        jLbl_language.setName("jLbl_language"); // NOI18N
        getContentPane().add(jLbl_language);
        jLbl_language.setBounds(130, 60, 590, 20);

        jTf_additionalLanguage.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTf_additionalLanguageFocusLost(evt);
            }
        });
        getContentPane().add(jTf_additionalLanguage);
        jTf_additionalLanguage.setBounds(60, 90, 59, 20);

        jLbl_additionalLanguage1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLbl_additionalLanguage1.setText("Additional Language");
        jLbl_additionalLanguage1.setName("jLbl_additionalLanguage"); // NOI18N
        getContentPane().add(jLbl_additionalLanguage1);
        jLbl_additionalLanguage1.setBounds(130, 90, 590, 20);
        getContentPane().add(jCb_eraseDecryptedFileAfterEncrypting);
        jCb_eraseDecryptedFileAfterEncrypting.setBounds(100, 170, 21, 21);

        jLbl_eraseDecryptedFileAfterEncrypting.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLbl_eraseDecryptedFileAfterEncrypting.setText("Erase decrypted file after encrypting");
        jLbl_eraseDecryptedFileAfterEncrypting.setName("jLbl_eraseDecryptedFileAfterEncrypting"); // NOI18N
        getContentPane().add(jLbl_eraseDecryptedFileAfterEncrypting);
        jLbl_eraseDecryptedFileAfterEncrypting.setBounds(130, 170, 590, 20);
        getContentPane().add(jCb_askToOverwriteOldEncryptedFile);
        jCb_askToOverwriteOldEncryptedFile.setBounds(100, 230, 21, 21);
        getContentPane().add(jCb_renameEncryptedFileToOldAfterDecrypting);
        jCb_renameEncryptedFileToOldAfterDecrypting.setBounds(100, 200, 21, 21);

        jLbl_renameEncryptedFileToOldAfterDecrypting.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLbl_renameEncryptedFileToOldAfterDecrypting.setText("Rename encrypted file to old after decrypting");
        jLbl_renameEncryptedFileToOldAfterDecrypting.setName("jLbl_renameEncryptedFileToOldAfterDecrypting"); // NOI18N
        getContentPane().add(jLbl_renameEncryptedFileToOldAfterDecrypting);
        jLbl_renameEncryptedFileToOldAfterDecrypting.setBounds(130, 200, 590, 20);

        jLbl_askToOverwriteOldEncryptedFile1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLbl_askToOverwriteOldEncryptedFile1.setText("Ask to overwrite old encrypted file");
        jLbl_askToOverwriteOldEncryptedFile1.setName("jLbl_askToOverwriteOldEncryptedFile"); // NOI18N
        getContentPane().add(jLbl_askToOverwriteOldEncryptedFile1);
        jLbl_askToOverwriteOldEncryptedFile1.setBounds(130, 230, 590, 20);

        jBtn_up.setText("UP");
        jBtn_up.setName("jBtn_up"); // NOI18N
        jBtn_up.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_upActionPerformed(evt);
            }
        });
        getContentPane().add(jBtn_up);
        jBtn_up.setBounds(30, 320, 90, 23);

        jBtn_down.setText("Down");
        jBtn_down.setName("jBtn_down"); // NOI18N
        jBtn_down.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_downActionPerformed(evt);
            }
        });
        getContentPane().add(jBtn_down);
        jBtn_down.setBounds(30, 350, 90, 23);

        jb_Ok.setText("OK");
        jb_Ok.setName("jBtn_Ok"); // NOI18N
        jb_Ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_OkActionPerformed(evt);
            }
        });
        getContentPane().add(jb_Ok);
        jb_Ok.setBounds(120, 390, 140, 23);

        jb_Cancel.setText("Cancel");
        jb_Cancel.setName("jBtn_Cancel"); // NOI18N
        jb_Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_CancelActionPerformed(evt);
            }
        });
        getContentPane().add(jb_Cancel);
        jb_Cancel.setBounds(320, 390, 140, 23);

        jT_priorityForEncryptingConfiguration.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jT_priorityForEncryptingConfiguration.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jT_priorityForEncryptingConfiguration.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        getContentPane().add(jT_priorityForEncryptingConfiguration);
        jT_priorityForEncryptingConfiguration.setBounds(130, 320, 580, 50);

        jLbl_additionalLanguageFolder.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLbl_additionalLanguageFolder.setText("Additional Language Folder:");
        jLbl_additionalLanguageFolder.setToolTipText("");
        jLbl_additionalLanguageFolder.setName("jLbl_additionalLanguageFolder"); // NOI18N
        getContentPane().add(jLbl_additionalLanguageFolder);
        jLbl_additionalLanguageFolder.setBounds(130, 115, 590, 20);

        jTf_additionalLanguageFolder.setEditable(false);
        jTf_additionalLanguageFolder.setBackground(new java.awt.Color(255, 255, 255));
        jTf_additionalLanguageFolder.setForeground(new java.awt.Color(153, 153, 153));
        jTf_additionalLanguageFolder.setName(""); // NOI18N
        jTf_additionalLanguageFolder.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTf_additionalLanguageFolderFocusLost(evt);
            }
        });
        getContentPane().add(jTf_additionalLanguageFolder);
        jTf_additionalLanguageFolder.setBounds(10, 140, 700, 20);

        jLbl_EncryptingConfigurationPriority1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLbl_EncryptingConfigurationPriority1.setText("Encrypting configuration priority :");
        jLbl_EncryptingConfigurationPriority1.setName("jLbl_EncryptingConfigurationPriority"); // NOI18N
        getContentPane().add(jLbl_EncryptingConfigurationPriority1);
        jLbl_EncryptingConfigurationPriority1.setBounds(130, 290, 590, 20);

        jCb_fontSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jCb_fontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCb_fontSizeActionPerformed(evt);
            }
        });
        getContentPane().add(jCb_fontSize);
        jCb_fontSize.setBounds(10, 260, 110, 20);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jb_OkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_OkActionPerformed
        // TODO add your handling code here:

		applyChanges();

		try
		{
			ApplicationConfiguration.M_getInstance().M_saveConfiguration();
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
		}
		
		if( M_hasToCreateAdditionalLanguageFolder() )
		{
			M_createFolderAndCopyLanguageConfigurationFilesFromEnglish();
		}
		
		M_saveInternationalization();
		a_userHasPressedOK = true;
		setVisible(false);
    }//GEN-LAST:event_jb_OkActionPerformed

    private void jb_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_CancelActionPerformed
        // TODO add your handling code here:
		
		// we load again the Strings configuration with the language which with was called the dialogue
		try
		{
			StringsConfiguration.M_getInstance().M_changeLanguage( a_languageInConstructor );
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
		}

		M_saveInternationalization();
        setVisible(false);
    }//GEN-LAST:event_jb_CancelActionPerformed

    private void jCb_languageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCb_languageActionPerformed
        // TODO add your handling code here:

		int selectedIndex = jCb_language.getSelectedIndex();
		
		if( selectedIndex >= 0 )
		{
			String language = (String) jCb_language.getItemAt( selectedIndex );
			try
			{
				StringsConfiguration.M_getInstance().M_changeLanguage( language );
			}
			catch( ConfigurationException ce )
			{
				ce.printStackTrace();
			}

			// after loading the strings of the new language we have to refresh the table.
			M_refreshJTable();
			M_refreshCbFontSize();
			try
			{
				if( a_intern != null )
				{
					a_intern.M_changeLanguage( language );
				}
			}
			catch( InternException ie )
			{
				ie.printStackTrace();
			}
		}
		
    }//GEN-LAST:event_jCb_languageActionPerformed

    private void jTf_additionalLanguageFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTf_additionalLanguageFocusLost
        // TODO add your handling code here:
		
		// for updating the additional language in the combobox.
		M_fillLanguageComboBox();
		jTf_additionalLanguageFolder.setText( M_getAdditionalLanguageFolder( jTf_additionalLanguage.getText() ) );

    }//GEN-LAST:event_jTf_additionalLanguageFocusLost

    private void jBtn_upActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_upActionPerformed
        // TODO add your handling code here:
		
		M_moveSelectionInTable( -1 );
		
    }//GEN-LAST:event_jBtn_upActionPerformed

    private void jBtn_downActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_downActionPerformed
        // TODO add your handling code here:

		M_moveSelectionInTable( 1 );
		
    }//GEN-LAST:event_jBtn_downActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:

		jb_CancelActionPerformed(null);
		
    }//GEN-LAST:event_formWindowClosing

    private void jTf_additionalLanguageFolderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTf_additionalLanguageFolderFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jTf_additionalLanguageFolderFocusLost

    private void jCb_fontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCb_fontSizeActionPerformed
        // TODO add your handling code here:

		float factor = 0;
		if( jCb_fontSize.getSelectedIndex() == 0 )	factor = SA_FACTOR_NORMAL_FONT_SIZE;
		else if( jCb_fontSize.getSelectedIndex() == 1 ) factor = SA_FACTOR_LARGE_FONT_SIZE;
		
		M_changeFontSize(factor);

    }//GEN-LAST:event_jCb_fontSizeActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(JDial_applicationConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(JDial_applicationConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(JDial_applicationConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(JDial_applicationConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
        //</editor-fold>
		/* Create and display the dialog */
/*
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				JDial_applicationConfiguration dialog = new JDial_applicationConfiguration(new javax.swing.JFrame(), true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
*/
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtn_down;
    private javax.swing.JButton jBtn_up;
    private javax.swing.JCheckBox jCb_askToOverwriteOldEncryptedFile;
    private javax.swing.JCheckBox jCb_eraseDecryptedFileAfterEncrypting;
    private javax.swing.JComboBox jCb_fontSize;
    private javax.swing.JComboBox jCb_language;
    private javax.swing.JCheckBox jCb_renameEncryptedFileToOldAfterDecrypting;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLbl_EncryptingConfigurationPriority;
    private javax.swing.JLabel jLbl_EncryptingConfigurationPriority1;
    private javax.swing.JLabel jLbl_additionalLanguage1;
    private javax.swing.JLabel jLbl_additionalLanguageFolder;
    private javax.swing.JLabel jLbl_askToOverwriteOldEncryptedFile1;
    private javax.swing.JLabel jLbl_eraseDecryptedFileAfterEncrypting;
    private javax.swing.JLabel jLbl_language;
    private javax.swing.JLabel jLbl_renameEncryptedFileToOldAfterDecrypting;
    private javax.swing.JTable jT_priorityForEncryptingConfiguration;
    private javax.swing.JTextField jTf_additionalLanguage;
    private javax.swing.JTextField jTf_additionalLanguageFolder;
    private javax.swing.JButton jb_Cancel;
    private javax.swing.JButton jb_Ok;
    // End of variables declaration//GEN-END:variables

	protected void M_adjustColumnWidths()
	{
		TableColumnAdjuster tca = new TableColumnAdjuster(jT_priorityForEncryptingConfiguration);
		tca.adjustColumns();
	}
	
	protected Object[] M_getRowData( int ii, EncryptingConfigurationPriority element )
	{
		Object[] result = new Object[2];

		result[0] = String.valueOf(ii);
		result[1] = element.toString();

		return( result );
	}

	protected Object[] M_getTableTitles()
	{
		Object[] result = new String[2];
		
		result[0] = "P";
		result[1] = "Configuration source";
		
		return( result );
	}
	
	protected void M_refreshJTable()
	{
		DefaultTableModel dtm = new DefaultTableModel();
		dtm.setColumnIdentifiers( M_getTableTitles() );

		for( int ii=0; ii<a_encryptingConfigurationPriorityArray.length; ii++ )
		{
			dtm.addRow( M_getRowData( ii+1, a_encryptingConfigurationPriorityArray[ii] ) );
		}

		int selectedRow = -1;
		int[] selection = jT_priorityForEncryptingConfiguration.getSelectedRows();
		if( (selection != null) && (selection.length > 0) )
		{
			selectedRow = jT_priorityForEncryptingConfiguration.convertRowIndexToModel(selection[0]);
		}

		jT_priorityForEncryptingConfiguration.setModel( dtm );

		for (int ii = 0; ii < jT_priorityForEncryptingConfiguration.getColumnCount(); ii++)
		{
			Class<?> col_class = jT_priorityForEncryptingConfiguration.getColumnClass(ii);
			jT_priorityForEncryptingConfiguration.setDefaultEditor(col_class, null);        // remove editor
		}

		M_adjustColumnWidths();

		if( selectedRow >= 0 )	jT_priorityForEncryptingConfiguration.setRowSelectionInterval( selectedRow, selectedRow );
	}

	protected void M_saveInternationalization()
	{
		try
		{
			if( a_intern != null ) a_intern.saveConfiguration();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}
	
	@Override
	public void setVisible( boolean visible )
	{
		if( visible ) a_userHasPressedOK = false;
		super.setVisible( visible );
	}
	
	public boolean M_getUserHasPressedOK()
	{
		return( a_userHasPressedOK );
	}

	protected int M_getJTableSelection()
	{
		int result = -1;

		int[] selection = jT_priorityForEncryptingConfiguration.getSelectedRows();
		if( ( selection != null ) && ( selection.length>0 ) )
		{
			for (int ii = 0; ii < selection.length; ii++)
			{
				selection[ii] = jT_priorityForEncryptingConfiguration.convertRowIndexToModel(selection[ii]);
			}

			result = selection[0];
		}
		else
		{
            Utils.showMessageDialog(this,
                    StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_HAVE_TO_SELECT_ROW_OF_TABLE),
                    StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_SELECTION_ERROR),
                    JOptionPane.ERROR_MESSAGE);
		}

		return( result );
	}

	protected void M_moveSelectionInTable( int increment )
	{
		int selectedRow = M_getJTableSelection();
		
		if( selectedRow >= 0 )
		{
			int newIndex = selectedRow + increment;
			
			if( (newIndex >= 0) && (newIndex < a_encryptingConfigurationPriorityArray.length) )
			{
				EncryptingConfigurationPriority element = a_encryptingConfigurationPriorityArray[newIndex];
				a_encryptingConfigurationPriorityArray[newIndex] = a_encryptingConfigurationPriorityArray[selectedRow];
				a_encryptingConfigurationPriorityArray[selectedRow] = element;
				
				M_refreshJTable();
				
				jT_priorityForEncryptingConfiguration.setRowSelectionInterval( newIndex, newIndex );
			}
		}
	}

	protected void applyChanges()
	{
		ApplicationConfiguration appConf = ApplicationConfiguration.M_getInstance();
		
		for( int ii=0; ii<a_encryptingConfigurationPriorityArray.length; ii++ )
		{
			appConf.M_setIntParamConfiguration( a_encryptingConfigurationPriorityArray[ii].M_getApplicationConfigurationParamName(), ii+1 );
		}

		appConf.M_setStrParamConfiguration( ApplicationConfiguration.CONF_ADDITIONAL_LANGUAGE, jTf_additionalLanguage.getText() );

		appConf.M_setIntParamConfiguration(ApplicationConfiguration.CONF_ERASE_DECRYPTED_FILE_AFTER_ENCRIPTING,
											( jCb_eraseDecryptedFileAfterEncrypting.isSelected() ? 1 : 0 ) );

		appConf.M_setIntParamConfiguration(ApplicationConfiguration.CONF_HAS_TO_RENAME_PRESENT_ENCRYPTED_FILE_TO_OLD_AFTER_DECRYPTING,
											( jCb_renameEncryptedFileToOldAfterDecrypting.isSelected() ? 1 : 0 ) );

		appConf.M_setIntParamConfiguration(ApplicationConfiguration.CONF_ASK_TO_OVERWRITE_OLD_ENCRYPTED_FILE_WHEN_IT_EXISTED_PREVIOUSLY,
											( jCb_askToOverwriteOldEncryptedFile.isSelected() ? 1 : 0 ) );


		String language = (String) jCb_language.getSelectedItem();
		if( (language != null) )
			appConf.M_setStrParamConfiguration( ApplicationConfiguration.CONF_LANGUAGE, language );

		float factor = 0.0F;
		if( jCb_fontSize.getSelectedIndex() == 0 )	factor = SA_FACTOR_NORMAL_FONT_SIZE;
		else if( jCb_fontSize.getSelectedIndex() == 1 )	factor = SA_FACTOR_LARGE_FONT_SIZE;
		
		if( factor > 0.0F )
		{
			appConf.M_setStrParamConfiguration( ApplicationConfiguration.CONF_APPLICATION_FONT_SIZE, Float.toString(factor) );
		}
	}

	protected boolean M_hasToCreateAdditionalLanguageFolder()
	{
		boolean result = false;
		
		String language = jTf_additionalLanguage.getText();
		if( (language != null) && (! language.equals( "CAT" ) ) )		// el català ja està traduït al .jar
		{
			String folderName = M_getAdditionalLanguageFolder( language );
			File directory = new File( folderName );
			result = ! directory.exists();
		}

		return( result );
	}

	protected void M_createFolderAndCopyLanguageConfigurationFilesFromEnglish()
	{
		String language = jTf_additionalLanguage.getText();
		
		File directory = new File( M_getAdditionalLanguageFolder( language ) );
		boolean ok = directory.mkdirs();
		
		if( ok )
		{
			M_copyLanguageConfigurationFileFromJarToAdditionalLanguageFolder( "EN", language, "FileJInternalFrame_LAN.properties" );
			M_copyLanguageConfigurationFileFromJarToAdditionalLanguageFolder( "EN", language, "JDial_about_LAN.properties" );
			M_copyLanguageConfigurationFileFromJarToAdditionalLanguageFolder( "EN", language, "JDial_applicationConfiguration_LAN.properties" );
			M_copyLanguageConfigurationFileFromJarToAdditionalLanguageFolder( "EN", language, "JDial_encryptingConfiguration_LAN.properties" );
			M_copyLanguageConfigurationFileFromJarToAdditionalLanguageFolder( "EN", language, "JDial_listOfEncryptingConfigurations_LAN.properties" );
			M_copyLanguageConfigurationFileFromJarToAdditionalLanguageFolder( "EN", language, "MainWindow_LAN.properties" );
			M_copyLanguageConfigurationFileFromJarToAdditionalLanguageFolder( "EN", language, "StringsConfiguration.properties" );
		}
	}

	protected void M_copyLanguageConfigurationFileFromJarToAdditionalLanguageFolder( 
							String originLanguage,
							String destinationLanguage,
							String fileName )
	{
		String longFileNameInJar = ApplicationConfiguration.sa_PATH_PROPERTIES_IN_JAR + "/" + originLanguage + "/" + fileName;
		String longFileNameInDisk = M_getAdditionalLanguageFolder( destinationLanguage ) + ConfigurationParent.sa_dirSeparator + fileName;

		M_copyPropertiesFileFromJarToDisk( longFileNameInJar, longFileNameInDisk );
	}

	protected void M_copyPropertiesFileFromJarToDisk(	String longFileNameInJar,
														String longFileNameInDisk )
	{
		boolean ok = false;
		Properties prop = null;
		InputStream in = null;
		ClassLoader loader = ClassLoader.getSystemClassLoader ();
		in = loader.getResourceAsStream (longFileNameInJar);

		if( in == null )
		{
			try
			{
				in = this.getClass().getClassLoader().getResource(longFileNameInJar).openStream();
			}
			catch( Throwable th )
			{
				in = null;
			}
		}
		

		if (in != null)
		{
			InputStreamReader isr = new InputStreamReader( in, StandardCharsets.UTF_8 );
			if( isr != null )
			{
				prop = new Properties ();
				try
				{
					prop.load (isr); // It can throw IOException
					ok=true;
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
				finally
				{
					try
					{
						isr.close();
					}
					catch( Throwable th1 )
					{
						th1.printStackTrace();
					}
				}
			}
		}
		
		if( ok )
		{
			try
			{
				FileOutputStream fos = new FileOutputStream( longFileNameInDisk );
				OutputStreamWriter osw = new OutputStreamWriter( fos, StandardCharsets.UTF_8 );
				prop.store( osw, "Default property values to be translated" );
				osw.close();
			}
			catch( IOException ex )
			{
				System.out.println( ex.getMessage() );
			}
		}
	}
	
	public void M_releaseResources()
	{
		a_intern=null;	// for the garbage collector to free the memory of the internationallization object and after the memory of this form
	}
	
	public void M_changeFontSize( float factor )
	{
		if( a_intern != null )
		{
			a_intern.M_changeFontSize(factor);
		}
	}

	protected void M_refreshCbFontSize( int selectedItem )
	{
		jCb_fontSize.setModel
		(
			new javax.swing.DefaultComboBoxModel
			(
				new String[] { 
					StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_NORMAL_FONT_SIZE),
					StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_LARGE_FONT_SIZE)
							}
			)
		);

		if( selectedItem >= 0 ) jCb_fontSize.setSelectedIndex(selectedItem);
	}

	protected void M_refreshCbFontSize()
	{
		int selectedItem = -1;
		if( jCb_fontSize.getSelectedIndex() >= 0 ) selectedItem = jCb_fontSize.getSelectedIndex();

		M_refreshCbFontSize( selectedItem );
	}

}
