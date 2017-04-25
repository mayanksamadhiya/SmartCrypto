/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.applications.fileencoderapplication;

import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.StringsConfiguration;
import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.ApplicationConfiguration;
import com.hotmail.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.hotmail.frojasg1.general.DateFunctions;
import com.hotmail.frojasg1.graphics.lens.Lens;
import com.hotmail.frojasg1.graphics.lens.LensJPanel;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import static java.lang.Thread.sleep;
import java.net.URI;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JTextArea;

/**
 *
 * @author Usuario
 */
public class JDial_about extends javax.swing.JDialog
{

	protected JFrameInternationalization a_intern = null;

	protected BufferedImage a_image = null;

	protected String a_title = "File Encoder Application";
	protected String a_version = "v1.1";
	protected final static String a_configurationBaseFileName = "JDial_about";

	protected static final String a_emailAddress = "frojasg1@hotmail.com";

	protected SelectionThread a_selectionThread = null;
	
	protected JPanel_about a_jPanelAbout = null;

	protected LensJPanel a_lensJPanel = null;
	protected Object _mutex = null;
/*
	protected class JTextAreaAbout extends JTextArea
	{

		protected class CaretAbout extends DefaultCaret
		{
			public CaretAbout()
			{
				super();
			}
			
			public void paint( Graphics gc )
			{
//				if( isVisible() )	super.paint(gc);
			}
		}

		protected JTextAreaAbout()
		{
			super();
//			setCaret( new CaretAbout() );
			
		}

		@Override
		public void setSelectionStart( int start )
		{
			getCaret().setDot(start);
		}

		@Override
		public void setSelectionEnd( int end )
		{
			getCaret().moveDot(end);
		}


		public void setCaretVisible( boolean visible )
		{
			getCaret().setVisible(visible);
			getCaret().setSelectionVisible(visible);
		}
		
		public void paint( Graphics gc )
		{
			if( getCaret().isVisible() )
			{
				super.paint( gc );
			}
		}

		public void update( Graphics gc )
		{
			if( getCaret().isVisible() )
			{
				super.update( gc );
			}
		}
	}
*/	
	protected class SelectionThread extends Thread
	{
		protected JDial_about a_parent;
		protected boolean a_hasToStop;
		protected static final long sa_sleepTime = 33;
		protected static final int sa_increment = 1;

		public SelectionThread( JDial_about parent )
		{
			super();
			a_parent=parent;
			a_hasToStop = false;
		}
		
		public void M_stop()
		{
			a_hasToStop = true;
		}
		
		public void run()
		{
			while( ! a_hasToStop && (a_parent != null) )
			{
				for( int ii=0; (ii<a_parent.getText().length()-2-sa_increment) && !a_hasToStop; ii = ii + sa_increment )
				{
					synchronized( _mutex )
					{
						a_parent.setSelectionStart( ii );
						a_parent.setSelectionEnd( ii + 2 );
					
						a_parent.repaint();
					}

					try
					{
						sleep( sa_sleepTime );
					}
					catch( Throwable th )
					{
						th.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Creates new form JDial_about
	 */
	public JDial_about(java.awt.Frame parent, boolean modal) {
		super(parent, modal);

		_mutex = new Object();
		
		initComponents();

		initComponentContents();
		
		initInternationalization( parent );

		float factor = ApplicationConfiguration.M_getInstance().M_getApplicationFontSize();
		M_changeFontSize(factor);
		
		jTa_text.requestFocusInWindow();
	}

	public String getText()
	{
		return( jTa_text.getText() );
	}
	
	public void setSelectionStart( int start )
	{
		jTa_text.setSelectionStart(start);
	}

	public void setSelectionEnd( int end )
	{
		jTa_text.setSelectionEnd(end);
	}
	
	
	protected void initInternationalization( Component parent )
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
			String language = ApplicationConfiguration.M_getInstance().M_getStrParamConfiguration(ApplicationConfiguration.CONF_LANGUAGE);
			a_intern.M_changeLanguage( language );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

		initJPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(655, 435));
        setPreferredSize(new java.awt.Dimension(655, 435));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(a_lensJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(a_lensJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

	private void initJPanel()
	{
		jScrollPane1 = new javax.swing.JScrollPane();
//        jTa_text = new JTextAreaAbout();
        jTa_text = new JTextArea();
        jBtn_sendEmail = new javax.swing.JButton();
        jBtn_exit = new javax.swing.JButton();

		a_jPanelAbout = new JPanel_about( this );
		a_jPanelAbout.setName( "aboutJPanel" );
		
        a_jPanelAbout.setLayout(null);

        jTa_text.setEditable(false);
        jTa_text.setColumns(20);
        jTa_text.setRows(5);
		
		jTa_text.getCaret().setVisible( false );
		
        jScrollPane1.setViewportView(jTa_text);

		jTa_text.removeNotify();		// for the component not displaying in the screen. The component will be painted specially
		
        a_jPanelAbout.add(jScrollPane1);
        jScrollPane1.setBounds(10, 130, 630, 230);

        jBtn_sendEmail.setText("Send e-mail to:  frojasg1@hotmail.com");
        jBtn_sendEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_sendEmailActionPerformed(evt);
            }
        });
        a_jPanelAbout.add(jBtn_sendEmail);
        jBtn_sendEmail.setBounds(10, 370, 630, 23);

        jBtn_exit.setText("Exit");
        jBtn_exit.setName("jBtn_exit"); // NOI18N
        jBtn_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_exitActionPerformed(evt);
            }
        });

        a_jPanelAbout.add(jBtn_exit);
        jBtn_exit.setBounds(540, 100, 100, 20);

		a_lensJPanel = new LensJPanel( a_jPanelAbout, 125, Lens.SA_MODE_AMPLIFY, true, _mutex );

		a_lensJPanel.setName( "lensJPanel" );
		a_lensJPanel.M_addComponentNotToPaint(jTa_text);

//        pack();
	}
	
    private void jBtn_sendEmailActionPerformed(java.awt.event.ActionEvent evt) {                                               
        // TODO add your handling code here:

		try
		{
			Desktop.getDesktop().mail( new URI( "mailto:" + a_emailAddress ) );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

    }                                              

    private void formWindowClosing(java.awt.event.WindowEvent evt) {                                   
        // TODO add your handling code here:

		if( a_selectionThread != null )	a_selectionThread.M_stop();

		M_saveInternationalization();
		setVisible(false);
		M_releaseResources();

		while( a_selectionThread.isAlive() )
		{
			try
			{
				Thread.sleep( 100 );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
		a_selectionThread = null;

    }                                  

    private void jBtn_exitActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
		
		formWindowClosing(null);
		
    }                                         

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
			java.util.logging.Logger.getLogger(JDial_about_design.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(JDial_about_design.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(JDial_about_design.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(JDial_about_design.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
        //</editor-fold>
        //</editor-fold>

		/* Create and display the dialog */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				JDial_about_design dialog = new JDial_about_design(new javax.swing.JFrame(), true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify                     
    private javax.swing.JButton jBtn_exit;
    private javax.swing.JButton jBtn_sendEmail;
    private javax.swing.JScrollPane jScrollPane1;
//    private JTextAreaAbout jTa_text;
    private JTextArea jTa_text;
	// End of variables declaration                   

	protected Date getReleaseDate()
	{
		Calendar calendar = new GregorianCalendar();
		calendar.set( 2016, Calendar.JANUARY, 30 );
		Date result = calendar.getTime();

		return( result );
	}

	protected String createAboutText()
	{
		String result = a_title + " " + a_version + "\n";

		String tmpStr = StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_RELEASED_ON) + " " +
							DateFunctions.formatDate_yyyy( getReleaseDate(), DateFormat.SHORT ) + " ";

		String baseLabel = StringsConfiguration.CONF_ABOUT_1.substring(0,StringsConfiguration.CONF_ABOUT_1.length()-1);
		int index=1;
		while( tmpStr != null )
		{
			result = result + tmpStr;
			String label = String.format( "%s%d", baseLabel, index );
			tmpStr = StringsConfiguration.M_getInstance().M_getStrParamConfiguration( label );
			index++;
		}

		result = result + " " + a_emailAddress + "\n";

		String spaces = "                                                  ";

		result = result + spaces + "\n" + spaces;

		return( result );
	}

	protected void initComponentContents()
	{
		String text = createAboutText();
		jTa_text.setWrapStyleWord(true);
		jTa_text.setLineWrap( true );
		jTa_text.setText( text );

		jTa_text.setSelectionStart(0);
		jTa_text.setSelectionEnd(0);

		jBtn_sendEmail.setText( StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_SEND_EMAIL_TO) + " " +
								a_emailAddress );		
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
	
	@Override
	public void setVisible( boolean visible )
	{
		if( visible )
		{
			a_selectionThread = new SelectionThread( this );
			a_selectionThread.start();
		}

		super.setVisible( visible );
	}
}