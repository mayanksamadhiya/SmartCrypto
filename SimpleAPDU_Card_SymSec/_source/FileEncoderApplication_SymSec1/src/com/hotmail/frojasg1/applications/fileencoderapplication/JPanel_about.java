/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.applications.fileencoderapplication;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Usuario
 */
public class JPanel_about extends JPanel
{
	protected static final String resourceToImage = "com/hotmail/frojasg1/applications/fileencoderapplication/resources/puzzle.redimensionado.png";
	protected BufferedImage a_image = null;
	protected JDial_about a_parent = null;

	public JPanel_about( JDial_about parent )
	{
		try
		{
			InputStream in = null;
			ClassLoader loader = ClassLoader.getSystemClassLoader ();
			in = loader.getResourceAsStream (resourceToImage);

			if( in == null )
			{
				try
				{
					in = this.getClass().getClassLoader().getResource(resourceToImage).openStream();
				}
				catch( Throwable th )
				{
					in = null;
				}
			}

			a_image = ImageIO.read(in);
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			a_image = null;
		}
		
		a_parent = parent;
	}

	public void M_releaseResources()
	{
		a_parent = null;
	}

	@Override
	public void paint(Graphics gc)
	{
		super.paint( gc );

		if(a_image!=null)
		{
			int width = 245;
			int height = ( new Double( ((double)width * (double)a_image.getHeight()) / (double) a_image.getWidth() ) ).intValue();
			int x = 200;
			int y = 10;
			
			gc.drawImage(a_image, x, y, x+width, y+height, 0, 0, a_image.getWidth(), a_image.getHeight(), null);
			gc.setColor( Color.BLACK );
			gc.drawRect( x, y, width, height );
			gc.drawRect( x+1, y+1, width-2, height-2 );
			gc.drawRect( x+2, y+2, width-4, height-4 );

			Font font = new Font("Tahoma", Font.BOLD, 17);
			gc.setFont(font);

			FontRenderContext frc = ((Graphics2D)gc).getFontRenderContext();
			Rectangle2D boundsTitle = font.getStringBounds(a_parent.a_title, frc);
			
			int textX = x + (width-(int)boundsTitle.getWidth())/2;
			int textY = y + (height-(int)boundsTitle.getHeight())/2;
			
			gc.drawString(a_parent.a_title, textX, textY);
			
			boundsTitle = font.getStringBounds(a_parent.a_version, frc);
			textX = x + (width-(int)boundsTitle.getWidth())/2;
			gc.drawString(a_parent.a_version, textX, textY+25);
		}

	}


}
