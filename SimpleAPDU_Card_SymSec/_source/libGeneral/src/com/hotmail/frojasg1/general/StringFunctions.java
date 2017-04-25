/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hotmail.frojasg1.general;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Usuario
 */
public class StringFunctions
{

	public static String[] M_joinStringArrays( String[] array1, String[] array2 )
	{
		String[] result = null;

		if( array1 == null )		result = array2;
		else if( array2 == null )	result = array1;
		else
		{
			result = new String[array1.length + array2.length ];
			
			System.arraycopy(array1, 0, result, 0, array1.length );
			System.arraycopy(array2, 0, result, array1.length, array2.length );
		}
		return( result );
	}

	public static int M_compare( char[] array1, char[] array2 )
	{
		int result = 0;
		
		int length = ( array1.length>array2.length ? array2.length : array1.length );
		
		for( int ii=0; (ii<length) && (result==0); ii++ )
		{
			if( array1[ii] > array2[ii] )	result = 1;
			else if( array1[ii] < array2[ii] )	result = -1;
		}
		
		if( (result == 0) && ( array1.length != array2.length ) )
		{
			if( array1.length > array2.length )	result = 1;
			else	result = -1;
		}

		return( result );
	}

	public static byte[] M_getBytes( char[] array )
	{
		byte[] result = null;
		
		if( array != null )
		{
			result = new byte[ array.length*4 ];
			for( int ii=0; ii<array.length; ii++ )
			{
				byte[] intBuffer = HexadecimalFunctions.M_getBufferFromInteger( array[ii] );
				System.arraycopy(intBuffer, 0, result, ii*4, 4 );
			}
		}

		return( result );
	}

	public static String[] runRegEx( String patronRegEx, String str )
	{
		String[] result = null;
		Pattern pat = Pattern.compile( patronRegEx );
		Matcher mat = pat.matcher( str );
		if( mat.matches() )
		{
			int numGrupos = mat.groupCount();
			result = new String[ numGrupos + 1 ];
			for( int ii=0; ii<numGrupos+1; ii++ )
			{
				result[ ii ] = mat.group( ii );
			}
		}
		return( result );
	}
}
