package com.hotmail.frojasg1.general;

public class HexadecimalFunctions
{
	private static byte[] a_countOnes;

	static
	{
		a_countOnes = new byte[256];
		for( int ii=0; ii<256; ii++ )
		{
			byte count = 0;
			int wei = 0x80;
			for( int jj=0; jj<8; jj++ )
			{
				if( (ii & wei)>0 )	count++;
				wei = wei >> 1;
			}
			a_countOnes[ii]=count;
		}
	}

	public static int M_convertHexDigitInValue( char hexDigit ) throws GeneralException
	{
		int result = Character.digit( hexDigit, 16 );
		if( result == -1 )	throw( new GeneralException( "Hexadecimal digit out of range" ) );
		return( result );
	}

	public static byte[] M_convertHexadecimalStringToByteArray( String hexStr ) throws GeneralException
	{
		byte[] result = null;
		if( hexStr.length() % 2 != 0 )	throw( new GeneralException( "Odd number of hexadecimal digits" ) );

		int numberOfBytes = hexStr.length()/2;
		result = new byte[numberOfBytes];
		int jj=0;
		for( int ii=0; ii<numberOfBytes; ii++ )
		{
			result[ii] = ( new Integer( M_convertHexDigitInValue( hexStr.charAt( jj ) ) * 16 +
										M_convertHexDigitInValue( hexStr.charAt( jj + 1) ) ) ).byteValue();
			jj=jj+2;
		}

		return( result );
	}
	
	public static String M_convertByteArrayToHexadecimalString( byte[] buffer )
	{
		StringBuilder result = new StringBuilder( buffer.length * 2 );
		int jj=0;
		for( int ii=0; ii<buffer.length; ii++ )
		{
			String hexOfByte = "0" + Integer.toHexString( buffer[ii] );
			String hexOfByte_of_two_chars = hexOfByte.substring( hexOfByte.length()-2, hexOfByte.length());
			result.replace( jj, jj+2, hexOfByte_of_two_chars );
			jj=jj+2;
		}

		return( new String( result ) );
	}
	
	public static int M_countOnes( byte value )
	{
		int index = value + (value<0?256:0);
		return( a_countOnes[index] );
	}
	
	public static int M_countOnes( byte[] ba, int pos )
	{
		int result = 0;
		for( int ii=pos; ii<ba.length; ii++ )
		{
			int index = ba[ii] + (ba[ii]<0?256:0);
			result = result + a_countOnes[index];
		}
		return( result );
	}
	
	public static int M_getIntegerFromBuffer( byte [] buffer ) throws GeneralException
	{
		if( (buffer == null) || (buffer.length != 4) ) throw( new GeneralException( "Buffer length not equal to 4 in M_getIntegerFromBuffer function") );

		int tmp0 = buffer[0] & 0xFF;
		int tmp1 = buffer[1] & 0xFF;
		int tmp2 = buffer[2] & 0xFF;
		int tmp3 = buffer[3] & 0xFF;

		int result = ( tmp0 | (tmp1<<8) | (tmp2<<16) | (tmp3<<24) );
		return( result );
	}

	public static byte[] M_getBufferFromInteger( int value )
	{
		byte[] result = new byte[4];
		result[0] = ( new Integer( value & 0xFF ) ).byteValue();
		result[1] = ( new Integer( (value>>>8) & 0xFF ) ).byteValue();
		result[2] = ( new Integer( (value>>>16) & 0xFF ) ).byteValue();
		result[3] = ( new Integer( (value>>>24) & 0xFF ) ).byteValue();
		return( result );
	}
	
	public static String M_getLogFromBuffer( byte[] buffer )
	{
		String log = "";

		int numBytesLogged = 0;
		while( (buffer != null) && (numBytesLogged < buffer.length) )
		{
			String offsetStr = String.format( "%06d: ", numBytesLogged );
			String lineCodes = "";
			String lineAsciis = "";
			for( int ii=0; ii<16; ii++ )
			{
				String code = "   ";
				String ascii = ".";
				if(numBytesLogged < buffer.length)
				{
					code = String.format( "%02X ", buffer[numBytesLogged] );
					if( (buffer[numBytesLogged] >= 32) &&
						(buffer[numBytesLogged] <127 ) )
					{
						ascii = String.valueOf( (char) buffer[numBytesLogged] );
					}
				}
				lineCodes = lineCodes + code;
				lineAsciis = lineAsciis + ascii;
				numBytesLogged++;
			}
			String line = offsetStr + lineCodes + lineAsciis + "\n";
			log = log + line;
		}
		return( log );
	}

	public static int[] M_getHistogram( byte[] buffer )
	{
		int[] result = new int[256];
		
		for( int ii=0; ii<256; ii++ ) result[ii]=0;
		
		for( int ii=0; ii<buffer.length; ii++ )
		{
			result[ (buffer[ii]>=0?buffer[ii]:buffer[ii]+256) ]++;
		}
		
		return( result );
	}

	public static String M_IntArrayToString( int[] array )
	{
		String result = "";
		for( int ii=0; ii<array.length; ii++ )
		{
			result = result + String.format( "int[%d]=%d\n", ii, array[ii] );
		}
		return( result );
	}
	
	public static byte[] M_joinByteArrays( byte[] array1, byte[] array2 )
	{
		byte[] result = null;

		if( array1 == null )		result = array2;
		else if( array2 == null )	result = array1;
		else
		{
			result = new byte[array1.length + array2.length ];
			
			System.arraycopy(array1, 0, result, 0, array1.length );
			System.arraycopy(array2, 0, result, array1.length, array2.length );
		}
		return( result );
	}

	
	public static void main( String[] args )
	{
		byte[] buffer = new byte[256];
		for( int ii=0; ii<256; ii++ ) buffer[ii]=( new Integer(ii) ).byteValue();
		String hexString = M_convertByteArrayToHexadecimalString( buffer );
		System.out.println( hexString );
		System.out.println( "longitud: " + hexString.length() );
	}
}
