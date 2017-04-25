package com.hotmail.frojasg1.encrypting.pseudorandomgenerator.chaoticgenerator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Date;

import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.PseudoRandomGenerator;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.PseudoRandomGeneratorException;
import com.hotmail.frojasg1.encrypting.randomnumbers.RandomSource;
import com.hotmail.frojasg1.general.GeneralException;
import com.hotmail.frojasg1.general.HexadecimalFunctions;

public class ChaoticGenerator_BigDecimal implements PseudoRandomGenerator
{

	public ChaoticGenerator_BigDecimal()
	{
		a_sizeOfNumbers = 16;
		try
		{
			M_setNumberOfBitsPerIteration( 4 );
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public ChaoticGenerator_BigDecimal( int sizeOfNumbers, int numberOfBitsPerIteration ) throws PseudoRandomGeneratorException
	{
		if( (numberOfBitsPerIteration == 4) && (sizeOfNumbers==1) || (sizeOfNumbers<1) )
			throw( new PseudoRandomGeneratorException( "sizeOfNumbers not allowed " + sizeOfNumbers ) );

		a_sizeOfNumbers = sizeOfNumbers;
		M_setNumberOfBitsPerIteration( numberOfBitsPerIteration );
	}

	protected void M_setNumberOfBitsPerIteration( int numberOfBitsPerIteration ) throws PseudoRandomGeneratorException
	{
		if( ( numberOfBitsPerIteration != 1 ) && (numberOfBitsPerIteration != 2) && (numberOfBitsPerIteration !=4) )
			throw( new PseudoRandomGeneratorException( "numberOfBitsPerIteration not allowed " + numberOfBitsPerIteration ) );

		a_numberOfBitsPerIteration = numberOfBitsPerIteration;
		a_numIterationsToCompleteAByte = 8/a_numberOfBitsPerIteration;

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
		return( 2*a_sizeOfNumbers );
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
		// TODO Auto-generated method stub

		double numberOfDecimalDigits = Math.ceil( ( ( a_prefixForK.length() - 2 ) + 16 * a_sizeOfNumbers * Math.log10(2) ) / 2 ) + 3;
		int numDigX = ( new Double( numberOfDecimalDigits ) ).intValue();
		
		int numDigK = numDigX - ( a_prefixForK.length() - 2 );
		int numBytesX = ( new Double( Math.ceil( numDigX / (8D * Math.log10(2)) ) ) ).intValue();
		int numBytesK = ( new Double( Math.ceil( numDigK / (8D * Math.log10(2)) ) ) ).intValue();
		
		
		
		a_mc = new MathContext( numDigX );
		
		a_xx = BigDecimal.ZERO;
		if( key.length > numBytesK )
		{
			byte[] kKey = new byte[numBytesK]; 
			System.arraycopy( key, 0, kKey, 0, numBytesK );
			BigInteger bi = new BigInteger( kKey );
			String sbi = bi.toString();
			String withOutSign = ( (sbi.charAt(0) == '-')? sbi.substring( 1, sbi.length()) : sbi);
			a_kk = new BigDecimal( a_prefixForK + withOutSign );
			
			byte[] xKey = new byte[key.length-numBytesK];
			System.arraycopy( key, numBytesK, xKey, 0, key.length-numBytesK);
			
			BigInteger bi2 = new BigInteger( xKey );
			String sbi2 = bi2.toString();
			String withOutSign2 = ( (sbi2.charAt(0) == '-')? sbi2.substring( 1, sbi2.length()) : sbi2);
			a_xx = new BigDecimal( "0." + withOutSign2 );
			
/*
			BigDecimal two = new BigDecimal(2);
			BigDecimal wei = BigDecimal.ONE;

			for( int ii=numBytesK; (ii<key.length) && (ii<numBytesK+numBytesX); ii++ )
			{
				int mask = 0x80;
				int byteInt = key[ii];
				for( int jj=0; jj<8; jj++ )
				{
					if( (byteInt & mask) > 0 )	a_xx = a_xx.add( wei );
					mask = mask >> 1;
					wei = wei.divide(two);
				}
			}

			a_xx = a_xx.subtract(BigDecimal.ONE);
*/
		}
		else if( key.length > 0 )
		{
			BigInteger bi = new BigInteger( key );
			String sbi = bi.toString();
			String withOutSign = ( (sbi.charAt(0) == '-')? sbi.substring( 1, sbi.length()) : sbi);
			a_kk = new BigDecimal( a_prefixForK + withOutSign );
		}
		else
		{
			throw( new PseudoRandomGeneratorException( "Key with length zero" ) );
		}
		a_initialized = true;
		
//		System.out.println( "a_kk: " + a_kk + ", a_xx: " + a_xx );
	}

	protected int M_countOnes( BigDecimal bd )
	{
		int result = -1;
		
		BigInteger bi = bd.unscaledValue();
		byte[] ba = bi.toByteArray();

		result = HexadecimalFunctions.M_countOnes( ba, (ba.length-a_sizeOfNumbers>0?ba.length-a_sizeOfNumbers:0));

		return( result );
	}
	
	public long M_nextPartOfByte() throws PseudoRandomGeneratorException
	{
		a_xx = a_xx.multiply(a_xx, a_mc);
		a_xx = a_xx.add( a_kk, a_mc );
		
//		System.out.println( "a_xx: " + a_xx );
		
		int ones = M_countOnes( a_xx );
		return( (byte)(ones & a_maskForIteration) );
	}
	
	@Override
	public byte M_next() throws PseudoRandomGeneratorException
	{
		if( ! a_initialized ) throw( new PseudoRandomGeneratorException("ChaoticGenerator not initialized"));
		int resultInt = 0;
		
		for( int ii=0; ii<a_numIterationsToCompleteAByte; ii++ )
		{
			resultInt = (resultInt<<a_numberOfBitsPerIteration) | ((byte)M_nextPartOfByte());
		}
		
		return( (byte) resultInt );
	}

	protected int a_sizeOfNumbers; 					// in bytes
	protected int a_numberOfBitsPerIteration;
	protected int a_numIterationsToCompleteAByte;
	protected int a_maskForIteration;

	
	protected BigDecimal a_xx;
	protected BigDecimal a_kk;

	protected int a_numBytesX=-1;
	
	protected final static String a_prefixForK = "-1.9999999";

	protected MathContext a_mc;
	protected boolean a_initialized = false;
	
	public static void main( String[] params )
	{
		try
		{
			Date time1 = new Date();
			
			ChaoticGenerator_BigDecimal chg = new ChaoticGenerator_BigDecimal( 24, 4 );
			RandomSource rs = RandomSource.M_getInstanceOf();

			byte[] randomKey = rs.M_getRandomBytes( 48 );
//			chg.M_initialize( new String( "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF090999999564fad6559579d9f9869876986d7f987698e9867863" ) );
			chg.M_initialize( randomKey );
			
			int[] histogram = new int[16];
			for( int ii=0; ii<16; ii++ )	histogram[ii]=0;
			
			for( int ii=0; ii<32; ii++ )
			{
				for( int jj=0; jj<32; jj++ )
				{
					histogram[ (byte)chg.M_nextPartOfByte() ]++;
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
		}
		catch( PseudoRandomGeneratorException prge )
		{
			prge.printStackTrace();
		}
	}
}
