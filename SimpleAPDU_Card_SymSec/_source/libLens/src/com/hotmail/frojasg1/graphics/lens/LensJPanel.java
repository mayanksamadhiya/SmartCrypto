/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotmail.frojasg1.graphics.lens;

import com.hotmail.frojasg1.graphics.Coordinate2D;
import com.hotmail.frojasg1.graphics.ScreenImage;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Usuario
 */
public class LensJPanel extends JPanel
{
	protected Lens a_lensTransformation = null;
	protected JPanel a_contentJPanel = null;

	protected Coordinate2D a_lensPosition = null;
	protected boolean a_moveLensWithMouse = false;

	protected Vector<Component> a_excludeComponentsFromPainting = new Vector<Component>();
	
	protected Object _mutex = null;

//	protected RepaintManager a_originalRepaintManager = null;
/*	
	protected class RepaintManagerLens extends RepaintManager
	{
		protected JTextComponent a_excludeComponent = null;
		protected Vector<JTextComponent> a_excludeComponents = new Vector<JTextComponent>();
		
		public RepaintManagerLens( )
		{
			super();
		}
			
		public void addDirtyRegion(JComponent c, int x, int y, int w, int h)
		{
			boolean ok=true;
			if( c instanceof JTextComponent )
			{
 				JTextComponent jtc = (JTextComponent) c;
				if( a_excludeComponents.indexOf(jtc) != -1 )
				{
					ok = jtc.getCaret().isVisible();
				}
			}
			if( ok ) super.addDirtyRegion( c, x, y, w, h );
		}
		
		public void M_addExcludeComponent( JTextComponent jtc )
		{
			a_excludeComponents.add(jtc);
		}
		
		public void M_clearExcludeComponents()
		{
			a_excludeComponents.clear();
		}
	}
*/
	/**
	 * 
	 * @param panel		Panel over which it will be done the lens effect
	 * @param radius	Radius of the lens
	 * @param mode		it can take the next values: Lens.SA_MODE_AMPLIFY and Lens.SA_MODE_REDUCE
	 * @param moveLensWithMouse it indicates if the lens is moved with the mouse pointer
	 */
	
	public LensJPanel( JPanel panel, int radius, int mode, boolean moveLensWithMouse, Object mutex )
	{
		if( mutex != null ) _mutex = mutex;
		else _mutex = new Object();

		try
		{
			if( mode == Lens.SA_MODE_AMPLIFY ) a_lensTransformation = new Lens( radius, Lens.SA_MODE_AMPLIFY );
			else if( mode == Lens.SA_MODE_REDUCE ) a_lensTransformation = new Lens( radius, Lens.SA_MODE_REDUCE );
			else a_lensTransformation = new Lens( radius, mode );		// will throw exception
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		a_contentJPanel = panel;
		a_moveLensWithMouse = moveLensWithMouse;

//		initComponents();

		initOwnComponents();

//		a_originalRepaintManager = RepaintManager.currentManager(a_contentJPanel);
//		RepaintManager.setCurrentManager( new RepaintManagerLens() );
		
//		addExcludedJTextComponents(a_contentJPanel);
	}
/*
	public void M_restoreOriginalRepaintManager()
	{
		if( RepaintManager.currentManager(a_contentJPanel) instanceof RepaintManagerLens )
		{
			RepaintManagerLens rml = (RepaintManagerLens) RepaintManager.currentManager(a_contentJPanel);
			rml.M_clearExcludeComponents();
		}

		if( a_originalRepaintManager != null ) RepaintManager.setCurrentManager(a_originalRepaintManager);
	}
*/	

	public void M_addComponentNotToPaint( Component comp )
	{
		a_excludeComponentsFromPainting.add(comp);
	}

	public void M_clearComponentsNotToPaint()
	{
		a_excludeComponentsFromPainting.clear();
	}
	
	public void M_setMouseMotionListener( Component comp, java.awt.event.MouseMotionAdapter mma )
	{
		comp.addMouseMotionListener( mma );

		if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				M_setMouseMotionListener( contnr.getComponent(ii), mma );
			}

			if( comp instanceof JMenu )
			{
				JMenu jmnu = (JMenu) comp;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					M_setMouseMotionListener( jmnu.getMenuComponent( ii ), mma );
			}
		}
	}

	public void initOwnComponents()
	{
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jPanelComponentResized(evt);
            }
        });

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(a_contentJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(a_contentJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

		if( a_moveLensWithMouse )
		{
			java.awt.event.MouseMotionAdapter mma = new java.awt.event.MouseMotionAdapter() {
				public void mouseMoved(java.awt.event.MouseEvent evt) {
					jPanelMouseMoved(evt);
				}
			};

			M_setMouseMotionListener( this, mma );
		}
	}

    private void jPanelMouseMoved(java.awt.event.MouseEvent evt)
	{
        // TODO add your handling code here:

		if( a_moveLensWithMouse )
		{
			Coordinate2D newPosition = new Coordinate2D( evt.getX(), evt.getY() );
			if( ! newPosition.equals( a_lensPosition ) )
			{
				int xx = newPosition.M_getX();
				int yy = newPosition.M_getY();
				if( evt.getComponent() != this )
				{
					xx = xx + evt.getComponent().getX();
					yy = yy + evt.getComponent().getY();

					Container cont = evt.getComponent().getParent();
					while( cont != this )
					{
						xx = xx + cont.getX();
						yy = yy + cont.getY();
						
						cont = cont.getParent();
					}
				}
				a_lensPosition = new Coordinate2D( xx, yy );
				repaint();
			}
		}
    }                               

    private void jPanelComponentResized(java.awt.event.ComponentEvent evt)
	{                                      
        // TODO add your handling code here:

		a_contentJPanel.setBounds( new Rectangle( 0, 0, evt.getComponent().getWidth(), evt.getComponent().getHeight() ) );
	}
	
	@Override
	public void paint(Graphics gc)
	{
		synchronized( _mutex )
		{
			if( a_lensPosition != null )
			{
				M_paintLens( gc, a_lensTransformation, a_lensPosition );
			}
			else
			{
				super.paint( gc );
			}
		}
	}

	protected void M_paintLens( Graphics gc, Lens lens, Coordinate2D lensPosition )
	{
//		M_activateCaretSelectionVisible( true, a_contentJPanel );
//		M_setDirtyRegions(a_contentJPanel);
		M_excludeComponentsFromPainting(true, a_contentJPanel);
		BufferedImage bi = ScreenImage.createImage( a_contentJPanel );
		M_excludeComponentsFromPainting(false, a_contentJPanel);
//		M_activateCaretSelectionVisible( false, a_contentJPanel );
		
		Graphics gc1 = bi.getGraphics();

		int radius = lens.M_getRadius();
		
		Coordinate2D lensPosition2 = new Coordinate2D( lensPosition.M_getX() - a_contentJPanel.getX(),
														lensPosition.M_getY() - a_contentJPanel.getY() );
		LensTransformationResult ltr = lens.M_getTransformedImage( bi, lensPosition2 );
		
		Coordinate2D upleft = ltr.M_getUpLeftCorner();
		Coordinate2D downright = ltr.M_getDownRightCorner();
		
		gc1.drawImage( ltr.M_getResultImage(),	lensPosition.M_getX() + a_contentJPanel.getX() - radius + upleft.M_getX(),
												lensPosition.M_getY() + a_contentJPanel.getY() - radius + upleft.M_getY(),
												lensPosition.M_getX() + a_contentJPanel.getX() - radius + downright.M_getX(),
												lensPosition.M_getY() + a_contentJPanel.getY() - radius + downright.M_getY(),
												upleft.M_getX(),
												upleft.M_getY(),
												downright.M_getX(),
												downright.M_getY(),
												null );
		
		gc1.setColor(Color.BLACK);
		gc1.drawOval( lensPosition.M_getX()-lens.M_getRadius(), lensPosition.M_getY()-lens.M_getRadius(),
					2*lens.M_getRadius(), 2*lens.M_getRadius() );
		
		gc.drawImage( bi, 0, 0, bi.getWidth(), bi.getHeight(), 0, 0, bi.getWidth(), bi.getHeight(), null );
	}
/*
	public BufferedImage M_getImage()
	{
		return( a_imageJPanel.M_getImage() );
	}


	protected void M_activateCaretSelectionVisible( boolean visible, Component comp )
	{
		if( comp instanceof Container	)
		{
			if ( comp instanceof JTextComponent )
			{
				JTextComponent jtc = (JTextComponent) comp;
//				jtc.getCaret().setVisible(visible);
				jtc.getCaret().setSelectionVisible(visible);
				jtc.setIgnoreRepaint(true);
				jtc.removeNotify();
			}

			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				M_activateCaretSelectionVisible( visible, contnr.getComponent(ii) );
			}

			if( comp instanceof JMenu )
			{
				JMenu jmnu = (JMenu) comp;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					M_activateCaretSelectionVisible( visible, jmnu.getMenuComponent( ii ) );
			}
		}
	}
*/
	protected void M_excludeComponentsFromPainting( boolean selectionVisible, Component comp )
	{
		if( a_excludeComponentsFromPainting.indexOf(comp) != -1 )
		{
			if ( comp instanceof JTextComponent )
			{
				JTextComponent jtc = (JTextComponent) comp;
//				jtc.getCaret().setVisible(visible);
				jtc.getCaret().setSelectionVisible(selectionVisible);
			}
			comp.setIgnoreRepaint(true);
			comp.removeNotify();
		}
		if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				M_excludeComponentsFromPainting( selectionVisible, contnr.getComponent(ii) );
			}

			if( comp instanceof JMenu )
			{
				JMenu jmnu = (JMenu) comp;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					M_excludeComponentsFromPainting( selectionVisible, jmnu.getMenuComponent( ii ) );
			}
		}
	}

	public Coordinate2D M_getLensCoordinates()
	{
		Coordinate2D result = null;
		if( ! a_moveLensWithMouse ) result = a_lensPosition;
		return( result );
	}
/*
	public void M_setDirtyRegions( Component comp )
	{
		if( comp instanceof Container	)
		{
			if ( comp instanceof JTextComponent )
			{
				JTextComponent jtc = (JTextComponent) comp;
				RepaintManager.currentManager(comp).markCompletelyDirty( jtc );
			}

			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				M_setDirtyRegions( contnr.getComponent(ii) );
			}

			if( comp instanceof JMenu )
			{
				JMenu jmnu = (JMenu) comp;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					M_setDirtyRegions( jmnu.getMenuComponent( ii ) );
			}
		}
	}
	
	public void addExcludedJTextComponents( Component comp )
	{
		if( comp instanceof Container	)
		{
			if ( comp instanceof JTextComponent )
			{
				JTextComponent jtc = (JTextComponent) comp;
				if( RepaintManager.currentManager(comp) instanceof RepaintManagerLens )
				{
					RepaintManagerLens rml = (RepaintManagerLens) RepaintManager.currentManager(a_contentJPanel);
					rml.M_addExcludeComponent(jtc);
				}
			}

			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				addExcludedJTextComponents( contnr.getComponent(ii) );
			}

			if( comp instanceof JMenu )
			{
				JMenu jmnu = (JMenu) comp;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					addExcludedJTextComponents( jmnu.getMenuComponent( ii ) );
			}
		}
	}
*/
}
