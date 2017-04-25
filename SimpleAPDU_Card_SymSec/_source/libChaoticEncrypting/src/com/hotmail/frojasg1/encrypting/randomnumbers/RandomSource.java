/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hotmail.frojasg1.encrypting.randomnumbers;

import com.hotmail.frojasg1.general.HexadecimalFunctions;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.security.SecureRandom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioSystem;


/**
 *
 * @author Fran
 */
public class RandomSource extends Thread
{
  protected final static int BYTES_TO_READ = 64000;
  protected final static int MAXBYTE = Byte.MAX_VALUE;
  protected final static int MINBYTE = Byte.MIN_VALUE;

  protected int a_minRandomBytesPerAudioBuffer = 200;

  protected int a_bytesRead = 0;
  protected byte[] a_buffer = null;
  
  protected boolean a_mustStop = false;
  
  protected int a_size;
  protected ArrayList<Byte> a_randomBytes = null;
  
  protected static RandomSource a_instance = null;
  
  protected static AudioFormat a_audioFormat = null;
  protected static TargetDataLine a_targetDataLine = null;

  
  protected RandomSource( int size )
  {
    a_mustStop = false;
    a_size = ( size < 500 ? 500 : size );
    a_randomBytes = new ArrayList();
  }

    public static RandomSource M_getInstanceOf()
  {
    if( a_instance == null )
    {
      a_instance = new RandomSource( 2000 );
//      a_instance.start();
    }
    return( a_instance );
  }
  
  public static void M_clearInstance()
  {
    if( a_instance != null )
    {
      a_instance.setMustStop(true);
      a_instance = null;
    }
  }
  
  public boolean isMustStop()                           { return( a_mustStop ); }
  public void setMustStop( boolean value )              { a_mustStop = value; }

  public int M_getSize()                                { return( a_size ); }
  public void M_setSize( int value )                    { a_size = value; }
  
  public boolean M_areRandomBytesAvailable( int size )
  {
    boolean result = ( size <= a_randomBytes.size() );
    if( a_size < size ) a_size=size;
    
    return( result );
  }

  public synchronized byte[] M_getRandomBytes( int size )
  {
    byte[] result = null;
    while( ! M_areRandomBytesAvailable( size ) )
    {
    	try
    	{
	        captureAudio();
	        M_calculateRandomBytesFromAudio();

    	}
    	catch( Exception ex )
    	{
    		ex.printStackTrace();
            M_calculateRandomBytesWithoutMicrophone();
    	}
/*
      try { wait(); }
      catch( InterruptedException ie ) { }
*/
    }

    result = new byte[ size ];
    for( int ii=0; (ii<size) && (a_randomBytes.size()>0); ii++ )
    {
      result[ii]=a_randomBytes.get(0).byteValue();
      a_randomBytes.remove(0);
    }
    notifyAll();

    return( result );
  }
  
  protected synchronized void M_makeRandomBytesFromAudioAvailable( Collection<Byte> cb )
  {
    a_randomBytes.addAll(cb);
    notifyAll();
  }

  protected byte getByteFromLong( long value )
  {
    long leastSignificantByte = ( value & 0xff );
    byte result = (byte) ( leastSignificantByte > MAXBYTE ? leastSignificantByte + 2*MINBYTE : leastSignificantByte );
    return( result );
  }
  
  protected Byte M_readRandomByteFromAudio( int[] ii ) throws RandomException
  {
    Byte result = null;
    
    int newByte = 0;
    int numberOfRandomBitsRead = 0;
    int peso = 1;
    while( (ii[0] < (a_bytesRead - a_bytesRead%2 - 1)) && (numberOfRandomBitsRead < Byte.SIZE) )
    {
      boolean bit0 = !( a_buffer[ ii[0] ] % 2 == 0 );   // get least significant bit
      boolean bit1 = false;   // get least significant bit
      ii[0] = ii[0] + 2;		// we increment in two units because the samples are of 16 bits, and they are big endian (thats because it begins in 1)
	  if( ii[0] < (a_bytesRead - a_bytesRead%2 - 1) )
	  {
		bit1 = !( a_buffer[ ii[0] ] % 2 == 0 );   // get least significant bit
        ii[0] = ii[0] + 2;
	  }
      
      if( bit0 ^ bit1 )  // if and only if one of the bits is 1
      {
        int newBit = 0;
        if( bit0 && !bit1 ) newBit = 1;           // von Neumann
        else if ( !bit0 && bit1 ) newBit = 0;     // von Neumann
        else throw new RandomException( "impossible" );
        
        numberOfRandomBitsRead++;
        newByte = newByte + newBit * peso;
        peso = peso << 1;
        
        if( numberOfRandomBitsRead == Byte.SIZE )
        {
          result = new Byte( getByteFromLong(newByte) );
        }
      }
    }
    return( result );
  }
  
  protected void M_calculateRandomBytesFromAudio() throws RandomException
  {
    ArrayList<Byte> cb = new ArrayList();
    
    int[] ii = {1};// the samples are big endian (thats because it begins in 1)
    Byte newByte = new Byte( (byte) 0 );
    for( ; (ii[0]<a_bytesRead) && (newByte != null); )
    {
      newByte = M_readRandomByteFromAudio( ii );
      if( newByte != null )
      {
        cb.add(newByte);
      }
    }

    M_makeRandomBytesFromAudioAvailable( cb );

    // System.out.println( "\n\n  Got " + cb.size() + " bytes.\n\n");

    System.out.println( "Number of random bytes got from the microphone: " + cb.size() );

    if( cb.size() < a_minRandomBytesPerAudioBuffer )
      throw new RandomException( "not enough number of random bytes read from audio" );
  }
  
  protected byte[] getSeed()
  {
    int size = a_randomBytes.size()+1;
    size = ( size > 16 ? 16 : size );
    byte[] result = new byte[ size ];

    Date dt = new Date();
    long time = dt.getTime();
    result[0] = this.getByteFromLong(time);

    for( int ii = 1; ii<size; ii++ )  result[ii] = a_randomBytes.get(ii-1).byteValue();

    return( result );
  }
  
  protected synchronized void M_calculateRandomBytesWithoutMicrophone()
  {
    while( a_randomBytes.size() < a_size )
    {
      SecureRandom sr = new SecureRandom( getSeed() );
      byte[] rb = new byte[ a_size - a_randomBytes.size() ];
      sr.nextBytes(rb);
      
      for( int ii=0; ii<rb.length; ii++ ) a_randomBytes.add( new Byte( rb[ii] ) );
    }
    notifyAll();
  }
  
  @Override
  public void run()
  {
    while( ! a_mustStop )
    {
      while( ( a_randomBytes.size() < a_size ) && ! a_mustStop )
      {
        try
        {
          System.out.println( "Capturing Audio ..." );
          captureAudio();
          M_calculateRandomBytesFromAudio();
        }
        catch( Exception ex )
        {
          ex.printStackTrace();
          M_calculateRandomBytesWithoutMicrophone();
        }
        
        System.out.println( "\n\nWe have got " + a_randomBytes.size() + " random bytes.\n\n");
      }
      
      try
      {
        sleep(200);
      }
      catch( InterruptedException ie )
      {
      }
    }
    a_instance = null;
  }
  
  protected void captureAudio() throws Exception
  {
    try
    {
    	if( a_targetDataLine == null )
    	{
    		a_audioFormat = getAudioFormat();
    		DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, a_audioFormat);
    		if (!AudioSystem.isLineSupported(dataLineInfo))
    		{
    			throw( new Exception( "Not supported audio format") );
    		}
    		a_targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
    	}

    	a_targetDataLine.open(a_audioFormat);
    	a_targetDataLine.start();

    	a_buffer = new byte[ BYTES_TO_READ ];
    	a_bytesRead = a_targetDataLine.read(a_buffer, 0, a_buffer.length);

//		System.out.println( "Bytes read from the microphone" );
//		System.out.println( HexadecimalFunctions.M_getLogFromBuffer(a_buffer) );
      /*
      System.out.println( "bytes read from the microphone" );
      String separador = "";
      for( int ii=0; ii<320; ii++ )
      {
        for( int jj=0; jj<200; jj++ )
        {
          System.out.print( separador + a_buffer[ ii * 200 + jj ] );
          separador = ", ";
        }
        System.out.println();
      }
       */
    }
    finally
    {
      if( a_targetDataLine != null ) a_targetDataLine.stop();
    }
  }//end captureAudio method

  protected AudioFormat getAudioFormat(){
	    float sampleRate = 22000.0F;
	    //8000,11025,16000,22050,44100
	    int sampleSizeInBits = 16;
	    //8,16
	    int channels = 1;
	    //1,2
	    boolean signed = true;
	    //true,false
	    boolean bigEndian = true;
	    //true,false
	    return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	  }//end getAudioFormat

  public static void main( String[] args )
  {
    RandomSource rs = RandomSource.M_getInstanceOf();
    
    InputStreamReader isr = new InputStreamReader( System.in );
    BufferedReader br = new BufferedReader( isr );

    boolean end = false;
    while( ! end )
    {
      String command = null;
      try
      {
        command = br.readLine();
      }
      catch( IOException ioe )
      {
        ioe.printStackTrace();
      }
      
      if( command != null )
      {
        if( command.compareToIgnoreCase( "end" ) == 0 )
        {
          end = true;
        }
        else
        {
          int numberOfBytesToRead = Integer.parseInt(command);
          if( numberOfBytesToRead > 0 )
          {
            byte[] bytes = rs.M_getRandomBytes(numberOfBytesToRead);

            System.out.println( "\n\nNumberOfBytesToRead=" + numberOfBytesToRead );

			System.out.println( HexadecimalFunctions.M_getLogFromBuffer(bytes) );
			
			System.out.println( "histogram" );
			System.out.println( "=========" );
			System.out.println( HexadecimalFunctions.M_IntArrayToString( HexadecimalFunctions.M_getHistogram(bytes) ) );
/*
			for( int ii=0; ii*100<numberOfBytesToRead; ii++ )
            {
              for( int jj=0; (jj<100) && (ii*100+jj<numberOfBytesToRead); jj++ )
              {
                System.out.print( " " + Integer.toHexString( ((int) bytes[ii*100+jj]) & 0xff ) );
              }
              System.out.println();
            }
*/
		  } // if
        } // else
      } // if
    } // while
    
    rs.setMustStop( true );
  }
  
}
