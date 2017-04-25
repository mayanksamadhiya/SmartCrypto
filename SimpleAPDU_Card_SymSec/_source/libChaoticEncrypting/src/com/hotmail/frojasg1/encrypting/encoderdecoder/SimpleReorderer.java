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

public class SimpleReorderer implements EncoderDecoder
{
	protected enum ENCODE_DECODE
	{
		ENCODE, DECODE
	}
	
	protected class ReturnNumBitsPerPositionAndMask
	{
		public int a_numBitsPerPosition;
		public int a_mask;
	}

	public SimpleReorderer() throws PseudoRandomGeneratorException
	{
		a_bitsPerIteration = 4;
		a_generator = new ChaoticGenerator_BigDecimal( 24, a_bitsPerIteration );
	}
	
	public SimpleReorderer(int sizeOfNumbers, int numberOfBitsPerIteration ) throws PseudoRandomGeneratorException
	{
		a_bitsPerIteration = numberOfBitsPerIteration;
		a_generator = new ChaoticGenerator_BigDecimal( sizeOfNumbers, a_bitsPerIteration );
	}
	
	// it is necessary that the parameter numberOfBitsPerIteration matches the
	// number of bits returned in every iteration by the a_generator function
	// M_nextPartOfByte
	public SimpleReorderer( PseudoRandomGenerator generator ) throws PseudoRandomGeneratorException
	{
		a_generator = generator;
		a_bitsPerIteration = a_generator.M_getNumberOfBitsPerIteration();
	}
	
	@Override
	public int M_getRecommendedNumberOfBytesToInitializeKey()
	{
		return( a_generator.M_getRecommendedNumberOfBytesToInitializeKey() );
	}
	
	@Override
	public void M_initializeKey(byte[] newKey) throws PseudoRandomGeneratorException
	{
		a_numBitsLeft = 0;
		a_bitsLeft_0 = 0;
		a_bitsLeft_1 = 0;

		a_generator.M_initialize(newKey);
	}

	@Override
	public void M_initializeKey(String newHexKey) throws PseudoRandomGeneratorException
	{
		a_numBitsLeft = 0;
		a_bitsLeft_0 = 0;
		a_bitsLeft_1 = 0;

		a_generator.M_initialize(newHexKey);
	}

	@Override
	public byte[] M_encode(byte[] input, UpdatingProgress up, OperationCancellation oc )
		throws PseudoRandomGeneratorException, CancellationException
	{
		return( M_reorder( input, ENCODE_DECODE.ENCODE, up, oc )	);
	}

	@Override
	public byte[] M_decode(byte[] input, UpdatingProgress up, OperationCancellation oc )
		throws PseudoRandomGeneratorException, CancellationException
	{
		return( M_reorder( input, ENCODE_DECODE.DECODE, up, oc )	);
	}

	public ReturnNumBitsPerPositionAndMask M_calculateNumBitsPerPosition( int numElem )
	{
		ReturnNumBitsPerPositionAndMask result = new ReturnNumBitsPerPositionAndMask();
		int numBits = 0;
		int mask = 0;
		if( numElem > 0 )
		{
			int wei = 1;
			numElem--;
			do
			{
				numBits++;
				mask = mask | wei;
				wei = wei << 1;
				numElem = numElem / 2;
			} while( numElem > 0 );
		}
		result.a_numBitsPerPosition = numBits;
		result.a_mask = mask;

		return( result );
	}

	protected int getNewPosition( ReturnNumBitsPerPositionAndMask numBitsAndMask ) throws PseudoRandomGeneratorException
	{
		long result = 0;
		boolean exit = false;
		while( ( a_numBitsLeft < numBitsAndMask.a_numBitsPerPosition ) && !exit )
		{
			if( a_numBitsLeft + a_bitsPerIteration < numBitsAndMask.a_numBitsPerPosition )
			{
				a_bitsLeft_0 = a_bitsLeft_0 | (a_generator.M_nextPartOfByte()<<a_numBitsLeft);
				a_numBitsLeft = a_numBitsLeft + a_bitsPerIteration;
			}
			else
			{
				a_bitsLeft_1 = a_generator.M_nextPartOfByte();
				exit = true;
			}
		}

		if( a_numBitsLeft >= numBitsAndMask.a_numBitsPerPosition )
		{
			result = a_bitsLeft_0 & (long) numBitsAndMask.a_mask;
			a_bitsLeft_0 = a_bitsLeft_0 >> numBitsAndMask.a_numBitsPerPosition;
			a_numBitsLeft = a_numBitsLeft - numBitsAndMask.a_numBitsPerPosition;
		}
		else
		{
			result = (a_bitsLeft_0 | (a_bitsLeft_1<<a_numBitsLeft)) & (long) numBitsAndMask.a_mask;
			a_bitsLeft_0 = a_bitsLeft_1 >>> (numBitsAndMask.a_numBitsPerPosition-a_numBitsLeft);
			a_numBitsLeft = a_bitsPerIteration + a_numBitsLeft - numBitsAndMask.a_numBitsPerPosition;
		}

		return( (int)result );
	}

	public byte[] M_reorder( 	byte[] input,
								ENCODE_DECODE endec,
								UpdatingProgress up,
								OperationCancellation oc )
								throws PseudoRandomGeneratorException, CancellationException
	{
		byte[] result = new byte[ input.length ];

		if( (endec != ENCODE_DECODE.ENCODE) && (endec != ENCODE_DECODE.DECODE) )
		{
			throw( new	PseudoRandomGeneratorException( "ENCODE_DECODE with bad value" ) );
		}

		if( up != null )
			up.beginProgress();

		ReturnNumBitsPerPositionAndMask numBitsAndMask = M_calculateNumBitsPerPosition( input.length );

		ReorderingTree tree = new ReorderingTree( input.length );

		int updatePeriod = 0;
		// al reves para que si intentan romper la clave a fuerza bruta, tengan que calcular todas las posiciones de la reordenacion.
		// Eso hace mucho mas robustos a los ficheros grandes.
		for( int ii=input.length-1; (ii>=0) && (result != null); ii-- )
		{
			int position = getNewPosition( numBitsAndMask );

			if( tree.M_isFull() )
			{
				System.out.println( "El arbol se lleno antes de tiempo. Abortamos" );
				result = null;
			}
			if( result != null )
			{
				position = tree.M_markBusyPosition( position );
				if( position == -1 ) result = null;
				if( result != null )
				{
					if( endec == ENCODE_DECODE.ENCODE ) result[position] = input[ii];
					else                    			result[ii] = input[position];
				}
				else
				{
					System.out.println( "Se produjo un error en la funcion de M_marcaPosicionOcupada ("+ii+"/"+input.length+
										"). Abortamos" );
				}
			}

			if( updatePeriod == 0 )
			{
				if( up != null )
				{
					int progress = (input.length-ii) * 100 / input.length;
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
		
		if( tree.M_isFull() )
		{
			//System.out.println( "Reordering Ok" );
		}
		else
		{
			result = null;
			System.out.println( "Error reordering" );
		}

		if( up != null )
			up.endProgress();

		return( result );
	}

	protected PseudoRandomGenerator a_generator;
	protected int a_bitsPerIteration;
	protected int a_numBitsLeft = 0;
	protected long a_bitsLeft_0 = 0;
	protected long a_bitsLeft_1 = 0;

	public static void M_testBasics()
	{
		try
		{
			SimpleReorderer sr = new SimpleReorderer();
			String key = "La clave para reordenar mas larga de todas. a ver si funciona123";
			byte[] newKey = key.getBytes();
			sr.M_initializeKey(newKey);
			
			byte[] output = sr.M_encode( newKey, null, null );
			System.out.println( "cadena codificada: " + new String( output ) );

			sr.M_initializeKey(newKey);
			byte[] inputAgain = sr.M_decode( output, null, null );
			System.out.println( "cadena decodificada again: " + new String( inputAgain ) );
		}
		catch( Throwable ex )
		{
			ex.printStackTrace();
		}
	}
	
	public static void M_testHistogram( int sizeOfNumbers, int bitsPerIteration ) throws PseudoRandomGeneratorException
	{
		RandomSource rs = RandomSource.M_getInstanceOf();

		Date time1 = new Date();
		ChaoticGenerator_BigLong gen = new ChaoticGenerator_BigLong( sizeOfNumbers, bitsPerIteration );
		SimpleReorderer sr = new SimpleReorderer( gen );
		byte[] randomKey = rs.M_getRandomBytes( sr.M_getRecommendedNumberOfBytesToInitializeKey() );

		sr.M_initializeKey(randomKey );

		int[] histogram = new int[256];
		for( int jj=0; jj<histogram.length; jj++ ) histogram[jj]=0;
		int[] positionArray = new int[64*1024];
		
		ReturnNumBitsPerPositionAndMask numBitsAndMask = sr.M_calculateNumBitsPerPosition( positionArray.length );
		for( int ii=0; ii<positionArray.length; ii++ )
		{
			positionArray[ii] = sr.getNewPosition( numBitsAndMask );
			byte value=(byte) (positionArray[ii] & 0xFF);
			histogram[ (value>=0?value:value+256) ]++;
			value=(byte) ( (positionArray[ii]>>>8) & 0xFF);
			histogram[ (value>=0?value:value+256) ]++;
			if( value >= positionArray.length )
			{
				System.out.println( "Error, a value greater than the maximum was received, " + positionArray[ii] );
			}
		}

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
		catch( PseudoRandomGeneratorException ex )
		{
			ex.printStackTrace();
		}
	}
	
	public static void main( String [] params )
	{
		M_testBasics();
		M_testHistograms();
	}
}
