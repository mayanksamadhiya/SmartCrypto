/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.applications.fileencoderapplication;

import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.StringsConfiguration;
import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.ApplicationConfiguration;
import com.hotmail.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.imageio.ImageIO;

/**
 *
 * @author Usuario
 */
public class JDial_about_design extends javax.swing.JDialog {

	protected JFrameInternationalization a_intern = null;

	protected BufferedImage a_image = null;

	protected String a_title = "File Encoder Application";
	protected String a_version = "v1.0";
	protected final static String a_configurationBaseFileName = "JDial_about";

	protected static final String a_emailAddress = "frojasg1@hotmail.com";

	protected SelectionThread a_selectionThread = null;
	
	protected class SelectionThread extends Thread
	{
		protected JDial_about_design a_parent;
		protected boolean a_hasToStop;
		protected static final long sa_sleepTime = 33;
		
		public SelectionThread( JDial_about_design parent )
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
				for( int ii=0; (ii<a_parent.getText().length()-2) && !a_hasToStop; ii++ )
				{
					a_parent.setSelectionStart( ii );
					a_parent.setSelectionEnd( ii + 2 );
					
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
	public JDial_about_design(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();

		try
		{
			a_image = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("com/hotmail/frojasg1/applications/fileencoderapplication/resources/puzzle.redimensionado.png"));
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			a_image = null;
		}
		
		initInternationalization( parent );

		float factor = ApplicationConfiguration.M_getInstance().M_getApplicationFontSize();
		M_changeFontSize(factor);
		
		initComponentContents();
		
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
													true, null );

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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTa_text = new javax.swing.JTextArea();
        jBtn_sendEmail = new javax.swing.JButton();
        jBtn_exit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(655, 435));
        setPreferredSize(new java.awt.Dimension(655, 435));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        jTa_text.setEditable(false);
        jTa_text.setColumns(20);
        jTa_text.setRows(5);
        jScrollPane1.setViewportView(jTa_text);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(10, 130, 630, 230);

        jBtn_sendEmail.setText("Send e-mail to:  frojasg1@hotmail.com");
        jBtn_sendEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_sendEmailActionPerformed(evt);
            }
        });
        getContentPane().add(jBtn_sendEmail);
        jBtn_sendEmail.setBounds(10, 370, 630, 23);

        jBtn_exit.setText("Exit");
        jBtn_exit.setName("jBtn_exit"); // NOI18N
        jBtn_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_exitActionPerformed(evt);
            }
        });
        getContentPane().add(jBtn_exit);
        jBtn_exit.setBounds(540, 100, 100, 20);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtn_sendEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_sendEmailActionPerformed
        // TODO add your handling code here:

		try
		{
			Desktop.getDesktop().mail( new URI( "mailto:" + a_emailAddress ) );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

    }//GEN-LAST:event_jBtn_sendEmailActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
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

    }//GEN-LAST:event_formWindowClosing

    private void jBtn_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_exitActionPerformed
        // TODO add your handling code here:
		
		formWindowClosing(null);
		
    }//GEN-LAST:event_jBtn_exitActionPerformed

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtn_exit;
    private javax.swing.JButton jBtn_sendEmail;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTa_text;
    // End of variables declaration//GEN-END:variables

	protected Date getReleaseDate()
	{
		Calendar calendar = new GregorianCalendar();
		calendar.set( 2015, 3, 8 );
		Date result = calendar.getTime();
			
		return( result );
	}
	
	protected String createAboutText()
	{
		String result = a_title + " " + a_version + "\n";

		result = result +	StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_RELEASED_ON) + " " +
							DateFormat.getDateInstance().format(getReleaseDate()) + " ";
		result = result + StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_ABOUT_1) + "\n\n";
		result = result + StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_ABOUT_2) + "\n";
		result = result + StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_ABOUT_3) + "\n\n";
		result = result + StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_ABOUT_4) + "\n";
		result = result + StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_ABOUT_5) + "\n\n";
		result = result + StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_ABOUT_6) + " " + a_emailAddress + "\n";
		
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

	
	@Override
	public void paint(Graphics gc)
	{
		super.paint( gc );

		if(a_image!=null)
		{
			int width = 245;
			int height = ( new Double( ((double)width * (double)a_image.getHeight()) / (double) a_image.getWidth() ) ).intValue();
			int x = 200;
			int y = 40;
			
			gc.drawImage(a_image, x, y, x+width, y+height, 0, 0, a_image.getWidth(), a_image.getHeight(), null);
			gc.setColor( Color.BLACK );
			gc.drawRect( x, y, width, height );
			gc.drawRect( x+1, y+1, width-2, height-2 );
			gc.drawRect( x+2, y+2, width-4, height-4 );

			Font font = new Font("Tahoma", Font.BOLD, 17);
			gc.setFont(font);

			FontRenderContext frc = ((Graphics2D)gc).getFontRenderContext();
			Rectangle2D boundsTitle = font.getStringBounds(a_title, frc);
			
			int textX = x + (width-(int)boundsTitle.getWidth())/2;
			int textY = y + (height-(int)boundsTitle.getHeight())/2;
			
			gc.drawString(a_title, textX, textY);
			
			boundsTitle = font.getStringBounds(a_version, frc);
			textX = x + (width-(int)boundsTitle.getWidth())/2;
			gc.drawString(a_version, textX, textY+25);
		}

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
