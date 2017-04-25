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
public interface UpdatingProgress
{
	public void beginProgress();
	public void updateProgress( int completedPercentage );
	public void endProgress();
}
