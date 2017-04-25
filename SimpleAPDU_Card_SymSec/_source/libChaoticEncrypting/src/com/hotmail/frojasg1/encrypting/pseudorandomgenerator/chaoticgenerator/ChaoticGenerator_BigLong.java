package com.hotmail.frojasg1.encrypting.pseudorandomgenerator.chaoticgenerator;


import com.hotmail.frojasg1.encrypting.encoderdecoder.ReordererEncoder;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.PseudoRandomGenerator;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.PseudoRandomGeneratorException;
import com.hotmail.frojasg1.encrypting.randomnumbers.RandomSource;
import com.hotmail.frojasg1.general.GeneralException;
import com.hotmail.frojasg1.general.HexadecimalFunctions;
import java.math.MathContext;
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Usuario
 */
public class ChaoticGenerator_BigLong implements PseudoRandomGenerator
{
	public ChaoticGenerator_BigLong()
	{
		a_sizeOfNumbers = 5;
		try
		{
			M_setNumberOfBitsPerIteration( 4 );
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public ChaoticGenerator_BigLong( int sizeOfNumbers, int numberOfBitsPerIteration ) throws PseudoRandomGeneratorException
	{
		if( sizeOfNumbers<1 )
			throw( new PseudoRandomGeneratorException( "sizeOfNumbers not allowed " + sizeOfNumbers ) );

		a_sizeOfNumbers = sizeOfNumbers;
		M_setNumberOfBitsPerIteration( numberOfBitsPerIteration );
	}

	protected void M_setNumberOfBitsPerIteration( int numberOfBitsPerIteration ) throws PseudoRandomGeneratorException
	{
		if( ( numberOfBitsPerIteration != 1 ) && (numberOfBitsPerIteration != 2) && (numberOfBitsPerIteration !=4) )	// less than 8
		{
			if( (numberOfBitsPerIteration>64) ||
				(numberOfBitsPerIteration%8 != 0) ||
				(numberOfBitsPerIteration/8 > 3*a_sizeOfNumbers) )	// iqual to or greater than 8
					throw( new PseudoRandomGeneratorException( "numberOfBitsPerIteration not allowed " + numberOfBitsPerIteration ) );
		}

		a_numberOfBitsPerIteration = numberOfBitsPerIteration;
		a_numIterationsToCompleteAByte = 8/a_numberOfBitsPerIteration;
		if( a_numberOfBitsPerIteration >= 8 ) a_numberOfBytesPerIteration = a_numberOfBitsPerIteration/8;
		

		if(a_numberOfBitsPerIteration == 1)				a_maskForIteration = 0x01;
		else if( a_numberOfBitsPerIteration == 2 )		a_maskForIteration = 0x03;
		else if( a_numberOfBitsPerIteration == 4 )		a_maskForIteration = 0x0F;
	}

	@Override
	public int M_getNumberOfBitsPerIteration()
	{
		return( a_numberOfBitsPerIteration );
	}
	
	@Override
	public int M_getRecommendedNumberOfBytesToInitializeKey()
	{
		return( 8*a_sizeOfNumbers-3 );	// there are two numbers to initialize, every one with approximately 4 bytes per element (31 bits per element).
										// we have to substract the prefix of K (23 bits) approximately 3 bytes.
	}
	
	@Override
	public void M_initialize(String hexkey) throws PseudoRandomGeneratorException
	{
		byte[] byteKey = null;
		try
		{
			byteKey = HexadecimalFunctions.M_convertHexadecimalStringToByteArray(hexkey);
		}
		catch( GeneralException ex )
		{
			ex.printStackTrace();
			throw( new PseudoRandomGeneratorException( ex.getMessage() ) );
		}
		M_initialize( byteKey );
	}

	@Override
	public void M_initialize( byte[] key ) throws PseudoRandomGeneratorException
	{
		if( key.length <= 0 )
		{
			throw( new PseudoRandomGeneratorException( "Key with length zero" ) );
		}

		a_availableBitsFromLastIteration=0;

		int numberOfBytesForX = (key.length + 3) / 2;
		int numberOfBytesForK = numberOfBytesForX - 3 + ((key.length+1) & 0x01);
		
		if( numberOfBytesForK < 1 )
		{
			numberOfBytesForX = numberOfBytesForX - ( 1 - numberOfBytesForK );
			numberOfBytesForK=1;
		}
		
		long[] arrayX = new long[a_sizeOfNumbers];
		long[] arrayK = new long[a_sizeOfNumbers];
		
		for( int ii=0; ii<a_sizeOfNumbers; ii++ )
		{
			arrayX[ii]=0;
			arrayX[ii]=0;
		}

		boolean k_is_minus_2 = true;
		int numBytesUsed = 0;
		arrayK[arrayK.length-1] = a_prefixForK;	// K is like -1.9999999.....
		if( numBytesUsed < numberOfBytesForK )
		{
			arrayK[arrayK.length-1] = a_prefixForK | ( ((long) key[numBytesUsed]) & 0xFFL );
			numBytesUsed++;
			k_is_minus_2 = k_is_minus_2 && (arrayK[arrayK.length-1] == a_prefixForK);
		}

		for( int ii=a_sizeOfNumbers-2; (ii>=0) && (numBytesUsed<numberOfBytesForK); ii-- )
		{
			long element = ((long)key[numBytesUsed]) & 0xFFL;
			numBytesUsed++;
			long byteLong = 0;
			for( int jj=8; (jj<32) && (numBytesUsed<numberOfBytesForK); jj=jj+8 )
			{
				if(numBytesUsed<numberOfBytesForK)
				{
					byteLong=(( long ) key[numBytesUsed]) & 0xFFL;
					element = element | ( byteLong<<jj );
					numBytesUsed++;
				}
			}
			arrayK[ii]=( element & BigLong.a_longElementMask );
			k_is_minus_2 = k_is_minus_2 && (arrayK[ii] == 0);
		}
		if( k_is_minus_2 ) arrayK[0]++;		// if by casuality all bytes used to initialize K are zeroes, K would be equal to -2,
											// which does not lead to a chaoting function. We increment the least significant long to achive K=-1.9999999.....
		
		numBytesUsed = numberOfBytesForK;
		for( int ii=a_sizeOfNumbers-1; (ii>=0) && (numBytesUsed<numberOfBytesForK+numberOfBytesForX); ii-- )
		{
			long element = ((long)key[numBytesUsed]) & 0xFFL;
			numBytesUsed++;
			long byteLong = 0;
			for( int jj=8; (jj<32) && (numBytesUsed<numberOfBytesForK+numberOfBytesForX); jj=jj+8 )
			{
				if(numBytesUsed<numberOfBytesForK+numberOfBytesForX)
				{
					byteLong=(( long ) key[numBytesUsed]) & 0xFFL;
					element = element | ( byteLong<<jj );
					numBytesUsed++;
				}
			}
			arrayX[ii]=( element & BigLong.a_longElementMask );
		}
		arrayX[arrayX.length-1] &= 0xFFFFFFFL;	// X is between 0.000000 and 0.999999....

		a_xx = new BigLong( arrayX );
		a_kk = new BigLong( arrayK );

//		System.out.println( "ChaotingGenerator_BigLong initialized" );
//		System.out.println( "a_xx: " + a_xx );
//		System.out.println( "a_kk: " + a_kk );
		
		a_initialized = true;
	}
	
	public long M_nextPartOfByte() throws PseudoRandomGeneratorException
	{
//		double xx_ini = a_xx.M_toDouble();
		a_xx = BigLong.M_square(a_xx);

//		double xx_square = a_xx.M_toDouble();
//		System.out.println( String.format( "BigLong: %.20g * %.20g = %.20g", xx_ini, xx_ini, xx_square ) );
//		System.out.println( String.format( "double: %.20g * %.20g = %.20g\n", xx_ini, xx_ini, xx_ini*xx_ini ) );
		a_xx = BigLong.M_add( a_xx, a_kk );
//		System.out.println( String.format( "BigLong: %.20g + %.20g = %.20g", xx_square, a_kk.M_toDouble(), a_xx.M_toDouble() ) );
//		System.out.println( String.format( "double: %.20g + %.20g = %.20g\n", xx_square, a_kk.M_toDouble(), (xx_square+a_kk.M_toDouble()) ) );
		
//		System.out.println( "a_xx: " + a_xx );
		long result = 0;
		if( a_numberOfBitsPerIteration < 8 )
		{
			int ones = a_xx.M_countOnes();
			result = (ones & a_maskForIteration);	// it fits in one byte
		}
		else
		{
			result = a_xx.M_getBytesInLong( a_numberOfBytesPerIteration );
		}
		return( result );
	}
	
	@Override
	public byte M_next() throws PseudoRandomGeneratorException
	{
		if( ! a_initialized ) throw( new PseudoRandomGeneratorException("ChaoticGenerator not initialized"));
		int resultInt = 0;
		
		if( a_numberOfBitsPerIteration < 8 )
		{
			for( int ii=0; ii<a_numIterationsToCompleteAByte; ii++ )
			{
				resultInt = (resultInt<<a_numberOfBitsPerIteration) | ( (byte) M_nextPartOfByte() );
			}
		}
		else
		{
			if( a_availableBitsFromLastIteration == 0 )
			{
				a_lastIteration = M_nextPartOfByte();
				a_availableBitsFromLastIteration = a_numberOfBitsPerIteration;
			}
			resultInt = (byte) (a_lastIteration & 0xFFL);
			a_availableBitsFromLastIteration = a_availableBitsFromLastIteration - 8;
			a_lastIteration=(a_lastIteration>>>8);
		}

		return( (byte) resultInt );
	}

	protected int a_sizeOfNumbers; 					// in blocks of 31 bits
	protected int a_numberOfBitsPerIteration;
	protected int a_numIterationsToCompleteAByte;
	protected int a_maskForIteration;

	
	// The operations will be done as if the numbers were long integers.
	// As the generator works with decimal numbers, the
	protected BigLong a_xx;
	protected BigLong a_kk;

	protected int a_numBytesX=-1;
	
	protected final static long a_prefixForK = 0x60000000L;	// prefix for K that makes K=-1.999999999...
	protected final static long a_maskForPrefixForK = 0x7FFFFF00L; // do not change.

	protected MathContext a_mc;
	protected boolean a_initialized = false;
	
	protected long a_lastIteration = 0;
	protected int a_availableBitsFromLastIteration = 0;
	protected int a_numberOfBytesPerIteration = 0;

	protected static void test_1()
	{
		try
		{
			Date time1 = new Date();
			
			PseudoRandomGenerator chg_1 = new ChaoticGenerator_BigLong( 6, 4 );
			PseudoRandomGenerator chg_2 = new ChaoticGenerator_BigLong( 6, 4 );
			RandomSource rs = RandomSource.M_getInstanceOf();

			ReordererEncoder rd = new ReordererEncoder( chg_1, chg_2 );
			
			byte[] randomKey = rs.M_getRandomBytes( rd.M_getRecommendedNumberOfBytesToInitializeKey() );
			
			System.out.println( "random key:" );
			System.out.println( HexadecimalFunctions.M_getLogFromBuffer(randomKey) );
			
			rd.M_initializeKey(randomKey );
			
			byte[] buffer = new byte[256*1024];
			
			byte[] result = rd.M_encode(buffer, null, null);
			
			Date time2 = new Date();
			System.out.println( "time in ms " + (time2.getTime() - time1.getTime() )  );
/*
			int[] histogram = new int[16];
			for( int ii=0; ii<16; ii++ )	histogram[ii]=0;
			
			for( int ii=0; ii<8; ii++ )
			{
				for( int jj=0; jj<8; jj++ )
				{
					histogram[ chg.M_nextPartOfByte() ]++;
//					String hexStr = "00" + Integer.toHexString( chg.M_next() );
//					System.out.print( " " + hexStr.substring( hexStr.length()-2, hexStr.length()) );
				}
				
//				System.out.println("");
			}
			Date time2 = new Date();
			
			int ones = 0;
			int zeroes = 0;
			
			for( byte ii=0; ii<16; ii++ )
			{
				int onesByte = HexadecimalFunctions.M_countOnes(ii);
				System.out.println( "ones(ii) = "  + onesByte + ", histogram[" + ii + "] = " + histogram[ii] );
				zeroes += (4-onesByte) * histogram[ii];
				ones += onesByte * histogram[ii];
			}

			System.out.println( "zeroes: " + zeroes + " ones: " + ones );
			System.out.println( "time in ms " + (time2.getTime() - time1.getTime() )  );
*/
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}
	

	protected static void test_2()
	{
		try
		{
			PseudoRandomGenerator chg_1 = new ChaoticGenerator_BigLong( 6, 4 );
			PseudoRandomGenerator chg_2 = new ChaoticGenerator_BigLong( 6, 4 );

			byte[] byteArrayKey = new byte[2];
			byteArrayKey[0]=117;
			byteArrayKey[1]=0;
			
			ReordererEncoder rd = new ReordererEncoder( chg_1, chg_2 );
			
			rd.M_initializeKey( byteArrayKey );
			
		}
		catch( PseudoRandomGeneratorException prge )
		{
			prge.printStackTrace();
		}
	}
	
	protected static void test_3()
	{
		try
		{
			String hexKey = "00112233";
			byte[] byteKey = HexadecimalFunctions.M_convertHexadecimalStringToByteArray(hexKey);
			System.out.println( "Cadena hex original: " + hexKey + ". Cadena hex recalculada: " +
								HexadecimalFunctions.M_convertByteArrayToHexadecimalString( byteKey ) );
		}
		catch( GeneralException ex )
		{
			ex.printStackTrace();
		}

		try
		{
			PseudoRandomGenerator chg_1 = new ChaoticGenerator_BigLong( 4, 2 );

			String key = "0000000000000000000000000012345678901234567890123456789012";
			chg_1.M_initialize( key );
			
		}
		catch( PseudoRandomGeneratorException prge )
		{
			prge.printStackTrace();
		}
	}
	
	
	public static void main( String[] params )
	{
		test_3();
	}
}
