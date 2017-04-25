/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.applications.common.components.internationalization;

import java.awt.Component;
import java.awt.Dimension;

/**
 *
 * @author Usuario
 */
public class ResizeRelocateItem
{
	public static final int RESIZE_TO_RIGHT = 1;
	public static final int MOVE_TO_RIGHT = 2;
	public static final int RESIZE_TO_BOTTOM = 4;
	public static final int MOVE_TO_BOTTOM = 8;
	
	public static final int MASK = 2 * MOVE_TO_BOTTOM - 1;
	
	protected Component _component;
	protected int		_flags;
	
	protected int		_pixelsLeftFromTheComponentRightBoundaryToTheRightOfTheParent = -1;		// it is needed if RESIZE_TO_RIGHT flag is active
	protected int		_pixelsFromTheComponentLeftBoundaryToTheRightOfTheParent = -1;			// it is needed if MOVE_TO_RIGHT flag is active
	protected int		_pixelsLeftFromTheComponentBottomBoundaryToTheBottomOfTheParent = -1;	// it is needed if RESIZE_TO_BOTTOM flag is active
	protected int		_pixelsFromTheComponentUpperBoundaryToTheBottomOfTheParent = -1;			// it is needed if MOVE_TO_BOTTOM flag is active
	
	
	public ResizeRelocateItem( Component comp, int flags ) throws InternException
	{
		_component = comp;
		_flags = flags;
		
		if( _component == null )
			throw( new InternException( "Component null" ) );

		Component parent = _component.getParent();
		
		if( parent == null )
			throw( new InternException( "Parent component null" ) );
		
		if( ( flags - ( flags & MASK ) ) != 0 )
			throw( new InternException( "Flags out of bounds" ) );
		
		if( isFlagActive( RESIZE_TO_RIGHT ) && isFlagActive( MOVE_TO_RIGHT ) )
			throw( new InternException( "Invalid flags: RESIZE_TO_RIGHT and MOVE_TO_RIGHT flags are both active" ) );
		else if( isFlagActive( RESIZE_TO_RIGHT ) )
		{
			_pixelsLeftFromTheComponentRightBoundaryToTheRightOfTheParent = new Double( parent.getWidth() ).intValue() -
																			new Double( _component.getX() ).intValue() -
																			new Double( _component.getWidth() ).intValue();
		}
		else if( isFlagActive( MOVE_TO_RIGHT ) )
		{
			_pixelsFromTheComponentLeftBoundaryToTheRightOfTheParent =	new Double( parent.getWidth() ).intValue() -
																		new Double( _component.getX() ).intValue();
		}
		
		if( isFlagActive( RESIZE_TO_BOTTOM ) && isFlagActive( MOVE_TO_BOTTOM ) )
			throw( new InternException( "Invalid flags: RESIZE_TO_BOTTOM and MOVE_TO_BOTTOM flags are both active" ) );
		else if( isFlagActive( RESIZE_TO_BOTTOM ) )
		{
			_pixelsLeftFromTheComponentBottomBoundaryToTheBottomOfTheParent =	new Double( parent.getHeight() ).intValue() -
																				new Double( _component.getY() ).intValue() -
																				new Double( _component.getHeight() ).intValue();
		}
		else if( isFlagActive( MOVE_TO_BOTTOM ) )
		{
			_pixelsFromTheComponentUpperBoundaryToTheBottomOfTheParent =	new Double( parent.getHeight() ).intValue() -
																			new Double( _component.getY() ).intValue();
		}
	}
	
	public boolean isFlagActive( int flag )
	{
		return( (_flags & flag) != 0 );
	}
	
	public void execute()
	{
		Component parent = _component.getParent();
		
		if( isFlagActive( RESIZE_TO_RIGHT ) )
		{
			int delta = new Double( parent.getWidth() ).intValue() -
						new Double( _component.getX() ).intValue() -
						new Double( _component.getWidth() ).intValue() -
						_pixelsLeftFromTheComponentRightBoundaryToTheRightOfTheParent;
			if( delta != 0 )	_component.setSize( new Double( _component.getWidth() ).intValue() + delta, new Double( _component.getHeight() ).intValue() );
		}
		else if( isFlagActive( MOVE_TO_RIGHT ) )
		{
			int delta = new Double( parent.getWidth() ).intValue() -
						new Double( _component.getX() ).intValue() -
						_pixelsFromTheComponentLeftBoundaryToTheRightOfTheParent;
			if( delta != 0 )	_component.setLocation( new Double( _component.getX() ).intValue() + delta, new Double( _component.getY() ).intValue() );
		}

		if( isFlagActive( RESIZE_TO_BOTTOM ) )
		{
			int delta = new Double( parent.getHeight() ).intValue() -
						new Double( _component.getY() ).intValue() -
						new Double( _component.getHeight() ).intValue() -
						_pixelsLeftFromTheComponentBottomBoundaryToTheBottomOfTheParent;
			if( delta != 0 )	_component.setSize( new Double( _component.getWidth() ).intValue(), new Double( _component.getHeight() ).intValue() + delta );
		}
		else if( isFlagActive( MOVE_TO_BOTTOM ) )
		{
			int delta = new Double( parent.getHeight() ).intValue() -
						new Double( _component.getY() ).intValue() -
						_pixelsFromTheComponentUpperBoundaryToTheBottomOfTheParent;
			if( delta != 0 )	_component.setLocation( new Double( _component.getX() ).intValue(), new Double( _component.getY() ).intValue() + delta );
		}
	}
	
}
