/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hotmail.frojasg1.applications.fileencoder;

import com.hotmail.frojasg1.general.HexadecimalFunctions;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Usuario
 */
public class TestFileEncoder
{

	protected static void M_encode()
	{
		String[] params_encode = new String[5];
		params_encode[0] = "-password";
		params_encode[1] = "kk_de_la_vaca";
		params_encode[2] = "-encode";
		params_encode[3] = "-fileName";
//		params_encode[4] = "J:\\N\\temp2\\GlobalConfiguration.properties";
		params_encode[4] = "J:\\N\\temp2\\Secuencias_pseudoaleatorias.pdf";
		FileEncoder.main( params_encode );
	}
	
	public static void M_decode()
	{
		String[] params_decode = new String[5];
		params_decode[0] = "-password";
		params_decode[1] = "kk_de_la_vaca";
		params_decode[2] = "-decode";
		params_decode[3] = "-fileName";
//		params_decode[4] = "J:\\N\\temp2\\GlobalConfiguration.properties.jfe";
		params_decode[4] = "J:\\N\\temp2\\Secuencias_pseudoaleatorias.pdf.jfe";
		FileEncoder.main( params_decode );
	}
	
	public static void M_logHeader()
	{
		String fileName = "J:\\N\\temp2\\Secuencias_pseudoaleatorias.pdf.jfe";
//		String fileName = "J:\\N\\temp2\\Secuencias_pseudoaleatorias.pdf.jfe";
		byte[] header = null;

		try
		{
			header = FileEncoder.M_getHeaderFromFile( fileName, 1024 );
		}
		catch (FileEncoderException ex)
		{
			Logger.getLogger(TestFileEncoder.class.getName()).log(Level.SEVERE, null, ex);
		}

		String log = HexadecimalFunctions.M_getLogFromBuffer(header);
		System.out.println( "Header of file: " + fileName );
		System.out.println( log );
	}
	
	public static void main( String[] args )
	{
		M_logHeader();
//		M_decode();
//		M_encode();
	}
	
}
