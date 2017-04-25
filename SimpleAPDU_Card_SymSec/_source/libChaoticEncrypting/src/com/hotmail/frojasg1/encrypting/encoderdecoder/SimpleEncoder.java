package com.hotmail.frojasg1.encrypting.encoderdecoder;

import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.CancellationException;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.UpdatingProgress;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.OperationCancellation;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.PseudoRandomGenerator;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.PseudoRandomGeneratorException;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.chaoticgenerator.ChaoticGenerator_BigDecimal;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.chaoticgenerator.ChaoticGenerator_BigLong;
import com.hotmail.frojasg1.encrypting.randomnumbers.RandomSource;
import com.hotmail.frojasg1.general.HexadecimalFunctions;
import java.util.Date;

public class SimpleEncoder implements EncoderDecoder
{
	public SimpleEncoder() throws PseudoRandomGeneratorException
	{
		a_generator = new ChaoticGenerator_BigDecimal( 24, 4 );
	}
	
	public SimpleEncoder(int sizeOfNumbers, int numberOfBitsPerIteration ) throws PseudoRandomGeneratorException
	{
		a_generator = new ChaoticGenerator_BigDecimal( sizeOfNumbers, numberOfBitsPerIteration );
	}
	
	public SimpleEncoder( PseudoRandomGenerator generator ) throws PseudoRandomGeneratorException
	{
		a_generator = generator;
	}
	
	@Override
	public int M_getRecommendedNumberOfBytesToInitializeKey()
	{
		return( a_generator.M_getRecommendedNumberOfBytesToInitializeKey() );
	}
	
	@Override
	public void M_initializeKey( byte[] newKey ) throws PseudoRandomGeneratorException
	{
		a_generator.M_initialize(newKey);
	}

	@Override
	public void M_initializeKey( String newHexKey ) throws PseudoRandomGeneratorException
	{
		a_generator.M_initialize(newHexKey);
	}


	@Override
	public byte[] M_encode( byte[] input, UpdatingProgress up, OperationCancellation oc )
		throws PseudoRandomGeneratorException, CancellationException
	{
		byte[] result = new byte[ input.length ];
		
		if( up != null )
			up.beginProgress();

		int updatePeriod = 0;
		for( int ii=0; ii<input.length; ii++ )
		{
			result[ii] = (byte)( input[ii] ^ a_generator.M_next() );
			
			if( updatePeriod == 0 )
			{
				if( up != null )
				{
					int progress = (ii+1) * 100 / input.length;
					up.updateProgress( progress );
				}
				
				if( oc != null )
				{
					if( oc.M_getHasToCancel() )
						throw( new CancellationException( "Operation cancelled" ) );
				}
				
				updatePeriod = -2000;
			}
			updatePeriod++;
		}

		if( up != null )
			up.endProgress();
		
		return( result );
	}

	@Override
	public byte[] M_decode( byte[] input, UpdatingProgress up, OperationCancellation oc )
		throws PseudoRandomGeneratorException, CancellationException
	{
		return( M_encode( input, up, oc ) );
	}

	protected PseudoRandomGenerator a_generator;

	public static void M_testHistogram( int sizeOfNumbers, int bitsPerIteration ) throws PseudoRandomGeneratorException, CancellationException
	{
		RandomSource rs = RandomSource.M_getInstanceOf();

		Date time1 = new Date();
		ChaoticGenerator_BigLong gen = new ChaoticGenerator_BigLong( sizeOfNumbers, bitsPerIteration );
		SimpleEncoder se = new SimpleEncoder( gen );
		byte[] randomKey = rs.M_getRandomBytes( se.M_getRecommendedNumberOfBytesToInitializeKey() );

		se.M_initializeKey(randomKey );

		byte[] buffer = new byte[256*1024];
		for( int jj=0; jj<buffer.length; jj++ ) buffer[jj]=0;
		byte[] bufferEncoded = se.M_encode(buffer, null, null);

		int[] histogram = HexadecimalFunctions.M_getHistogram( bufferEncoded );
		System.out.println( String.format( "Test. sizeOfNumbers: %d, bitsPerIteration: %d", sizeOfNumbers, bitsPerIteration ) );
		System.out.println( HexadecimalFunctions.M_IntArrayToString( histogram ) );

		Date time2 = new Date();
		System.out.println( "time in ms " + (time2.getTime() - time1.getTime() ) + "\n\n"  );
	}
	
	public static void M_testHistograms()
	{
		try
		{
			for( int ii=8; ii<=64; ii=ii+8 )
			{
				M_testHistogram( 10, ii );
			}
		}
		catch( Throwable ex )
		{
			ex.printStackTrace();
		}
	}
	
	public static void main( String [] params )
	{
		M_testHistograms();
	}

}
