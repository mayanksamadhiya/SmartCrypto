/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hotmail.frojasg1.encrypting.pseudorandomgenerator.chaoticgenerator;

import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.PseudoRandomGeneratorException;
import com.hotmail.frojasg1.general.HexadecimalFunctions;

/**
 *
 * @author Usuario
 */
public class BigLong
{
	
/*
	The class will store a number made of an array of long.
	Every element of the array stores a part of the number, and every element has 31 bits.
	The most significant bit of the most significant element is the sign bit.
	Opposite numbers are calculated using the two's complement
	The calculations are done a la big long, with integer aritmetic.
	This class only works if the numbers are limited always in the same range, as well as the result of the operations implemented.
*/

	
	long[] a_numberArray = null;
	static final long a_mostSignificantBitMask = 0x40000000L;
	public static final long a_longElementMask = 0x7FFFFFFFL;

	public BigLong( long[] numberArray ) throws PseudoRandomGeneratorException
	{
		a_numberArray = numberArray;
		if( numberArray == null || numberArray.length == 0 )
			throw( new PseudoRandomGeneratorException( "numberArray not valid" ) );

		for( int ii=0; ii<numberArray.length; ii++ )
			if( numberArray[ii] > a_longElementMask )
				throw( new PseudoRandomGeneratorException( "Long element greater than the maximum posible value for an element (more than 31 bits)" ) );
	}

	public static BigLong M_opposite( BigLong value ) throws PseudoRandomGeneratorException
	{
		long[] array = new long[value.a_numberArray.length];
		
		long element=(~value.a_numberArray[0]) & a_longElementMask;
		long carry = 0;
		element++;
		if( element > a_longElementMask )
		{
			carry=1;
			element = element & a_longElementMask;
		}
		array[0]=element;
		for( int ii=1; ii<array.length; ii++ )
		{
			element=(~value.a_numberArray[ii]) & a_longElementMask;
			if( carry == 1 )
			{
				element++;
				if( element > a_longElementMask )	element = element & a_longElementMask;
				else								carry=0;
			}
			array[ii]=element;
		}
		BigLong result = new BigLong( array );
		
		return( result );
	}
	
	public boolean M_isNegative()
	{
		return( (a_numberArray[a_numberArray.length-1] & a_mostSignificantBitMask) == a_mostSignificantBitMask );
	}

	// to simplify, it is supposed that term1 and term2 have the same length, and that it wont be overflow in the most significant long
	public static BigLong M_add( BigLong term1, BigLong term2 ) throws PseudoRandomGeneratorException
	{
		long[] array = new long[term1.a_numberArray.length];
		for( int ii=0; ii<array.length; ii++ )	array[ii]=0;

		for( int ii=0; ii<array.length; ii++ )
		{
			long element = term1.a_numberArray[ii] + term2.a_numberArray[ii];
			array[ii] = array[ii] + ( element & a_longElementMask );
			if( (ii<array.length-1) && element>a_longElementMask )
			{
				// if there was overflow in the current sum, we add the carry to the next element.
				// it is supposed that it wont be overflow in the most significant long, because the result of the pseudorandomgenerator is bounded between (-2, 2)
				array[ii+1] = 1;
			}
			
			if( (ii<array.length-1) && array[ii] > a_longElementMask )
			{
				array[ii+1]++;
				array[ii] = array[ii] & a_longElementMask;
			}
		}
		
		return( new BigLong( array ) );
	}

	public static BigLong M_square( BigLong factorParam ) throws PseudoRandomGeneratorException
	{
		BigLong factor = ( factorParam.M_isNegative() ? M_opposite( factorParam ) : factorParam );	// if factor param is negative, we calculate the opposite for doing the square. The result will be positive always.
/*
		if( factorParam.M_isNegative() )
			System.out.println( String.format( "Call to M_opposite:\n value:%s\n-value=%s\n", factorParam, factor ) );
*/
		int factorLength = factor.a_numberArray.length;
		long[] array = new long[factorLength+1];
		for( int ii=0; ii<array.length; ii++ )	array[ii]=0;

		long carry = 0;
		for( int ii=0; ii<factorLength; ii++ )
		{
			long element = carry; // it has 31 bits.
			carry=0;
			for( int jj=ii; jj<factorLength; jj++ )
			{
				element =	element +
							factor.a_numberArray[jj] *
							factor.a_numberArray[factorLength-1-jj+ii];
				carry = carry + ( element>>31 );
				element = element & a_longElementMask;
			}
			array[ii]=element;
		}
		array[array.length-1]=carry;

		long[] result = new long[factorLength];
		for( int ii=0; ii<factorLength; ii++ ) result[ii] = ( (array[ii] & 0x70000000L)>>28 ) | ( ( array[ii+1] & 0xFFFFFFFL) << 3 ); // we remove the carry part, to remain with a result with the same length

		return( new BigLong( result ) );
	}
	
	protected static int M_countOnes( long value )	// count the ones appearing in one element of the longArray
	{
		int result = HexadecimalFunctions.M_countOnes( (byte) ( value & 0xFFL ) );
		result += HexadecimalFunctions.M_countOnes( (byte) ( (value>>8) & 0xFFL ) );
		result += HexadecimalFunctions.M_countOnes( (byte) ( (value>>16) & 0xFFL ) );
		result += HexadecimalFunctions.M_countOnes( (byte) ( (value>>24) & 0xFFL ) );

		return( result );
	}
	
	public int M_countOnes()
	{
		int result = 0;
		for( int ii=0; ii<a_numberArray.length; ii++ ) result = result + M_countOnes( a_numberArray[ii] );
		return( result );
	}
	
	public double M_toDouble() throws PseudoRandomGeneratorException
	{
		BigLong bl = ( M_isNegative() ? M_opposite( this ) : this );
		
		double weight = 4.0D;
		double result = 0.0D;
		for( int ii=0; (ii<2) && (ii<bl.a_numberArray.length); ii++ )
		{
			long longWeight = a_mostSignificantBitMask;
			for( int jj=0; jj<31; jj++ )
			{
				if( (bl.a_numberArray[ bl.a_numberArray.length-1-ii ] & longWeight) == longWeight ) result = result + weight;
				weight = weight / 2.0D;
				longWeight = longWeight >> 1;
			}
		}
		if( M_isNegative() ) result = -result;
		
		return( result );
	}
	
	@Override
	public String toString()
	{
		String result = "";
		
		String separator = "";
		for( int ii=0; ii<a_numberArray.length; ii++ )
		{
			String longLog = String.format( "%s array[%d]=0x%X", separator, ii, a_numberArray[ii] );
			result = result + longLog;
			separator = ",";
		}

		double doubleValue = 0.0D;
		try
		{
			doubleValue = M_toDouble();
		}
		catch( PseudoRandomGeneratorException pe )
		{
			pe.printStackTrace();
		}
		result = result + String.format( ". float=%.20g", doubleValue );
		
		return( result );
	}
	
	public long M_getBytesInLong( int numberOfBytes ) throws PseudoRandomGeneratorException
	{
		long result = 0;
		
		int bytesRead = 0;
		for( int ii=0; (ii<a_numberArray.length) && (bytesRead<numberOfBytes); ii++ )
		{
			int bytesLeft = numberOfBytes-bytesRead;
			if( bytesLeft >= 3 )
			{
				result = (result<<24) | ( a_numberArray[ii] & 0xFFFFFFL );
				bytesRead=bytesRead + 3;
			}
			else if( bytesLeft == 2 )
			{
				result = (result<<16) | ( a_numberArray[ii] & 0xFFFFL );
				bytesRead=bytesRead + 2;
			}
			else if ( bytesLeft == 1 )
			{
				result = (result<<8) | ( a_numberArray[ii] & 0xFFL );
				bytesRead++;
			}
		}
		
		return( result );
	}
	
	public static void main( String[] args )
	{
		long[] arrayNumber1 = { 1449404011, 847656116, 826277099, 532624464 };
		long[] arrayNumber2 = { 2050283919, 1502424817, 1321206548, 1610612881 };
		
		try
		{
			BigLong term1 = new BigLong( arrayNumber1 );
			BigLong term2 = new BigLong( arrayNumber2 );
			
			BigLong result = M_add( term1, term2 );
			System.out.println( "result: " + result );
		}
		catch( PseudoRandomGeneratorException prge )
		{
			prge.printStackTrace();
		}
	}
}
