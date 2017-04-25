/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hotmail.frojasg1.applications.fileencoderapplication.threads;

import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.ApplicationConfiguration;
import com.hotmail.frojasg1.applications.fileencoderapplication.FileJInternalFrame;
import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.StringsConfiguration;
import com.hotmail.frojasg1.applications.fileencoderapplication.utils.Utils;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.CancellationException;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoder;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoderException;
import com.hotmail.frojasg1.applications.fileencoder.FileEncoderParameters;
import com.hotmail.frojasg1.encrypting.encoderdecoder.progress.OperationCancellation;
import com.hotmail.frojasg1.general.StringFunctions;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Usuario
 */
public class EncryptFileThread extends ParentThread
{
    protected String a_decryptedFileName = null;
    protected char[] a_password = null;
    protected String a_encryptedFileName = null;
    
    public EncryptFileThread( FileJInternalFrame parent, String fileName, char[] password, OperationCancellation oc )
    {
        super(parent, oc);
        a_decryptedFileName = fileName;
        a_password = password;
    }

    public String M_getEncryptedFileName()      { return( a_encryptedFileName );    }
    
	@Override
    public void run()
    {
		M_setIsCancellation( false );
        File file = new File( a_decryptedFileName );
        if( file.exists() )
        {
            if( ! Utils.M_getExtension(a_decryptedFileName).equals( ApplicationConfiguration.sa_ENCRYPT_FILE_EXTENSION ) )
            {
                a_encryptedFileName = file.toString() + "." + ApplicationConfiguration.sa_ENCRYPT_FILE_EXTENSION;
                File newFile = new File( a_encryptedFileName );

				boolean exit = !newFile.exists();
				while( !exit )
				{
					String message = null;
					
					if( ! Utils.M_getExtension( a_encryptedFileName ).equals( ApplicationConfiguration.sa_ENCRYPT_FILE_EXTENSION ) )
					{
						message = StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_ENCRYPTED_FILE_NAME_MUST_BE_JFE);
					}
					else if( newFile.exists() )
					{
						message = StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ALREADY_EXISTS);
					}
					else
					{
						exit = true;
					}
					
					if( ! exit )
					{
						Object[] options = {StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_YES),
											StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_NO)};
						int nn = Utils.showOptionDialog(a_parent,
							a_encryptedFileName + "  " + message + " " +
							StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_DO_YOU_WANT_TO_CHOOSE_ANOTHER_FILE_NAME_FOR_ENCRYPTED_FILE),
							StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ATENTION),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[0]);

						if( nn == 0 )
						{
							File parentDirectory = newFile.getParentFile();
							JFileChooser chooser=new JFileChooser(parentDirectory);
							chooser.setSelectedFile(newFile);
							Utils.M_changeFontToApplicationFontSize_forComponent( chooser );

							FileNameExtensionFilter fnef =
								new FileNameExtensionFilter(	StringsConfiguration.M_getInstance().
																	M_getStrParamConfiguration(StringsConfiguration.CONF_ENCRYPTED_FILE_JFE),
																"jfe" );
							chooser.addChoosableFileFilter(fnef);

							int returnVal = chooser.showOpenDialog(a_parent);
							if(returnVal == JFileChooser.APPROVE_OPTION)
							{
								String path=chooser.getSelectedFile().getParentFile().getAbsolutePath();
								newFile = chooser.getSelectedFile();

								ApplicationConfiguration.M_getInstance().M_setStrParamConfiguration(
										ApplicationConfiguration.CONF_LAST_DIRECTORY,
										( path ) );

								a_encryptedFileName = newFile.getAbsolutePath();
								if( Utils.M_getExtension( a_encryptedFileName ).equals( ApplicationConfiguration.sa_ENCRYPT_FILE_EXTENSION )  &&
									!newFile.exists() )
								{
									exit = true;
								}
							}
							else
							{
								exit = true;
							}
						}
						else
						{
							exit = true;
						}
					}
				}
				
				if( !Utils.M_getExtension( a_encryptedFileName ).equals( ApplicationConfiguration.sa_ENCRYPT_FILE_EXTENSION ) )
				{
                    M_setErrorString(   a_encryptedFileName + "   " +
                                        StringsConfiguration.M_getInstance().
                                        M_getStrParamConfiguration(StringsConfiguration.CONF_ENCRYPTED_FILE_NAME_MUST_BE_JFE) + " " +
                                        StringsConfiguration.M_getInstance().
                                        M_getStrParamConfiguration(StringsConfiguration.CONF_CAN_NOT_ENCRYPT)
                                    );
				}
				else if( newFile.exists() )
                {
                    M_setErrorString(   a_encryptedFileName + "   " +
                                        StringsConfiguration.M_getInstance().
                                        M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ALREADY_EXISTS) + " " +
                                        StringsConfiguration.M_getInstance().
                                        M_getStrParamConfiguration(StringsConfiguration.CONF_CAN_NOT_ENCRYPT)
                                    );
                }
				else
                {
                    a_parent.M_addStatusInformation(StringsConfiguration.M_getInstance().
                                        M_getStrParamConfiguration(StringsConfiguration.CONF_STARTING_TO_ENCRYPT_FILE) +
                                        "   " + a_decryptedFileName );

                    String basicArgs[] = new String[5];
                    basicArgs[0] = FileEncoderParameters.STR_ENCODE_TAG;
                    basicArgs[1] = FileEncoderParameters.STR_DECODED_FILE_NAME_TAG;
                    basicArgs[2] = a_decryptedFileName;
                    basicArgs[3] = FileEncoderParameters.STR_ENCODED_FILE_NAME_TAG;
                    basicArgs[4] = a_encryptedFileName;

					String[] args = StringFunctions.M_joinStringArrays( basicArgs, a_parent.M_getAdditionalArgsForEncrypting() );

                    try
                    {
                        FileEncoder.M_doActions( a_password, args, a_parent, a_oc );
						
						long hasToErase = ApplicationConfiguration.M_getInstance().
											M_getIntParamConfiguration(ApplicationConfiguration.CONF_ERASE_DECRYPTED_FILE_AFTER_ENCRIPTING);
						if( (hasToErase != 0) && (hasToErase != -1) )
						{
							try
							{
								M_secureEraseFile( a_decryptedFileName, a_parent );
							}
							catch( Throwable th )
							{
								th.printStackTrace();
 							}
						}
                    }
                    catch( FileEncoderException ex )
                    {
                        M_setErrorString( ex.getMessage() );
                    }
					catch (CancellationException ex)
					{
                        a_parent.M_addStatusInformation( StringsConfiguration.M_getInstance().
                                    M_getStrParamConfiguration(StringsConfiguration.CONF_CANCELLED_BY_USER_WHEN_ENCRYPTING) + " " +
									a_decryptedFileName );

						try
						{
							M_secureEraseFile( a_encryptedFileName, a_parent );
						}
						catch( Throwable th )
						{
							th.printStackTrace();
						}
						
						M_setIsCancellation( true );
					}
					catch( Throwable th )
					{
                        M_setErrorString( th.getMessage() );
					}
                }
            }
            else
            {
                M_setErrorString(   a_decryptedFileName + "   " +
                                    StringsConfiguration.M_getInstance().
                                    M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_HAS_A_WRONG_EXTENSION_IT_IS_PROBABLY_ALREADY_ENCRYPTED)
                                );
            }
        }
        else
        {
            M_setErrorString(   a_decryptedFileName + "   " +
                                StringsConfiguration.M_getInstance().
                                M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_DOES_NOT_EXIST_STRING)
                            );
        }

        if( M_getItWasError() )
        {
            a_parent.M_addStatusInformation(M_getErrorString());
        }
        else
        {
			if( ! M_isCancellation() )
	            a_parent.M_addStatusInformation( StringsConfiguration.M_getInstance().
						                           M_getStrParamConfiguration(StringsConfiguration.CONF_ENCRYPTION_SUCCESSFULLY_ENDED) );
        }
        
        M_setHasEnded(true);
    }
}
