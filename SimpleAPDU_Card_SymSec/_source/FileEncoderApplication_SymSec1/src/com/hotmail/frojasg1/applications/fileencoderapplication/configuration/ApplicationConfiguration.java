/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hotmail.frojasg1.applications.fileencoderapplication.configuration;

import com.hotmail.frojasg1.applications.common.configuration.ConfigurationParent;
import java.util.Properties;

/**
 *
 * @author Usuario
 */
public class ApplicationConfiguration extends ConfigurationParent
{
	public static final String sa_ENCRYPT_FILE_EXTENSION = "jfe";

	public static final String CONF_LAST_DIRECTORY = "LAST_DIRECTORY";
	public static final String CONF_LANGUAGE = "LANGUAGE";
	public static final String CONF_ADDITIONAL_LANGUAGE = "ADDITIONAL_LANGUAGE";
	public static final String CONF_ERASE_DECRYPTED_FILE_AFTER_ENCRIPTING = "ERASE_UNENCRYPTED_FILE_WHEN_ENCRIPTING";
	public static final String CONF_HAS_TO_RENAME_PRESENT_ENCRYPTED_FILE_TO_OLD_AFTER_DECRYPTING = "HAS_TO_RENAME_PRESENT_ENCRYPTED_FILE_TO_OLD_WHEN_DECRYPTING";
	public static final String CONF_ASK_TO_OVERWRITE_OLD_ENCRYPTED_FILE_WHEN_IT_EXISTED_PREVIOUSLY = "ASK_TO_OVERWRITE_OLD_ENCRYPTED_FILE_WHEN_IT_EXISTED_PREVIOUSLY";
	public static final String CONF_MANUAL_ENCRYPTING_CONFIGURATION_PRIORITY = "MANUAL_ENCRYPTING_CONFIGURATION_PRIORITY";
	public static final String CONF_GET_ENCRYPTING_CONFIGURATION_FROM_ENCRYPTED_FILE_FOR_NEXT_ENCRYPTIONS_PRIORITY = "GET_ENCRYPTING_CONFIGURATION_FROM_ENCRYPTED_FILE_FOR_NEXT_ENCRYPTIONS_PRIORITY";
	public static final String CONF_GET_ENCRYPTING_CONFIGURATION_FROM_FILE_SIZE_PRIORITY = "GET_ENCRYPTING_CONFIGURATION_FROM_FILE_SIZE_PRIORITY";
	public static final String CONF_APPLICATION_FONT_SIZE = "APPLICATION_FONT_SIZE";

//	protected static final String APPLICATION_GROUP = "general";
	protected static final String GLOBAL_CONF_FILE_NAME = "GlobalConfiguration.properties";
/*
	protected static final String sa_PATH_PROPERTIES_IN_JAR =	"com" + ConfigurationParent.sa_dirSeparator + 
																"hotmail" + ConfigurationParent.sa_dirSeparator +
																"frojasg1" + ConfigurationParent.sa_dirSeparator + 
																"aplicaciones" + ConfigurationParent.sa_dirSeparator +
																"fileencoderapplication" + ConfigurationParent.sa_dirSeparator +
																"view" + ConfigurationParent.sa_dirSeparator +
																"internationalization" + ConfigurationParent.sa_dirSeparator +
																"properties";
*/
	public static final String sa_PATH_PROPERTIES_IN_JAR="com/hotmail/frojasg1/applications/fileencoderapplication/view/internationalization/properties";
	public static final String sa_MAIN_FOLDER = "frojasg1.apps";
	public static String sa_APPLICATION_NAME = null;
	public static final String sa_CONFIGURATION_GROUP = "Configuration";

	public static final String ES_LANGUAGE = "ES";	 // Spanish language
	public static final String EN_LANGUAGE = "EN";	 // English language

	private static ApplicationConfiguration a_instance = null;

	private ApplicationConfiguration()
	{
//		super( sa_MAIN_FOLDER, sa_APPLICATION_NAME, APPLICATION_GROUP, null, GLOBAL_CONF_FILE_NAME );
		super( sa_MAIN_FOLDER, sa_APPLICATION_NAME, sa_CONFIGURATION_GROUP, null, GLOBAL_CONF_FILE_NAME );
	}
	
	public static ApplicationConfiguration M_getInstance()
	{
		if( a_instance == null )
		{
			a_instance = new ApplicationConfiguration();
		}
		
		return( a_instance );
	}

	public Properties M_getDefaultProperties( String language )
	{
		Properties result = new Properties();

		result.setProperty(CONF_LANGUAGE, ES_LANGUAGE );
		result.setProperty(CONF_ADDITIONAL_LANGUAGE, "CAT" );
		result.setProperty(CONF_ERASE_DECRYPTED_FILE_AFTER_ENCRIPTING, "0" );
		result.setProperty(CONF_HAS_TO_RENAME_PRESENT_ENCRYPTED_FILE_TO_OLD_AFTER_DECRYPTING, "1" );
		result.setProperty(CONF_ASK_TO_OVERWRITE_OLD_ENCRYPTED_FILE_WHEN_IT_EXISTED_PREVIOUSLY, "1" );
		result.setProperty(CONF_MANUAL_ENCRYPTING_CONFIGURATION_PRIORITY, "1" );
		result.setProperty(CONF_GET_ENCRYPTING_CONFIGURATION_FROM_FILE_SIZE_PRIORITY, "2" );
		result.setProperty(CONF_GET_ENCRYPTING_CONFIGURATION_FROM_ENCRYPTED_FILE_FOR_NEXT_ENCRYPTIONS_PRIORITY, "3" );
		result.setProperty(CONF_APPLICATION_FONT_SIZE, "1.0" );

		return( result );
	}
	
	public float M_getApplicationFontSize()
	{
		String factorStr = M_getStrParamConfiguration(ApplicationConfiguration.CONF_APPLICATION_FONT_SIZE);
		float factor = 1.0F;
		try
		{
			factor = Float.valueOf( factorStr );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		return( factor );
	}

}
