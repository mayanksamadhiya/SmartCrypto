/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.applications.fileencoderapplication;

import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.StringsConfiguration;
import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.ApplicationConfiguration;
import com.hotmail.frojasg1.applications.fileencoder.configuration.ListOfEncryptingConfigurations;
import com.hotmail.frojasg1.applications.fileencoder.configuration.EncryptingConfiguration;
import com.hotmail.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoder;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoderException;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoderParameters;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoderType;
import com.hotmail.frojasg1.applications.fileencoderapplication.utils.Utils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

/**
 *
 * @author Usuario
 */
public class JDial_encryptingConfiguration extends javax.swing.JDialog {

	protected FileJInternalFrame	a_parent = null;
	protected boolean				a_modal = false;
	protected boolean				a_userHasPressedOK = false;
	protected boolean				a_hasToUseSizeOfFileFrom = false;

	protected JFrameInternationalization a_intern = null;

	protected final static String a_configurationBaseFileName = "JDial_encryptingConfiguration";
	protected Vector<JPopupMenu> a_vectorJpopupMenus = null;

	/**
	 * Creates new form JDial_encryptingConfiguration
	 */
	public JDial_encryptingConfiguration(FileJInternalFrame parent, boolean modal,
											String language ) {
		super((parent==null?null:parent.M_getParent()), modal);
		initComponents();

		initInternationalization( language, parent );

		a_hasToUseSizeOfFileFrom = false;
		a_parent = parent;
		a_modal = modal;

		float factor = ApplicationConfiguration.M_getInstance().M_getApplicationFontSize();
		M_changeFontSize(factor);
		
		M_adaptComponents();
	}

	public JDial_encryptingConfiguration(JDial_listOfEncryptingConfigurations parent, boolean modal,
											String language ) {
		super(parent, modal);
		initComponents();

		initInternationalization( language, parent );

		a_hasToUseSizeOfFileFrom = true;	// if the parent is the JDial_list..., it has to be used the field of sizeOfFilesFrom.
		a_parent = null;
		a_modal = modal;

		float factor = ApplicationConfiguration.M_getInstance().M_getApplicationFontSize();
		M_changeFontSize(factor);
		
		M_adaptComponents();
	}

	public JDial_encryptingConfiguration( JDial_encryptingConfiguration other, String language )
	{
		super( ((other==null) || (other.a_parent==null)?null:other.a_parent.M_getParent()), other.a_modal );
		
		initComponents();

		initInternationalization( language, other.a_parent );
		
		if( other != null )
		{
			jCb_fileEncoderType.setSelected( other.jCb_fileEncoderType.isSelected());
//			jCheckBox2.setSelected( other.jCheckBox2.isSelected());
			jCheckBox3.setSelected( other.jCheckBox3.isSelected());
			jCheckBox4.setSelected( other.jCheckBox4.isSelected());
			jCheckBox5.setSelected( other.jCheckBox5.isSelected());
			jCheckBox6.setSelected( other.jCheckBox6.isSelected());
			jCheckBox7.setSelected( other.jCheckBox7.isSelected());

			jTF_fileEncoderType.setText( other.jTF_fileEncoderType.getText() );
//			jTextField2.setText( other.jTextField2.getText() );
			jTf_numBytesFileSlice.setText( other.jTf_numBytesFileSlice.getText() );
			jTextField4.setText( other.jTextField4.getText() );
			jTextField5.setText( other.jTextField5.getText() );
			jTextField6.setText( other.jTextField6.getText() );
			jTextField7.setText( other.jTextField7.getText() );

			a_hasToUseSizeOfFileFrom = other.a_hasToUseSizeOfFileFrom;
			a_parent = other.a_parent;
			a_modal = other.a_modal;
			
			M_changeUnitNameOfSizeOfNumbers();

			float factor = ApplicationConfiguration.M_getInstance().M_getApplicationFontSize();
			M_changeFontSize(factor);

			M_adaptComponents();
		}
	}

	protected void M_adaptComponents()
	{
		if( a_hasToUseSizeOfFileFrom )
		{
			jCb_sizeOfFilesFrom.setSelected( true );
			jTf_sizeOfFilesFrom.setEnabled( true );
			jB_loadConfigurationCorrespondingToFileSize.setEnabled(false);
		}
		else
		{
			jCb_sizeOfFilesFrom.setSelected( false );
			jTf_sizeOfFilesFrom.setEnabled( false );
			jB_loadConfigurationCorrespondingToFileSize.setEnabled(true);
		}
		jCb_sizeOfFilesFrom.setEnabled( false );
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
													a_vectorJpopupMenus,
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
		
	public void M_releaseResources()
	{
		a_intern=null;	// for the garbage collector to free the memory of the internationallization object and after the memory of this form
	}
	
	public void M_setFileEncoderParameters( FileEncoderParameters fep )
	{
		if( fep != null )
		{
			jCb_fileEncoderType.setSelected( true );
	//        jCheckBox2.setSelected( true );
			jCheckBox3.setSelected( true );
			jCheckBox4.setSelected( true );
			jCheckBox5.setSelected( true );
			jCheckBox6.setSelected( true );
			jCheckBox7.setSelected( true );

			jTF_fileEncoderType.setText( String.valueOf(fep.M_getFileEncoderType()) );
	//        jTextField2.setText( fep.M_getEncryptedFileExtension() );
			jTf_numBytesFileSlice.setText( String.valueOf(fep.M_getNumberOfBytesOfFileSlice()) );
			jTextField4.setText( String.valueOf(fep.M_getSizeOfNumbersSimpleEncoder()) );
			jTextField5.setText( String.valueOf(fep.M_getSizeOfNumbersReordererEncoder()) );
			jTextField6.setText( String.valueOf(fep.M_getNumberOfBitsPerIterationSimpleEncoder()) );
			jTextField7.setText( String.valueOf(fep.M_getNumberOfBitsPerIterationReordererEncoder()) );

			M_changeUnitNameOfSizeOfNumbers();
		}
	}

	public void M_setEncryptingConfiguration( EncryptingConfiguration ec )
	{
		if( ec != null )
		{
			jTf_sizeOfFilesFrom.setText( ec.M_getStrParamConfiguration( EncryptingConfiguration.STR_CONF_SIZE_FROM) );

			String fileEncoderType = ec.M_getStrParamConfiguration( EncryptingConfiguration.STR_CONF_FILE_ENCODER_TYPE);
			String numberOfBytesFileSlice = ec.M_getStrParamConfiguration( EncryptingConfiguration.STR_CONF_NUM_BYTES_FILE_SLICE);
			String sizeOfNumbersSimpleEncoder = ec.M_getStrParamConfiguration( EncryptingConfiguration.STR_CONF_SIZE_OF_NUMBERS_SIMPLE_ENCODER);
			String sizeOfNumbersReordererEncoder = ec.M_getStrParamConfiguration( EncryptingConfiguration.STR_CONF_SIZE_OF_NUMBERS_REORDERER_ENCODER);
			String numberOfBitsPerIterationSimpleEncoder = ec.M_getStrParamConfiguration( EncryptingConfiguration.STR_CONF_NUMBER_OF_BITS_PER_ITERATION_SIMPLE_ENCODER);
			String numberOfBitsPerIterationReordererEncoder = ec.M_getStrParamConfiguration( EncryptingConfiguration.STR_CONF_NUMBER_OF_BITS_PER_ITERATION_REORDERER_ENCODER);

			jCb_fileEncoderType.setSelected( (fileEncoderType != null) );
			jCheckBox3.setSelected( (numberOfBytesFileSlice != null) );
			jCheckBox4.setSelected( (sizeOfNumbersSimpleEncoder != null) );
			jCheckBox5.setSelected( (sizeOfNumbersReordererEncoder != null) );
			jCheckBox6.setSelected( (numberOfBitsPerIterationSimpleEncoder != null) );
			jCheckBox7.setSelected( (numberOfBitsPerIterationReordererEncoder != null) );

			if(jCb_fileEncoderType.isSelected() ) jTF_fileEncoderType.setText( fileEncoderType );
			else jTF_fileEncoderType.setText("");

			if(jCheckBox3.isSelected() ) jTf_numBytesFileSlice.setText( numberOfBytesFileSlice );
			else jTf_numBytesFileSlice.setText("");

			if(jCheckBox4.isSelected() ) jTextField4.setText( sizeOfNumbersSimpleEncoder );
			else jTextField4.setText("");

			if(jCheckBox5.isSelected() ) jTextField5.setText( sizeOfNumbersReordererEncoder );
			else jTextField5.setText("");

			if(jCheckBox6.isSelected() ) jTextField6.setText( numberOfBitsPerIterationSimpleEncoder );
			else jTextField6.setText("");

			if(jCheckBox7.isSelected() ) jTextField7.setText( numberOfBitsPerIterationReordererEncoder );
			else jTextField7.setText("");
			
			M_changeUnitNameOfSizeOfNumbers();
		}
	}
	
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCb_sizeOfFilesFrom = new javax.swing.JCheckBox();
        jLbl_sizeOfFilesFrom = new javax.swing.JLabel();
        jTF_fileEncoderType = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTf_numBytesFileSlice = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jCheckBox3 = new javax.swing.JCheckBox();
        jTextField4 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jCheckBox4 = new javax.swing.JCheckBox();
        jTextField5 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jCheckBox5 = new javax.swing.JCheckBox();
        jTextField6 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jCheckBox6 = new javax.swing.JCheckBox();
        jTextField7 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jCheckBox7 = new javax.swing.JCheckBox();
        jb_Ok = new javax.swing.JButton();
        jb_Cancel = new javax.swing.JButton();
        jLbl_SizeOfNumbersSimpleEncoderUnits = new javax.swing.JLabel();
        jLbl_SizeOfNumbersReordererEncoderUnits = new javax.swing.JLabel();
        jLbl_numBytesFileSlice = new javax.swing.JLabel();
        jCb_fileEncoderType = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jTf_sizeOfFilesFrom = new javax.swing.JTextField();
        jLbl_numBytesFileSlice1 = new javax.swing.JLabel();
        jB_loadConfigurationCorrespondingToFileSize = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(540, 378));
        setPreferredSize(new java.awt.Dimension(540, 378));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);
        getContentPane().add(jCb_sizeOfFilesFrom);
        jCb_sizeOfFilesFrom.setBounds(30, 50, 21, 21);

        jLbl_sizeOfFilesFrom.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLbl_sizeOfFilesFrom.setText("Size of files (from)");
        jLbl_sizeOfFilesFrom.setName("jLbl_sizeOfFilesFrom"); // NOI18N
        getContentPane().add(jLbl_sizeOfFilesFrom);
        jLbl_sizeOfFilesFrom.setBounds(60, 50, 300, 20);

        jTF_fileEncoderType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTF_fileEncoderTypeFocusLost(evt);
            }
        });
        getContentPane().add(jTF_fileEncoderType);
        jTF_fileEncoderType.setBounds(410, 80, 40, 20);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Encrypting configuration");
        jLabel2.setName("jLbl_title"); // NOI18N
        getContentPane().add(jLabel2);
        jLabel2.setBounds(113, 5, 260, 30);
        getContentPane().add(jTf_numBytesFileSlice);
        jTf_numBytesFileSlice.setBounds(390, 110, 60, 20);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("NumBytesFileSlice");
        jLabel4.setName("jLbl_NumBytesFileSlice"); // NOI18N
        getContentPane().add(jLabel4);
        jLabel4.setBounds(60, 110, 320, 20);
        getContentPane().add(jCheckBox3);
        jCheckBox3.setBounds(30, 110, 21, 21);
        getContentPane().add(jTextField4);
        jTextField4.setBounds(410, 140, 38, 20);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("SizeOfNumbersSimpleEncoder");
        jLabel5.setName("jLbl_SizeOfNumbersSimpleEncoder"); // NOI18N
        getContentPane().add(jLabel5);
        jLabel5.setBounds(60, 140, 330, 20);
        getContentPane().add(jCheckBox4);
        jCheckBox4.setBounds(30, 140, 21, 21);
        getContentPane().add(jTextField5);
        jTextField5.setBounds(410, 170, 38, 20);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("SizeOfNumbersReordererEncoder");
        jLabel6.setName("lLbl_SizeOfNumbersReordererEncoder"); // NOI18N
        getContentPane().add(jLabel6);
        jLabel6.setBounds(60, 170, 340, 20);
        getContentPane().add(jCheckBox5);
        jCheckBox5.setBounds(30, 170, 21, 21);
        getContentPane().add(jTextField6);
        jTextField6.setBounds(410, 200, 38, 20);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("NumBitsPerIterationSimpleEncoder");
        jLabel7.setName("jLbl_NumBitsPerIterationSimpleEncoder"); // NOI18N
        getContentPane().add(jLabel7);
        jLabel7.setBounds(60, 200, 340, 20);
        getContentPane().add(jCheckBox6);
        jCheckBox6.setBounds(30, 200, 21, 21);
        getContentPane().add(jTextField7);
        jTextField7.setBounds(410, 230, 38, 20);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("NumBitsPerIterationReordererEncoder");
        jLabel8.setName("jLbl_NumBitsPerIterationReordererEncoder"); // NOI18N
        getContentPane().add(jLabel8);
        jLabel8.setBounds(60, 230, 340, 20);
        getContentPane().add(jCheckBox7);
        jCheckBox7.setBounds(30, 230, 21, 21);

        jb_Ok.setText("OK");
        jb_Ok.setName("jBtn_Ok"); // NOI18N
        jb_Ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_OkActionPerformed(evt);
            }
        });
        getContentPane().add(jb_Ok);
        jb_Ok.setBounds(30, 310, 200, 23);

        jb_Cancel.setText("Cancel");
        jb_Cancel.setName("jBtn_Cancel"); // NOI18N
        jb_Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_CancelActionPerformed(evt);
            }
        });
        getContentPane().add(jb_Cancel);
        jb_Cancel.setBounds(290, 310, 210, 23);

        jLbl_SizeOfNumbersSimpleEncoderUnits.setText("bytes");
        getContentPane().add(jLbl_SizeOfNumbersSimpleEncoderUnits);
        jLbl_SizeOfNumbersSimpleEncoderUnits.setBounds(460, 140, 70, 20);

        jLbl_SizeOfNumbersReordererEncoderUnits.setText("bytes");
        getContentPane().add(jLbl_SizeOfNumbersReordererEncoderUnits);
        jLbl_SizeOfNumbersReordererEncoderUnits.setBounds(460, 170, 70, 20);

        jLbl_numBytesFileSlice.setText("bytes");
        jLbl_numBytesFileSlice.setName("jLbl_bytes_SizeOfFilesFrom"); // NOI18N
        getContentPane().add(jLbl_numBytesFileSlice);
        jLbl_numBytesFileSlice.setBounds(460, 50, 60, 20);
        getContentPane().add(jCb_fileEncoderType);
        jCb_fileEncoderType.setBounds(30, 80, 21, 21);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("FileEncoderType");
        jLabel3.setName("jLbl_FileEncoderType"); // NOI18N
        getContentPane().add(jLabel3);
        jLabel3.setBounds(60, 80, 340, 20);
        getContentPane().add(jTf_sizeOfFilesFrom);
        jTf_sizeOfFilesFrom.setBounds(370, 50, 80, 20);

        jLbl_numBytesFileSlice1.setText("bytes");
        jLbl_numBytesFileSlice1.setName("jLbl_bytes_numBytesFileSlice"); // NOI18N
        getContentPane().add(jLbl_numBytesFileSlice1);
        jLbl_numBytesFileSlice1.setBounds(460, 110, 60, 20);

        jB_loadConfigurationCorrespondingToFileSize.setText("Load configuration from file size");
        jB_loadConfigurationCorrespondingToFileSize.setName("jB_loadConfigurationCorrespondingToFileSize"); // NOI18N
        jB_loadConfigurationCorrespondingToFileSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_loadConfigurationCorrespondingToFileSizeActionPerformed(evt);
            }
        });
        getContentPane().add(jB_loadConfigurationCorrespondingToFileSize);
        jB_loadConfigurationCorrespondingToFileSize.setBounds(30, 280, 470, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jb_OkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_OkActionPerformed
        // TODO add your handling code here:

		try
		{
			checkIfTheConfigurationParametersAreGood(); // if exception is thrown, the parameters are not valid and the next instructions will not be execute

			if( a_parent != null ) a_parent.M_setJDial_encriptingConfigurationFromManualConfigurationApplied( this );
			M_saveInternationalization();
			a_userHasPressedOK = true;
			setVisible(false);
		}
		catch( FileEncoderException fe )
		{
			Utils.showMessageDialog(this,
				StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_PARAMETERS_NOT_VALID) + "\n" +
						fe.getMessage(),
					StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ENCRYPTING_CONFIGURATION_ERROR),
					JOptionPane.ERROR_MESSAGE);
		}
    }//GEN-LAST:event_jb_OkActionPerformed

    private void jb_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_CancelActionPerformed
        // TODO add your handling code here:
		M_saveInternationalization();
		setVisible(false);
    }//GEN-LAST:event_jb_CancelActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
		M_saveInternationalization();
		setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    private void jTF_fileEncoderTypeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTF_fileEncoderTypeFocusLost
        // TODO add your handling code here:
		M_changeUnitNameOfSizeOfNumbers();
    }//GEN-LAST:event_jTF_fileEncoderTypeFocusLost

    private void jB_loadConfigurationCorrespondingToFileSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_loadConfigurationCorrespondingToFileSizeActionPerformed
        // TODO add your handling code here:

		if( a_parent != null )
		{
			M_setEncryptingConfiguration( a_parent.M_getEncrytingConfigurationFromFileSize() );
		}
    }//GEN-LAST:event_jB_loadConfigurationCorrespondingToFileSizeActionPerformed

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
			java.util.logging.Logger.getLogger(JDial_encryptingConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(JDial_encryptingConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(JDial_encryptingConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(JDial_encryptingConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
        //</editor-fold>
		/* Create and display the dialog */
/*
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				JDial_encryptingConfiguration dialog = new JDial_encryptingConfiguration(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jB_loadConfigurationCorrespondingToFileSize;
    private javax.swing.JCheckBox jCb_fileEncoderType;
    private javax.swing.JCheckBox jCb_sizeOfFilesFrom;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLbl_SizeOfNumbersReordererEncoderUnits;
    private javax.swing.JLabel jLbl_SizeOfNumbersSimpleEncoderUnits;
    private javax.swing.JLabel jLbl_numBytesFileSlice;
    private javax.swing.JLabel jLbl_numBytesFileSlice1;
    private javax.swing.JLabel jLbl_sizeOfFilesFrom;
    private javax.swing.JTextField jTF_fileEncoderType;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTf_numBytesFileSlice;
    private javax.swing.JTextField jTf_sizeOfFilesFrom;
    private javax.swing.JButton jb_Cancel;
    private javax.swing.JButton jb_Ok;
    // End of variables declaration//GEN-END:variables

	public String[] M_getArgs()
	{
		String[] result = null;
		
		int size = 0;
        if( jCb_fileEncoderType.isSelected() ) size = size + 2;
//        if( jCheckBox2.isSelected() ) size = size + 2;
        if( jCheckBox3.isSelected() ) size = size + 2;
        if( jCheckBox4.isSelected() ) size = size + 2;
        if( jCheckBox5.isSelected() ) size = size + 2;
        if( jCheckBox6.isSelected() ) size = size + 2;
        if( jCheckBox7.isSelected() ) size = size + 2;

		result = new String[size];
		int index = 0;

        if( jCb_fileEncoderType.isSelected() )
		{
			result[index] = FileEncoderParameters.STR_FILE_ENCODER_TYPE_TAG;
			result[index+1] = jTF_fileEncoderType.getText();
			index = index + 2;
		}
/*
        if( jCheckBox2.isSelected() )
		{
			result[index] = FileEncoderParameters.STR_ENCRYPTED_FILE_EXTENSION_TAG;
			result[index+1] = jTextField2.getText();
			index = index + 2;
		}
*/
        if( jCheckBox3.isSelected() )
		{
			result[index] = FileEncoderParameters.STR_NUM_BYTES_FILE_SLICE_TAG;
			result[index+1] = jTf_numBytesFileSlice.getText();
			index = index + 2;
		}

        if( jCheckBox4.isSelected() )
		{
			result[index] = FileEncoderParameters.STR_SIZE_OF_NUMBERS_SIMPLE_ENCODER_TAG;
			result[index+1] = jTextField4.getText();
			index = index + 2;
		}

        if( jCheckBox5.isSelected() )
		{
			result[index] = FileEncoderParameters.STR_SIZE_OF_NUMBERS_REORDERER_ENCODER_TAG;
			result[index+1] = jTextField5.getText();
			index = index + 2;
		}

        if( jCheckBox6.isSelected() )
		{
			result[index] = FileEncoderParameters.STR_NUMBER_OF_BITS_PER_ITERATION_SIMPLE_ENCODER_TAG;
			result[index+1] = jTextField6.getText();
			index = index + 2;
		}

        if( jCheckBox7.isSelected() )
		{
			result[index] = FileEncoderParameters.STR_NUMBER_OF_BITS_PER_ITERATION_REORDERER_ENCODER_TAG;
			result[index+1] = jTextField7.getText();
			index = index + 2;
		}

		return( result );
	}

	public EncryptingConfiguration M_getEncryptingConfiguration(  )
	{
		EncryptingConfiguration result = new EncryptingConfiguration();
		
		if( jCb_sizeOfFilesFrom.isSelected() ) result.M_setStrParamConfiguration(EncryptingConfiguration.STR_CONF_SIZE_FROM, jTf_sizeOfFilesFrom.getText() );
		if( jCb_fileEncoderType.isSelected() ) result.M_setStrParamConfiguration(EncryptingConfiguration.STR_CONF_FILE_ENCODER_TYPE, jTF_fileEncoderType.getText() );
		if( jCheckBox3.isSelected() ) result.M_setStrParamConfiguration(EncryptingConfiguration.STR_CONF_NUM_BYTES_FILE_SLICE, jTf_numBytesFileSlice.getText() );
		if( jCheckBox4.isSelected() ) result.M_setStrParamConfiguration(EncryptingConfiguration.STR_CONF_SIZE_OF_NUMBERS_SIMPLE_ENCODER, jTextField4.getText() );
		if( jCheckBox5.isSelected() ) result.M_setStrParamConfiguration(EncryptingConfiguration.STR_CONF_SIZE_OF_NUMBERS_REORDERER_ENCODER, jTextField5.getText() );
		if( jCheckBox6.isSelected() ) result.M_setStrParamConfiguration(EncryptingConfiguration.STR_CONF_NUMBER_OF_BITS_PER_ITERATION_SIMPLE_ENCODER, jTextField6.getText() );
		if( jCheckBox7.isSelected() ) result.M_setStrParamConfiguration(EncryptingConfiguration.STR_CONF_NUMBER_OF_BITS_PER_ITERATION_REORDERER_ENCODER, jTextField7.getText() );

		return( result );
	}
	

    private void M_saveInternationalization()
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

	private void M_setUnitNameOfSizeOfNumbers( String unitName )
	{
		if( unitName != null )
		{
			jLbl_SizeOfNumbersSimpleEncoderUnits.setText( unitName );
			jLbl_SizeOfNumbersReordererEncoderUnits.setText( unitName );
		}
	}
	
	private void M_changeUnitNameOfSizeOfNumbers()
	{
		String unitName = null;
		Font font = jTF_fileEncoderType.getFont();
		int style = Font.PLAIN;
		Color fg_color = Color.BLACK;
		Color bg_color = Color.WHITE;
		
		if( jTF_fileEncoderType.getText().equals( String.valueOf( FileEncoderParameters.INT_FILE_ENCODER_TYPE_1 ) ) )
		{
			unitName = StringsConfiguration.M_getInstance().getProperty(StringsConfiguration.CONF_FILE_SIZE_OF_NUMBERS_UNITS_FILE_ENCODER_TYPE_1);
		}
		else if( jTF_fileEncoderType.getText().equals( String.valueOf( FileEncoderParameters.INT_FILE_ENCODER_TYPE_2 ) ) )
		{
			unitName = StringsConfiguration.M_getInstance().getProperty(StringsConfiguration.CONF_FILE_SIZE_OF_NUMBERS_UNITS_FILE_ENCODER_TYPE_2);
		}
		else
		{
			unitName = "";
			style = Font.BOLD;
			fg_color = Color.RED;
			bg_color = Color.YELLOW;
		}

		jTF_fileEncoderType.setFont( jTF_fileEncoderType.getFont().deriveFont( style ) );
		jTF_fileEncoderType.setForeground( fg_color );
		jTF_fileEncoderType.setBackground( bg_color );

		if( unitName != null ) M_setUnitNameOfSizeOfNumbers( unitName );
	}

	private void checkIfTheConfigurationParametersAreGood() throws FileEncoderException
	{
		if( a_hasToUseSizeOfFileFrom && jCb_sizeOfFilesFrom.isSelected() )
		{
			long size = -1;
			try
			{
				size = Long.parseLong( jTf_sizeOfFilesFrom.getText() );
			}
			catch( NumberFormatException nfe )
			{
				String message =	StringsConfiguration.M_getInstance().getProperty(StringsConfiguration.CONF_FILE_NUMBER_CONFIGURED_IN) + " " +
									jLbl_sizeOfFilesFrom.getText() + " " +
									StringsConfiguration.M_getInstance().getProperty(StringsConfiguration.CONF_FILE_HAS_TO_BE_A_CORRECT_LONG_INTEGER );
				throw( new FileEncoderException( message ) );
			}
			
			if( size < 0 )
			{
				String message =	StringsConfiguration.M_getInstance().getProperty(StringsConfiguration.CONF_FILE_NUMBER_CONFIGURED_IN) + " " +
									jLbl_sizeOfFilesFrom.getText() + " " +
									StringsConfiguration.M_getInstance().getProperty(StringsConfiguration.CONF_FILE_HAS_TO_BE_GREATER_THAN_OR_EQUAL_TO_ZERO );
				throw( new FileEncoderException( message ) );
			}
			
			if( ListOfEncryptingConfigurations.M_getInstance().M_vectorOfConfigurationsContains_SIZE_FROM(size) )
			{
				String message =	StringsConfiguration.M_getInstance().getProperty(StringsConfiguration.CONF_FILE_NUMBER_CONFIGURED_IN) + " " +
									jLbl_sizeOfFilesFrom.getText() + " " +
									StringsConfiguration.M_getInstance().getProperty(StringsConfiguration.CONF_FILE_IS_EQUAL_TO_THE_ONE_OF_ANOTHER_ENCRYIPTING_CONFIGURATION );
				throw( new FileEncoderException( message ) );
			}
		}
		
		FileEncoderParameters fep = new FileEncoderParameters( M_getArgs(), true, true );

		// if any of the parameters is wrong, any of the next two functions will throw an exception
		FileEncoderType fet = FileEncoder.M_newFileEncoderType( fep.M_getFileEncoderType(), null );
		fet.M_newEncoderDecoder(fep);
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

	public void M_changeFontSize( float factor )
	{
		if( a_intern != null )
		{
			a_intern.M_changeFontSize(factor);
		}
	}
}
