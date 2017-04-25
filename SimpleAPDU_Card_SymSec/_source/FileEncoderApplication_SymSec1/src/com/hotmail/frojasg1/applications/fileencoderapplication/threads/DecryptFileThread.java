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
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Usuario
 */
public class DecryptFileThread extends ParentThread
{
    protected String a_encryptedFileName = null;
    protected char[] a_password = null;

    protected String a_decryptedFileName = null;

	public DecryptFileThread( FileJInternalFrame parent, String fileName, char[] password, OperationCancellation oc )
    {
        super(parent, oc);
        a_encryptedFileName = fileName;
        a_password = password;
    }
    
    public String M_getDecryptedFileName()      { return( a_decryptedFileName );    }

    protected boolean M_renameEncryptedFile( String fileName )
    {
        String extension2 = Utils.M_getExtension( fileName );
        String fileWithoutExtension = Utils.M_putOffExtension(fileName);
        
        String extension1 = Utils.M_getExtension( fileWithoutExtension );
        String fileWithout2ndExtension = Utils.M_putOffExtension(fileWithoutExtension);
/*
        int ii=1;
        File fileDest = null;
        while( (fileDest = new File( fileWithout2ndExtension + ".old" + ii + "." + extension1 + "." + extension2 ) ).exists() )
        {
            ii++;
        }
*/

		long hasToAskIfOverwrite = ApplicationConfiguration.M_getInstance().
							M_getIntParamConfiguration(ApplicationConfiguration.CONF_ASK_TO_OVERWRITE_OLD_ENCRYPTED_FILE_WHEN_IT_EXISTED_PREVIOUSLY);

		boolean hasToOverwrite = true;

		File fileDest = new File( fileWithout2ndExtension + ".old" + "." + extension1 + "." + extension2 );

		if( fileDest.exists() )
		{
			if( hasToAskIfOverwrite != 0 )
			{
				//Custom button text
				Object[] options = {StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_YES),
									StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_NO)};
				int nn = Utils.showOptionDialog(a_parent,
					StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_WANT_TO_OVERWRITE_OLD_ENCRYPTED_FILE) +
					fileDest + " ?",
					StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ATENTION),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[1]);

				if( nn == 1 )
				{
					hasToOverwrite = false;
				}
			}
			if( hasToOverwrite ) fileDest.delete();
		}

		boolean result = false;
		if( hasToOverwrite )
		{
			File fileOri = new File( fileName );
			result = fileOri.renameTo( fileDest );

			if( result )
			{
				a_parent.M_addStatusInformation(StringsConfiguration.M_getInstance().
											M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_WAS_SUCCESSFULLY_RENAMED) +
											"   " +
											StringsConfiguration.M_getInstance().
											M_getStrParamConfiguration(StringsConfiguration.CONF_FROM) +
											": " + fileName + "   " +
											StringsConfiguration.M_getInstance().
											M_getStrParamConfiguration(StringsConfiguration.CONF_TO) +
											": " + fileDest.toString()
												);
			}
		}
		
        return( result );
    }

	@Override
    public void run()
    {
		M_setIsCancellation( false );
        File file = new File( a_encryptedFileName );
        if( file.exists() )
        {
            if( Utils.M_getExtension(a_encryptedFileName).equals( ApplicationConfiguration.sa_ENCRYPT_FILE_EXTENSION ) )
            {
                a_decryptedFileName = Utils.M_putOffExtension( file.toString() );
                File newFile = new File( a_decryptedFileName );

				boolean exit = !newFile.exists();
				while( !exit )
				{
					String message = null;
					
					if( Utils.M_getExtension( a_decryptedFileName ).equals( ApplicationConfiguration.sa_ENCRYPT_FILE_EXTENSION ) )
					{
						message = StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_DECRYPTED_FILE_NAME_CANNOT_BE_JFE);
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
							a_decryptedFileName + "  " + message + " " +
							StringsConfiguration.M_getInstance().M_getStrParamConfiguration(StringsConfiguration.CONF_DO_YOU_WANT_TO_CHOOSE_ANOTHER_FILE_NAME_FOR_DECRYPTED_FILE),
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
							FileNameExtensionFilter fnef =
								new FileNameExtensionFilter(	StringsConfiguration.M_getInstance().
																	M_getStrParamConfiguration(StringsConfiguration.CONF_ENCRYPTED_FILE_JFE),
																"jfe" );
							chooser.addChoosableFileFilter(fnef);
							Utils.M_changeFontToApplicationFontSize_forComponent( chooser );

							int returnVal = chooser.showOpenDialog(a_parent);
							if(returnVal == JFileChooser.APPROVE_OPTION)
							{
								String path=chooser.getSelectedFile().getParentFile().getAbsolutePath();
								newFile = chooser.getSelectedFile();

								ApplicationConfiguration.M_getInstance().M_setStrParamConfiguration(
										ApplicationConfiguration.CONF_LAST_DIRECTORY,
										( path ) );

								a_decryptedFileName = newFile.getAbsolutePath();
								if( !Utils.M_getExtension( a_decryptedFileName ).equals( ApplicationConfiguration.sa_ENCRYPT_FILE_EXTENSION )  &&
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
				
				if( Utils.M_getExtension( a_decryptedFileName ).equals( ApplicationConfiguration.sa_ENCRYPT_FILE_EXTENSION ) )
				{
                    M_setErrorString(   a_decryptedFileName + "   " +
                                        StringsConfiguration.M_getInstance().
                                        M_getStrParamConfiguration(StringsConfiguration.CONF_DECRYPTED_FILE_NAME_CANNOT_BE_JFE) + " " +
                                        StringsConfiguration.M_getInstance().
                                        M_getStrParamConfiguration(StringsConfiguration.CONF_CAN_NOT_DECRYPT)
                                    );
				}
				else if( newFile.exists() )
                {
                    M_setErrorString(   a_decryptedFileName + "   " +
                                        StringsConfiguration.M_getInstance().
                                        M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_ALREADY_EXISTS) + " " +
                                        StringsConfiguration.M_getInstance().
                                        M_getStrParamConfiguration(StringsConfiguration.CONF_CAN_NOT_DECRYPT)
                                    );
                }
				else
                {
                    a_parent.M_addStatusInformation(StringsConfiguration.M_getInstance().
                                        M_getStrParamConfiguration(StringsConfiguration.CONF_STARTING_TO_DECRYPT_FILE) +
                                        "   " + a_encryptedFileName );
                    
                    String args[] = new String[5];
					args[0] = FileEncoderParameters.STR_DECODE_TAG;
                    args[1] = FileEncoderParameters.STR_ENCODED_FILE_NAME_TAG;
                    args[2] = a_encryptedFileName;
                    args[3] = FileEncoderParameters.STR_DECODED_FILE_NAME_TAG;
                    args[4] = a_decryptedFileName;

                    try
                    {
                        FileEncoderParameters fep = FileEncoder.M_doActions( a_password, args, a_parent, a_oc );
						a_parent.M_setEncoderParameters_toJDial_encriptingConfiguration_fromDecodingFileParameters(fep);

						if( ApplicationConfiguration.M_getInstance().
							M_getIntParamConfiguration(ApplicationConfiguration.CONF_HAS_TO_RENAME_PRESENT_ENCRYPTED_FILE_TO_OLD_AFTER_DECRYPTING) != 0 )
						{
							boolean wasRenamed = false;
							try
							{
								wasRenamed = M_renameEncryptedFile( a_encryptedFileName );
							}
							catch( Throwable th )
							{
								th.printStackTrace();
							}

							if( !wasRenamed )
							{
								a_parent.M_addStatusInformation(	a_encryptedFileName + 
																	StringsConfiguration.M_getInstance().
																	M_getStrParamConfiguration(StringsConfiguration.CONF_ENCRYPTED_FILE_COULD_NOT_BE_RENAMED));
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
                                    M_getStrParamConfiguration(StringsConfiguration.CONF_CANCELLED_BY_USER_WHEN_DECRYPTING) + " " +
									a_encryptedFileName );

						try
						{
							M_secureEraseFile( a_decryptedFileName, a_parent );
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
                M_setErrorString(   a_encryptedFileName + " " +
                                    StringsConfiguration.M_getInstance().
                                    M_getStrParamConfiguration(StringsConfiguration.CONF_FILE_HAS_A_WRONG_EXTENSION_IT_IS_PROBABLY_NOT_ENCRYPTED)
                                );
            }
        }
        else
        {
            M_setErrorString(   a_encryptedFileName + " " +
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
													M_getStrParamConfiguration(StringsConfiguration.CONF_DECRYPTION_SUCCESSFULLY_ENDED) );
        }
        
        M_setHasEnded(true);
    }
}
