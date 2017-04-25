/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.applications.fileencoder.configuration;

import com.hotmail.frojasg1.applications.common.configuration.ConfigurationException;
import com.hotmail.frojasg1.applications.common.configuration.ConfigurationParent;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoderParameters;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Usuario
 */
public class EncryptingConfiguration extends ConfigurationParent
{
	public final static String STR_CONF_SIZE_FROM = "SIZE_FROM";
	public final static String STR_CONF_FILE_ENCODER_TYPE = FileEncoderParameters.STR_FILE_ENCODER_TYPE_TAG;
	public final static String STR_CONF_NUM_BYTES_FILE_SLICE = FileEncoderParameters.STR_NUM_BYTES_FILE_SLICE_TAG;
	public final static String STR_CONF_SIZE_OF_NUMBERS_SIMPLE_ENCODER = FileEncoderParameters.STR_SIZE_OF_NUMBERS_SIMPLE_ENCODER_TAG;
	public final static String STR_CONF_SIZE_OF_NUMBERS_REORDERER_ENCODER = FileEncoderParameters.STR_SIZE_OF_NUMBERS_REORDERER_ENCODER_TAG;
	public final static String STR_CONF_NUMBER_OF_BITS_PER_ITERATION_SIMPLE_ENCODER = FileEncoderParameters.STR_NUMBER_OF_BITS_PER_ITERATION_SIMPLE_ENCODER_TAG;
	public final static String STR_CONF_NUMBER_OF_BITS_PER_ITERATION_REORDERER_ENCODER = FileEncoderParameters.STR_NUMBER_OF_BITS_PER_ITERATION_REORDERER_ENCODER_TAG;
	
	public EncryptingConfiguration()
	{
		super();
	}

	protected Properties M_getDefaultProperties( String language )
	{
		return( null );
	}

	protected void M_openConfigurationFromClassPath( String propName ) throws ConfigurationException, IOException
	{
		Properties properties = null;
		properties = cargarPropertiesClassPath( propName );
		a_properties = properties; // only if there is no exception, we change the values.
		
		if( a_defaultProperties == null ) a_defaultProperties = M_getDefaultProperties(a_language);
		
		if( (a_properties == null) && (a_defaultProperties == null) )
			throw( new ConfigurationException( "The configuration file in class path: " + propName + " could not be open" ) );
	}

	public String[] M_getParamListFromEncryptingConfiguration()
	{
		String result[] = null;

		String fileEncoderType = M_getStrParamConfiguration( EncryptingConfiguration.STR_CONF_FILE_ENCODER_TYPE);
		String numberOfBytesFileSlice = M_getStrParamConfiguration( EncryptingConfiguration.STR_CONF_NUM_BYTES_FILE_SLICE);
		String sizeOfNumbersSimpleEncoder = M_getStrParamConfiguration( EncryptingConfiguration.STR_CONF_SIZE_OF_NUMBERS_SIMPLE_ENCODER);
		String sizeOfNumbersReordererEncoder = M_getStrParamConfiguration( EncryptingConfiguration.STR_CONF_SIZE_OF_NUMBERS_REORDERER_ENCODER);
		String numberOfBitsPerIterationSimpleEncoder = M_getStrParamConfiguration( EncryptingConfiguration.STR_CONF_NUMBER_OF_BITS_PER_ITERATION_SIMPLE_ENCODER);
		String numberOfBitsPerIterationReordererEncoder = M_getStrParamConfiguration( EncryptingConfiguration.STR_CONF_NUMBER_OF_BITS_PER_ITERATION_REORDERER_ENCODER);

		int numberOfParams = 0;
		
		if( fileEncoderType != null ) numberOfParams = numberOfParams + 2;
		if( numberOfBytesFileSlice != null ) numberOfParams = numberOfParams + 2;
		if( sizeOfNumbersSimpleEncoder != null ) numberOfParams = numberOfParams + 2;
		if( sizeOfNumbersReordererEncoder != null ) numberOfParams = numberOfParams + 2;
		if( numberOfBitsPerIterationSimpleEncoder != null ) numberOfParams = numberOfParams + 2;
		if( numberOfBitsPerIterationReordererEncoder != null ) numberOfParams = numberOfParams + 2;

		if( numberOfParams > 0 )
		{
			result = new String[numberOfParams];
			int ii=0;
			if( fileEncoderType != null )
			{
				result[ii] = FileEncoderParameters.STR_FILE_ENCODER_TYPE_TAG;
				result[ii+1] = fileEncoderType;
				ii=ii+2;
			}

			if( numberOfBytesFileSlice != null )
			{
				result[ii] = FileEncoderParameters.STR_NUM_BYTES_FILE_SLICE_TAG;
				result[ii+1] = numberOfBytesFileSlice;
				ii=ii+2;
			}

			if( sizeOfNumbersSimpleEncoder != null )
			{
				result[ii] = FileEncoderParameters.STR_SIZE_OF_NUMBERS_SIMPLE_ENCODER_TAG;
				result[ii+1] = sizeOfNumbersSimpleEncoder;
				ii=ii+2;
			}

			if( sizeOfNumbersReordererEncoder != null )
			{
				result[ii] = FileEncoderParameters.STR_SIZE_OF_NUMBERS_REORDERER_ENCODER_TAG;
				result[ii+1] = sizeOfNumbersReordererEncoder;
				ii=ii+2;
			}

			if( numberOfBitsPerIterationSimpleEncoder != null )
			{
				result[ii] = FileEncoderParameters.STR_NUMBER_OF_BITS_PER_ITERATION_SIMPLE_ENCODER_TAG;
				result[ii+1] = numberOfBitsPerIterationSimpleEncoder;
				ii=ii+2;
			}

			if( numberOfBitsPerIterationReordererEncoder != null )
			{
				result[ii] = FileEncoderParameters.STR_NUMBER_OF_BITS_PER_ITERATION_REORDERER_ENCODER_TAG;
				result[ii+1] = numberOfBitsPerIterationReordererEncoder;
				ii=ii+2;
			}
		}

		return( result );
	}
}
