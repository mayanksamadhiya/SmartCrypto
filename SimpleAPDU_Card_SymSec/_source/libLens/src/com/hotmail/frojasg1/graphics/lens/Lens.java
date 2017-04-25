/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.graphics.lens;

import com.hotmail.frojasg1.graphics.Coordinate2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;

/**
 *
 * @author Usuario
 */
public class Lens
{
	protected Coordinate2D[][] a_transformationArray = null;
	protected int a_radius = 0;

	protected double a_range_Y = 1;
	protected double a_initialValue_Y = 0;
	protected double a_range_X = 1;
	protected double a_initialValue_X = 0;
	protected double a_base = 2;
	
	public static final int SA_MODE_AMPLIFY = 0;
	public static final int SA_MODE_REDUCE = 1;
	
	public Lens( int radius, double base, double min_x_transformation, double max_x_transformation,
					int mode ) throws LensException
	{
		if( radius <= 1 )
		{
			throw( new LensException( "Parameter radius must be greater than 1." ) );
		}
		
		if( base <= 1 )
		{
			throw( new LensException( "Parameter base must be greater than 1." ) );
		}
		
		if( max_x_transformation <= min_x_transformation )
		{
			throw( new LensException( "Parameter max_x_transformation must be greater than min_x_transformation." ) );
		}
		
		a_base = base;
		a_initialValue_X = min_x_transformation;
		a_range_X = max_x_transformation - min_x_transformation;
		a_initialValue_Y = Math.pow(base, a_initialValue_X );
		a_range_Y = Math.pow( base, max_x_transformation ) - a_initialValue_Y;

		M_calculateTransformationArray( radius, mode );
		
	}
	
	public Lens( int radius, int mode ) throws LensException
	{
		this( radius, 2, -2, 1, mode );
	}
	
	public void M_calculateTransformationArray( int radius, int mode ) throws LensException
	{
		if( (mode != SA_MODE_AMPLIFY) && (mode != SA_MODE_REDUCE) )
		{
			throw( new LensException( "Parameter mode must be either equal to SA_MODE_AMPLIFY or equal to SA_MODE_REDUCE." ) );
		}
		
		if( radius > 0 )
		{
			a_radius = radius;
			
			int arrayLength = 2 * radius + 1;
			
			a_transformationArray = new Coordinate2D[arrayLength][arrayLength];
			
			a_transformationArray[a_radius][a_radius] = new Coordinate2D( a_radius, a_radius );
			
			for( int xx=1; xx<=radius; xx++ )
				for( int yy=0; yy<=radius; yy++ )
				{
					Coordinate2D coor = new Coordinate2D( xx, yy );
					Coordinate2D transformedCoor = F_calculateTransformation( coor, a_radius, mode );
					
					int transformed_X = transformedCoor.M_getX();
					int transformed_Y = transformedCoor.M_getY();

					if( yy>0 )
					{
						a_transformationArray[a_radius + xx][a_radius + yy] = new Coordinate2D( a_radius + transformed_X, a_radius + transformed_Y );
						a_transformationArray[a_radius - xx][a_radius + yy] = new Coordinate2D( a_radius - transformed_X, a_radius + transformed_Y );
						a_transformationArray[a_radius + xx][a_radius - yy] = new Coordinate2D( a_radius + transformed_X, a_radius - transformed_Y );
						a_transformationArray[a_radius - xx][a_radius - yy] = new Coordinate2D( a_radius - transformed_X, a_radius - transformed_Y );
					}
					else if( yy==0 )
					{
						a_transformationArray[a_radius + xx][a_radius + yy] = new Coordinate2D( a_radius + transformed_X, a_radius + transformed_Y );
						a_transformationArray[a_radius - xx][a_radius + yy] = new Coordinate2D( a_radius - transformed_X, a_radius + transformed_Y );
						a_transformationArray[a_radius + yy][a_radius + xx] = new Coordinate2D( a_radius + transformed_Y, a_radius + transformed_X );
						a_transformationArray[a_radius + yy][a_radius - xx] = new Coordinate2D( a_radius + transformed_Y, a_radius - transformed_X );
					}
				}
		}
	}

	protected Coordinate2D F_calculateTransformation( Coordinate2D coor, double radius, int mode )
	{
		double xx = coor.M_getX();
		double yy = coor.M_getY();
		double radius2 = Math.sqrt(xx*xx + yy*yy);

		int transformed_xx = coor.M_getX();
		int transformed_yy = coor.M_getY();
		
		if( radius2 <= radius )
		{
			double angle = Math.acos( xx / radius2 );

			double transformedRadius = 0;
			
			if( mode == SA_MODE_AMPLIFY )
				transformedRadius = radius * M_transformationFunctionForRadius( radius2 / radius );
			else if( mode == SA_MODE_REDUCE )
				transformedRadius = radius * ( 1 - M_transformationFunctionForRadius( 1 - radius2 / radius ) );
			else
				transformedRadius = radius2;
				
			transformed_xx = (int) Math.round( transformedRadius * Math.cos( angle ) );
			transformed_yy = (int) Math.round( transformedRadius * Math.sin( angle ) );

		}
		Coordinate2D result = new Coordinate2D( transformed_xx, transformed_yy );

		return( result );
	}

	protected double M_transformationFunctionForRadius( double radiusRatio )
	{
		double result = ( Math.pow(a_base, a_initialValue_X + radiusRatio * a_range_X ) - a_initialValue_Y ) / a_range_Y;
		
		return( result );
	}
	
	public int M_getRadius()	{ return( a_radius );	}
	
	public Coordinate2D M_getTransformation( Coordinate2D coor )
	{
		Coordinate2D result = null;
		if( (coor.M_getX() >= 0) && (coor.M_getX() < a_transformationArray.length) &&
			(coor.M_getY() >= 0) && (coor.M_getY() < a_transformationArray.length) )
		{
			result = new Coordinate2D( a_transformationArray[ coor.M_getX() ][ coor.M_getY() ] );
		}
		else
		{
			result = new Coordinate2D( coor );
		}
		return( result );
	}

	/**
	* Returns an array of integer pixels in the default RGB color model
	* (TYPE_INT_ARGB) and default sRGB color space,
	* from a portion of the image data.  Color conversion takes
	* place if the default model does not match the image
	* <code>ColorModel</code>.  There are only 8-bits of precision for
	* each color component in the returned data when
	* using this method.  With a specified coordinate (x,&nbsp;y) in the
	* image, the ARGB pixel can be accessed in this way:
	* </p>
	*
	* <pre>
	*    pixel   = rgbArray[offset + (y-startY)*scansize + (x-startX)]; </pre>
	*
	* <p>
	*
	* An <code>ArrayOutOfBoundsException</code> may be thrown
	* if the region is not in bounds.
	* However, explicit bounds checking is not guaranteed.
	*
	* @param startX      the starting X coordinate
	* @param startY      the starting Y coordinate
	* @param w           width of region
	* @param h           height of region
	* @param rgbArray    if not <code>null</code>, the rgb pixels are
	*          written here
	* @param offset      offset into the <code>rgbArray</code>
	* @param scansize    scanline stride for the <code>rgbArray</code>
	* @return            array of RGB pixels.
	* @see #setRGB(int, int, int)
	* @see #setRGB(int, int, int, int, int[], int, int)
	*/
	public int[] getRGB(int startX, int startY, int w, int h,
						BufferedImage bi )
	{
		ColorModel colorModel = bi.getColorModel();

		Raster raster = bi.getRaster();
//		WritableRaster raster = colorModel.createCompatibleWritableRaster( bi.getWidth(), bi.getHeight() );

		int scansize = w;
		int offset =0;
		
		int yoff  = offset;
		int off;
		Object data;
		int nbands = raster.getNumBands();
		int dataType = raster.getDataBuffer().getDataType();
		switch (dataType)
		{
			case DataBuffer.TYPE_BYTE:
			data = new byte[nbands];
				break;
			case DataBuffer.TYPE_USHORT:
				data = new short[nbands];
				break;
			case DataBuffer.TYPE_INT:
				data = new int[nbands];
				break;
			case DataBuffer.TYPE_FLOAT:
				data = new float[nbands];
				break;
			case DataBuffer.TYPE_DOUBLE:
				data = new double[nbands];
				break;
			default:
				throw new IllegalArgumentException("Unknown data buffer type: "+
													dataType);
		}

		int[] rgbArray = new int[offset+h*scansize];

		for (int y = startY; y < startY+h; y++, yoff+=scansize)
		{
			off = yoff;
			for (int x = startX; x < startX+w; x++)
			{
				if( (x>=0) && (x<bi.getWidth()) && (y>=0) && (y<bi.getHeight() ) )
				{
					rgbArray[off++] = colorModel.getRGB(raster.getDataElements(	x,
																				y,
																				data));
				}
				else
				{
					rgbArray[off++] = 0;
				}
		   }
	   }

		return rgbArray;
	}

	public LensTransformationResult M_getTransformedImage( BufferedImage bi,
															Coordinate2D lensPosition )
	{
		LensTransformationResult result = null;
		
		BufferedImage bi_result = new BufferedImage( a_transformationArray.length, a_transformationArray.length, BufferedImage.TYPE_INT_RGB );
		
		int radiusX2 = 2*a_radius;
		
		int lensX =  lensPosition.M_getX();
		int lensY =  lensPosition.M_getY();
		
		int fromX = ( lensX-a_radius >= 0 ? 0 : a_radius - lensX );
		int fromY = ( lensY-a_radius >= 0 ? 0 : a_radius - lensY );
		
		int toX = ( lensX+a_radius < bi.getWidth() ? radiusX2 : radiusX2 - (lensX+a_radius - bi.getWidth() )  );
		int toY = ( lensY+a_radius < bi.getHeight() ? radiusX2 : radiusX2 - (lensY+a_radius -bi.getHeight() ) );
		
		int[] pixelColors = getRGB( lensPosition.M_getX()-a_radius, lensPosition.M_getY()-a_radius, radiusX2+1, radiusX2+1, bi );
		
		for( int yy=fromY; yy<=toY; yy++ )
			for( int xx=fromX; xx<=toX; xx++ )
			{
				Coordinate2D transformedCoor = M_getTransformation( new Coordinate2D( xx, yy ) );
				int xx_original = lensX - a_radius + transformedCoor.M_getX();
				int yy_original = lensY - a_radius + transformedCoor.M_getY();
				
				if( xx_original<0 ) xx_original=0;
				else if( xx_original>=bi.getWidth() ) xx_original=bi.getWidth()-1;
				
				if( yy_original<0 ) yy_original=0;
				else if( yy_original>=bi.getHeight() ) yy_original=bi.getHeight()-1;
				
				int offsetPixelColors = ( xx_original + a_radius - lensX ) +
										( yy_original + a_radius - lensY ) * (radiusX2+1);
				
				bi_result.setRGB( xx, yy, pixelColors[ offsetPixelColors ] );
			}
		
		Coordinate2D upleft = new Coordinate2D( fromX, fromY );
		Coordinate2D downright = new Coordinate2D( toX, toY );
		result = new LensTransformationResult( bi_result, upleft, downright );

		return( result );
	}
	
}
