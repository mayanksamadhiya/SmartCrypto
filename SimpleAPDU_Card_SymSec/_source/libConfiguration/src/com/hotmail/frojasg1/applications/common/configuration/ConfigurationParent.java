/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.applications.common.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 *
 * @author Usuario
 */
public abstract class ConfigurationParent extends Properties
{
	protected Properties a_properties = null;
	protected Properties a_defaultProperties = null;

	protected String a_mainFolder = null;
	protected String a_applicationName = null;
	protected String a_group = null;
	protected String a_language = null;
	protected String a_configurationFileName = null;

	public static String  sa_dirSeparator = System.getProperty( "file.separator" );
	public static String  sa_lineSeparator = System.getProperty( "line.separator" );
	protected String a_rootApplicationConfigurationPath = null;
	protected boolean a_existedConfigurationFile = false;
	protected boolean a_existedConfigurationFileInClassPath = false;
	
//	public static String FROJASG1_FOLDER = "frojasg1.apps";
	
	protected abstract Properties M_getDefaultProperties( String language );
	
	public ConfigurationParent()
	{
		a_properties = new Properties();
	}

	public ConfigurationParent( String mainFolder, String applicationName, String group,
								String language, String configurationFileName )
	{
		a_mainFolder = mainFolder;
		a_applicationName = applicationName;
		a_group = group;
		a_language = language;
		a_configurationFileName = configurationFileName;
		a_properties = new Properties();

		a_rootApplicationConfigurationPath = M_getRootApplicationConfigurationPath();
	}

	protected String M_getRootApplicationConfigurationPath()
	{
		String result = System.getProperty("user.home") + sa_dirSeparator  + 
						a_mainFolder + sa_dirSeparator + a_applicationName;
		if( a_group != null ) result = result + sa_dirSeparator + a_group;
		return( result );
	}
	
	public void M_openConfiguration() throws ConfigurationException
	{
		String fileName = M_getConfigurationFileName( a_language );
		M_openConfiguration( fileName );
	}
	
	public void M_openConfiguration( String fileName ) throws ConfigurationException
	{
		Properties properties = null;
		properties = M_loadConfigurationFile( fileName );
		a_properties = properties; // only if there is no exception, we change the values.
		
		if( a_defaultProperties == null ) a_defaultProperties = M_getDefaultProperties(a_language);
		
		if( (a_properties == null) && (a_defaultProperties == null) )
			throw( new ConfigurationException( "The configuration file " + fileName + " could not be open" ) );
	}
	
	public void M_changeLanguage( String language ) throws ConfigurationException
	{
		try
		{
			if( a_language != null ) M_saveConfiguration();
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
		}
		
		try
		{
			a_defaultProperties=M_getDefaultProperties( language );
			M_openConfiguration( M_getConfigurationFileName(language) );
			a_language = language;
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
			try
			{
				a_defaultProperties=M_getDefaultProperties( a_language );
				M_openConfiguration( a_language );
			}
			catch( ConfigurationException ce1 )
			{
				ce1.printStackTrace();
			}
			throw( ce );
		}
	}

	public String M_getConfigurationFileName()
	{
		return( M_getConfigurationFileName( a_language ) );
	}

	protected String M_getConfigurationFileName( String language )
	{
		String fileName = a_rootApplicationConfigurationPath;
		if( language != null ) fileName = fileName + sa_dirSeparator + language;
		fileName = fileName + sa_dirSeparator + a_configurationFileName;

		return( fileName );
	}

	protected Properties M_loadConfigurationFile( String fileName )
	{
		Properties result = null;
		try
		{
			a_existedConfigurationFile = false;
			result = M_loadProperties( fileName );
			a_existedConfigurationFile = true;
		}
		catch( IOException ioe )
		{
//			ioe.printStackTrace();  // we do not retrhrow the exception, and we work with default values.
			System.out.println( ioe.getMessage() );
		}

		return( result );
	}

	protected Properties M_loadProperties( String filename ) throws IOException
	{
		File file = new File( filename );
		Properties result = new Properties();

		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader( fis, StandardCharsets.UTF_8 );
		result.load( isr );
		isr.close();

		return( result );
	}

	public long M_getIntParamConfiguration( String label )
	{
		long result = -1;
		String resultStr = M_getStrParamConfiguration( label );
		try
		{
			result = Integer.parseInt( resultStr );
		}
		catch( NumberFormatException ex )
		{
			System.out.println( "Error when reading numerical configuration parameter: " +
								label );
			result = -1;
		}

		return( result );
	}

	public String M_getStrParamConfiguration( String label )
	{
		String result = null;
		if( a_properties != null ) result = a_properties.getProperty(label);
		if( (result == null) && (a_defaultProperties != null) ) result = a_defaultProperties.getProperty(label);
		return( result );
	}

	public void M_setIntParamConfiguration( String label, int value )
	{
		M_setStrParamConfiguration( label, String.valueOf( value ) );
	}

	public void M_setStrParamConfiguration( String label, String value )
	{
		if( a_properties == null ) a_properties = new Properties();
		a_properties.setProperty( label, value );
	}

	protected Properties M_makePropertiesAddingDefaults( Properties values, Properties defaults )
	{
		Properties result = null;
		
		if( defaults == null ) result = values;
		else if( values == null ) result = defaults;
		else
		{
			result = (Properties) values.clone();
			Object[] labels = defaults.stringPropertyNames().toArray();
			for( int ii=0; ii<labels.length; ii++ )
			{
				String key = (String) labels[ii];
				String value = result.getProperty(key );
				if( value == null )
				{
					value = defaults.getProperty( key );
					if( value != null ) result.setProperty( key, value );
				}
			}
		}
		
		return( result );
	}
	
	public void M_saveConfiguration() throws ConfigurationException
	{
		M_saveConfiguration( M_getConfigurationFileName(a_language) );
	}

	public void M_saveConfiguration( String fileName ) throws ConfigurationException
	{
		File file = new File( fileName );
		File fPath = new File( file.getParent() );

		if( !fPath.exists() )
		{
			fPath.mkdirs();
		}
		else if ( !fPath.isDirectory() )
		{
			throw( new ConfigurationException(  "Error. Could not save the " +
												"configuration because " + fPath.getName() +
												" is not a directory"  ) );
		}

		Properties prop = M_makePropertiesAddingDefaults( a_properties, a_defaultProperties );
		if( prop != null )
		{
			try
			{
				FileOutputStream fos = new FileOutputStream( file );
				OutputStreamWriter osw = new OutputStreamWriter( fos, StandardCharsets.UTF_8 );
				prop.store( osw, a_configurationFileName + " Configuration" );
				osw.close();
			}
			catch( IOException ex )
			{
				System.out.println( ex.getMessage() );
				throw( new ConfigurationException(  "Error. Could not save the configuration file " +
													"because of " + ex.getMessage() ) );
			}
		}
	}
	
	public String M_getLanguage()	{ return( a_language );	}

	@Override
	public String getProperty( String label )
	{
		return( M_getStrParamConfiguration( label ) );
	}
	
	@Override
	public Object setProperty( String key, String value )
	{
		M_setStrParamConfiguration( key, value );
		return( null );
	}

	protected Properties cargarPropertiesClassPath( String propName ) throws IOException
	{
		a_existedConfigurationFileInClassPath = false;
		Properties result = null;
		InputStream in = null;
		ClassLoader loader = ClassLoader.getSystemClassLoader ();
		in = loader.getResourceAsStream (propName);
		
		if( in == null )
		{
			try
			{
				in = this.getClass().getClassLoader().getResource(propName).openStream();
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
				result = new Properties ();
				result.load (isr); // It can throw IOException
				isr.close();
			}
		}
		a_existedConfigurationFileInClassPath = ( result != null );
		return( result );
	}

	public boolean M_isFirstTime()
	{
		return( !a_existedConfigurationFile );
	}
	
	public boolean M_existedConfigurationFileInClassPath()
	{
		return( a_existedConfigurationFileInClassPath );
	}
	
	public void M_setProperties( Properties prop )
	{
		a_properties = prop;
	}

	public static void main( String[] args )
	{
		String fileName = "C:\\Users\\Usuario\\frojasg1.apps\\FileEncoderApplication\\Frames\\CAT\\JDial_applicationConfiguration_LAN.properties";
		
		File file = new File( fileName );
		Properties result = new Properties();

		try
		{

			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader( fis, StandardCharsets.UTF_8 );
			result.load( isr );
			isr.close();

			boolean success = file.delete();

			System.out.println( "Delete " + file + "   Success: " + success );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}
}
