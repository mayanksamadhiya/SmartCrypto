/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hotmail.frojasg1.applications.fileencoderapplication.utils;

import com.hotmail.frojasg1.applications.fileencoderapplication.configuration.ApplicationConfiguration;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

/**
 *
 * @author Usuario
 */
public class Utils
{
    protected Utils(){}
    
    static public String M_getExtension( File file )
    {
        String fileName = file.toString();
        return( M_getExtension( fileName ) );
    }

    static public String M_getExtension( String fileName )
    {
        String extension = "";
        
        String longFileName = fileName;
        String shortFileName = null;
        int pos = longFileName.lastIndexOf( File.separator );
        while( (pos>0) && (pos==(longFileName.length()-1) ))
        {
            longFileName = longFileName.substring( 0, longFileName.length()-1 );
            pos = longFileName.lastIndexOf( File.separator );
        }

        if( (pos >= 0) && (pos<(longFileName.length()-1) ) )
        {
            shortFileName = longFileName.substring( pos + 1 );
        }
        else if( pos < 0 )
        {
            shortFileName = longFileName;
        }

        pos = shortFileName.lastIndexOf( "." );
        if( (pos>0) && (pos<(shortFileName.length()-1) ))
        {
            extension = shortFileName.substring( pos+1, shortFileName.length() );
        }

/*
        System.out.println( "File:" + fileName +
                            "shortFileName: " + shortFileName +
                            ", extension: " + extension +
                            ", separator: " + File.separator );
*/
        return( extension );
    }
	
	static protected Font M_getNewFontForComponentFromApplicationFontSize( Component comp, float factor )
	{
		Font result = null;
		
		Font oldFont = comp.getFont();
		if( oldFont != null )
		{
			if( factor > 0.0F )
			{
				result = oldFont.deriveFont(factor * oldFont.getSize2D() );
			}
		}
		
		return( result );
	}
	
	static protected void M_changeFontToApplicationFontSize( Component comp, float factor )
	{
		Font newFont = M_getNewFontForComponentFromApplicationFontSize( comp, factor );
		if( newFont != null )
		{
			comp.setFont( newFont );
		}
		
		if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				M_changeFontToApplicationFontSize( contnr.getComponent(ii), factor );
			}

			if( contnr instanceof JMenu )
			{
				JMenu jmnu = (JMenu) contnr;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					M_changeFontToApplicationFontSize( jmnu.getMenuComponent( ii ), factor );
			}
		}
	}
    
	static public void M_changeFontToApplicationFontSize_forComponent( Component comp )
	{
		float factor = ApplicationConfiguration.M_getInstance().M_getApplicationFontSize();

		if( factor > 0.0F )
		{
			M_changeFontToApplicationFontSize( comp, factor );

			if( comp instanceof JFileChooser )
			{
				Dimension dim = comp.getPreferredSize();
				int width = (int) ( ( (float) dim.getWidth()) * factor );
				int height = (int) ( ( (float) dim.getHeight()) * factor );
				Dimension newDim = new Dimension( width, height);

				comp.setPreferredSize( newDim );
			}
		}
	}
	
    static public String M_putOffExtension( String fileName )
    {
        String extension = M_getExtension( fileName );
        int lenExt = (extension.length() > 0 ? extension.length() + 1 : 0 );    // para el punto
        String outputFileName = fileName.substring(0, fileName.length()-lenExt );
        System.out.println( "fileName: " + fileName + "without extension: " + outputFileName );
        
        return( outputFileName );
    }
	
	static public void showMessageDialog( Component parent, Object message, String title, int messageType )
	{
		JOptionPane option = new JOptionPane(	message, messageType );
		M_changeFontToApplicationFontSize_forComponent( option );
		JDialog dialog = option.createDialog(parent, title );
		dialog.setVisible(true);
	}

	static public int showOptionDialog( Component parentComponent, Object message, String title, int optionType,
										int messageType, Icon icon, Object[] options, Object initialValue )
	{
		JOptionPane option = new JOptionPane(	message, messageType );
		option.setIcon(icon);
		option.setOptionType(optionType);
		option.setOptions( options );
		option.setInitialValue( initialValue );
		M_changeFontToApplicationFontSize_forComponent( option );
		JDialog dialog = option.createDialog(parentComponent, title );
		dialog.setVisible(true);

		int result = -1;
		if( options != null )
		{
			for( int ii=0; (result==-1) && ii<options.length; ii++ )
				if( option.getValue() == options[ii] ) result = ii;
		}
		return( result );
	}
}
