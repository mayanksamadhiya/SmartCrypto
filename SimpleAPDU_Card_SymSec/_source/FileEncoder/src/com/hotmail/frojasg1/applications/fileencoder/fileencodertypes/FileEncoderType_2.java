/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hotmail.frojasg1.applications.fileencoder.fileencodertypes;

import com.hotmail.frojasg1.applications.fileencoder.FileEncoder;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoderException;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoderParameters;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.UpdatingProgress;
import com.hotmail.frojasg1.encrypting.encoderdecoder.EncoderDecoder;
import com.hotmail.frojasg1.encrypting.encoderdecoder.ReordererEncoder;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.PseudoRandomGenerator;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.PseudoRandomGeneratorException;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.chaoticgenerator.ChaoticGenerator_BigLong;
import com.hotmail.frojasg1.encrypting.randomnumbers.RandomSource;
import com.hotmail.frojasg1.general.GeneralException;
import com.hotmail.frojasg1.general.HexadecimalFunctions;
import java.io.File;
import java.util.Arrays;

/**
 *
 * @author Usuario
 */
public class FileEncoderType_2 extends FileEncoderType_1
{
	public FileEncoderType_2( UpdatingProgress up ) throws FileEncoderException
	{
		super( up );
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
				
			prg_se = new ChaoticGenerator_BigLong( sizeOfNumbersSimpleEncoder, numberOfBitsPerIterationSimpleEncoder );
			prg_sr = new ChaoticGenerator_BigLong( sizeOfNumbersReordererEncoder, numberOfBitsPerIterationReordererEncoder );
				
			result = new ReordererEncoder( prg_se, prg_sr );
		}
		catch( PseudoRandomGeneratorException ex )
		{
			ex.printStackTrace();
			throw( new FileEncoderException( ex.getMessage() ) );
		}
		
		return( result );
	}

	@Override
	protected EncoderDecoder M_newEncoderDecoder_forShortKey_1( int numberSize, int passwordLength ) throws FileEncoderException
	{
		EncoderDecoder result = null;

		try
		{
			PseudoRandomGenerator prg_se = null;
			PseudoRandomGenerator prg_sr = null;
				
			prg_se = new ChaoticGenerator_BigLong( numberSize, 1 );
			if( prg_se.M_getRecommendedNumberOfBytesToInitializeKey() >= passwordLength )
			{
				prg_sr = new ChaoticGenerator_BigLong( numberSize, 1 );
				result = new ReordererEncoder( prg_se, prg_sr );
			}
		}
		catch( PseudoRandomGeneratorException ex )
		{
			ex.printStackTrace();
			throw( new FileEncoderException( ex.getMessage() ) );
		}
		
		return( result );
	}

	public int M_getFileEncoderType()
	{
		return( FileEncoderParameters.INT_FILE_ENCODER_TYPE_2 );
	}
}
