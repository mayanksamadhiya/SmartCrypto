/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.general;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Usuario
 */
public class ResourceFunctions
{

	public static BufferedImage getResourceImage( String resourcePath )
	{
		BufferedImage result = null;
		try
		{
			InputStream in = null;
			ClassLoader loader = ClassLoader.getSystemClassLoader ();
			in = loader.getResourceAsStream (resourcePath);

			if( in == null )
			{
				try
				{
					in = Class.forName("getResourceImage").getClassLoader().getResource(resourcePath).openStream();
				}
				catch( Throwable th )
				{
					in = null;
				}
			}

			result = ImageIO.read(in);
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			result = null;
		}
		return( result );
	}
	
}
