package com.hotmail.frojasg1.applications.fileencoder;

import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.CancellationException;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.OperationCancellation;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.UpdatingProgress;
import com.hotmail.frojasg1.applications.fileencoder.configuration.EncryptingConfiguration;
import com.hotmail.frojasg1.applications.fileencoder.configuration.ListOfEncryptingConfigurations;
import com.hotmail.frojasg1.applications.fileencoder.fileencodertypes.FileEncoderType_1;
import com.hotmail.frojasg1.applications.fileencoder.fileencodertypes.FileEncoderType_2;
import com.hotmail.frojasg1.encrypting.encoderdecoder.EncoderDecoder;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.GeneralUpdatingProgress;
import com.hotmail.frojasg1.encrypting.randomnumbers.RandomSource;
import com.hotmail.frojasg1.general.StringFunctions;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileEncoder
{
	public FileEncoder( )
	{
	}

	public static byte[] M_getBytesFromFile( String fileName ) throws FileEncoderException
	{
		return( M_getHeaderFromFile( fileName, -1 ) );
	}
	
	public static byte[] M_getHeaderFromFile( String fileName, int numberOfBytesToRead ) throws FileEncoderException
	{
		File file = new File(fileName);

		long numBytes = ( numberOfBytesToRead == -1 ? file.length() :
						( numberOfBytesToRead > file.length() ? file.length() : numberOfBytesToRead )
						);
		
		if( numBytes > 100000000 ) throw( new FileEncoderException( "File Too long: " + fileName ));
	    byte[] fileData = new byte[(int) numBytes];
	    DataInputStream dis = null;
	    try
	    {
	    	dis = new DataInputStream(new FileInputStream(file));
		    dis.readFully(fileData);
	    }
		catch( IOException ex )
		{
			ex.printStackTrace();
			throw( new FileEncoderException( ex.getMessage() ) );
		}
		finally
		{
			try
			{
				if( dis != null ) dis.close();
			}
			catch( IOException ex )
			{
				ex.printStackTrace();
				throw( new FileEncoderException( ex.getMessage() ) );
			}
		}
	    return( fileData );
	}

	public static byte[] M_getMD5( byte[] input ) throws FileEncoderException
	{
	      MessageDigest m = null;
	      try
	      {
	        m=MessageDigest.getInstance("MD5");
	      }
	      catch( Exception ex )
	      {
	        throw new FileEncoderException( "Excepcion intentando recoger el algoritmo MD5" );
	      }
	      m.update(input,0,input.length);
	      return( m.digest() );
	}
	
	public static void M_writeEncodedFile( String newFileName, byte[] encodedKey, byte[] encodedMD5, byte[] encodedFileData )
		throws FileEncoderException
	{
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream( new File( newFileName ) );
			fos.write(encodedKey);
			fos.flush();
			fos.write(encodedMD5);
			fos.flush();
			fos.write(encodedFileData);
			fos.flush();
		}
		catch( IOException ex )
		{
			ex.printStackTrace();
			throw( new FileEncoderException( ex.getMessage() ) );
		}
		finally
		{
			try
			{
				if( fos != null ) fos.close();
			}
			catch( IOException ex )
			{
				ex.printStackTrace();
				throw( new FileEncoderException( ex.getMessage() ) );
			}
		}
	}

    public static void M_writeFile( String newFileName, byte[] fileData ) throws FileEncoderException
    {
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream( new File( newFileName ) );
			fos.write(fileData);
			fos.flush();
		}
		catch( IOException ex )
		{
			ex.printStackTrace();
			throw( new FileEncoderException( ex.getMessage() ) );
		}
		finally
		{
			try
			{
				if( fos != null ) fos.close();
			}
			catch( IOException ex )
			{
				ex.printStackTrace();
				throw( new FileEncoderException( ex.getMessage() ) );
			}
		}
    }

	public static String M_removejfeExtension( String fileName, String extension ) throws FileEncoderException
	{
		String result = null;
		String dotExtension = "." + extension;
		
		if( fileName.length() > dotExtension.length() )
		{
			if( fileName.substring( fileName.length()-dotExtension.length(), fileName.length() ).compareTo( dotExtension ) == 0 )
			{
				result = new String( fileName.substring( 0, fileName.length()-dotExtension.length() ));
			}
		}
		if( result == null ) throw( new FileEncoderException( "Bad file name for an encrypted file. It has to end in " + dotExtension ) );
		return( result );
	}
	
	public static FileEncoderParameters M_doActions( char[] password, String[] args, UpdatingProgress up, OperationCancellation oc ) throws FileEncoderException, CancellationException
	{
		int fileEncoderType = -1;
		
		FileEncoderParameters fep = new FileEncoderParameters( args, false, true );
		
		if( fep.M_getHasToUseFileSizeForEncryptingParams() && fep.M_isEncode() &&
			( fep.M_getDecodedFileName() != null ) )
		{
			EncryptingConfiguration ec = ListOfEncryptingConfigurations.M_getInstance().
										M_getEncrytingConfigurationFromFileSize(fep.M_getDecodedFileName());

			if( ec != null )
			{
				String argsFromFileSize[] = ec.M_getParamListFromEncryptingConfiguration();

				String args2[] = StringFunctions.M_joinStringArrays( argsFromFileSize, args );

				fep = new FileEncoderParameters( args2, false, true );
			}
			else
			{
				System.out.println( "The file " + fep.M_getDecodedFileName() + " did not produce any encrypting configuration. May be the file does not exist." );
			}
		}
		
		FileEncoderType fet = null;

		if( fep.M_isEncode() )
		{
			fileEncoderType = fep.M_getFileEncoderType();
		}
		else
		{
			String fileName = fep.M_getEncodedFileName();
			byte[] header = M_getHeaderFromFile( fileName, 30 );
			byte[] headerHead = new byte[ FileEncoderParameters.STR_HEADER_HEAD.getBytes().length ];
			
			if( header.length > headerHead.length )
			{
				System.arraycopy(header, 0, headerHead, 0, headerHead.length );
				String str_headerHead = new String( headerHead );
				if( !str_headerHead.equals( FileEncoderParameters.STR_HEADER_HEAD ) )
				{
					throw( new FileEncoderException( "The supposed encrypted file has not got the appropriate format." ) );
				}
				
				fileEncoderType = header[ headerHead.length ];
			}
			else
			{
				throw( new FileEncoderException( "The supposed encrypted file has not got the appropriate format." ) );
			}
		}

		fet = M_newFileEncoderType( fileEncoderType, up );
		fet.M_doActions( password, fep, oc );

		return( fet.M_getFileEncoderParameters() );
	}

	public static FileEncoderType M_newFileEncoderType( int fileEncoderType, UpdatingProgress up ) throws FileEncoderException
	{
		FileEncoderType result = null;

		switch( fileEncoderType )
		{
			case FileEncoderParameters.INT_FILE_ENCODER_TYPE_1: result = new FileEncoderType_1( up ); break;
			case FileEncoderParameters.INT_FILE_ENCODER_TYPE_2: result = new FileEncoderType_2( up ); break;

			default:
			{
				throw( new FileEncoderException( "Bad file encoder type: " + fileEncoderType ) );
			}
//			break;
		}

		return( result );
	}
	
	public static byte[] M_getMD5_fromFile_sliced( String fileName, int sliceSize ) throws FileEncoderException
	{
		Path path = FileSystems.getDefault().getPath(fileName);
		ByteBuffer bb = ByteBuffer.allocate( sliceSize );
		
		MessageDigest m = null;
		try
		{
			m=MessageDigest.getInstance("MD5");
		}
		catch( Exception ex )
		{
			throw new FileEncoderException( "Excepcion intentando recoger el algoritmo MD5" );
		}
		
		FileChannel fc = null;
		
		try
		{
			fc = FileChannel.open( path, StandardOpenOption.READ );
		
			long totalBytesRead = 0;
			int bytesRead = -1;

			while( ( bytesRead = fc.read( bb, totalBytesRead ) ) > 0 )
			{
				totalBytesRead = totalBytesRead + bytesRead;
				byte[] buffer = bb.array();
				m.update(buffer,0,bytesRead);

				bb.clear();
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			throw( new FileEncoderException( "Excepcion calculating MD5 hash from sliced file" ) );
		}
		finally
		{
			try
			{
				if( fc != null ) fc.close();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
		return( m.digest() );
	}


	public static void M_writeEncodedFile_sliced( String fileName, String newFileName,
													byte[] header, EncoderDecoder ed,
													int sliceSize, UpdatingProgress up,
													OperationCancellation oc )  throws FileEncoderException, CancellationException
	{
		if( sliceSize < 1 )
			throw( new FileEncoderException( "Slice size must be greater than zero" ) );
		
		Path path_in = FileSystems.getDefault().getPath(fileName);
		Path path_out = FileSystems.getDefault().getPath(newFileName);
		ByteBuffer bb_in = ByteBuffer.allocate( sliceSize );
		ByteBuffer bb_out = ByteBuffer.wrap(header);

		File file = new File( fileName );
		long inFileSize = file.length();
		
		FileChannel fc_in = null;
		FileChannel fc_out = null;

		if( up != null )
			up.beginProgress();

		GeneralUpdatingProgress gup = new GeneralUpdatingProgress( up, inFileSize );
//		gup.setDebug(true);
		try
		{
			fc_in = FileChannel.open( path_in, StandardOpenOption.READ );
			fc_out = FileChannel.open(path_out, StandardOpenOption.WRITE, StandardOpenOption.CREATE );

			int numBytesWritten = fc_out.write(bb_out);
			
			if( numBytesWritten != header.length ) throw( new FileEncoderException( "Error writing header to encoded file: " +
																					newFileName ) );
			
			long totalBytesRead = 0;
			int bytesRead = -1;

			while( ( bytesRead = readFully(fc_in, bb_in, totalBytesRead) ) > 0 )
			{
				gup.prepareNextSlice(bytesRead);

				totalBytesRead = totalBytesRead + bytesRead;
				byte[] buffer_in = bb_in.array();
				byte[] buffer_in_wrapped = null;

				if( bytesRead == sliceSize ) buffer_in_wrapped = buffer_in;
				else
				{
					buffer_in_wrapped = new byte[bytesRead];
					System.arraycopy(buffer_in, 0, buffer_in_wrapped, 0, bytesRead );
				}

				byte[] buffer_out = ed.M_encode( buffer_in_wrapped, gup, oc );
				bb_out = ByteBuffer.wrap( buffer_out );
				
				numBytesWritten = fc_out.write(bb_out );
				if( numBytesWritten != bytesRead ) throw( new FileEncoderException( "Error writing file data to encoded file: " +
																					newFileName ) );

				bb_in.clear();
			}
		}
		catch( CancellationException ce )
		{
			throw( new CancellationException( ce.getMessage() + " when Encrypting file ( " + fileName + " )" ) );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			throw( new FileEncoderException( "Excepcion when reading file " + fileName + " and writing file " + newFileName ) );
		}
		finally
		{
			try
			{
				if( fc_in != null ) fc_in.close();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}

			try
			{
				if( fc_out != null ) fc_out.close();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}

		if( up != null )
			up.endProgress();
	}
	
	
	public static void M_writeDecodedFile_sliced( String fileName, String newFileName,
													int headerLength, EncoderDecoder ed,
													int sliceSize, UpdatingProgress up,
													OperationCancellation oc ) throws FileEncoderException, CancellationException
	{
		if( sliceSize < 1 )
			throw( new FileEncoderException( "Slice size must be greater than zero" ) );
		
		Path path_in = FileSystems.getDefault().getPath(fileName);
		Path path_out = FileSystems.getDefault().getPath(newFileName);
		ByteBuffer bb_in = ByteBuffer.allocate( sliceSize );
		ByteBuffer bb_out = null;

		File file = new File( fileName );
		long inFileSize = file.length();
		
		FileChannel fc_in = null;
		FileChannel fc_out = null;

		if( up != null )
			up.beginProgress();

		GeneralUpdatingProgress gup = new GeneralUpdatingProgress( up, inFileSize );
//		gup.setDebug(true);
		try
		{
			fc_in = FileChannel.open( path_in, StandardOpenOption.READ );
			fc_out = FileChannel.open(path_out, StandardOpenOption.WRITE, StandardOpenOption.CREATE );

			long totalBytesRead = headerLength;
			int bytesRead = -1;
			
			gup.skip( headerLength );

			while( ( bytesRead = readFully(fc_in, bb_in, totalBytesRead) ) > 0 )
			{
				gup.prepareNextSlice(bytesRead);

				totalBytesRead = totalBytesRead + bytesRead;
				byte[] buffer_in = bb_in.array();
				byte[] buffer_in_wrapped = null;

				if( bytesRead == sliceSize ) buffer_in_wrapped = buffer_in;
				else
				{
					buffer_in_wrapped = new byte[bytesRead];
					System.arraycopy(buffer_in, 0, buffer_in_wrapped, 0, bytesRead );
				}

				byte[] buffer_out = ed.M_decode( buffer_in_wrapped, gup, oc );
				bb_out = ByteBuffer.wrap( buffer_out );
				
				int numBytesWritten = fc_out.write(bb_out );
				if( numBytesWritten != bytesRead ) throw( new FileEncoderException( "Error writing file data to decoded file: " +
																					newFileName ) );

				bb_in.clear();
			}
		}
		catch( CancellationException ce )
		{
			throw( new CancellationException( ce.getMessage() + " when Decrypting file ( " + fileName + " )" ) );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			throw( new FileEncoderException( "Excepcion when reading file " + fileName + " and writing file " + newFileName ) );
		}
		finally
		{
			try
			{
				if( fc_in != null ) fc_in.close();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}

			try
			{
				if( fc_out != null ) fc_out.close();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}

		if( up != null )
			up.endProgress();
	}
	
    public static int readFully(FileChannel fc, ByteBuffer bb, long position ) throws FileEncoderException
	{
        int toRead = bb.remaining();
		int bytesRead = 0;
		int totalBytesRead = 0;
		
		try
		{
			while( (totalBytesRead < toRead) && (bytesRead != -1) )	// when the file ends (bytesRead==-1) is true
			{
				if( position >= 0 )
				{
					bytesRead = fc.read( bb, position );
					if( bytesRead > 0 ) position = position + bytesRead;
				}
				else
				{
					bytesRead = fc.read( bb );
				}

				if( bytesRead > 0 ) totalBytesRead = totalBytesRead + bytesRead;
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			throw( new FileEncoderException( th.getMessage() ) );
		}
		
		return( totalBytesRead );
    }
	
	public static void main( String[] args )
	{
		RandomSource.M_getInstanceOf();

		int returnValue = 0;
		
		try
		{
			M_doActions(	null,
							args,
							new UpdatingProgress()
							{
								public void beginProgress()
								{
									System.out.println( "Beginning" );
								}
								public void updateProgress( int completedPercentage )
								{
									System.out.println( "Progress: " + completedPercentage + "%" );
								}
								public void endProgress()
								{
									System.out.println( "End" );
								}
							},
							null );
			returnValue = 0;
		}
		catch( Throwable ex )
		{
			ex.printStackTrace();
			returnValue = 1;
		}
		finally
		{
			RandomSource.M_getInstanceOf().setMustStop(true);
			
			while( RandomSource.M_getInstanceOf().isAlive() )
			{
				try
				{
					Thread.sleep( 100 );
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}
		}
		
		System.exit( returnValue );
/*
		try
		{
			String fileName = "J:\\N\\temp2\\Secuencias_pseudoaleatorias.pdf";
//			fileName = "C:\\Users\\Usuario\\frojasg1.apps\\FileEncoder\\general\\GlobalConfiguration.properties";
			byte[] fileData = M_getBytesFromFile( fileName );
			byte[] md5 =  M_getMD5( fileData );

			byte[] md5_2 = M_getMD5_fromFile_sliced( fileName, 65536 );

			String md5_1_string = HexadecimalFunctions.M_convertByteArrayToHexadecimalString(md5);
			String md5_2_string = HexadecimalFunctions.M_convertByteArrayToHexadecimalString(md5_2);

			System.out.println( "md5_1: " + md5_1_string );
			System.out.println( "md5_2: " + md5_2_string );

			System.out.println( "son iguales: " + (boolean) ( md5_1_string.equals( md5_2_string ) ) );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
*/
	}
	
//	protected boolean a_isEncode = false;
//	protected boolean a_hasToEraseOriginalFile = false;
//	protected String a_password = null;
//	protected String[] a_files = null;

//	protected static FileEncoder a_fe = null;

//	protected static RandomSource a_rs = null;
	
//	protected ReordererEncoder a_encoder = null;
}
