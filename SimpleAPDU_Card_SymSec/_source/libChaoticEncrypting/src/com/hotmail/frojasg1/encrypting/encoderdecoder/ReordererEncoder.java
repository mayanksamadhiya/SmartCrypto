package com.hotmail.frojasg1.encrypting.encoderdecoder;

import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.CancellationException;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.GeneralUpdatingProgress;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.UpdatingProgress;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.OperationCancellation;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.PseudoRandomGenerator;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.PseudoRandomGeneratorException;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.chaoticgenerator.ChaoticGenerator_BigLong;
import com.hotmail.frojasg1.general.GeneralException;
import com.hotmail.frojasg1.general.HexadecimalFunctions;

public class ReordererEncoder implements EncoderDecoder
{
	public ReordererEncoder() throws PseudoRandomGeneratorException
	{
		a_simpleEncoder = new SimpleEncoder();
		a_simpleReorderer = new SimpleReorderer();
	}
	
	public ReordererEncoder(int sizeOfNumbers, int numberOfBitsPerIteration ) throws PseudoRandomGeneratorException
	{
		a_simpleEncoder = new SimpleEncoder(sizeOfNumbers, numberOfBitsPerIteration);
		a_simpleReorderer = new SimpleReorderer(sizeOfNumbers, numberOfBitsPerIteration);
	}
	
	public ReordererEncoder(int sizeOfNumbersSimpleEncoder, int numberOfBitsPerIterationSimpleEncoder,
							int sizeOfNumbersReordererEncoder, int numberOfBitsPerIterationReordererEncoder )
		throws PseudoRandomGeneratorException
	{
		a_simpleEncoder = new SimpleEncoder(sizeOfNumbersSimpleEncoder, numberOfBitsPerIterationSimpleEncoder);
		a_simpleReorderer = new SimpleReorderer(sizeOfNumbersReordererEncoder, numberOfBitsPerIterationReordererEncoder);
	}

	// The parameter numberOfBitsPerIterationReordererEncoder must be the number of bits
	// returned by generatorReordererEncoder in every iteration of function M_nextPartOfByte
	public ReordererEncoder(	PseudoRandomGenerator generatorSimpleEncoder,
								PseudoRandomGenerator generatorReordererEncoder )
		 throws PseudoRandomGeneratorException
	{
		a_simpleEncoder = new SimpleEncoder( generatorSimpleEncoder );
		a_simpleReorderer = new SimpleReorderer( generatorReordererEncoder );
	}

	@Override
	public int M_getRecommendedNumberOfBytesToInitializeKey()
	{
		return( a_simpleReorderer.M_getRecommendedNumberOfBytesToInitializeKey() +
				a_simpleEncoder.M_getRecommendedNumberOfBytesToInitializeKey() );
	}
	
	@Override
	public void M_initializeKey(byte[] newKey) throws PseudoRandomGeneratorException
	{
		int sizeKey1 = -1;
		int sizeKey2 = -1;
		
		if( newKey.length == M_getRecommendedNumberOfBytesToInitializeKey() )
		{
			sizeKey1 = a_simpleEncoder.M_getRecommendedNumberOfBytesToInitializeKey();
			sizeKey2 = a_simpleReorderer.M_getRecommendedNumberOfBytesToInitializeKey();
		}
		else
		{
			sizeKey1 = newKey.length/2;
			sizeKey2 = newKey.length-sizeKey1;
		}

		byte[] key1 = new byte[sizeKey1];
		byte[] key2 = new byte[sizeKey2];

		System.arraycopy( newKey, 0, key1, 0, sizeKey1 );
		System.arraycopy( newKey, sizeKey1, key2, 0, sizeKey2 );

		a_simpleEncoder.M_initializeKey(key1);
		a_simpleReorderer.M_initializeKey(key2);
	}

	@Override
	public void M_initializeKey(String newHexKey) throws PseudoRandomGeneratorException
	{
		try
		{
			byte[] newKey = HexadecimalFunctions.M_convertHexadecimalStringToByteArray( newHexKey );
			M_initializeKey(newKey);
		}
		catch( GeneralException ex )
		{
			ex.printStackTrace();
			throw( new PseudoRandomGeneratorException( ex.getMessage() ) );
		}
	}

	@Override
	public byte[] M_encode(byte[] input, UpdatingProgress up, OperationCancellation oc )
		throws PseudoRandomGeneratorException, CancellationException
	{
		if( up != null )
			up.beginProgress();

		GeneralUpdatingProgress gup = new GeneralUpdatingProgress( up, 2 * input.length );
//		gup.setDebug(true);
		gup.prepareNextSlice(input.length);
		byte[] inter = a_simpleEncoder.M_encode( input, gup, oc );

		gup.prepareNextSlice(input.length);
		byte[] result = a_simpleReorderer.M_encode( inter, gup, oc );
		
		if( up != null )
			up.endProgress();
		
		return( result );
	}

	@Override
	public byte[] M_decode(byte[] input, UpdatingProgress up, OperationCancellation oc )
		throws PseudoRandomGeneratorException, CancellationException
	{
		if( up != null )
			up.beginProgress();

		GeneralUpdatingProgress gup = new GeneralUpdatingProgress( up, 2 * input.length );
//		gup.setDebug(true);
		gup.prepareNextSlice(input.length);
		byte[] inter = a_simpleReorderer.M_decode( input, gup, oc );

		gup.prepareNextSlice(input.length);
		byte[] result = a_simpleEncoder.M_decode( inter, gup, oc );

		if( up != null )
			up.endProgress();
		
		return( result );
	}

	SimpleEncoder a_simpleEncoder;
	SimpleReorderer a_simpleReorderer;


	protected static void test1( String key, int sizeOfNumbers, int bitsPerIteration )
	{
		EncoderDecoder ed = null;
		
		try
		{
			PseudoRandomGenerator prg_se = null;
			PseudoRandomGenerator prg_sr = null;
				
			prg_se = new ChaoticGenerator_BigLong( sizeOfNumbers, bitsPerIteration );
			prg_sr = new ChaoticGenerator_BigLong( sizeOfNumbers, bitsPerIteration );
				
			ed = new ReordererEncoder( prg_se, prg_sr );

			ed.M_initializeKey( key );

			byte[] codedBytes = new byte[1000000];
			byte[] plainBytes = ed.M_decode( codedBytes, null, null );

			System.out.println( "Se acabo la decodificacion" );
		}
		catch( Throwable ex )
		{
			ex.printStackTrace();
		}
	}

	public static void main( String [] params )
	{
		test1(	"5ea7d62cc533aad00fe63825d0e6a488145857df6da8631dc46fd46534911403c04ef12e8dd98fd934fa3aa82bf5855a494f3cd0a9c6d1ac8cd6",
				4, 2 );

		try
		{
			ReordererEncoder re = new ReordererEncoder();
			String key = "La clave para reordenar mas larga de todas. a ver si funciona";
			byte[] newKey = key.getBytes();
			re.M_initializeKey(newKey);
			
			byte[] output = re.M_encode( newKey, null, null );
			System.out.println( "cadena codificada: " + new String( output ) );

			re.M_initializeKey(newKey);
			byte[] inputAgain = re.M_decode( output, null, null );
			System.out.println( "cadena decodificada again: " + new String( inputAgain ) );
		}
		catch( Throwable ex )
		{
			ex.printStackTrace();
		}
	}
}
