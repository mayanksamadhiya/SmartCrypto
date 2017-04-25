/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hotmail.frojasg1.applications.fileencoderapplication.threads;

import com.hotmail.frojasg1.applications.fileencoderapplication.FileJInternalFrame;
import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.StringsConfiguration;
import com.hotmail.frojasg1.applications.fileencoderapplication.utils.Utils;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoderException;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.OperationCancellation;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author Usuario
 */
public class OpenEncryptedFileThread extends ParentThread
{
    protected String a_encryptedFileName = null;
    protected char[] a_password = null;

    protected String a_decryptedFileName = null;
    
    public OpenEncryptedFileThread( FileJInternalFrame parent, String fileName, char[] password, OperationCancellation oc )
    {
        super(parent, oc);
        a_encryptedFileName = fileName;
        a_password = password;
    }
/*
    protected boolean renameEncryptedFile( String fileName )
    {
        String extension2 = Utils.M_getExtension( fileName );
        String fileWithoutExtension = Utils.M_putOffExtension(fileName);
        
        String extension1 = Utils.M_getExtension( fileWithoutExtension );
        String fileWithout2ndExtension = Utils.M_putOffExtension(fileWithoutExtension);
        
        int ii=1;
        File fileDest = null;
        while( (fileDest = new File( fileWithout2ndExtension + ".old" + ii + "." + extension1 + "." + extension2 ) ).exists() )
        {
            ii++;
        }
        File fileOri = new File( fileName );

        boolean result = fileOri.renameTo( fileDest );
        if( result )
        {
            a_parent.M_addStatusInformation(StringsConfiguration.M_getInstance().
                                        M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_WAS_SUCCESSFULLY_RENAMED) +
                                        "   from: " + fileName +
                                        "   to: " + fileDest.toString()
                                            );
        }
        return( result );
    }

    static public boolean M_isOpenedOk( Process pr )
    {
        boolean hasExited = false;
        int processResult = -1;

        try
        {
            sleep(1000);
        }
        catch( InterruptedException ex )
        {
            ex.printStackTrace();
        }

        try
        {
            processResult = pr.exitValue();
            hasExited=true;
        }
        catch( IllegalThreadStateException ex )
        {
            // no hay que hacer nada
        }
        
        return( ! hasExited );
    }

    static public Process openDocument( String fileName )
    {
        Process result = null;
        
        String command1[] = { "cmd", "/c", "start", fileName };
        try
        {
            result = Runtime.getRuntime().exec( command1 );
            if( M_isOpenedOk( result ) )  return( result );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        String command2[] = { "gnome-open", fileName };
        try
        {
            result = Runtime.getRuntime().exec( command2 );
            if( M_isOpenedOk( result ) )  return( result );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        String command3[] = { "open", fileName };
        try
        {
            result = Runtime.getRuntime().exec( command3 );
            if( M_isOpenedOk( result ) )  return( result );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return( null );
    }

    @Override
    public void run()
    {
		M_setIsCancellation( false );
        boolean hasExited = false;
        int processResult = -1;

        DecryptFileThread dft = new DecryptFileThread( a_parent, a_encryptedFileName, a_password, a_oc );
        dft.run();  // we want to execute the actions of the thread, but not in a new thread
        if( !dft.M_getItWasError() && !dft.M_isCancellation() )
        {
            a_decryptedFileName = dft.M_getDecryptedFileName();
            dft = null;
            if( (a_decryptedFileName != null) && renameEncryptedFile( a_encryptedFileName ) )
            {
                Process pp = null;
                try
                {
                    pp = openDocumentMultiplatform( a_decryptedFileName );
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                if( pp!=null )
                {
                    while( !hasExited && !M_getHasToStop() )
                    {
                        try
                        {
                            sleep(1000);
                        }
                        catch( InterruptedException ex )
                        {
                            ex.printStackTrace();
                        }

                        try
                        {
                            processResult = pp.exitValue();
                            hasExited=true;
                        }
                        catch( IllegalThreadStateException ex )
                        {
                            // no hay que hacer nada
                        }
                    }

                    if( hasExited )
                    {
                        if( processResult == 0 )
                        {
                            a_parent.M_addStatusInformation(StringsConfiguration.M_getInstance().
                                    M_getStrParamConfiguration(StringsConfiguration.CONF_PROCESS_SUCCESSFULLY_CLOSED)
                                                            );
                        }
                        else
                        {
                            a_parent.M_addStatusInformation(StringsConfiguration.M_getInstance().
                                    M_getStrParamConfiguration(StringsConfiguration.CONF_PROCESS_CLOSED_WITH_ERROR) + ": " + processResult
                                                            );
                        }
                        
                        EncryptFileThread eft = new EncryptFileThread( a_parent, a_decryptedFileName, a_password, a_oc );
                        eft.run();  // we want to execute the actions of the thread, but not in a new thread
                        if( !eft.M_getItWasError() && !eft.M_isCancellation() )
                        {
                            a_parent.M_addStatusInformation(    a_decryptedFileName + "   " +
                                                                StringsConfiguration.M_getInstance().
                                    M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_COULD_BE_SUCCESSFULLY_ENCRYPTED)
                                                            );
                            M_secureEraseFile( a_decryptedFileName, a_parent );
                        }
                        else
                        {
							if( eft.M_isCancellation() )	M_setIsCancellation( true );
							else
							{
								M_setErrorString(   a_decryptedFileName + "   " +
													StringsConfiguration.M_getInstance().
													M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_COULD_NOT_BE_ENCRYPTED)
												);
							}
                        }
                    }
                }
                else
                {
                    M_setErrorString(   a_decryptedFileName + "   " +
                                        StringsConfiguration.M_getInstance().
                                        M_getStrParamConfiguration(StringsConfiguration.CONF_PROCESS_COULD_NOT_BE_CREATED)
                                    );
                }
            }
            else
            {
                M_setErrorString(   a_encryptedFileName + "   " +
                                    StringsConfiguration.M_getInstance().
                                    M_getStrParamConfiguration(StringsConfiguration.CONF_ENCRYPTED_FILE_COULD_NOT_BE_RENAMED)
                                );
            }
        }
        else
        {
			if( dft.M_isCancellation() )	M_setIsCancellation( true );
			else
			{
				M_setErrorString(   a_encryptedFileName + "   " +
									StringsConfiguration.M_getInstance().
									M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_COULD_NOT_BE_DECRYPTED)
								);
			}
        }

        if( M_getItWasError() )
        {
            a_parent.M_addStatusInformation( M_getErrorString());
        }
        else
        {
            a_parent.M_addStatusInformation( StringsConfiguration.M_getInstance().
                                        M_getStrParamConfiguration(StringsConfiguration.CONF_ENCRYPTION_SUCCESSFULLY_ENDED) );
        }
        
        M_setHasEnded(true);
    }

	public static boolean M_isWindows()
	{
		return( System.getProperty("os.name").startsWith( "Windows" ) );
	}
	
	public static boolean M_isMacOS()
	{
		return( System.getProperty("os.name").startsWith( "Mac OS" ) );
	}
	
	public static boolean M_isLinux()
	{
		return( System.getProperty("os.name").startsWith( "Linux" ) );
	}

	public Process openDocumentMultiplatform( String fileName ) throws IOException
	{
		Process result = null;
		if( M_isWindows() )	result = openDocument_Windows( fileName );
		else if( M_isMacOS() ) result = openDocument_MacOS( fileName );
		else if( M_isLinux() ) result = openDocument_Linux( fileName );
		else
			M_setErrorString(   StringsConfiguration.M_getInstance().
									M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_OS_NOT_RECOGNIZED_FOR_AUTOMATICALLY_OPENING_A_DOCUMENT) + " " +
									System.getProperty("os.name")
							);

		return( result );
	}

    public static Process openDocument_Windows(String path) throws IOException {
    	path = "\"" + path + "\"";
//    	File f = new File( path );
//    	String command = "cmd /c " + f.getPath() + "";
    	String command = "cmd /c " + path + "";
        return( Runtime.getRuntime().exec(command) );
    }

    public static Process openDocument_MacOS(String path) throws IOException {
//    	path = "\"" + path + "\"";
//    	File f = new File( path );
    	String[] command_args = new String[2];
		command_args[0] = "open";
		command_args[1] = path;
        return( Runtime.getRuntime().exec(command_args) );
    }

    public static Process openDocument_Linux(String path) throws IOException {
//    	path = "\"" + path + "\"";
    	File f = new File( path );
//    	String command = "execute gnome-open " + f.getPath() + "";
		String command = "gnome-open " + path + "";
		
		System.out.println( "comando para el sistema operativo:\n" + command );

	return( Runtime.getRuntime().exec(command) );
    }
*/	
	public static void M_openDocument( String fileName ) throws IOException
	{
		File file = new File( fileName );
		Desktop dt = Desktop.getDesktop();
		dt.open(file);
	}
/*
    static public void main( String args[] ) throws IOException
    {
        String fileName = "J:\\N\\temp2\\Copia de Agenda.xls";
        Process pr = openDocument_Windows( fileName );
        
        boolean hasExited = false;
        int processResult = -1;
        
        while( !hasExited )
        {
            try
            {
                sleep(1000);
            }
            catch( InterruptedException ex )
            {
                ex.printStackTrace();
            }

            try
            {
                processResult = pr.exitValue();
                hasExited=true;
            }
            catch( IllegalThreadStateException ex )
            {
                // no hay que hacer nada
            }
        }
        System.out.println( "hasExited:" + hasExited + ", processResult: " + processResult );
    }
*/
    @Override
    public void run()
    {
		M_setIsCancellation( false );
//        boolean hasExited = false;
//        int processResult = -1;

        DecryptFileThread dft = new DecryptFileThread( a_parent, a_encryptedFileName, a_password, a_oc );
        dft.run();  // we want to execute the actions of the thread, but not in a new thread
        if( !dft.M_getItWasError() && !dft.M_isCancellation() )
        {
            a_decryptedFileName = dft.M_getDecryptedFileName();
            dft = null;
            if( a_decryptedFileName != null )
            {
                try
                {
					M_openDocument( a_decryptedFileName );
					a_parent.M_addStatusInformation(StringsConfiguration.M_getInstance().
								M_getStrParamConfiguration(StringsConfiguration.CONF_DOCUMENT_OPENED)
							);
                }
				catch (Throwable th)
				{
                    th.printStackTrace();
					a_parent.M_addStatusInformation( "ERROR!! "	+ StringsConfiguration.M_getInstance().
										M_getStrParamConfiguration(StringsConfiguration.CONF_ERROR_OPENING_DOCUMENT)
										+ a_decryptedFileName + "   "
							);
                }
            }
            else
            {
                M_setErrorString(   a_encryptedFileName + "   " +
                                    StringsConfiguration.M_getInstance().
                                    M_getStrParamConfiguration(StringsConfiguration.CONF_ENCRYPTED_FILE_COULD_NOT_BE_RENAMED)
                                );
            }
        }
        else
        {
			if( dft.M_isCancellation() )	M_setIsCancellation( true );
			else
			{
				M_setErrorString(   a_encryptedFileName + "   " +
									StringsConfiguration.M_getInstance().
									M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_COULD_NOT_BE_DECRYPTED)
								);
			}
        }

        if( M_getItWasError() )
        {
            a_parent.M_addStatusInformation( M_getErrorString());
        }
        
        M_setHasEnded(true);
    }
}
