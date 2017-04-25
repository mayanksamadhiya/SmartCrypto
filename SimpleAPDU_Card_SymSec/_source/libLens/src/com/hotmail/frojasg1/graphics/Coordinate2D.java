/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.graphics;

/**
 *
 * @author Usuario
 */
public class Coordinate2D
{
	protected int a_xx = -1;
	protected int a_yy = -1;
	
	public Coordinate2D( int xx, int yy )
	{
		a_xx = xx;
		a_yy = yy;
	}
	
	public Coordinate2D( Coordinate2D other )
	{
		a_xx = other.M_getX();
		a_yy = other.M_getY();
	}

	public int M_getX()	{	return( a_xx );	}
	public int M_getY()	{	return( a_yy );	}
	
	@Override
	public boolean equals( Object obj )
	{
		boolean result = false;
		if( obj != null )
		{
			if( obj instanceof Coordinate2D )
			{
				Coordinate2D other = (Coordinate2D) obj;
				
				result = (a_xx == other.M_getX() ) && (a_yy == other.M_getY() );
			}
		}
		return( result );
	}
}
