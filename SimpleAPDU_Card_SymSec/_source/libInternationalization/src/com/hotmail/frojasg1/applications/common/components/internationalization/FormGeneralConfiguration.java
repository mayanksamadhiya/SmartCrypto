package com.hotmail.frojasg1.applications.common.components.internationalization;


import com.hotmail.frojasg1.applications.common.configuration.ConfigurationParent;
import java.io.IOException;
import java.util.Properties;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Usuario
 */
public class FormGeneralConfiguration extends ConfigurationParent
{
    protected static final String CONF_ANCHO_VENTANA = "ANCHO_VENTANA";
    protected static final String CONF_ALTO_VENTANA = "ALTO_VENTANA";
    protected static final String CONF_POSICION_X = "POSICION_X";
    protected static final String CONF_POSICION_Y = "POSICION_Y";

	protected Properties a_defaultPropertiesReadFromComponents = null;
	
	FormGeneralConfiguration(	String mainFolder,
								String applicationName, String group,
								String configurationFileName,
								Properties defaultPropertiesReadFromComponents )
	{
		super( mainFolder, applicationName, group, null, configurationFileName );
		
		a_defaultPropertiesReadFromComponents = defaultPropertiesReadFromComponents;
	}
	
	protected Properties M_getDefaultProperties( String language )
	{
		Properties result = a_defaultPropertiesReadFromComponents;
		
		a_defaultPropertiesReadFromComponents.setProperty( this.CONF_ALTO_VENTANA, "500" );
		a_defaultPropertiesReadFromComponents.setProperty(this.CONF_ANCHO_VENTANA, "200" );
		a_defaultPropertiesReadFromComponents.setProperty(this.CONF_POSICION_X, "400" );
		a_defaultPropertiesReadFromComponents.setProperty(this.CONF_POSICION_Y, "150" );

		result = M_makePropertiesAddingDefaults(result, a_defaultPropertiesReadFromComponents );

		return( result );
	}
}
