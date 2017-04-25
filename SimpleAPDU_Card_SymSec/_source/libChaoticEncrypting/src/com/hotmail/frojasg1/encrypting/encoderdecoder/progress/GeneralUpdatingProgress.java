/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.encrypting.encoderdecoder.progress;

import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.UpdatingProgress;

/**
 *
 * @author Usuario
 */
public class GeneralUpdatingProgress implements UpdatingProgress
{
	protected UpdatingProgress _parentUpdatingProgress = null;
	protected long _totalUnits = 1;
	protected long _completedUnits = 0;
	protected int _currentUnitsSlice = 0;
	protected boolean _debug = false;
	protected int _sliceNumber = 0;

	protected int _lastReportedProgress = -1;

	public GeneralUpdatingProgress( UpdatingProgress up, long totalUnits )
	{
		_parentUpdatingProgress = up;
		_totalUnits = totalUnits;
		if( _parentUpdatingProgress != null )
			_parentUpdatingProgress.beginProgress();
	}
	
	public void prepareNextSlice( int unitsSlice )
	{
		_sliceNumber++;
		_currentUnitsSlice = unitsSlice;
		if( _debug )
		{
			System.out.println( "New slice. Slice number: " + _sliceNumber + " amount: " + _currentUnitsSlice );
		}
	}

	public void skip( int unitsToSkip )
	{
		_sliceNumber++;
		_completedUnits = _completedUnits + unitsToSkip;
		if( _debug )
		{
			System.out.println( "New skyped slice. Slice number: " + _sliceNumber + " amount: " + unitsToSkip );
		}
		updateProgress(0);
	}
	
	@Override
	public void beginProgress()
	{}

	@Override
	public void updateProgress( int partialCompletedPercentage )
	{
		double partialCompletedUnits = partialCompletedPercentage * _currentUnitsSlice;
		partialCompletedUnits = partialCompletedUnits / 100;
		double completedPercentage = ( partialCompletedUnits + _completedUnits ) * 100;
		completedPercentage = completedPercentage / _totalUnits;
		Double cp = new Double( Math.floor(completedPercentage) );
		if( _debug )
		{
			System.out.println( "Update progress. Slice number: " + _sliceNumber +
								" progress received: " + partialCompletedPercentage +
								" progress to send: " + cp );
		}
		
		int currentProgress = cp.intValue();
		if( ( _parentUpdatingProgress != null ) && (_lastReportedProgress < currentProgress ) )
		{
			_parentUpdatingProgress.updateProgress( currentProgress );
			_lastReportedProgress = currentProgress;
		}
	}

	@Override
	public void endProgress()
	{
		if( _completedUnits < _totalUnits )
		{
			_completedUnits = _completedUnits + _currentUnitsSlice;
			
		}
		updateProgress(0);
	}

	public void setDebug( boolean debug )
	{
		_debug = debug;
	}
}
