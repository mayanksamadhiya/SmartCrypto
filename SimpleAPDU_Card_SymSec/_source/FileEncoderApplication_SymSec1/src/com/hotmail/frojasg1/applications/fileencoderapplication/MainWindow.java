/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJFrame.java
 *
 * Created on 03-abr-2010, 23:16:35
 */

package com.hotmail.frojasg1.applications.fileencoderapplication;

import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.StringsConfiguration;
import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.ApplicationConfiguration;
import com.hotmail.frojasg1.applications.fileencoder.configuration.ListOfEncryptingConfigurations;
import com.hotmail.frojasg1.applications.common.configuration.ConfigurationException;
import com.hotmail.frojasg1.applications.common.components.internationalization.InternException;
import com.hotmail.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.hotmail.frojasg1.applications.fileencoderapplication.utils.Utils;
import com.hotmail.frojasg1.encrypting.randomnumbers.RandomSource;
import com.hotmail.frojasg1.general.ResourceFunctions;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;



/**
 *
 * @author Fran
 */
public class MainWindow extends javax.swing.JFrame {

//	public static final String sa_applicationGroup = "frojasg1.apps";
//	public static final String sa_configurationGroup = "Frames";
	public static final String sa_configurationBaseFileName = "MainWindow";
	

	Properties a_configuration = null;

//	protected static String sa_applicationName = null;
 
	protected String a_configurationProfileName = null;
	protected String a_actualContactName = null;

	protected Vector<JPopupMenu> a_vectorJpopupMenus = null;

	protected JFrameInternationalization a_intern = null;

	protected String a_language = null;
	protected String a_additionalLanguage = null;

	protected static RandomSource a_rs = null;

	protected int a_newX = 0;
	protected int a_newY = 0;
	protected int a_numInstance = 1;

	protected static final String sa_homeWebPage = "http://frojasg1.com";

	/** Creates new form NewJFrame */
	public MainWindow( String windowName, String applicationName ) throws ConfigurationException
	{
		super( windowName );

		ApplicationConfiguration.sa_APPLICATION_NAME = applicationName;
		try
		{
			Thread.sleep(100);
		}
		catch( InterruptedException ie )
		{
			ie.printStackTrace();
		}

		try
		{
			ApplicationConfiguration.M_getInstance().M_openConfiguration();
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
			Utils.showMessageDialog(null, ce.getMessage() + " Exiting from application",
											"Configuration error", JOptionPane.ERROR_MESSAGE);
			throw( ce );
		}
		
		try
		{
			StringsConfiguration.M_getInstance().M_openConfiguration();
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
		}

		initComponents();

		a_vectorJpopupMenus = new Vector<JPopupMenu>();
		a_vectorJpopupMenus.add( jPopupMenuFileOperations );

		buttonGroupIdiomas.add(jRadioButtonMenuItemIdiomaEn);
		buttonGroupIdiomas.add(jRadioButtonMenuItemIdiomaEs);
		buttonGroupIdiomas.add(jRadioButtonMenuItemIdiomaOtro);

		// creates and loads the instance of list of encrypting configurations.
		ListOfEncryptingConfigurations.M_getInstance();

		a_intern = new JFrameInternationalization(	ApplicationConfiguration.sa_MAIN_FOLDER,
													ApplicationConfiguration.sa_APPLICATION_NAME,
													ApplicationConfiguration.sa_CONFIGURATION_GROUP,
													ApplicationConfiguration.sa_PATH_PROPERTIES_IN_JAR,
													sa_configurationBaseFileName,
													this,
													null,
													a_vectorJpopupMenus,
													true,
													null );
		
		M_applyNewApplicationConfiguration();
		
		setIcon();
	}

	protected void setIcon()
	{
		String resourceToImage = "com/hotmail/frojasg1/applications/fileencoderapplication/resources/puzzle.icon.png";
		BufferedImage image = ResourceFunctions.getResourceImage(resourceToImage);
		if( image != null ) setIconImage( image );
	}
	
	protected void M_applyNewApplicationConfiguration()
	{
		a_language = ApplicationConfiguration.M_getInstance().M_getStrParamConfiguration(ApplicationConfiguration.CONF_LANGUAGE);
		if( a_language == null ) a_language = ApplicationConfiguration.EN_LANGUAGE;
		a_additionalLanguage = ApplicationConfiguration.M_getInstance().M_getStrParamConfiguration(ApplicationConfiguration.CONF_ADDITIONAL_LANGUAGE);
		jRadioButtonMenuItemIdiomaOtro.setText( a_additionalLanguage );

		String idiomaTemp = a_language;
		a_language = "";
		marcaIdiomaEnMenu( idiomaTemp );

		if( a_language.equals( "" ) )	a_language = idiomaTemp;
		
		try
		{
		  a_intern.M_changeLanguage(a_language);
		}
		catch( InternException ex )
		{
		  ex.printStackTrace();
		}
		
		float factor = ApplicationConfiguration.M_getInstance().M_getApplicationFontSize();
		M_changeFontSizeInApplication( factor );
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenuFileOperations = new javax.swing.JPopupMenu();
        jPopUpMenuNew = new javax.swing.JMenuItem();
        buttonGroupIdiomas = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jMainDesktopPane = new javax.swing.JDesktopPane();
        jB_visitHomeWebPage = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuBarNew = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jRadioButtonMenuItemIdiomaEn = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemIdiomaEs = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemIdiomaOtro = new javax.swing.JRadioButtonMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMI_encryptingConfigurations = new javax.swing.JMenuItem();
        jMI_applicationConfiguration = new javax.swing.JMenuItem();
        jMen_about = new javax.swing.JMenu();
        jMI_about = new javax.swing.JMenuItem();

        jPopupMenuFileOperations.setName("jPopupMenuFileOperations"); // NOI18N

        jPopUpMenuNew.setText("New");
        jPopUpMenuNew.setName("jPopUpMenuNew"); // NOI18N
        jPopUpMenuNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPopUpMenuNewActionPerformed(evt);
            }
        });
        jPopupMenuFileOperations.add(jPopUpMenuNew);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setName("MainWindow"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Files");
        jLabel1.setToolTipText("");
        jLabel1.setName("jlabelFiles"); // NOI18N

        jMainDesktopPane.setBackground(new java.awt.Color(255, 255, 255));
        jMainDesktopPane.setComponentPopupMenu(jPopupMenuFileOperations);
        jMainDesktopPane.setName("jDesktopPane"); // NOI18N

        javax.swing.GroupLayout jMainDesktopPaneLayout = new javax.swing.GroupLayout(jMainDesktopPane);
        jMainDesktopPane.setLayout(jMainDesktopPaneLayout);
        jMainDesktopPaneLayout.setHorizontalGroup(
            jMainDesktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 815, Short.MAX_VALUE)
        );
        jMainDesktopPaneLayout.setVerticalGroup(
            jMainDesktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 582, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jMainDesktopPane);

        jB_visitHomeWebPage.setBackground(new java.awt.Color(255, 0, 0));
        jB_visitHomeWebPage.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jB_visitHomeWebPage.setForeground(new java.awt.Color(255, 255, 255));
        jB_visitHomeWebPage.setText("Visit home web page");
        jB_visitHomeWebPage.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jB_visitHomeWebPage.setContentAreaFilled(false);
        jB_visitHomeWebPage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jB_visitHomeWebPage.setOpaque(true);
        jB_visitHomeWebPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_visitHomeWebPageActionPerformed(evt);
            }
        });

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jMenu1.setText("File");
        jMenu1.setName("jMenu1Archivo"); // NOI18N

        jMenuBarNew.setText("New");
        jMenuBarNew.setName("jMenuBarNew"); // NOI18N
        jMenuBarNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuBarNewActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuBarNew);
        jMenu1.add(jSeparator1);

        jMenuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemExit.setText("Exit");
        jMenuItemExit.setName("jMenuItem1Salir"); // NOI18N
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemExit);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Tools");
        jMenu2.setName("jMenu2Herramientas"); // NOI18N

        jMenu3.setText("Language");
        jMenu3.setName("jMenu3Idioma"); // NOI18N

        jRadioButtonMenuItemIdiomaEn.setText("EN");
        jRadioButtonMenuItemIdiomaEn.setName(""); // NOI18N
        jRadioButtonMenuItemIdiomaEn.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButtonMenuItemIdiomaEnStateChanged(evt);
            }
        });
        jRadioButtonMenuItemIdiomaEn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonMenuItemIdiomaItemStateChanged(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItemIdiomaEn);

        jRadioButtonMenuItemIdiomaEs.setText("ES");
        jRadioButtonMenuItemIdiomaEs.setName(""); // NOI18N
        jRadioButtonMenuItemIdiomaEs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButtonMenuItemIdiomaEsStateChanged(evt);
            }
        });
        jRadioButtonMenuItemIdiomaEs.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonMenuItemIdiomaItemStateChanged(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItemIdiomaEs);

        jRadioButtonMenuItemIdiomaOtro.setText("CAT");
        jRadioButtonMenuItemIdiomaOtro.setName(""); // NOI18N
        jRadioButtonMenuItemIdiomaOtro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonMenuItemIdiomaItemStateChanged(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItemIdiomaOtro);

        jMenu2.add(jMenu3);
        jMenu2.add(jSeparator2);

        jMI_encryptingConfigurations.setText("Encrypting configurations");
        jMI_encryptingConfigurations.setName("jMI_encryptingConfigurations"); // NOI18N
        jMI_encryptingConfigurations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_encryptingConfigurationsActionPerformed(evt);
            }
        });
        jMenu2.add(jMI_encryptingConfigurations);

        jMI_applicationConfiguration.setText("Application Configuration");
        jMI_applicationConfiguration.setName("jMI_applicationConfiguration"); // NOI18N
        jMI_applicationConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_applicationConfigurationActionPerformed(evt);
            }
        });
        jMenu2.add(jMI_applicationConfiguration);

        jMenuBar1.add(jMenu2);

        jMen_about.setText("About");
        jMen_about.setName("jMen_about"); // NOI18N

        jMI_about.setText("About");
        jMI_about.setName("jMI_about"); // NOI18N
        jMI_about.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_aboutActionPerformed(evt);
            }
        });
        jMen_about.add(jMI_about);

        jMenuBar1.add(jMen_about);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jB_visitHomeWebPage, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(124, 124, 124))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jB_visitHomeWebPage))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItemExitActionPerformed
    {//GEN-HEADEREND:event_jMenuItemExitActionPerformed
	  // TODO add your handling code here:
	  formWindowClosing();
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
	  // TODO add your handling code here:
	  formWindowClosing();
    }//GEN-LAST:event_formWindowClosing



    private void formComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_formComponentResized
    {//GEN-HEADEREND:event_formComponentResized
	  // TODO add your handling code here:
/*
		int width = getWidth();
		int height = getHeight();
		jMainDesktopPane.setBounds( 10, 25, width-20, height-25  );
		
		System.out.println( "width = " + width + ", height = " + height );
*/
    }//GEN-LAST:event_formComponentResized

    private void jRadioButtonMenuItemIdiomaEsStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_jRadioButtonMenuItemIdiomaEsStateChanged
    {//GEN-HEADEREND:event_jRadioButtonMenuItemIdiomaEsStateChanged
	  // TODO add your handling code here:

    }//GEN-LAST:event_jRadioButtonMenuItemIdiomaEsStateChanged

    private void jRadioButtonMenuItemIdiomaEnStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_jRadioButtonMenuItemIdiomaEnStateChanged
    {//GEN-HEADEREND:event_jRadioButtonMenuItemIdiomaEnStateChanged
	  // TODO add your handling code here:


    }//GEN-LAST:event_jRadioButtonMenuItemIdiomaEnStateChanged

    private void jRadioButtonMenuItemIdiomaItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_jRadioButtonMenuItemIdiomaItemStateChanged
    {//GEN-HEADEREND:event_jRadioButtonMenuItemIdiomaItemStateChanged
		// TODO add your handling code here:

		if( evt.getItem() instanceof JRadioButtonMenuItem )
		{
			JRadioButtonMenuItem btn = (JRadioButtonMenuItem) evt.getItem();
			if( btn.isSelected() )
			{
				if( a_language.compareTo( btn.getText() ) != 0 )
				{
					try
					{
						M_changeLanguageInForms( btn.getText() );
						a_language = btn.getText();

						ApplicationConfiguration.M_getInstance().M_setStrParamConfiguration(ApplicationConfiguration.CONF_LANGUAGE, a_language);
						try
						{
							ApplicationConfiguration.M_getInstance().M_saveConfiguration();
						}
						catch( ConfigurationException ce )
						{
							ce.printStackTrace();
						}
					}
					catch( Throwable ex )
					{
						ex.printStackTrace();
						String idiomaTmp = a_language;
						a_language = btn.getText();
						marcaIdiomaEnMenu( idiomaTmp );
					}
					ponNombresIdiomas();
				}
			}
		}
    }//GEN-LAST:event_jRadioButtonMenuItemIdiomaItemStateChanged

    private void jPopUpMenuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPopUpMenuNewActionPerformed
		// TODO add your handling code here:
		FileJInternalFrame fjif = new FileJInternalFrame(	"File-" + a_numInstance,
															this,
															a_intern.M_getLanguage() );
		a_numInstance++;
		fjif.setLocation( a_newX, a_newY );
		
		if( a_newY == 125 )
		{
			a_newX = a_newX - 95;
			a_newY = 0;

			if( a_newX >= 120 )
			{
				a_newX = 0;
				a_newY = 0;
			}

		}
		else
		{
			a_newX = a_newX + 25;
			a_newY = a_newY + 25;
		}

		jMainDesktopPane.add(fjif,jMainDesktopPane.DEFAULT_LAYER);

//		fjif.show();
		fjif.setVisible(true);
		fjif.revalidate();
		fjif.repaint();
//		formComponentResized(null);
    }//GEN-LAST:event_jPopUpMenuNewActionPerformed

    private void jMenuBarNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuBarNewActionPerformed
		// TODO add your handling code here:
		jPopUpMenuNewActionPerformed(evt);
    }//GEN-LAST:event_jMenuBarNewActionPerformed

    private void jMI_encryptingConfigurationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_encryptingConfigurationsActionPerformed
        // TODO add your handling code here:
		
		JDial_listOfEncryptingConfigurations dialog = new JDial_listOfEncryptingConfigurations( this, a_language, true );
		dialog.setVisible(true);

    }//GEN-LAST:event_jMI_encryptingConfigurationsActionPerformed

    private void jMI_applicationConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_applicationConfigurationActionPerformed
        // TODO add your handling code here:
		
		JDial_applicationConfiguration dialog = new JDial_applicationConfiguration( this, a_language, true );
		dialog.setVisible(true);

		if( dialog.M_getUserHasPressedOK() )
		{
			M_applyNewApplicationConfiguration();
		}
		dialog.M_releaseResources();
		dialog.dispose();
		dialog = null;
    }//GEN-LAST:event_jMI_applicationConfigurationActionPerformed

    private void jMI_aboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_aboutActionPerformed
        // TODO add your handling code here:

		JDial_about dialog = new JDial_about( this, true );
		dialog.setVisible(true);
		dialog = null;

    }//GEN-LAST:event_jMI_aboutActionPerformed

    private void jB_visitHomeWebPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_visitHomeWebPageActionPerformed
        // TODO add your handling code here:

        if(Desktop.isDesktopSupported())
		{
            Desktop desktop = Desktop.getDesktop();
            try
			{
                desktop.browse(new URI(sa_homeWebPage));
            }
			catch (IOException e )
			{
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
			catch( URISyntaxException e)
			{
                // TODO Auto-generated catch block
                e.printStackTrace();
			}
        }
		else
		{
            Runtime runtime = Runtime.getRuntime();
			String command = null;
			if( System.getProperty("os.name").toLowerCase().indexOf( "win" ) >= 0)
			{
				command = "cmd /k start " + sa_homeWebPage;
			}
			else if( System.getProperty("os.name").toLowerCase().indexOf( "mac" ) >= 0)
			{
				command = "open " + sa_homeWebPage;
			}
			else if( System.getProperty("os.name").toLowerCase().indexOf( "nix" ) >= 0 ||
					System.getProperty("os.name").toLowerCase().indexOf( "nux" ) >= 0 )
			{
				String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
                                 "netscape","opera","links","lynx"};

				StringBuffer cmd = new StringBuffer();
				for (int i=0; i<browsers.length; i++)
					cmd.append( (i==0  ? "" : " || " ) + browsers[i] +" \"" + sa_homeWebPage + "\" ");

				try
				{
					runtime.exec(new String[] { "sh", "-c", cmd.toString() });
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if( command != null )
			{
				try
				{
					runtime.exec(command);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        }

    }//GEN-LAST:event_jB_visitHomeWebPageActionPerformed

	/**
	* @param args the command line arguments
	*/
	public static void main(String args[]) {

		
		/*
			Result of this parameter for different operating systems.
			O.S. name:Linux
			O.S. name:Mac OS X
			O.S. name:Windows 8
		*/
		System.out.println( "O.S. name:" + System.getProperty("os.name") );
		System.out.println( "O.S. arch:" + System.getProperty("os.arch") );
		System.out.println( "O.S. version:" + System.getProperty("os.version") );
		a_rs = RandomSource.M_getInstanceOf();

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try
				{
					new MainWindow("File Encoder Application", "FileEncoderApplication" ).setVisible(true);
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}
		});
		a_rs.setMustStop(true);
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupIdiomas;
    private javax.swing.JButton jB_visitHomeWebPage;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuItem jMI_about;
    private javax.swing.JMenuItem jMI_applicationConfiguration;
    private javax.swing.JMenuItem jMI_encryptingConfigurations;
    private javax.swing.JDesktopPane jMainDesktopPane;
    private javax.swing.JMenu jMen_about;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuBarNew;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jPopUpMenuNew;
    private javax.swing.JPopupMenu jPopupMenuFileOperations;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemIdiomaEn;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemIdiomaEs;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemIdiomaOtro;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    // End of variables declaration//GEN-END:variables

	protected void marcaIdiomaEnMenu( String idioma )
	{
		try
		{
			if( ( idioma.compareTo(jRadioButtonMenuItemIdiomaEs.getText()) == 0 ) &&
				! jRadioButtonMenuItemIdiomaEs.isSelected() )
					jRadioButtonMenuItemIdiomaEs.setSelected(true);
			else if ( ( idioma.compareTo( jRadioButtonMenuItemIdiomaEn.getText()) == 0 )  &&
				! jRadioButtonMenuItemIdiomaEn.isSelected() )
					jRadioButtonMenuItemIdiomaEn.setSelected(true);
			else if ( ( idioma.compareTo( jRadioButtonMenuItemIdiomaOtro.getText() ) == 0 )  &&
				! jRadioButtonMenuItemIdiomaOtro.isSelected() )
					jRadioButtonMenuItemIdiomaOtro.setSelected(true);
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	protected void formWindowClosing()
	{
		setConfigurationChanges();

		try
		{
			ApplicationConfiguration.M_getInstance().M_saveConfiguration();
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
		}

		try
		{
			StringsConfiguration.M_getInstance().M_saveConfiguration();
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
		}

		if( a_intern != null )
		{
			try
			{
				a_intern.saveConfiguration();
			}
			catch( InternException ex )
			{
				ex.printStackTrace();
			}
		}

		setVisible(false);
		dispose();
	}

	protected void setConfigurationChanges()
	{
		ApplicationConfiguration.M_getInstance().M_setStrParamConfiguration( ApplicationConfiguration.CONF_LANGUAGE, a_language );
	}

	protected void addProperty( Properties prop, String label, String value )
	{
		prop.setProperty( label, value);
	}

	protected void ponNombresIdiomas()
	{
		jRadioButtonMenuItemIdiomaEn.setText( "EN" );
		jRadioButtonMenuItemIdiomaEs.setText( "ES" );
		jRadioButtonMenuItemIdiomaOtro.setText( a_additionalLanguage );
	}

	protected void M_setTextJButton_visitWebPage()
	{
		jB_visitHomeWebPage.setText( StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_VISIT_HOME_PAGE) + " " +
										sa_homeWebPage );
	}
	
	protected void M_changeLanguageInFormsIntern( String language, boolean withThrow ) throws InternException, ConfigurationException
	{
		try
		{
			StringsConfiguration.M_getInstance().M_changeLanguage(language);
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
			if( withThrow ) throw( ce );
		}

		M_setTextJButton_visitWebPage();
		
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
			if( withThrow ) throw( ie );
		}
		
		JInternalFrame[] ifArray = jMainDesktopPane.getAllFrames();
		for( int ii=0; ii<ifArray.length; ii++ )
		{
			if( ifArray[ii] instanceof FileJInternalFrame)
			{
				FileJInternalFrame jif = (FileJInternalFrame) ifArray[ii];
				
				try
				{
					jif.M_changeLanguage( language );
				}
				catch( ConfigurationException ce )
				{
					ce.printStackTrace();
					if( withThrow ) throw( ce );
				}
				catch( InternException ie )
				{
					ie.printStackTrace();
					if( withThrow ) throw( ie );
				}
			}
		}
	}
	
	protected void M_changeLanguageInForms( String language ) throws InternException, ConfigurationException
	{
		String languageOld = a_language;
		try
		{
			StringsConfiguration.M_getInstance().M_saveConfiguration();
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
		}

		try
		{
			M_changeLanguageInFormsIntern( language, true );
		}
		catch( InternException ie )
		{
			ie.printStackTrace();
			M_changeLanguageInFormsIntern( languageOld, false );
			throw( ie );
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
			M_changeLanguageInFormsIntern( languageOld, false );
			throw( ce );
		}
		a_language = language;
		jRadioButtonMenuItemIdiomaOtro.setText( a_additionalLanguage );
	}

	protected void M_changeFontSizeInApplication( float factor )
	{
		if( a_intern != null )
		{
			a_intern.M_changeFontSize( factor );
		}

		JInternalFrame[] ifArray = jMainDesktopPane.getAllFrames();
		for( int ii=0; ii<ifArray.length; ii++ )
		{
			if( ifArray[ii] instanceof FileJInternalFrame)
			{
				FileJInternalFrame jif = (FileJInternalFrame) ifArray[ii];
				
				jif.M_changeFontSize( factor );
			}
		}

	}
}
