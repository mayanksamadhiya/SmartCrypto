/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hotmail.frojasg1.applications.fileencoderapplication.threads;

import com.hotmail.frojasg1.applications.fileencoder.FileEncoderException;
import com.hotmail.frojasg1.applications.fileencoderapplication.FileJInternalFrame;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.OperationCancellation;
import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.StringsConfiguration;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author Usuario
 */
public abstract class ParentThread extends Thread
{
    protected boolean a_hasToStop = false;
    protected boolean a_hasEnded = false;

    private final Object a_lock1 = new Object();
    private final Object a_lock2 = new Object();

    protected boolean a_itWasError = false;
    protected String a_errorString = null;

    protected FileJInternalFrame a_parent = null;
    
	protected OperationCancellation a_oc = null;
	
	protected boolean a_isCancellation = false;
	
    public ParentThread( FileJInternalFrame parent, OperationCancellation oc )
    {
        super();
        
        a_hasToStop = false;
        a_hasEnded = false;
        a_itWasError = false;
        a_errorString = null;
        a_parent = parent;
		a_oc = oc;
    }

    @Override
    public abstract void run();

    public boolean M_getItWasError()        { return( a_itWasError );    }
    protected void M_setItWasError( boolean itWasError )
    {
        a_itWasError = itWasError;
    }

    public String M_getErrorString()        { return( a_errorString );    }
    protected void M_setErrorString( String errorString )
    {
        M_setItWasError( true );
        a_errorString = "\nERROR!!!\n" + errorString;
    }

    public boolean M_getHasEnded()
    {
        synchronized( a_lock1 )
        {
            return( a_hasEnded );
        }
    }
    protected synchronized void M_setHasEnded( boolean hasEnded )
    {
        synchronized( a_lock1 )
        {
            a_hasEnded = hasEnded;
        }
    }

    protected boolean M_getHasToStop()
    {
        synchronized( a_lock2 )
        {
            return( a_hasToStop );
        }
    }

    public void M_setHasToStop( boolean hasToStop )
    {
        synchronized( a_lock2 )
        {
            a_hasToStop = hasToStop;
        }
    }

	public static void M_secureEraseFile(	String fileName,
											FileJInternalFrame parent)
	{
		int sliceSize = 65536;
		Path path_out = FileSystems.getDefault().getPath(fileName);
		ByteBuffer bb_out = null;

		byte[] buffer_out = new byte[sliceSize];
		for( int ii=0; ii<sliceSize; ii++ ) buffer_out[ii]=0;
		
		File file = new File( fileName );
		long fileSize = file.length();
		
		FileChannel fc_out = null;

		try
		{
			fc_out = FileChannel.open(path_out, StandardOpenOption.WRITE );

			long totalByteswritten = 0;

			while( totalByteswritten < fileSize )
			{
				bb_out = ByteBuffer.wrap( buffer_out );
				int numBytesWritten = fc_out.write( bb_out, totalByteswritten );
				if( numBytesWritten != sliceSize ) throw( new FileEncoderException( "Error erasing file data: " +
																					fileName ) );

				totalByteswritten = totalByteswritten + numBytesWritten;
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		finally
		{
			try
			{
				if( fc_out != null ) fc_out.close();

				if( !file.exists() )
				{
					parent.M_addStatusInformation(	fileName + "   " +
													StringsConfiguration.M_getInstance().
								M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_DOES_NOT_EXIST_STRING) );
				}
				else if( file.delete() )
                {
					parent.M_addStatusInformation(	fileName + "   " +
													StringsConfiguration.M_getInstance().
											M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_WAS_SUCCESSFULLY_DELETED) );
				}
				else
                {
					parent.M_addStatusInformation(	"ERROR!!!\n" + fileName + "   " +
													StringsConfiguration.M_getInstance().
											M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_COULD_NOT_BE_DELETED) );
				}
			}
			catch( Throwable th )
			{
				th.printStackTrace();
				parent.M_addStatusInformation(	"ERROR!!!\n" + fileName + "   " +
												StringsConfiguration.M_getInstance().
										M_getStrParamConfiguration(StringsConfiguration.CONF_ERROR_DELETING_FILE) );
			}
		}
	}
/*
	static public void M_secureEraseFile( String fileName, FileJInternalFrame parent )
    {
        FileOutputStream fos = null;
        File file = null;
		try
		{
            file = new File( fileName );
            int len = ( new Long( file.length() ) ).intValue();
            byte buffer[] = new byte[len];
            for( int ii=0; ii<len; ii++ ) buffer[ii]=0;
            
            fos = new FileOutputStream( file );
            fos.write(buffer);
            fos.flush();
        }
		catch( IOException ex )
		{
				ex.printStackTrace();
                parent.M_addStatusInformation(    fileName + "   " +
                                                  StringsConfiguration.M_getInstance().
                                        M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_WAS_SUCCESSFULLY_DELETED) );
		}
		finally
		{
			try
			{
				if( fos != null ) fos.close();
				if( !file.exists() )
				{
					parent.M_addStatusInformation(	fileName + "   " +
													StringsConfiguration.M_getInstance().
								M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_TO_DELETE_DID_NOT_EXIST) );
				}
				else if( file.delete() )
                {
					parent.M_addStatusInformation(	fileName + "   " +
													StringsConfiguration.M_getInstance().
											M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_WAS_SUCCESSFULLY_DELETED) );
				}
				else
                {
					parent.M_addStatusInformation(	"ERROR!!!\n" + fileName + "   " +
													StringsConfiguration.M_getInstance().
											M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_COULD_NOT_BE_DELETED) );
				}
			}
			catch( IOException ex )
			{
				ex.printStackTrace();
			}
		}
	}
*/


	
	public boolean M_isCancellation()		{ return( a_isCancellation );	}
	protected void M_setIsCancellation( boolean value )	{ a_isCancellation = value;	}

}
