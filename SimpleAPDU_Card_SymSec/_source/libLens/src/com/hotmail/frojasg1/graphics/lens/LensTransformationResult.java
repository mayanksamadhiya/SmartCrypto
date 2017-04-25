/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.graphics.lens;

import com.hotmail.frojasg1.graphics.Coordinate2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Usuario
 */
public class LensTransformationResult
{
	protected BufferedImage a_resultImage = null;
	protected Coordinate2D a_upLeftCorner = null;
	protected Coordinate2D a_downRightCorner = null;
	
	public LensTransformationResult( BufferedImage bi, Coordinate2D upLeftCorner,
										Coordinate2D downRightCorner )
	{
		a_resultImage = bi;
		a_upLeftCorner = upLeftCorner;
		a_downRightCorner = downRightCorner;
	}

	public BufferedImage M_getResultImage()	{	return( a_resultImage );	}
	public Coordinate2D M_getUpLeftCorner()	{	return( a_upLeftCorner );	}
	public Coordinate2D M_getDownRightCorner()	{	return( a_downRightCorner );	}
}
