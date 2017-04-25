package com.hotmail.frojasg1.encrypting.pseudorandomgenerator;

public interface PseudoRandomGenerator
{
	public void M_initialize( String hexkey ) throws PseudoRandomGeneratorException;
	public void M_initialize( byte[] key ) throws PseudoRandomGeneratorException;
	public byte M_next() throws PseudoRandomGeneratorException;	// it returns exactly a pseudorandom byte
	public long M_nextPartOfByte() throws PseudoRandomGeneratorException; // it returns a pseudorandom part of a byte

	public int M_getNumberOfBitsPerIteration();
	public int M_getRecommendedNumberOfBytesToInitializeKey();
}
