/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hotmail.frojasg1.applications.common.components.internationalization;

import com.hotmail.frojasg1.applications.common.configuration.ConfigurationException;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextComponent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Fran
 */
public class JFrameInternationalization implements ComponentListener, WindowStateListener
{
	protected float a_lastFactor = 1.0F;

	protected FormLanguageConfiguration a_formLanguageConfiguration = null;
	protected FormGeneralConfiguration a_formGeneralConfiguration = null;

	protected Properties a_languageProperties = null;		// except on the constructor, it is the same as a_formLanguageConfiguration
	protected Properties a_generalProperties = null;		// except on the constructor, it is the same as a_formGeneralConfiguration
	
	protected static String	sa_dirSeparator = System.getProperty( "file.separator" );
	protected static String	sa_lineSeparator = System.getProperty( "line.separator" );

	protected static final String TXT_TEXT = "TEXT";
	protected static final String TXT_ELEMENT_AT = "ELEMENT_AT";
	protected static final String TXT_LABEL = "LABEL";
	protected static final String TXT_ITEM = "ITEM";
	protected static final String TXT_TITLE = "TITLE";
	protected static final String TXT_BORDER = "BORDER";
	protected static final String TXT_DIVIDER_LOCATION = "DIVIDER_LOCATION";

	protected Vector<JPopupMenu> a_vectorJpopupMenus = null;
	protected Component a_parentFrame = null;
	protected Component a_parentParentFrame = null;
	protected String a_configurationBaseFileName = null;
	
	protected Map< Component, ResizeRelocateItem >	a_mapResizeRelocateComponents = null;
	protected Map< Component, InfoForResizingPanels > a_mapResizingPanels = null;

	protected boolean a_hasComponentListenerBeenSet = false;
	
	protected ComponentListener a_componentListener_this = null;
	protected WindowStateListener a_windowStateListener_this = null;
	
	public JFrameInternationalization(	String mainFolder,
										String applicationName,
										String group,
										String paquetePropertiesIdiomas,
										String configurationBaseFileName,
										Component parentFrame,
										Component parentParentFrame,
										Vector<JPopupMenu> vPUMenus,
										boolean hasToPutWindowPosition,
										MapResizeRelocateComponentItem map
									)
	{
		a_vectorJpopupMenus = vPUMenus;
		a_parentFrame = parentFrame;
		a_parentParentFrame = parentParentFrame;

		a_mapResizeRelocateComponents = new Hashtable< Component, ResizeRelocateItem >();
		a_mapResizingPanels = new Hashtable< Component, InfoForResizingPanels >();

		a_configurationBaseFileName = configurationBaseFileName;
		String languageConfigurationFileName = configurationBaseFileName + "_LAN.properties";
		String generalConfigurationFileName = configurationBaseFileName + "_GEN.properties";

		a_languageProperties = new Properties();
		a_generalProperties = new Properties();
		try
		{
			convertAttributesIntoProperties( a_parentFrame, 0 );
		}
		catch (InternException ex)
		{
			ex.printStackTrace();
		}

		a_formLanguageConfiguration = new FormLanguageConfiguration(
								mainFolder,
								applicationName, group,
								languageConfigurationFileName,
								paquetePropertiesIdiomas,
								a_languageProperties);
		a_formGeneralConfiguration = new FormGeneralConfiguration(
								mainFolder,
								applicationName, group,
								generalConfigurationFileName,
								a_generalProperties);

		if( map != null )
		{
			resizeOrRelocateComponent( a_parentFrame, true );	// we get the information of the root panels for maximize event.
			a_mapResizeRelocateComponents = map;
		}

		try
		{
			a_formGeneralConfiguration.M_openConfiguration();
//			if( hasToPutWindowPosition && !a_formGeneralConfiguration.M_isFirstTime() ) putWindowPosition();
			putWindowPosition(hasToPutWindowPosition);
			resizeOrRelocateComponent();
			convertPropertiesIntoAttributes( a_parentFrame, 0 );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		a_languageProperties = a_formLanguageConfiguration;
		a_generalProperties = a_formGeneralConfiguration;
		
		a_lastFactor = 1.0F;
		
		setListeners();
	}

	protected void putWindowPosition(boolean hasToPutWindowPosition)
	{
		try
		{
			if( !a_formGeneralConfiguration.M_isFirstTime() )
			{
				if( hasToPutWindowPosition )
				{
					a_parentFrame.setBounds(	(int) a_formGeneralConfiguration.M_getIntParamConfiguration( FormGeneralConfiguration.CONF_POSICION_X),
												(int) a_formGeneralConfiguration.M_getIntParamConfiguration(FormGeneralConfiguration.CONF_POSICION_Y),
												(int) a_formGeneralConfiguration.M_getIntParamConfiguration(FormGeneralConfiguration.CONF_ANCHO_VENTANA),
												(int) a_formGeneralConfiguration.M_getIntParamConfiguration(FormGeneralConfiguration.CONF_ALTO_VENTANA)
											);
				}
				else if( a_parentFrame instanceof Frame )
				{
					Frame frame = (Frame) a_parentFrame;
					if( frame.isResizable() )
					{
						a_parentFrame.setSize(	(int) a_formGeneralConfiguration.M_getIntParamConfiguration(FormGeneralConfiguration.CONF_ANCHO_VENTANA),
												(int) a_formGeneralConfiguration.M_getIntParamConfiguration(FormGeneralConfiguration.CONF_ALTO_VENTANA)
											);
					}
				}
				else
				{
					a_parentFrame.setSize(	(int) a_formGeneralConfiguration.M_getIntParamConfiguration(FormGeneralConfiguration.CONF_ANCHO_VENTANA),
											(int) a_formGeneralConfiguration.M_getIntParamConfiguration(FormGeneralConfiguration.CONF_ALTO_VENTANA)
										);
				}
			}
			else if( (a_parentParentFrame != null) && hasToPutWindowPosition )
				a_parentFrame.setLocation(	new Double( a_parentParentFrame.getLocationOnScreen().getX() ).intValue(),
											new Double( a_parentParentFrame.getLocationOnScreen().getY() ).intValue() + 50 );
			else if ( hasToPutWindowPosition )
			{
				int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
				int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
				a_parentFrame.setLocation( width/2 - a_parentFrame.getWidth()/2, height/2 - a_parentFrame.getHeight()/2 );
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	protected void getConfigurationChanges()
	{
		a_formGeneralConfiguration.M_setIntParamConfiguration(FormGeneralConfiguration.CONF_ALTO_VENTANA, a_parentFrame.getHeight() );
		a_formGeneralConfiguration.M_setIntParamConfiguration(FormGeneralConfiguration.CONF_ANCHO_VENTANA, a_parentFrame.getWidth() );
		a_formGeneralConfiguration.M_setIntParamConfiguration(FormGeneralConfiguration.CONF_POSICION_X, a_parentFrame.getX() );
		a_formGeneralConfiguration.M_setIntParamConfiguration(FormGeneralConfiguration.CONF_POSICION_Y, a_parentFrame.getY() );
	}

	public void M_saveGeneralConfiguration() throws ConfigurationException
	{
		getConfigurationChanges();
		a_formGeneralConfiguration.M_saveConfiguration();
	}
	
	public void saveConfiguration( ) throws InternException
	{
		try
		{
			convertAttributesIntoProperties( a_parentFrame, 0 );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		try
		{
			M_saveGeneralConfiguration();
			saveLanguageConfiguration();
		}
		catch( ConfigurationException ex )
		{
			ex.printStackTrace();
			throw new InternException( "EXCEPTION: Saving form configuration of form : " +
										a_configurationBaseFileName +
										sa_lineSeparator + ex.getMessage() );
		}
	}

	protected void convertJPopUpMenuTextsIntoProperties( JPopupMenu jpumnu )
	{
		String name = jpumnu.getName();
//		addProperty( prop, name + "." + TXT_TEXT, jpumnu.getText() );
	}

	protected void convertAbstractButtonTextsIntoProperties( AbstractButton absbtn ) throws InternException
	{
		String name = absbtn.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			addProperty( a_languageProperties, name + "." + TXT_TEXT, absbtn.getText() );

			if( absbtn instanceof JMenu )
			{
				JMenu jmnu = (JMenu) absbtn;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					convertAttributesIntoProperties( jmnu.getMenuComponent( ii ), 2 );
			}
		}
	}

	protected void convertJLabelTextsIntoProperties( JLabel jlbl )
	{
		String name = jlbl.getName();
		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_languageProperties, name + "." + TXT_TEXT, jlbl.getText() );
	}

	protected void convertJListTextsIntoProperties( JList jlst )
	{
		String name = jlst.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			ListModel lm = jlst.getModel();
			for( int ii=0; ii<lm.getSize(); ii++ )
				addProperty( a_languageProperties, name + "." + TXT_ELEMENT_AT + "." + String.valueOf(ii), lm.getElementAt(ii).toString() );
		}
	}

	protected void convertJTextComponentTextsIntoProperties( JTextComponent jtxtcmp )
	{
		String name = jtxtcmp.getName();
		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_languageProperties, name + "." + TXT_TEXT, jtxtcmp.getText() );
	}

	protected void convertJSplitPaneAttributesIntoProperties( JSplitPane jsp ) throws InternException
	{
		String name = jsp.getName();

		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_generalProperties, name + "." + TXT_DIVIDER_LOCATION, String.valueOf( jsp.getDividerLocation() ) );
	}

	protected void convertContainerAttributesIntoProperties( Container contnr ) throws InternException
	{
		String name = contnr.getName();
		
		if( ( name != null ) && ( !name.equals("") ) )
		{
			if( contnr instanceof JComponent )
			{
				JComponent jcomp = (JComponent) contnr;
				if( jcomp.getBorder() instanceof TitledBorder )
				{
					TitledBorder tb = (TitledBorder) jcomp.getBorder();
					addProperty( a_languageProperties, name + "." + TXT_BORDER + "." + TXT_TITLE, tb.getTitle() );
				}
			}

			
			if( contnr instanceof JSplitPane )
			{
				JSplitPane jsp = (JSplitPane) contnr;
				convertJSplitPaneAttributesIntoProperties( jsp );
			}
			if( contnr instanceof AbstractButton )
			{
				AbstractButton absbtn = (AbstractButton) contnr;
				convertAbstractButtonTextsIntoProperties( absbtn );
			}
			else if( contnr instanceof JLabel )
			{
				JLabel jlbl = (JLabel) contnr;
				convertJLabelTextsIntoProperties( jlbl );
			}
			else if( contnr instanceof JList )
			{
				JList jlst = (JList) contnr;
				convertJListTextsIntoProperties( jlst );
			}
			else if( contnr instanceof JTextComponent )
			{
				JTextComponent jtxtcmp = (JTextComponent) contnr;
				convertJTextComponentTextsIntoProperties( jtxtcmp );
			}
			else if( contnr instanceof JPopupMenu )
			{
				JPopupMenu jpumnu = (JPopupMenu) contnr;
				convertJPopUpMenuTextsIntoProperties( jpumnu );
			}
			else if( contnr instanceof JFrame )
			{
				JFrame jfr = (JFrame) contnr;
				convertJFrameTextsIntoProperties( jfr );
			}
			else if( contnr instanceof JInternalFrame )
			{
				JInternalFrame jif = (JInternalFrame) contnr;
				convertJInternalFrameTextsIntoProperties( jif );
			}
		}
	}

	protected void addProperty( Properties prop, String label, String value )
	{
		prop.setProperty( label, value);
	}

	protected void convertButtonTextsIntoProperties( Button btn )
	{
		String name = btn.getName();
		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_languageProperties, name + "." + TXT_LABEL, btn.getLabel() );
	}

	protected void convertCheckBoxTextsIntoProperties( Checkbox ckb )
	{
		String name = ckb.getName();
		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_languageProperties, name + "." + TXT_LABEL, ckb.getLabel() );
	}

	protected void convertChoiceTextsIntoProperties( Choice chc )
	{
		String name = chc.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			for( int ii=0; ii<chc.getItemCount(); ii++ )
				addProperty( a_languageProperties, name + "." + TXT_ITEM + "." + String.valueOf(ii), chc.getItem(ii) );
		}
	}

	protected void convertLabelTextsIntoProperties( Label lbl )
	{
		String name = lbl.getName();
		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_languageProperties, name + "." + TXT_TEXT, lbl.getText() );
	}

	protected void convertListTextsIntoProperties( List lst )
	{
//		String name = lst.getName();
	}

	protected void convertTextComponentTextsIntoProperties( TextComponent txtcmp )
	{
		String name = txtcmp.getName();
	}

	protected void convertJFrameTextsIntoProperties( JFrame jfr )
	{
		String name = jfr.getName();
		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_languageProperties, name + "." + TXT_TITLE, jfr.getTitle() );
	}

	protected void convertJInternalFrameTextsIntoProperties( JInternalFrame jif )
	{
		String name = jif.getName();

//		addProperty( prop, name + "." + TXT_TITLE, jif.getTitle() );
	}

	protected void convertAttributesIntoProperties( Component comp, int level ) throws InternException
	{
		level = level + 1;
		if( level == 1 )
		{
			if( a_vectorJpopupMenus != null )
			{
				for( int ii=0; ii<a_vectorJpopupMenus.size(); ii++ )
				{
					convertAttributesIntoProperties( a_vectorJpopupMenus.elementAt(ii), level );
				}
			}
		}

		if( comp instanceof JDesktopPane )
		{
		}
		else if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;
			convertContainerAttributesIntoProperties( contnr );
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				convertAttributesIntoProperties( contnr.getComponent(ii), level );
			}
		}

		String name = comp.getName();
		if( ( ( name != null ) && ( !name.equals("") ) ) || ( comp instanceof JRootPane ) )
		{
			if( comp instanceof JDesktopPane )
			{
			}
			else if( comp instanceof Container	)
			{
			}
			else if( comp instanceof Button )
			{
				Button btn = (Button) comp;
				convertButtonTextsIntoProperties( btn );
			}
			else if( comp instanceof Checkbox )
			{
				Checkbox ckb = (Checkbox) comp;
				convertCheckBoxTextsIntoProperties( ckb );
			}
			else if( comp instanceof Choice )
			{
				Choice chc = (Choice) comp;
				convertChoiceTextsIntoProperties( chc );
			}
			else if( comp instanceof Label )
			{
				Label lbl = (Label) comp;
				convertLabelTextsIntoProperties( lbl );
			}
			else if( comp instanceof	List )
			{
				List lst = (List) comp;
				convertListTextsIntoProperties( lst );
			}
			else if( comp instanceof TextComponent )
			{
				TextComponent txtcmp = (TextComponent) comp;
				convertTextComponentTextsIntoProperties( txtcmp );
			}
			else
			{
				throw new InternException( "Class of component not expected. " + comp.getClass().getName() );
			}
		}
	}

	protected void convertPropertiesIntoAbstractButtonTexts( AbstractButton absbtn ) throws InternException
	{
		String name = absbtn.getName();

		if( ( name != null ) && ( !name.equals("") ) )
		{
			String value = a_languageProperties.getProperty( name + "." + TXT_TEXT );

			if( value != null )
				absbtn.setText( value );
			else
				System.out.println("Error cargando propiedades de idioma de " + name + " en " + a_parentFrame.getName() );

			if( absbtn instanceof JMenu )
			{
				JMenu jmnu = (JMenu) absbtn;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					convertPropertiesIntoAttributes( jmnu.getMenuComponent( ii ), 2 );
			}
		}
	}

	protected void convertPropertiesIntoJLabelTexts( JLabel jlbl ) throws InternException
	{
		String name = jlbl.getName();

		if( ( name != null ) && ( !name.equals("") ) )
		{
			String value = a_languageProperties.getProperty( name + "." + TXT_TEXT );

			if( value != null )
				jlbl.setText( value );
			else
			{
//				throw new InternException( "Error cargando propiedades de idioma de " + name + " en " + a_frameParent.getName() );
			}
		}
	}

	protected void convertPropertiesIntoJListTexts( JList jlst ) throws InternException
	{
/*
		String name = jlst.getName();

		DefaultListModel dlm = new DefaultListModel();
		String value = "";
		for( int ii=0; ( value != null ); ii++ )
		{
			value = prop.getProperty( name + "." + TXT_TEXT + "." + String.valueOf(ii) );
			if( value != null )
				dlm.setElementAt( value, ii );
		}
		jlst.setModel( dlm );
 */
	}

	protected void convertPropertiesIntoJTextComponentTexts( JTextComponent jtxtcmp ) throws InternException
	{
		String name = jtxtcmp.getName();
	}

	protected void convertPropertiesIntoJPopUpMenuTexts( JPopupMenu jpumnu ) throws InternException
	{
		String name = jpumnu.getName();
//		addProperty( prop, name + "." + TXT_TEXT, jpumnu.getText() );
	}

	protected void convertPropertiesIntoJSplitPaneAttributes( JSplitPane jsp ) throws InternException
	{
		String name = jsp.getName();

		int value = (int) a_formGeneralConfiguration.M_getIntParamConfiguration( name + "." + TXT_DIVIDER_LOCATION );
		
		if( value > 0 ) jsp.setDividerLocation(value);
	}

	protected void convertPropertiesIntoContainerAttributes( Container contnr ) throws InternException
	{
		String name = contnr.getName();

		if( ( name != null ) && ( !name.equals("") ) )
		{
			if( contnr instanceof JComponent )
			{
				JComponent jcomp = (JComponent) contnr;
				if( jcomp.getBorder() instanceof TitledBorder )
				{
					String value = a_languageProperties.getProperty( name + "." + TXT_BORDER + "." + TXT_TITLE );
					TitledBorder tb = (TitledBorder) jcomp.getBorder();
					if( value != null )
						tb.setTitle( value );
				}
			}

			if( contnr instanceof JSplitPane )
			{
				JSplitPane jsp = (JSplitPane) contnr;
				convertPropertiesIntoJSplitPaneAttributes( jsp );
			}
			if( contnr instanceof AbstractButton )
			{
				AbstractButton absbtn = (AbstractButton) contnr;
				convertPropertiesIntoAbstractButtonTexts( absbtn );
			}
			else if( contnr instanceof JLabel )
			{
				JLabel jlbl = (JLabel) contnr;
				convertPropertiesIntoJLabelTexts( jlbl );
			}
			else if( contnr instanceof JList )
			{
				JList jlst = (JList) contnr;
				convertPropertiesIntoJListTexts( jlst );
			}
			else if( contnr instanceof JTextComponent )
			{
				JTextComponent jtxtcmp = (JTextComponent) contnr;
				convertPropertiesIntoJTextComponentTexts( jtxtcmp );
			}
			else if( contnr instanceof JPopupMenu )
			{
				JPopupMenu jpumnu = (JPopupMenu) contnr;
				convertPropertiesIntoJPopUpMenuTexts( jpumnu );
			}
			else if( contnr instanceof JFrame )
			{
				JFrame jfr = (JFrame) contnr;
				convertPropertiesIntoJFrameTexts( jfr );
			}
			else if( contnr instanceof JInternalFrame )
			{
				JInternalFrame jif = (JInternalFrame) contnr;
				convertPropertiesIntoJInternalFrameTexts( jif );
			}
		}
	}

	protected void convertPropertiesIntoButtonTexts( Button btn ) throws InternException
	{
		String name = btn.getName();
		if( name !=  null )
		{
			String value = a_languageProperties.getProperty( name + "." + TXT_LABEL );

			if( value != null )
				btn.setLabel( value );
			else
			{
//				throw new InternException( "Error cargando propiedades de idioma de " + name + " en " + a_frameParent.getName() );
			}
		}
	}

	protected void convertPropertiesIntoCheckBoxTexts( Checkbox ckb ) throws InternException
	{
		String name = ckb.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			String value = a_languageProperties.getProperty( name + "." + TXT_LABEL );

			if( value != null )
				ckb.setLabel( value );
			else
			{
//				throw new InternException( "Error cargando propiedades de idioma de " + name + " en " + a_frameParent.getName() );
			}
		}
	}

	protected void convertPropertiesIntoChoiceTexts( Choice chc ) throws InternException
	{
		String name = chc.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			chc.removeAll();

			String value = "";
			for( int ii=0; (value != null) && (ii<chc.getItemCount()); ii++ )
			{
				value = a_languageProperties.getProperty( name + "." + TXT_ITEM + "." + String.valueOf(ii) );

				if( value != null )
					chc.add( value );
				else
				{
/*					throw new InternException(	"Needed property not found. " + name + "." + TXT_ITEM + "." + String.valueOf(ii) +
																			" in " + a_frameParent.getName() ); */
				}
			}
		}
	}

	protected void convertPropertiesIntoLabelTexts( Label lbl ) throws InternException
	{
		String name = lbl.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			String value = a_languageProperties.getProperty( name + "." + TXT_TEXT );

			if( value != null )
				lbl.setText( value );
			else
			{
//				throw new InternException( "Error cargando propiedades de idioma de " + name + " en " + a_frameParent.getName() );
			}
		}
	}

	protected void convertPropertiesIntoListTexts( List lst ) throws InternException
	{
//		String name = lst.getName();
	}

	protected void convertPropertiesIntoTextComponentTexts( TextComponent txtcmp ) throws InternException
	{
		String name = txtcmp.getName();
	}

	protected void convertPropertiesIntoJFrameTexts( JFrame jfr ) throws InternException
	{
		String name = jfr.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			String value = a_languageProperties.getProperty( name + "." + TXT_TITLE );

			if( value != null )
				jfr.setTitle( value );
			else
			{
//				throw new InternException( "Error cargando propiedades de idioma de " + name + " en " + a_frameParent.getName() );
			}
		}
	}

	protected void convertPropertiesIntoJInternalFrameTexts( JInternalFrame jif ) throws InternException
	{
		String name = jif.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			String value = a_languageProperties.getProperty( name + "." + TXT_TITLE );

			if( value != null )
				jif.setTitle( value );
			else
			{
//				throw new InternException( "Error cargando propiedades de idioma de " + name + " en " + a_frameParent.getName() );
			}
		}
	}

	protected void convertPropertiesIntoAttributes( Component comp, int level ) throws InternException
	{
		level = level + 1;

		if( level == 1 )
		{
			if( a_vectorJpopupMenus != null )
			{
				for( int ii=0; ii<a_vectorJpopupMenus.size(); ii++ )
				{
					convertPropertiesIntoAttributes( a_vectorJpopupMenus.get(ii), level );
				}
			}
		}
		
		if( comp instanceof JDesktopPane )
		{
		}
		else if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;
			convertPropertiesIntoContainerAttributes( contnr );
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				convertPropertiesIntoAttributes( contnr.getComponent(ii), level );
			}
		}

		String name = comp.getName();
		if( ( name != null ) && ( !name.equals("") ) || ( comp instanceof JRootPane ) )
		{
			if( comp instanceof JDesktopPane )
			{
			}
			else if( comp instanceof Container	)
			{
			}
			else if( comp instanceof Button )
			{
				Button btn = (Button) comp;
				convertPropertiesIntoButtonTexts( btn );
			}
			else if( comp instanceof Checkbox )
			{
				Checkbox ckb = (Checkbox) comp;
				convertPropertiesIntoCheckBoxTexts( ckb );
			}
			else if( comp instanceof Choice )
			{
				Choice chc = (Choice) comp;
				convertPropertiesIntoChoiceTexts( chc );
			}
			else if( comp instanceof Label )
			{
				Label lbl = (Label) comp;
				convertPropertiesIntoLabelTexts( lbl );
			}
			else if( comp instanceof	List )
			{
				List lst = (List) comp;
				convertPropertiesIntoListTexts( lst );
			}
			else if( comp instanceof TextComponent )
			{
				TextComponent txtcmp = (TextComponent) comp;
				convertPropertiesIntoTextComponentTexts( txtcmp );
			}
			else
			{
				throw new InternException( "unexpected class of component " + comp.getClass().getName() );
			}
		}
	}

	public void M_changeLanguage( String language ) throws InternException
	{
		try
		{
			if( a_formLanguageConfiguration != null )
			{
				a_formLanguageConfiguration.M_changeLanguage( language );
				convertPropertiesIntoAttributes( a_parentFrame, 0 );
			}
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
			throw( new InternException( ce.getMessage() ) );
		}
	}

	protected void saveLanguageConfiguration() throws InternException
	{
		if( a_formLanguageConfiguration != null )
		{
			try
			{
				a_formLanguageConfiguration.M_saveConfiguration();
			}
			catch( ConfigurationException ex )
			{
				ex.printStackTrace();
				throw new InternException(	"Exception saving form configuration file for form: " + a_configurationBaseFileName );
			}
		}
	}
	
	public String M_getLanguage()
	{
		String result = null;
		
		if( a_formLanguageConfiguration != null ) result = a_formLanguageConfiguration.M_getLanguage();
		
		return( result );
	}

	protected void changeFontSize( Component comp, float factor )
	{
		Font oldFont = comp.getFont();
		if( oldFont != null )
		{
			Font newFont = oldFont.deriveFont( (float) Math.round( factor * comp.getFont().getSize() ) );
			comp.setFont( newFont );
		}
	}

	protected void changeFontSizeRecursive( Component comp, float factor )
	{

		if( !(comp instanceof JInternalFrame) )
		{
			changeFontSize( comp, factor );
			if( comp instanceof Container	)
			{
				Container contnr = (Container) comp;
				for( int ii=0; ii<contnr.getComponentCount(); ii++ )
				{
					changeFontSizeRecursive( contnr.getComponent(ii), factor );
				}

				if( comp instanceof JMenu )
				{
					JMenu jmnu = (JMenu) comp;
					for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
						changeFontSizeRecursive( jmnu.getMenuComponent( ii ), factor );
				}
			}
		}
	}

	public void M_changeFontSize( float factor )
	{
		if( factor > 0 )
		{
			float newFactor = factor / a_lastFactor;
			
			if( (a_parentFrame != null) && (a_parentFrame instanceof Container ) )
			{
				Container cont = (Container) a_parentFrame;
				for( int ii=0; ii<cont.getComponentCount(); ii++ )
				{
					changeFontSizeRecursive( cont.getComponent(ii), newFactor );
				}
			}
			else
			{
				changeFontSizeRecursive( a_parentFrame, factor );
			}

			if( a_vectorJpopupMenus != null )
			{
				for( int ii=0; ii<a_vectorJpopupMenus.size(); ii++ )
				{
					changeFontSizeRecursive( a_vectorJpopupMenus.get(ii), newFactor );
				}
			}
			
			a_lastFactor = factor;
		}
	}

	@Override
	public void componentHidden(ComponentEvent e)
	{
		
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
		
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		resizeOrRelocateComponent();
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
		
	}

	@Override
	public void windowStateChanged(WindowEvent arg0)
	{
		resizeOrRelocateComponent();
	}

	protected void setListeners( )
	{
		if( ! a_hasComponentListenerBeenSet )
		{
			a_hasComponentListenerBeenSet = true;

			a_componentListener_this = this;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					a_parentFrame.addComponentListener( a_componentListener_this );
				}
			});

//			a_parentFrame.addComponentListener( this );
			if( a_parentFrame instanceof JFrame )
			{
				JFrame jframe = (JFrame) a_parentFrame;
				jframe.addWindowStateListener(this);
			}
		}
	}

	public void removeResizeRelocateComponentItem( Component comp )
	{
		a_mapResizeRelocateComponents.remove( comp );
	}

	public ResizeRelocateItem getResizeRelocateComponentItem( Component comp )
	{
		return( a_mapResizeRelocateComponents.get( comp ) );
	}
	
	public void resizeOrRelocateComponent()
	{
		resizeOrRelocateComponent( a_parentFrame, false );
	}
	
	protected void resizeOrRelocateComponent( Component comp, boolean onlyGetInfo )
	{
		ResizeRelocateItem rri = a_mapResizeRelocateComponents.get(comp);
		if( ( rri != null ) ||
			( comp instanceof JFrame ) ||
			( comp instanceof JRootPane ) ||
			( comp instanceof JLayeredPane ) ||
			( comp instanceof JPanel ) ||
			( comp instanceof JInternalFrame ) )
		{
			Component parent = comp.getParent();
			if( ( comp instanceof JLayeredPane ) || ( comp instanceof JRootPane ) ||
				( comp instanceof JPanel ) && ( comp.getName() != null ) && comp.getName().substring( 0, 5 ).equals( "null." ) )
			{
				if( parent != null )
				{
					if( onlyGetInfo )	insertComponentIntoResizingPanels( comp );
					else				setSizeForResizePanels( comp );
				}
			}
			else if( rri != null )
				rri.execute();

			if( comp instanceof Container	)
			{
				Container contnr = (Container) comp;
				for( int ii=0; ii<contnr.getComponentCount(); ii++ )
				{
					resizeOrRelocateComponent( contnr.getComponent(ii), onlyGetInfo );
				}
			}
		}
	}
	
	protected void insertComponentIntoResizingPanels( Component comp )
	{
		a_mapResizingPanels.put( comp, new InfoForResizingPanels( comp ) );
	}
	
	protected void setSizeForResizePanels( Component comp )
	{
		InfoForResizingPanels ifrp = a_mapResizingPanels.get( comp );
		if( ifrp != null )	ifrp.resize();
	}

	protected class InfoForResizingPanels
	{
		protected int _widthDifference = -1;
		protected int _heightDifference = -1;
		protected Component _component = null;
		
		public InfoForResizingPanels( Component comp )
		{
			_component = comp;
			Component parent = comp.getParent();
			if( parent != null )
			{
				_widthDifference = parent.getWidth() - comp.getWidth();
				_heightDifference = parent.getHeight() - comp.getHeight();
			}
		}
		
		public void resize()
		{
			Component parent = _component.getParent();
			if( parent != null )
			{
				_component.setSize( parent.getWidth() - _widthDifference, parent.getHeight() - _heightDifference);
			}
		}
	}
	
	// the Map returned will contain the original cursors in every component.
	// so the change of the cursor can be reverted in the future.
	public Map< Component, Cursor > M_changeCursor( Cursor cursor )
	{
		Map< Component, Cursor > result = new Hashtable< Component, Cursor >();
		
		M_changeCursor( a_parentFrame, cursor, result );
		
		return( result );
	}

	protected static void M_changeCursor( Component comp, Cursor cursor, Map< Component, Cursor > map )
	{
		if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				M_changeCursor( contnr.getComponent(ii), cursor, map );
			}

			if( contnr instanceof JMenu )
			{
				JMenu jmnu = (JMenu) contnr;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					M_changeCursor( jmnu.getMenuComponent( ii ), cursor, map );
			}
		}
		Cursor c2 = map.get(comp);
		if( c2 == null )
		{
			map.put(comp, comp.getCursor() );
			comp.setCursor(cursor);
		}
	}

	public void M_rollbackChangeCursor( Map< Component, Cursor > rollbackMap )
	{
		if( rollbackMap == null ) rollbackMap = new Hashtable<Component, Cursor>();
		Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

		M_rollbackChangeCursor(a_parentFrame, rollbackMap, defaultCursor );
	}

	protected static void M_rollbackChangeCursor( Component comp, Map< Component, Cursor > rollbackMap, Cursor defaultCursor )
	{
		Cursor cursor = rollbackMap.get( comp );
		if( cursor == null )	cursor = defaultCursor;
		comp.setCursor(cursor);
		
		if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				M_rollbackChangeCursor(contnr.getComponent(ii), rollbackMap, defaultCursor );
			}

			if( contnr instanceof JMenu )
			{
				JMenu jmnu = (JMenu) contnr;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					M_rollbackChangeCursor(jmnu.getMenuComponent( ii ), rollbackMap, defaultCursor );
			}
		}
	}

}
