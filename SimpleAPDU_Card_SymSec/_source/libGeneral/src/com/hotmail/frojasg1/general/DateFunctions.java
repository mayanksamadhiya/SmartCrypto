package com.hotmail.frojasg1.general;

import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFunctions
{
	
	// Both dateFormat and timeFormat, may take one of the following values:
	// { DateFormat.SHORT, DateFormat.MEDIUM, DateFormat.LONG, DateFormat.FULL };
	public static String formatDateTime( Date date, Locale locale, int dateFormat, int timeFormat )
	{
		String result = null;

		if( timeFormat < 0 ) result = DateFormat.getDateInstance( dateFormat, locale).format( date );
		else
		{
			DateFormat df = DateFormat.getDateTimeInstance( dateFormat, timeFormat, locale);
			result = df.format(date);
		}

		return( result );
	}

	public static String formatDate( Date date, String format )
	{
		String result = null;

		SimpleDateFormat sdf = new SimpleDateFormat( format );
		result = sdf.format( date );

		return( result );
	}
	
	public static String formatDate( Date date )
	{
		return( formatDate( date, "dd/MM/yyyy" ) );
	}
	
	public static String formatDateTime( Date date )
	{
		return( formatDate( date, "dd/MM/yyyy HH:mm:ss" ) );
	}
	
	public static String formatDate_yyyy( Date date, int format  )
	{
		DateFormat df = DateFormat.getDateInstance( DateFormat.SHORT );
		String result = df.format( date );

		SimpleDateFormat sdf = null;
		if( df instanceof SimpleDateFormat )
		{
			sdf = (SimpleDateFormat) df;
			String pattern = sdf.toPattern();
			pattern = pattern.replaceAll( "y+", "yyyy" );
			
			result = formatDate( date, pattern );
		}
		
		return( result );
	}
	
	public static Date parseSheetDate( String cadena, Locale locale )
	{
		Date date = null;
		
//		Locale locale = new Locale( "es", "ES" );

		int arrayOfTimeOrDateFormats[] = { DateFormat.SHORT, DateFormat.MEDIUM, DateFormat.LONG, DateFormat.FULL };

		for( int ii=0; ii<arrayOfTimeOrDateFormats.length; ii ++ )
		{
			try
			{
				date = DateFormat.getDateInstance( arrayOfTimeOrDateFormats[ii], locale).parse( cadena );
				return( date );
			}
			catch( ParseException ex )
			{
			}
		}

		for( int ii=0; ii<arrayOfTimeOrDateFormats.length; ii ++ )
			for( int jj=0; ii<arrayOfTimeOrDateFormats.length; ii ++ )
			{
				try
				{
					DateFormat df = DateFormat.getDateTimeInstance( arrayOfTimeOrDateFormats[ii], arrayOfTimeOrDateFormats[jj], locale);
					date = df.parse( cadena );
					return( date );
				}
				catch( ParseException ex )
				{
				}
			}

		return( date );
	}
	
	public static void main( String args[] )
	{
		Locale locale = new Locale( "es", "ES" );
		DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		if( formatter instanceof SimpleDateFormat )
		{
			SimpleDateFormat sdf = (SimpleDateFormat) formatter;
			System.out.println( "locale:" + locale.getDisplayCountry() + "   Formato de fechas: " + sdf.toPattern() + "   DateFormat.SHORT :" + DateFormat.SHORT );
		}
		
		DecimalFormatSymbols dfs = new DecimalFormatSymbols( locale );
		System.out.println( dfs.getDecimalSeparator() );
		
		DateFormat formatter1 = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.MEDIUM, locale);
		if( formatter1 instanceof SimpleDateFormat )
		{
			SimpleDateFormat sdf = (SimpleDateFormat) formatter1;
			System.out.println( "locale:" + locale.getDisplayCountry() + "   Formato de fechas: " + sdf.toPattern() );
		}
		
		Date date = new Date();
		System.out.println("now: " + DateFunctions.formatDateTime(date, locale, DateFormat.SHORT, DateFormat.MEDIUM ) );
	}
}
