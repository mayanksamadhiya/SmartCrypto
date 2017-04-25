/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hotmail.frojasg1.applications.fileencoder.fileencodertypes;

import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.CancellationException;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoder;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoderException;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoderParameters;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoderType;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.OperationCancellation;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.UpdatingProgress;
import com.hotmail.frojasg1.encrypting.encoderdecoder.EncoderDecoder;
import com.hotmail.frojasg1.encrypting.encoderdecoder.ReordererEncoder;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.PseudoRandomGenerator;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.PseudoRandomGeneratorException;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.chaoticgenerator.ChaoticGenerator_BigDecimal;
import com.hotmail.frojasg1.encrypting.randomnumbers.RandomSource;
import com.hotmail.frojasg1.general.GeneralException;
import com.hotmail.frojasg1.general.HexadecimalFunctions;
import com.hotmail.frojasg1.general.StringFunctions;
import java.io.File;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 *
 * @author Usuario
 */
public class FileEncoderType_1 implements FileEncoderType
{
	protected UpdatingProgress a_updatingProgress = null;
	
	public FileEncoderType_1( UpdatingProgress up ) throws FileEncoderException
	{
		a_updatingProgress = up;
	}

	public void M_checkSizeOfNumbers( FileEncoderParameters fep ) throws FileEncoderException
	{
		if( ( fep.M_getSizeOfNumbersSimpleEncoder() < 1 ) || (fep.M_getSizeOfNumbersSimpleEncoder() > 127 ) )
			throw( new FileEncoderException( "Bad value for SizeOfNumbersSimpleEncoder. It must be between 1 and 127" ) );
		
		if( ( fep.M_getSizeOfNumbersReordererEncoder() < 1 ) || (fep.M_getSizeOfNumbersReordererEncoder() > 127 ) )
			throw( new FileEncoderException( "Bad value for SizeOfNumbersReordererEncoder. It must be between 1 and 127" ) );
	}
	
	public EncoderDecoder M_newEncoderDecoder( FileEncoderParameters fep2 ) throws FileEncoderException
	{
		EncoderDecoder result = null;
		
		M_checkSizeOfNumbers( fep2 );
		
		FileEncoderParameters fep = ( (fep2==null) ? a_fep : fep2 );
		int sizeOfNumbersSimpleEncoder = fep.M_getSizeOfNumbersSimpleEncoder();
		int sizeOfNumbersReordererEncoder = fep.M_getSizeOfNumbersReordererEncoder();
		int numberOfBitsPerIterationSimpleEncoder = fep.M_getNumberOfBitsPerIterationSimpleEncoder();
		int numberOfBitsPerIterationReordererEncoder = fep.M_getNumberOfBitsPerIterationReordererEncoder();
		
		try
		{
			PseudoRandomGenerator prg_se = null;
			PseudoRandomGenerator prg_sr = null;
				
			prg_se = new ChaoticGenerator_BigDecimal( sizeOfNumbersSimpleEncoder, numberOfBitsPerIterationSimpleEncoder );
			prg_sr = new ChaoticGenerator_BigDecimal( sizeOfNumbersReordererEncoder, numberOfBitsPerIterationReordererEncoder );
				
			result = new ReordererEncoder( prg_se, prg_sr );
		}
		catch( PseudoRandomGeneratorException ex )
		{
			ex.printStackTrace();
			throw( new FileEncoderException( ex.getMessage() ) );
		}
		
		return( result );
	}

	protected EncoderDecoder M_newEncoderDecoder_forShortKey_1( int numberSize, int keyLength ) throws FileEncoderException
	{
		EncoderDecoder result = null;

		try
		{
			PseudoRandomGenerator prg_se = null;
			PseudoRandomGenerator prg_sr = null;
				
			prg_se = new ChaoticGenerator_BigDecimal( numberSize, 1 );
			prg_sr = new ChaoticGenerator_BigDecimal( numberSize, 1 );
			result = new ReordererEncoder( prg_se, prg_sr );
			if( !(result.M_getRecommendedNumberOfBytesToInitializeKey() >= keyLength) )
			{
				result = null;
			}
		}
		catch( PseudoRandomGeneratorException ex )
		{
			ex.printStackTrace();
			throw( new FileEncoderException( ex.getMessage() ) );
		}
		
		return( result );
	}

	protected EncoderDecoder M_newEncoderDecoder_forShortKey( int keyLength ) throws FileEncoderException
	{
		EncoderDecoder result = null;

		int numberSize = 0;
		while( result == null )
		{
			numberSize++;
			result = M_newEncoderDecoder_forShortKey_1(numberSize, keyLength);
		}
		
		return( result );
	}

	protected byte[] M_encodeHeader( byte[] encodedKey, byte[] signature ) throws FileEncoderException
	{
		byte[] header_head_buffer = a_fep.M_getHeaderHead().getBytes();
		byte[] variable_part_buffer = new byte[5];
		variable_part_buffer[0] = ( new Integer( a_fep.M_getFileEncoderType() ) ).byteValue();
		variable_part_buffer[1] = ( new Integer( a_fep.M_getSizeOfNumbersSimpleEncoder() ) ).byteValue();
		variable_part_buffer[2] = ( new Integer( a_fep.M_getSizeOfNumbersReordererEncoder() ) ).byteValue();
		variable_part_buffer[3] = ( new Integer( a_fep.M_getNumberOfBitsPerIterationSimpleEncoder() ) ).byteValue();
		variable_part_buffer[4] = ( new Integer( a_fep.M_getNumberOfBitsPerIterationReordererEncoder() ) ).byteValue();
		
		byte[] sliceSize_buffer = HexadecimalFunctions.M_getBufferFromInteger(a_fep.M_getNumberOfBytesOfFileSlice() );
		
		byte[] result = new byte[ header_head_buffer.length + variable_part_buffer.length +
									sliceSize_buffer.length +
									encodedKey.length + signature.length ];
		
		System.arraycopy(header_head_buffer, 0, result, 0, header_head_buffer.length );
		System.arraycopy(variable_part_buffer, 0,
							result, header_head_buffer.length, variable_part_buffer.length );
		System.arraycopy(sliceSize_buffer, 0, result,
							header_head_buffer.length + variable_part_buffer.length,
							sliceSize_buffer.length );
		System.arraycopy(encodedKey, 0,
							result, header_head_buffer.length + variable_part_buffer.length +
							sliceSize_buffer.length,
							encodedKey.length );
		System.arraycopy( signature, 0, result,	header_head_buffer.length +
									variable_part_buffer.length +
									sliceSize_buffer.length +
									encodedKey.length,
									signature.length );

		return( result );
	}

	public int M_getFileEncoderType()
	{
		return( FileEncoderParameters.INT_FILE_ENCODER_TYPE_1 );
	}
	
	protected void M_updateParametersWithHeader( byte[] header, String fileName ) throws FileEncoderException
	{
		int lenHead = a_fep.M_getHeaderHead().getBytes().length;
		
		byte[] fileHead_buffer = new byte[lenHead];
		byte[] variable_part_buffer = new byte[5];
		byte[] sliceSize_buffer = new byte[4];
		
		System.arraycopy(header, 0, fileHead_buffer, 0, lenHead );
		System.arraycopy(header, lenHead, variable_part_buffer, 0, 5 );
		System.arraycopy(header, lenHead + variable_part_buffer.length, sliceSize_buffer, 0, 4 );

		String fileHead = new String( fileHead_buffer );
		if( ! fileHead.equals( a_fep.M_getHeaderHead() ) )	throw( new FileEncoderException( "HeaderHead unexpected in file " + fileName ) );
		
		int sliceSize = -1;
		try
		{
			sliceSize = HexadecimalFunctions.M_getIntegerFromBuffer(sliceSize_buffer);
		}
		catch( GeneralException ge )
		{
			ge.printStackTrace();
			throw( new FileEncoderException( "Error getting sliceSize from buffer in file: " + fileName ) );
		}

		int fileEncoderType = variable_part_buffer[0];
		int sizeOfNumbersSimpleEncoder = variable_part_buffer[1];
		int sizeOfNumbersReordererEncoder = variable_part_buffer[2];
		int numberOfBitsPerIterationSimpleEncoder = variable_part_buffer[3];
		int numberOfBitsPerIterationReordererEncoder = variable_part_buffer[4];

		if( fileEncoderType != M_getFileEncoderType() )	throw( new FileEncoderException( "FileEncoderType unexpected in file " + fileName ) );
		
		a_fep.M_setFileEncoderType(fileEncoderType);
		a_fep.M_setSliceSize(sliceSize);
		a_fep.M_setSizeOfNumbersSimpleEncoder(sizeOfNumbersSimpleEncoder);
		a_fep.M_setSizeOfNumbersReordererEncoder(sizeOfNumbersReordererEncoder);
		a_fep.M_setNumberOfBitsPerIterationSimpleEncoder(numberOfBitsPerIterationSimpleEncoder);
		a_fep.M_setNumberOfBitsPerIterationReordererEncoder(numberOfBitsPerIterationReordererEncoder);
	}

	protected byte[] M_calculateSHA_256( byte[] in ) throws FileEncoderException
	{
		byte[] result = null;
		
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(in);
			result = md.digest();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			throw( new FileEncoderException( ex.getMessage() ) );
		}
		return( result );
	}
	
	@Override
	public void M_encodeFile( char[] password, String fileName, String newFileName, OperationCancellation oc ) throws FileEncoderException, CancellationException
	{
		try
		{
			if( ( new File( newFileName )).exists() )	throw( new FileEncoderException( "ERROR!! File exists: "+newFileName ) );
			a_encoder = M_newEncoderDecoder(a_fep);
			int keySize = a_encoder.M_getRecommendedNumberOfBytesToInitializeKey();

			byte[] key = RandomSource.M_getInstanceOf().M_getRandomBytes(keySize);
//			a_encoder = M_newEncoderDecoder_forShortKey( a_fep.M_getPassword().getBytes().length );
//			a_encoder.M_initializeKey( HexadecimalFunctions.M_joinByteArrays(a_fep.M_getPassword().getBytes(), a_fep.M_getPassword().getBytes() ) );
			a_encoder = M_newEncoderDecoder_forShortKey( 32 ); // number of bytes returned by sha-256

			if( password == null )	password = a_fep.M_getPassword();
			if( password == null )	throw( new FileEncoderException( "Password needed" ) );
			
			a_encoder.M_initializeKey( M_calculateSHA_256( StringFunctions.M_getBytes(password) ) );

			byte[] encodedKey = a_encoder.M_encode( key, null, oc );
			
			a_encoder = M_newEncoderDecoder(a_fep);
			a_encoder.M_initializeKey( key );
			
			int numberOfBytesFileSlice = a_fep.M_getNumberOfBytesOfFileSlice();
			
			byte[] md5 =  FileEncoder.M_getMD5_fromFile_sliced( fileName, numberOfBytesFileSlice );
			byte[] encodedMD5 = a_encoder.M_encode( md5, null, oc );

			byte[] header = M_encodeHeader( encodedKey, encodedMD5 );

		    FileEncoder.M_writeEncodedFile_sliced( fileName, newFileName, header, a_encoder, numberOfBytesFileSlice, a_updatingProgress, oc );
		}
		catch( PseudoRandomGeneratorException ex )
		{
			ex.printStackTrace();
			throw( new FileEncoderException( ex.getMessage() ) );
		}
	}

	@Override
	public void M_decodeFile( char[] password, String fileName, String newFileName, OperationCancellation oc ) throws FileEncoderException, CancellationException
	{
		int offsetEncodedKey = a_fep.M_getHeaderHead().getBytes().length + 9;
		int keySize = 0;

		try
		{
			if( ( new File( newFileName )).exists() )	throw( new FileEncoderException( "ERROR!! File exists: "+newFileName ) );

			// we read the header, with the fixed amount of bytes of 1 KiB, assuring that the whole header will be read
			byte[] header = FileEncoder.M_getHeaderFromFile( fileName, 4096 );
			M_updateParametersWithHeader( header, a_fep.M_getEncodedFileName() );

			a_encoder = M_newEncoderDecoder(a_fep);
			keySize = a_encoder.M_getRecommendedNumberOfBytesToInitializeKey();

			if( header.length <= offsetEncodedKey + keySize + 16 )
			{
				throw( new FileEncoderException( "File too short. It is not a JavaFileEncoder file, FileName:"+ fileName ) );
			}
			
			byte[] encodedKey = new byte[ keySize ];
			System.arraycopy(header, offsetEncodedKey, encodedKey, 0, keySize );
			byte[] encodedMD5 = new byte[16];
			System.arraycopy(header, offsetEncodedKey + keySize, encodedMD5, 0, 16 );
			
//			a_encoder = M_newEncoderDecoder_forShortKey( a_fep.M_getPassword().getBytes().length );
//			a_encoder.M_initializeKey( HexadecimalFunctions.M_joinByteArrays(a_fep.M_getPassword().getBytes(), a_fep.M_getPassword().getBytes() ) );
			a_encoder = M_newEncoderDecoder_forShortKey( 32 ); // number of bytes returned by sha-256
			
			if( password == null )	password = a_fep.M_getPassword();
			if( password == null )	throw( new FileEncoderException( "Password needed" ) );
			
			a_encoder.M_initializeKey( M_calculateSHA_256( StringFunctions.M_getBytes(password) ) );

			byte[] key = a_encoder.M_decode( encodedKey, null, oc );

//			System.out.println( HexadecimalFunctions.M_convertByteArrayToHexadecimalString( key ) );

			a_encoder = M_newEncoderDecoder(a_fep);
			a_encoder.M_initializeKey( key );

		    byte[] md5 =  a_encoder.M_decode( encodedMD5, null, oc );

			int numberOfBytesFileSlice = a_fep.M_getNumberOfBytesOfFileSlice();

			FileEncoder.M_writeDecodedFile_sliced( fileName, newFileName,
													offsetEncodedKey + a_encoder.M_getRecommendedNumberOfBytesToInitializeKey() + 16,
													a_encoder,
													numberOfBytesFileSlice, a_updatingProgress, oc );

			byte[] calculatedMD5 = FileEncoder.M_getMD5_fromFile_sliced( newFileName, numberOfBytesFileSlice );
		    if( ! Arrays.equals(md5, calculatedMD5) )
			{
				try
				{
					File file = new File( newFileName );
					file.delete();
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
		    	throw( new FileEncoderException( "MD5 signature not correct. Aborting. File: " + fileName ) );
			}

		    System.out.println( "MD5 signature correct !!  Recovered unencrypted file: " + newFileName );

		}
		catch( PseudoRandomGeneratorException ex )
		{
			ex.printStackTrace();
			throw( new FileEncoderException( ex.getMessage() ) );
		}
	}
	
	@Override
	public void M_doActions( char[] password, FileEncoderParameters fep, OperationCancellation oc ) throws FileEncoderException, CancellationException
	{
		a_fep = fep;
		if( a_fep == null )	throw( new FileEncoderException("The object with the parameters of the action is null" ) );
		boolean isEncode = fep.M_isEncode();
		
		if( isEncode )
		{
			M_encodeFile( password, a_fep.M_getDecodedFileName(), a_fep.M_getEncodedFileName(), oc );
		}
		else
		{
			M_decodeFile( password, a_fep.M_getEncodedFileName(), a_fep.M_getDecodedFileName(), oc );
		}
	}
	
	@Override
	public FileEncoderParameters M_getFileEncoderParameters()	{ return( a_fep );	}

	protected EncoderDecoder a_encoder = null;
	protected FileEncoderParameters a_fep = null;
}
