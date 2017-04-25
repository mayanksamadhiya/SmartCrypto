/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.graphics.lens;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Usuario
 */
public class ImageJPanel extends JPanel
{

	protected BufferedImage a_image = null;

	public ImageJPanel( String imageFileName )
	{
		File file = new File( imageFileName );
		try
		{
			a_image = ImageIO.read(file);
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			a_image = null;
		}
	}
	
	@Override
	public void paint(Graphics gc)
	{
		super.paint(gc);

		if(a_image!=null)
		{
			int width = a_image.getWidth();
			int height = a_image.getHeight();

			if( width > getWidth() )	width = getWidth();
			if( height > getHeight() )	height = getHeight();

			gc.drawImage(a_image, 0, 0, width, height, 0, 0, width, height, null);
		}
	}

	public BufferedImage M_getImage()
	{
		return( a_image );
	}
	
}
