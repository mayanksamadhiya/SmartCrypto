/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hotmail.frojasg1.applications.fileencoder;

import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.CancellationException;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.OperationCancellation;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.UpdatingProgress;
import com.hotmail.frojasg1.encrypting.encoderdecoder.EncoderDecoder;

/**
 *
 * @author Usuario
 */
public interface FileEncoderType
{
	public void M_encodeFile( char[] password, String fileName, String newFileName, OperationCancellation oc ) throws FileEncoderException, CancellationException;
	public void M_decodeFile( char[] password, String fileName, String newFileName, OperationCancellation oc ) throws FileEncoderException, CancellationException;
	public void M_doActions( char[] password, FileEncoderParameters fep, OperationCancellation oc ) throws FileEncoderException, CancellationException;
	public FileEncoderParameters M_getFileEncoderParameters();
	public EncoderDecoder M_newEncoderDecoder( FileEncoderParameters fep ) throws FileEncoderException;
}

