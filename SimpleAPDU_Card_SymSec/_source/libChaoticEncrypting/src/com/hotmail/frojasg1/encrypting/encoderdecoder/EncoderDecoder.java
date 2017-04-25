package com.hotmail.frojasg1.encrypting.encoderdecoder;

import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.CancellationException;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.UpdatingProgress;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.OperationCancellation;
import com.hotmail.frojasg1.encrypting.pseudorandomgenerator.PseudoRandomGeneratorException;

public interface EncoderDecoder
{
	public void M_initializeKey( byte[] newKey ) throws PseudoRandomGeneratorException;
	public void M_initializeKey( String newHexKey ) throws PseudoRandomGeneratorException;
	public byte[] M_encode( byte[] input, UpdatingProgress up, OperationCancellation oc ) throws PseudoRandomGeneratorException, CancellationException;
	public byte[] M_decode( byte[] input, UpdatingProgress up, OperationCancellation oc ) throws PseudoRandomGeneratorException, CancellationException;

	public int M_getRecommendedNumberOfBytesToInitializeKey();
}
