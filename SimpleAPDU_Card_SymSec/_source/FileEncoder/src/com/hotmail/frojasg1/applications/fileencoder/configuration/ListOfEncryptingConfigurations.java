/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.applications.fileencoder.configuration;

import com.hotmail.frojasg1.applications.common.configuration.ConfigurationException;
import com.hotmail.frojasg1.applications.common.configuration.ConfigurationParent;
import java.io.File;
import java.util.Vector;

/**
 *
 * @author Usuario
 */
public class ListOfEncryptingConfigurations
{

	public static final String sa_MAIN_FOLDER = "frojasg1.apps";

	protected String a_configurationPropertiesPathInUserFolder = null;
	protected static String a_baseFileName = "EncryptingConfiguration";
	protected static String sa_applicationName = "FileEncoder";

	protected static final String APPLICATION_GROUP = "EncryptingConfigurations";
	protected static final String sa_PATH_PROPERTIES_IN_JAR="com/hotmail/frojasg1/applications/fileencoder/encryptingconfigurations";

	protected Vector<EncryptingConfiguration> a_vectorOfConfigurations = null;
	
	protected static ListOfEncryptingConfigurations a_instance = null;
	
	private ListOfEncryptingConfigurations()
	{
		a_configurationPropertiesPathInUserFolder = M_getRootApplicationConfigurationPath();
	}


	protected static String M_getRootApplicationConfigurationPath()
	{
		String result = System.getProperty("user.home") + ConfigurationParent.sa_dirSeparator  + 
						sa_MAIN_FOLDER + ConfigurationParent.sa_dirSeparator + sa_applicationName;
		if( APPLICATION_GROUP != null ) result = result + ConfigurationParent.sa_dirSeparator + APPLICATION_GROUP;
		return( result );
	}


	public static ListOfEncryptingConfigurations M_getInstance()
	{
		if( a_instance == null )
		{
			a_instance = new ListOfEncryptingConfigurations();
			a_instance.M_loadEncryptingConfigurations();
		}
		return( a_instance );
	}
	
	public static void deleteFilesOfFolder( String path )
	{
		File folder = new File( path );
		if( ( folder != null ) && folder.isDirectory() )
		{
			File[] files = folder.listFiles();
			if(files!=null)
			{
				for(File file: files)
				{
					if(!file.isDirectory())
					{
						boolean success = file.delete();
						System.out.println( "deleted file " + file + "  Success: " + success );
					}
				}
				System.out.println( "deleted all files of folder" );
			}
		}
	}

	public void M_loadEncryptingConfigurations()
	{
		if( M_areThereEncryptingConfigurationFilesInUserFolder() )
		{
			M_loadEncryptingConfigurationsFromUserFolder();
		}
		else
		{
			M_loadEncryptingConfigurationsFromClassPath();
		}
	}

	public int M_getIndexOfFileSize( long fileSize )
	{
		int size = a_vectorOfConfigurations.size();
		int index = -1;
		for( int ii=0; (ii<size) && (index==-1); ii++ )
		{
			long sizeFromElement = a_vectorOfConfigurations.get(ii).M_getIntParamConfiguration(EncryptingConfiguration.STR_CONF_SIZE_FROM);
			if( sizeFromElement > fileSize ) index = ii;
		}
		if( index == -1 ) index = size;  // if fileSize is greater than any sizeFrom of the EncryptingConfiguration, it has to point to the next position of the last element

		return( index );
	}
	
	public void M_encryptingConfigurationOrderedInsert( EncryptingConfiguration ec )
	{
		long sizeFrom = ec.M_getIntParamConfiguration(EncryptingConfiguration.STR_CONF_SIZE_FROM);
		int index = M_getIndexOfFileSize( sizeFrom );
		a_vectorOfConfigurations.insertElementAt(ec, index);
	}

	public boolean M_vectorOfConfigurationsContains_SIZE_FROM( EncryptingConfiguration ec )
	{
		long sizeFrom = ec.M_getIntParamConfiguration(EncryptingConfiguration.STR_CONF_SIZE_FROM);

		return( M_vectorOfConfigurationsContains_SIZE_FROM(sizeFrom) );
	}

	public boolean M_vectorOfConfigurationsContains_SIZE_FROM( long sizeFrom )
	{
		int index = M_getIndexOfFileSize( sizeFrom );

		boolean found = false;
		if( index != 0 )
		{
			long sizeFromElement = a_vectorOfConfigurations.get(index-1).M_getIntParamConfiguration(EncryptingConfiguration.STR_CONF_SIZE_FROM);
			found = ( sizeFrom == sizeFromElement );
		}

		return( found );
	}

	public EncryptingConfiguration M_extractEncryptingConfigurationFromVector( int index )
	{
		EncryptingConfiguration result = null;
		if( (index>=0) && (index<a_vectorOfConfigurations.size()) ) result = a_vectorOfConfigurations.remove(index);
		
		return( result );
	}
	
	public EncryptingConfiguration M_getEncryptingConfigurationFromVector( int index )
	{
		EncryptingConfiguration result = null;
		if( (index>=0) && (index<a_vectorOfConfigurations.size()) ) result = a_vectorOfConfigurations.get(index);
		
		return( result );
	}
	
	public EncryptingConfiguration M_getEncryptingConfigurationForAParticularFileSize( long fileSize )
	{
		EncryptingConfiguration result = null;
		
		int index = M_getIndexOfFileSize( fileSize );
		if( index != 0 ) result = a_vectorOfConfigurations.get(index-1);
		
		return( result );
	}

	protected String M_getFileNameInUserFolder( int index )
	{
		String result =	a_configurationPropertiesPathInUserFolder + ConfigurationParent.sa_dirSeparator +
						a_baseFileName + String.format( "%02d", index ) + ".properties";
		return( result );
	}
	
	protected EncryptingConfiguration M_loadEncryptingConfigurationFromUserFolder( int index )
	{
		EncryptingConfiguration result = new EncryptingConfiguration();
		
		String fileName = M_getFileNameInUserFolder(index);
		
		try
		{
			result.M_openConfiguration( fileName );
		}
		catch( ConfigurationException ce )
		{
			System.out.println( ce.getMessage() );
		}
		return( result );
	}
	
	protected boolean M_areThereEncryptingConfigurationFilesInUserFolder()
	{
		EncryptingConfiguration ec = M_loadEncryptingConfigurationFromUserFolder( 1 );
		return( ! ec.M_isFirstTime() );
	}

	protected void M_loadEncryptingConfigurationsFromUserFolder()
	{
		a_vectorOfConfigurations = new Vector<EncryptingConfiguration>();
		
		int index = 1;
		boolean success = true;
		while( success )
		{
			EncryptingConfiguration ec = M_loadEncryptingConfigurationFromUserFolder( index );
			if( ! ec.M_isFirstTime() ) M_encryptingConfigurationOrderedInsert( ec );
			else	success = false;
			index++;
		}
	}
	
	protected EncryptingConfiguration M_loadEncryptingConfigurationFromClassPath( int index )
	{
		EncryptingConfiguration result = new EncryptingConfiguration();
		
		String fileName =	sa_PATH_PROPERTIES_IN_JAR + "/" + a_baseFileName + String.format( "%02d", index ) + ".properties";
		
		try
		{
			result.M_openConfigurationFromClassPath( fileName );
		}
		catch( Throwable th )
		{
			System.out.println( th.getMessage() );
		}
		return( result );
	}

	public void M_loadEncryptingConfigurationsFromClassPath()
	{
		a_vectorOfConfigurations = new Vector<EncryptingConfiguration>();
		
		int index = 1;
		boolean success = true;
		while( success )
		{
			EncryptingConfiguration ec = M_loadEncryptingConfigurationFromClassPath( index );
			if( ec.M_existedConfigurationFileInClassPath() ) M_encryptingConfigurationOrderedInsert( ec );
			else	success = false;
			index++;
		}
	}

	public void M_saveEncryptingConfigurationsToUserFolder()
	{
		deleteFilesOfFolder(a_configurationPropertiesPathInUserFolder);

		int size = a_vectorOfConfigurations.size();
		for( int ii=0; ii<size; ii++ )
		{
			try
			{
				a_vectorOfConfigurations.get(ii).M_saveConfiguration(M_getFileNameInUserFolder(ii+1));
			}
			catch( ConfigurationException ce )
			{
				System.out.println( ce.getMessage() );
			}
		}
	}

	public EncryptingConfiguration M_getEncrytingConfigurationFromFileSize( String fileName )
	{
		File file = new File( fileName );
		long size = -1;
		if( file.exists() ) size = file.length();
		EncryptingConfiguration result = null;
		if( size>-1 ) result = M_getEncryptingConfigurationForAParticularFileSize(size);

		return( result );
	}

}
