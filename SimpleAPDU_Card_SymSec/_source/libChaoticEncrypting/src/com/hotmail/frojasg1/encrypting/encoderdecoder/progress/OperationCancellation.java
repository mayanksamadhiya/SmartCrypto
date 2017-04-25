/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.encrypting.encoderdecoder.progress;

/**
 *
 * @author Usuario
 */
public class OperationCancellation
{
	public class CancellationException extends Exception
	{
		public CancellationException( String message )
		{
			super( message );
		}
	}
	
	protected boolean a_hasToCancel=false;

	public OperationCancellation( boolean value )	{ M_setHasToCancel( value ); }
	
	public boolean M_getHasToCancel()				{ return( a_hasToCancel );	}
	public void M_setHasToCancel( boolean value )	{ a_hasToCancel=value; }
}
