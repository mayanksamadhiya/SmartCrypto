package com.hotmail.frojasg1.applications.common.components.internationalization;


import com.hotmail.frojasg1.applications.common.configuration.ConfigurationParent;
import static com.hotmail.frojasg1.applications.common.components.internationalization.JFrameInternationalization.sa_dirSeparator;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Usuario
 */
public class FormLanguageConfiguration extends ConfigurationParent
{
	protected String a_pathPropertiesInJar = null;
	protected Properties a_textValuesFrom_properties = null;

	
	FormLanguageConfiguration(	String mainFolder,
								String applicationName, String group,
								String configurationFileName,
								String pathPropertiesInJar,
								Properties textValuesFromFrom_properties )
	{
		super( mainFolder, applicationName, group, null, configurationFileName );
		a_pathPropertiesInJar = pathPropertiesInJar;
		a_textValuesFrom_properties = textValuesFromFrom_properties;
	}
	
	protected String M_getPropertiesNameFromClassPath( String language )
	{
		String result;
//		result = a_pathPropertiesInJar + sa_dirSeparator + language + sa_dirSeparator + a_configurationFileName;
		result = a_pathPropertiesInJar + "/" + language + "/" + a_configurationFileName;
		return( result );
	}
	
	protected Properties M_getDefaultProperties( String language )
	{
		Properties result = null;
		
		try
		{
			result = cargarPropertiesClassPath( M_getPropertiesNameFromClassPath( language ) );
		}
		catch( IOException ioe )
		{
			ioe.printStackTrace();
			result = null;
		}
		
		if( result == null )
		{
			result = a_textValuesFrom_properties;
		}
		else
		{
			result = M_makePropertiesAddingDefaults(result, a_textValuesFrom_properties );
		}

		return( result );
	}
}
