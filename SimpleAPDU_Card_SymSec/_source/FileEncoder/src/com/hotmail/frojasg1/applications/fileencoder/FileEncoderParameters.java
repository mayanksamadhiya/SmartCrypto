/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hotmail.frojasg1.applications.fileencoder;

/**
 *
 * @author Usuario
 */
public class FileEncoderParameters
{
	public static final int INT_FILE_ENCODER_TYPE_1 = 1;	// it uses a ReordererEncoder with ChaoticGenerator_BigDecimal
	public static final int INT_FILE_ENCODER_TYPE_2 = 2;	// it uses a ReordererEncoder with ChaotingGenerator_BigLong,
															// which can be used to assure the compatibility with next versions of RunTime Java.
//	public static final int FILE_ENCODER_TYPE_3 = 3;
//	public static final int FILE_ENCODER_TYPE_4 = 4;
//	public static final int FILE_ENCODER_TYPE_5 = 5;
//	public static final int FILE_ENCODER_TYPE_6 = 6;
//	public static final int FILE_ENCODER_TYPE_7 = 7;
//	public static final int FILE_ENCODER_TYPE_8 = 8;
//	public static final int FILE_ENCODER_TYPE_9 = 9;
//	public static final int FILE_ENCODER_TYPE_10 = 10;

	public static final String STR_HEADER_HEAD = "JavaFileEncoder";
	public static final String STR_DEFAULT_ENCRYPTED_FILE_EXTENSION = "jfe";

	public static final String STR_ERASE_ORIGINAL_TAG = "-eraseoriginal";
	public static final String STR_PASSWORD_TAG = "-password";
	public static final String STR_ENCODED_FILE_NAME_TAG = "-encodedFileName";
	public static final String STR_DECODED_FILE_NAME_TAG = "-decodedFileName";
	public static final String STR_ENCRYPTED_FILE_EXTENSION_TAG = "-encryptedFileExtension";
	public static final String STR_ENCODE_TAG = "-encode";
	public static final String STR_GLOBAL_SIZE_NUMBERS_TAG = "-globalSizeOfNumbers";
	public static final String STR_GLOBAL_NUMBER_OF_BITS_PER_ITERATION_TAG = "-globalNumberOfBitsPerIteration";
	public static final String STR_SIZE_OF_NUMBERS_SIMPLE_ENCODER_TAG = "-sizeOfNumbersSimpleEncoder";
	public static final String STR_SIZE_OF_NUMBERS_REORDERER_ENCODER_TAG = "-sizeOfNumbersReordererEncoder";
	public static final String STR_NUMBER_OF_BITS_PER_ITERATION_SIMPLE_ENCODER_TAG = "-numberOfBitsPerIterationSimpleEncoder";
	public static final String STR_NUMBER_OF_BITS_PER_ITERATION_REORDERER_ENCODER_TAG = "-numberOfBitsPerIterationReordererEncoder";
	public static final String STR_NUM_BYTES_FILE_SLICE_TAG = "-numBytesFileSlice";
	public static final String STR_FILE_ENCODER_TYPE_TAG = "-fileEncoderType";
	public static final String STR_DECODE_TAG = "-decode";
	public static final String STR_USE_FILE_SIZE_FOR_ENCRYPTING_PARAMS_TAG = "-useFileSizeForEncryptingParams";
	
	public static final int INT_DEF_FILE_SLICE = 256 * 1024;
	
	
	public FileEncoderParameters( String[] params, boolean analyzeOnlyEncryptingParameters, boolean throwVoidParameterExceptions ) throws FileEncoderException
	{
		if( analyzeOnlyEncryptingParameters )	M_analyzeEncodingParametersFromArgs( params, throwVoidParameterExceptions );
		else									M_analizeArgs( params, throwVoidParameterExceptions );
	}

	static protected int M_getParamValue( String[] args, String str, String[] value )
	{
		int result = -1;
		
		for( int ii=0; (ii<args.length-1) && (result==-1); ii++ )
		{
			if( args[ii].compareToIgnoreCase( str ) == 0 )
			{
				value[0] = args[ii+1];
				result = ii+2;
			}
		}
		return( result );
	}
	
	static protected int M_isPresent( String[] args, String str )
	{
		int result = -1;
		
		for( int ii=0; (ii<args.length) && (result==-1); ii++ )
		{
			if( args[ii].compareToIgnoreCase( str ) == 0 ) result = ii+1;
		}
		return( result );
	}

	static protected int M_getIntegerFromString( String paramName, String valueToParse ) throws FileEncoderException
	{
		int result = -1;
		try
		{
			result = Integer.parseInt( valueToParse );
		}
		catch( Throwable thr )
		{
			throw( new FileEncoderException(	"Error parsing integer in FileEncoder parsing. ParamName: " + paramName +
												" value tempted to parse: " + valueToParse ) );
		}
		return( result );
	}

	public void M_analyzeEncodingParametersFromArgs( String[] args, boolean throwVoidParameterExceptions ) throws FileEncoderException
	{
		int num = -1;
		String[] ref_string = new String[1];

		num = M_isPresent( args, STR_USE_FILE_SIZE_FOR_ENCRYPTING_PARAMS_TAG );
		if( num>-1 )
		{
			a_useFileSizeForEncryptingParams = true;
		}
		
		num = M_getParamValue( args, STR_GLOBAL_SIZE_NUMBERS_TAG, ref_string );
		if( num>-1 )
		{
			a_globalSizeOfNumbers = M_getIntegerFromString( STR_GLOBAL_SIZE_NUMBERS_TAG, ref_string[0] );
		}

		num = M_getParamValue( args, STR_GLOBAL_NUMBER_OF_BITS_PER_ITERATION_TAG, ref_string );
		if( num>-1 )
		{
			a_globalNumberOfBitsPerIteration = M_getIntegerFromString( STR_GLOBAL_NUMBER_OF_BITS_PER_ITERATION_TAG, ref_string[0] );
		}

		num = M_getParamValue( args, STR_SIZE_OF_NUMBERS_SIMPLE_ENCODER_TAG, ref_string );
		if( num>-1 )
		{
			a_sizeOfNumbersSimpleEncoder = M_getIntegerFromString( STR_SIZE_OF_NUMBERS_SIMPLE_ENCODER_TAG, ref_string[0] );
		}

		num = M_getParamValue( args, STR_SIZE_OF_NUMBERS_REORDERER_ENCODER_TAG, ref_string );
		if( num>-1 )
		{
			a_sizeOfNumbersReordererEncoder = M_getIntegerFromString( STR_SIZE_OF_NUMBERS_REORDERER_ENCODER_TAG, ref_string[0] );
		}

		num = M_getParamValue( args, STR_NUMBER_OF_BITS_PER_ITERATION_SIMPLE_ENCODER_TAG, ref_string );
		if( num>-1 )
		{
			a_numberOfBitsPerIterationSimpleEncoder = M_getIntegerFromString( STR_NUMBER_OF_BITS_PER_ITERATION_SIMPLE_ENCODER_TAG, ref_string[0] );
		}

		num = M_getParamValue( args, STR_NUMBER_OF_BITS_PER_ITERATION_REORDERER_ENCODER_TAG, ref_string );
		if( num>-1 )
		{
			a_numberOfBitsPerIterationReordererEncoder = M_getIntegerFromString( STR_NUMBER_OF_BITS_PER_ITERATION_REORDERER_ENCODER_TAG, ref_string[0] );
		}

		num = M_getParamValue( args, STR_NUM_BYTES_FILE_SLICE_TAG, ref_string );
		if( num>-1 )
		{
			a_numberOfBytesOfFileSlice = M_getIntegerFromString( STR_NUM_BYTES_FILE_SLICE_TAG, ref_string[0] );
			if( (a_numberOfBytesOfFileSlice < 1) && throwVoidParameterExceptions )
			{
				throw( new FileEncoderException( "There must be the argument -numBytesFileSlice must be greater than zero" ) );
			}
		}
		else
		{
			a_numberOfBytesOfFileSlice = INT_DEF_FILE_SLICE;
		}

		num = M_getParamValue( args, STR_FILE_ENCODER_TYPE_TAG, ref_string );
		if( num>-1 )
		{
			a_fileEncoderType = M_getIntegerFromString( STR_FILE_ENCODER_TYPE_TAG, ref_string[0] );
		}
/*   it has been set in the parameter declaration the default value.
		else
		{
			a_fileEncoderType = INT_FILE_ENCODER_TYPE_2;
		}
*/
		switch( a_fileEncoderType )
		{
			case INT_FILE_ENCODER_TYPE_1:
			{
				if( M_getSizeOfNumbersSimpleEncoder() == -1 ) a_sizeOfNumbersSimpleEncoder = 24;
				if( M_getSizeOfNumbersReordererEncoder() == -1 ) a_sizeOfNumbersReordererEncoder = 24;
				if( M_getNumberOfBitsPerIterationSimpleEncoder() == -1 ) a_numberOfBitsPerIterationSimpleEncoder = 4;
				if( M_getNumberOfBitsPerIterationReordererEncoder() == -1 ) a_numberOfBitsPerIterationReordererEncoder = 4;
			}
			break;
				
			case INT_FILE_ENCODER_TYPE_2:
			{
				if( M_getSizeOfNumbersSimpleEncoder() == -1 ) a_sizeOfNumbersSimpleEncoder = 6;
				if( M_getSizeOfNumbersReordererEncoder() == -1 ) a_sizeOfNumbersReordererEncoder = 6;
				if( M_getNumberOfBitsPerIterationSimpleEncoder() == -1 ) a_numberOfBitsPerIterationSimpleEncoder = 4;
				if( M_getNumberOfBitsPerIterationReordererEncoder() == -1 ) a_numberOfBitsPerIterationReordererEncoder = 4;
			}
			break;
				
			default:
			{
				throw( new FileEncoderException( "Value for " + STR_FILE_ENCODER_TYPE_TAG +
												" out of range: " + a_fileEncoderType ) );
			}
		}
	}

	
	public void M_analizeArgs( String[] args, boolean throwVoidParameterExceptions ) throws FileEncoderException
	{
		if( args == null )
		{
			throw( new FileEncoderException( "The arguments string array must be different from null" ) );
		}
		else if( args.length < 1 )
		{
			throw( new FileEncoderException( "The arguments string array must be different from null" ) );
		}
		
		int num = 0;
		String[] ref_string = new String[1];
		
		num = M_isPresent( args, STR_ERASE_ORIGINAL_TAG );
		if( num>-1 )
		{
			a_hasToEraseOriginalFile = true;
		}
		else
		{
			a_hasToEraseOriginalFile = false;
		}
		
		num = M_getParamValue( args, STR_PASSWORD_TAG, ref_string );
		if( num>-1 )
		{
			a_password = ref_string[0].toCharArray();
		}
/*		else if( throwVoidParameterExceptions )
			throw( new FileEncoderException( "There must be a password after the parameter " + STR_PASSWORD_TAG ) );
*/
		num = M_getParamValue( args, STR_ENCODED_FILE_NAME_TAG, ref_string );
		if( num>-1 )
		{
			a_encodedFileName = ref_string[0];
		}
		else if( throwVoidParameterExceptions )
		{
			throw( new FileEncoderException( "There must be the argument -encodedFileName followed by the name of the file to be processed" ) );
		}
		
		num = M_getParamValue( args, STR_DECODED_FILE_NAME_TAG, ref_string );
		if( num>-1 )
		{
			a_decodedFileName = ref_string[0];
		}
		else if( throwVoidParameterExceptions )
		{
			throw( new FileEncoderException( "There must be the argument -decodedFileName followed by the name of the file to be processed" ) );
		}
		
		num = M_getParamValue( args, STR_ENCRYPTED_FILE_EXTENSION_TAG, ref_string );
		if( num>-1 )
		{
			a_encriptedFileExtension = ref_string[0];
		}
		else
		{
			a_encriptedFileExtension = STR_DEFAULT_ENCRYPTED_FILE_EXTENSION;
		}

		num = M_isPresent( args, STR_ENCODE_TAG );
		if( num>-1 )
		{
			a_isEncode = true;
			
			M_analyzeEncodingParametersFromArgs( args, throwVoidParameterExceptions );
			
		}
		else
		{
			num = M_isPresent( args, new String( STR_DECODE_TAG ) );
			if( num > -1 ) a_isEncode=false;
			else	throw( new FileEncoderException( "There must be the argument " +
														STR_ENCODE_TAG + " or the argument " +
														STR_DECODE_TAG ) );
		}
	}

	public boolean M_isEncode()					{ return( a_isEncode ); }
	public boolean M_hasToEraseOriginalFile()	{ return( a_hasToEraseOriginalFile ); }
	public char[] M_getPassword()				{ return( a_password ); }
	public String M_getEncodedFileName()		{ return( a_encodedFileName ); }
	public String M_getDecodedFileName()		{ return( a_decodedFileName ); }
	public int M_getNumberOfBytesOfFileSlice()	{	return( a_numberOfBytesOfFileSlice );	}
	public int M_getFileEncoderType()			{	return( a_fileEncoderType );	}
	public String M_getEncryptedFileExtension()	{	return( a_encriptedFileExtension );	}
	public String M_getHeaderHead()				{	return( STR_HEADER_HEAD );	}

	protected int M_getParamValue_from_value_and_globalValue( int value, int globalValue )
	{
		return( ( value > -1 ? value : globalValue ) );
	}
	
	public int M_getSizeOfNumbersSimpleEncoder()
	{
		return( M_getParamValue_from_value_and_globalValue( a_sizeOfNumbersSimpleEncoder, a_globalSizeOfNumbers ) );
	}
	
	public int M_getSizeOfNumbersReordererEncoder()
	{
		return( M_getParamValue_from_value_and_globalValue( a_sizeOfNumbersReordererEncoder, a_globalSizeOfNumbers ) );
	}
	
	public int M_getNumberOfBitsPerIterationSimpleEncoder()
	{
		return( M_getParamValue_from_value_and_globalValue( a_numberOfBitsPerIterationSimpleEncoder, a_globalNumberOfBitsPerIteration ) );
	}
	
	public int M_getNumberOfBitsPerIterationReordererEncoder()
	{
		return( M_getParamValue_from_value_and_globalValue( a_numberOfBitsPerIterationReordererEncoder, a_globalNumberOfBitsPerIteration ) );
	}

	public boolean M_getHasToUseFileSizeForEncryptingParams()
	{
		return( a_useFileSizeForEncryptingParams );
	}
	
	public void M_setSizeOfNumbersSimpleEncoder( int value )				{a_sizeOfNumbersSimpleEncoder = value; }
	public void M_setSizeOfNumbersReordererEncoder( int value )				{a_sizeOfNumbersReordererEncoder = value; }
	public void M_setNumberOfBitsPerIterationSimpleEncoder( int value )		{a_numberOfBitsPerIterationSimpleEncoder = value; }
	public void M_setNumberOfBitsPerIterationReordererEncoder( int value )	{a_numberOfBitsPerIterationReordererEncoder = value; }

	public void M_setSliceSize( int value )									{a_numberOfBytesOfFileSlice = value;	}
	public void M_setFileEncoderType( int value )							{a_fileEncoderType = value;	}
	
	protected boolean a_isEncode = true;
	protected boolean a_hasToEraseOriginalFile = false;
	protected char[] a_password = null;
	protected String a_encodedFileName = null;
	protected String a_decodedFileName = null;
	
	protected int a_sizeOfNumbersSimpleEncoder = -1;
	protected int a_sizeOfNumbersReordererEncoder = -1;
	protected int a_numberOfBitsPerIterationSimpleEncoder = -1;
	protected int a_numberOfBitsPerIterationReordererEncoder = -1;

	protected int a_globalSizeOfNumbers = -1;
	protected int a_globalNumberOfBitsPerIteration = -1;

	protected int a_numberOfBytesOfFileSlice = -1;

	protected int a_fileEncoderType = INT_FILE_ENCODER_TYPE_2;	// by default we will use the fileEncoderType = 2

	protected String a_encriptedFileExtension = null;

	protected boolean a_useFileSizeForEncryptingParams = false;
}
