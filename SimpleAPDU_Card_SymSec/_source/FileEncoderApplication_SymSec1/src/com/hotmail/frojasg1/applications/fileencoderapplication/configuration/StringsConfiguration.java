/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.applications.fileencoderapplication.configuration;

import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.ApplicationConfiguration;
import com.hotmail.frojasg1.applications.common.configuration.ConfigurationParent;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Usuario
 */
public class StringsConfiguration extends ConfigurationParent
{
	public static final String CONF_FILE_DOES_NOT_EXIST_STRING = "FILE_DOES_NOT_EXIST_STRING";
	public static final String CONF_FILE_HAS_A_WRONG_EXTENSION_IT_IS_PROBABLY_ALREADY_ENCRYPTED = "FILE_HAS_A_WRONG_EXTENSION_IT_IS_PROBABLY_ALREADY_ENCRYPTED";
	public static final String CONF_FILE_HAS_A_WRONG_EXTENSION_IT_IS_PROBABLY_NOT_ENCRYPTED = "FILE_HAS_A_WRONG_EXTENSION_IT_IS_PROBABLY_NOT_ENCRYPTED";
	public static final String CONF_FILE_ALREADY_EXISTS = "FILE_ALREADY_EXISTS";
	public static final String CONF_CAN_NOT_ENCRYPT = "CAN_NOT_ENCRYPT";
	public static final String CONF_CAN_NOT_DECRYPT = "CAN_NOT_DECRYPT";
	public static final String CONF_ENCRYPTION_SUCCESSFULLY_ENDED = "ENCRYPTION_SUCCESSFULLY_ENDED";
	public static final String CONF_DECRYPTION_SUCCESSFULLY_ENDED = "DECRYPTION_SUCCESSFULLY_ENDED";
	public static final String CONF_STARTING_TO_ENCRYPT_FILE = "STARTING_TO_ENCRYPT_FILE";
	public static final String CONF_STARTING_TO_DECRYPT_FILE = "STARTING_TO_DECRYPT_FILE";
//	public static final String CONF_PROCESS_SUCCESSFULLY_CREATED = "PROCESS_SUCCESSFULLY_CREATED";
//	public static final String CONF_PROCESS_COULD_NOT_BE_CREATED = "PROCESS_COULD_NOT_BE_CREATED";
//	public static final String CONF_PROCESS_SUCCESSFULLY_CLOSED = "PROCESS_SUCCESSFULLY_CLOSED";
//	public static final String CONF_PROCESS_CLOSED_WITH_ERROR = "PROCESS_CLOSED_WITH_ERROR";
	public static final String CONF_FILE_COULD_NOT_BE_DECRYPTED = "FILE_COULD_NOT_BE_DECRYPTED";
	public static final String CONF_ENCRYPTED_FILE_COULD_NOT_BE_RENAMED = "ENCRYPTED_FILE_COULD_NOT_BE_RENAMED";
	public static final String CONF_FILE_COULD_BE_SUCCESSFULLY_ENCRYPTED = "FILE_COULD_BE_SUCCESSFULLY_ENCRYPTED";
	public static final String CONF_FILE_COULD_NOT_BE_ENCRYPTED = "FILE_COULD_NOT_BE_ENCRYPTED";
	public static final String CONF_FILE_WAS_SUCCESSFULLY_RENAMED = "FILE_WAS_SUCCESSFULLY_RENAMED";
	public static final String CONF_FILE_COULD_NOT_BE_RENAMED = "FILE_COULD_NOT_BE_RENAMED";
	public static final String CONF_FILE_WAS_SUCCESSFULLY_DELETED = "FILE_WAS_SUCCESSFULLY_DELETED";
	public static final String CONF_FILE_COULD_NOT_BE_DELETED = "FILE_COULD_NOT_BE_DELETED";
	public static final String CONF_FILE_SIZE_OF_NUMBERS_UNITS_FILE_ENCODER_TYPE_1 = "SIZE_OF_NUMBERS_UNITS_FILE_ENCODER_TYPE_1";
	public static final String CONF_FILE_SIZE_OF_NUMBERS_UNITS_FILE_ENCODER_TYPE_2 = "SIZE_OF_NUMBERS_UNITS_FILE_ENCODER_TYPE_2";
	public static final String CONF_FILE_PARAMETERS_NOT_VALID = "PARAMETERS_NOT_VALID";
	public static final String CONF_FILE_ENCRYPTING_CONFIGURATION_ERROR = "ENCRYPTING_CONFIGURATION_ERROR";
	public static final String CONF_FILE_ENCRYPTION_TWO_PASSWORDS_MUST_BE_EQUAL = "ENCRYPTION_TWO_PASSWORDS_MUST_BE_EQUAL";
	public static final String CONF_FILE_PASSWORD_ERROR = "PASSWORD_ERROR";
	public static final String CONF_FILE_ONGOING_ACTION = "ONGOING_ACTION";
	public static final String CONF_FILE_DECRYPTION_TWO_PASSWORDS_MUST_BE_EQUAL = "DECRYPTION_TWO_PASSWORDS_MUST_BE_EQUAL";
	public static final String CONF_FILE_FORM_HAS_PENDING_TASKS = "FORM_HAS_PENDING_TASKS";
	public static final String CONF_FILE_PENDING_TASKS = "PENDING_TASKS";
	public static final String CONF_FILE_OPEN_TWO_PASSWORDS_MUST_BE_EQUAL = "OPEN_TWO_PASSWORDS_MUST_BE_EQUAL";
	public static final String CONF_FILE_SURE_DELETE_FILE = "SURE_DELETE_FILE";
	public static final String CONF_FILE_ATENTION = "ATENTION";
	public static final String CONF_FILE_YES = "YES";
	public static final String CONF_FILE_NO = "NO";
	public static final String CONF_FILE_HAVE_TO_SELECT_ROW_OF_TABLE = "HAVE_TO_SELECT_ROW_OF_TABLE";
	public static final String CONF_FILE_SELECTION_ERROR = "SELECTION_ERROR";
	public static final String CONF_FILE_NUMBER_CONFIGURED_IN = "NUMBER_CONFIGURED_IN";
	public static final String CONF_FILE_HAS_TO_BE_A_CORRECT_LONG_INTEGER = "HAS_TO_BE_A_CORRECT_LONG_INTEGER";
	public static final String CONF_FILE_HAS_TO_BE_GREATER_THAN_OR_EQUAL_TO_ZERO = "HAS_TO_BE_GREATER_THAN_OR_EQUAL_TO_ZERO";
	public static final String CONF_FILE_IS_EQUAL_TO_THE_ONE_OF_ANOTHER_ENCRYIPTING_CONFIGURATION = "IS_EQUAL_TO_THE_ONE_OF_ANOTHER_ENCRYIPTING_CONFIGURATION";
//	public static final String CONF_FILE_OS_NOT_RECOGNIZED_FOR_AUTOMATICALLY_OPENING_A_DOCUMENT = "OS_NOT_RECOGNIZED_FOR_AUTOMATICALLY_OPENING_A_DOCUMENT";
	public static final String CONF_ERROR_OPENING_DOCUMENT = "ERROR_OPENING_DOCUMENT";
	public static final String CONF_DOCUMENT_OPENED = "DOCUMENT_OPENED";
	public static final String CONF_WANT_TO_OVERWRITE_OLD_ENCRYPTED_FILE = "WANT_TO_OVERWRITE_OLD_ENCRYPTED_FILE";
	public static final String CONF_MANUAL_ENCRYPTING_CONFIGURATION = "MANUAL_ENCRYPTING_CONFIGURATION";
	public static final String CONF_FILE_SIZE_ENCRYPTING_CONFIGURATION = "FILE_SIZE_ENCRYPTING_CONFIGURATION";
	public static final String CONF_ENCRYPTED_FILE_CONFIGURATION = "ENCRYPTED_FILE_CONFIGURATION";
	public static final String CONF_ERROR_DELETING_FILE = "ERROR_DELETING_FILE";
	public static final String CONF_ABOUT_1 = "ABOUT_1";
	public static final String CONF_ABOUT_2 = "ABOUT_2";
	public static final String CONF_ABOUT_3 = "ABOUT_3";
	public static final String CONF_ABOUT_4 = "ABOUT_4";
	public static final String CONF_ABOUT_5 = "ABOUT_5";
	public static final String CONF_ABOUT_6 = "ABOUT_6";
	public static final String CONF_ABOUT_7 = "ABOUT_7";
	public static final String CONF_ABOUT_8 = "ABOUT_8";
	public static final String CONF_ABOUT_9 = "ABOUT_9";
	public static final String CONF_RELEASED_ON = "RELEASED_ON";
	public static final String CONF_SEND_EMAIL_TO = "SEND_EMAIL_TO";
	public static final String CONF_DECRYPTED_FILE_NAME_CANNOT_BE_JFE = "DECRYPTED_FILE_NAME_CANNOT_BE_JFE";
	public static final String CONF_ENCRYPTED_FILE_NAME_MUST_BE_JFE = "ENCRYPTED_FILE_NAME_MUST_BE_JFE";
	public static final String CONF_DO_YOU_WANT_TO_CHOOSE_ANOTHER_FILE_NAME_FOR_DECRYPTED_FILE = "DO_YOU_WANT_TO_CHOOSE_ANOTHER_FILE_NAME_FOR_DECRYPTED_FILE";
	public static final String CONF_DO_YOU_WANT_TO_CHOOSE_ANOTHER_FILE_NAME_FOR_ENCRYPTED_FILE = "DO_YOU_WANT_TO_CHOOSE_ANOTHER_FILE_NAME_FOR_ENCRYPTED_FILE";
	public static final String CONF_ENCRYPTED_FILE_JFE = "ENCRYPTED_FILE_JFE";
	public static final String CONF_NORMAL_FONT_SIZE = "NORMAL_FONT_SIZE";
	public static final String CONF_LARGE_FONT_SIZE = "LARGE_FONT_SIZE";
	public static final String CONF_CANCELLED_BY_USER_WHEN_ENCRYPTING = "CANCELLED_BY_USER_WHEN_ENCRYPTING";
	public static final String CONF_CANCELLED_BY_USER_WHEN_DECRYPTING = "CANCELLED_BY_USER_WHEN_DECRYPTING";
	public static final String CONF_FROM = "FROM";
	public static final String CONF_TO = "TO";
	public static final String CONF_VISIT_HOME_PAGE = "VISIT_HOME_PAGE";


//	protected static final String APPLICATION_GROUP = "general";
//	protected static final String APPLICATION_NAME = "FileEncoder";
	protected static final String GLOBAL_CONF_FILE_NAME = "StringsConfiguration.properties";

	protected String a_pathPropertiesInJar = null;
	private static StringsConfiguration a_instance = null;

	private StringsConfiguration( String pathPropertiesInJar )
	{
		super( ApplicationConfiguration.sa_MAIN_FOLDER, ApplicationConfiguration.sa_APPLICATION_NAME,
//				ApplicationConfiguration.APPLICATION_GROUP,
				ApplicationConfiguration.sa_CONFIGURATION_GROUP,
				null,
				GLOBAL_CONF_FILE_NAME );

		a_pathPropertiesInJar = pathPropertiesInJar;
	}
	
	public static StringsConfiguration M_getInstance()
	{
		if( a_instance == null )
		{
			a_instance = new StringsConfiguration( ApplicationConfiguration.sa_PATH_PROPERTIES_IN_JAR );
		}
		
		return( a_instance );
	}

	protected Properties M_getDefaultProperties2( String language )
	{
		Properties result = new Properties();

		result.setProperty(CONF_FILE_DOES_NOT_EXIST_STRING, "File does not exist" );
		result.setProperty(CONF_FILE_HAS_A_WRONG_EXTENSION_IT_IS_PROBABLY_ALREADY_ENCRYPTED, "File has the extension of an already encrypted file" );
		result.setProperty(CONF_FILE_HAS_A_WRONG_EXTENSION_IT_IS_PROBABLY_NOT_ENCRYPTED, "File has the extension of a plain file, not yet encrypted" );
		result.setProperty(CONF_FILE_ALREADY_EXISTS, "File already exists." );
		result.setProperty(CONF_CAN_NOT_ENCRYPT, "Can not encrypt" );
		result.setProperty(CONF_CAN_NOT_DECRYPT, "Can not decrypt" );
		result.setProperty(CONF_ENCRYPTION_SUCCESSFULLY_ENDED, "File encryption successfully ended" );
		result.setProperty(CONF_DECRYPTION_SUCCESSFULLY_ENDED, "File decryption successfully ended" );
		result.setProperty(CONF_STARTING_TO_ENCRYPT_FILE, "Starting to encrypt file" );
		result.setProperty(CONF_STARTING_TO_DECRYPT_FILE, "Starting to decrypt file" );
//		result.setProperty(CONF_PROCESS_SUCCESSFULLY_CREATED, "Process successfully created. Waiting for process to close ..." );
//		result.setProperty(CONF_PROCESS_COULD_NOT_BE_CREATED, "Process could not be created. Document could not be opened" );
//		result.setProperty(CONF_PROCESS_SUCCESSFULLY_CLOSED, "Process successfully closed." );
//		result.setProperty(CONF_PROCESS_CLOSED_WITH_ERROR, "Process closed with error." );
		result.setProperty(CONF_FILE_COULD_NOT_BE_DECRYPTED, "File could not be decrypted." );
		result.setProperty(CONF_ENCRYPTED_FILE_COULD_NOT_BE_RENAMED, "The encrypted file could not be renamed." );
		result.setProperty(CONF_FILE_COULD_BE_SUCCESSFULLY_ENCRYPTED, "The file could be successfully encrypted." );
		result.setProperty(CONF_FILE_COULD_NOT_BE_ENCRYPTED, "The file could not be encrypted." );
		result.setProperty(CONF_FILE_WAS_SUCCESSFULLY_RENAMED, "Backup file was successfully renamed." );
		result.setProperty(CONF_FILE_COULD_NOT_BE_RENAMED, "The file could not be renamed." );
		result.setProperty(CONF_FILE_WAS_SUCCESSFULLY_DELETED, "The file was successfully deleted." );
		result.setProperty(CONF_FILE_COULD_NOT_BE_DELETED, "File could not be deleted." );
		result.setProperty(CONF_FILE_SIZE_OF_NUMBERS_UNITS_FILE_ENCODER_TYPE_1, "bytes" );
		result.setProperty(CONF_FILE_SIZE_OF_NUMBERS_UNITS_FILE_ENCODER_TYPE_2, "blocks" );
		result.setProperty(CONF_FILE_PARAMETERS_NOT_VALID, "The value of some parameters are not valid. Error produced:" );
		result.setProperty(CONF_FILE_ENCRYPTING_CONFIGURATION_ERROR, "Encrypting configuration error" );
		result.setProperty(CONF_FILE_ENCRYPTION_TWO_PASSWORDS_MUST_BE_EQUAL, "To continue with the encryption, the two passwords must be equal." );
		result.setProperty(CONF_FILE_PASSWORD_ERROR, "Password error" );
		result.setProperty(CONF_FILE_ONGOING_ACTION, "Ongoing action" );
		result.setProperty(CONF_FILE_DECRYPTION_TWO_PASSWORDS_MUST_BE_EQUAL, "To continue with the decryption, the two passwords must be equal." );
		result.setProperty(CONF_FILE_FORM_HAS_PENDING_TASKS, "The form has pending tasks: " );
		result.setProperty(CONF_FILE_PENDING_TASKS, "Pending Tasks" );
		result.setProperty(CONF_FILE_OPEN_TWO_PASSWORDS_MUST_BE_EQUAL, "To continue with the opening of file, the two passwords must be equal." );
		result.setProperty(CONF_FILE_SURE_DELETE_FILE, "Are you sure you want to delete the file" );
		result.setProperty(CONF_FILE_ATENTION, "Atention" );
		result.setProperty(CONF_FILE_YES, "Yes" );
		result.setProperty(CONF_FILE_NO, "No" );
		result.setProperty(CONF_FILE_HAVE_TO_SELECT_ROW_OF_TABLE, "You have to select a row of the table" );
		result.setProperty(CONF_FILE_SELECTION_ERROR, "Selection Error" );
		result.setProperty(CONF_FILE_NUMBER_CONFIGURED_IN, "The number configured in" );
		result.setProperty(CONF_FILE_HAS_TO_BE_A_CORRECT_LONG_INTEGER, "has to be a correct long integer" );
		result.setProperty(CONF_FILE_HAS_TO_BE_GREATER_THAN_OR_EQUAL_TO_ZERO, "has to be greater than or equal to zero" );
		result.setProperty(CONF_FILE_IS_EQUAL_TO_THE_ONE_OF_ANOTHER_ENCRYIPTING_CONFIGURATION, "is equal to the one of another encrypting configuration" );
//		result.setProperty(CONF_FILE_OS_NOT_RECOGNIZED_FOR_AUTOMATICALLY_OPENING_A_DOCUMENT, "Operating System not recognized for automatically opening a document" );
		result.setProperty(CONF_ERROR_OPENING_DOCUMENT, "Error opening document" );
		result.setProperty(CONF_DOCUMENT_OPENED, "Document tried to be opened" );
		result.setProperty(CONF_WANT_TO_OVERWRITE_OLD_ENCRYPTED_FILE, "Do you want to overwrite the .old encrypted file" );
		result.setProperty(CONF_MANUAL_ENCRYPTING_CONFIGURATION, "Manual" );
		result.setProperty(CONF_FILE_SIZE_ENCRYPTING_CONFIGURATION, "Based on file size" );
		result.setProperty(CONF_ENCRYPTED_FILE_CONFIGURATION, "Based on encrypted file parameters after decrypting" );
		result.setProperty(CONF_ERROR_DELETING_FILE, "Error deleting file" );
		result.setProperty(CONF_ABOUT_1, "By Francisco Javier Rojas Garrido" );
		result.setProperty(CONF_ABOUT_2, "Encrypting application based on the chaoting pseudorandom generator." );
		result.setProperty(CONF_ABOUT_3, "Thanks to Raül Rodríguez and Quim Blesa for their suggestions about the application." );
		result.setProperty(CONF_ABOUT_4, "Thanks to Albert Sala for doing the tests over Mac and cross-platforms." );
		result.setProperty(CONF_ABOUT_5, "Thanks to José Luis Moisés for reviewing the English translation of the documents and application interface." );
		result.setProperty(CONF_ABOUT_6, "Thanks to the authors of book \"Secuencias pseudoaleatorias para telecomunicaciones\" Edicions UPC (1996)" );
		result.setProperty(CONF_ABOUT_7, "Thanks to http://stackoverflow.com for the great help that they provide to developers" );
		result.setProperty(CONF_ABOUT_8, "Thanks to Rob Camick for the post: https://tips4java.wordpress.com/2008/11/10/table-column-adjuster/ which has been used in this application." );
		result.setProperty(CONF_ABOUT_9, "For bug reports or comments, please send an e-mail to:" );
		result.setProperty(CONF_RELEASED_ON, "Released on" );
		result.setProperty(CONF_SEND_EMAIL_TO, "Send e-mail to:" );
		result.setProperty(CONF_DECRYPTED_FILE_NAME_CANNOT_BE_JFE, "Decrypted file extension cannot be .jfe." );
		result.setProperty(CONF_DO_YOU_WANT_TO_CHOOSE_ANOTHER_FILE_NAME_FOR_DECRYPTED_FILE, "Do you want to choose another file name for the output decrypted file?" );
		result.setProperty(CONF_ENCRYPTED_FILE_NAME_MUST_BE_JFE, "Encrypted file extension must be .jfe.");
		result.setProperty(CONF_DO_YOU_WANT_TO_CHOOSE_ANOTHER_FILE_NAME_FOR_ENCRYPTED_FILE, "Do you want to choose another file name for the output encrypted file?");
		result.setProperty(CONF_ENCRYPTED_FILE_JFE, "Encrypted file (*.jfe)");
		result.setProperty(CONF_NORMAL_FONT_SIZE, "Normal Size");
		result.setProperty(CONF_LARGE_FONT_SIZE, "Large Size");
		result.setProperty(CONF_CANCELLED_BY_USER_WHEN_ENCRYPTING, "Cancelled by user when encrypting file: ");
		result.setProperty(CONF_CANCELLED_BY_USER_WHEN_DECRYPTING, "Cancelled by user when decrypting file: ");
		result.setProperty(CONF_FROM, "from");
		result.setProperty(CONF_TO, "to");
		result.setProperty(CONF_VISIT_HOME_PAGE, "Visit home page");

		return( result );
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
			result = M_getDefaultProperties2(language);
		}
		else
		{
			result = M_makePropertiesAddingDefaults( result, M_getDefaultProperties2(language) );
		}

		return( result );
	}
}
