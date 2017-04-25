/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.applications.common.components.internationalization;

import java.awt.Component;
import java.util.Hashtable;

/**
 *
 * @author Usuario
 */
public class MapResizeRelocateComponentItem extends Hashtable< Component, ResizeRelocateItem >
{
	public MapResizeRelocateComponentItem()
	{}
	
	// public function to add a resize and relocate policy for a particular component
	// an example of invocation for this function:
	// a_intern.putResizeRelocateComponentItem(jPanel1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
	public void putResizeRelocateComponentItem( Component comp, int flags ) throws InternException
	{
		ResizeRelocateItem rri = new ResizeRelocateItem( comp, flags );
		put(comp, rri);
	}

}
