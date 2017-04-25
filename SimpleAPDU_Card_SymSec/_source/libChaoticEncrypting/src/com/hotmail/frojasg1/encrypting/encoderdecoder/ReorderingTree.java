package com.hotmail.frojasg1.encrypting.encoderdecoder;


public class ReorderingTree
{
	private class PosTreeNode
	{
		public PosTreeNode()
		{
			A_parent = null;
			A_descendent = new PosTreeNode[4];
			A_flags = 0;
			for( int ii=0; ii<4; ii++ )	A_descendent[ii]=null;
		}

        public PosTreeNode 		A_parent;
        public PosTreeNode[] 	A_descendent;
        int          			A_flags;
	}
	
    private class HexNum
    {
    	public HexNum( int numDigitos )
    	{
    		A_numDigitsOf2Bits = numDigitos;
    		A_dato = new byte[numDigitos+1];
        }

        public byte[] A_dato;
        public int    A_numDigitsOf2Bits;
    }

	ReorderingTree( int numElem)
	{
		M_initializeTree( numElem );
	}

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // funcion que marca en el arbol que una determinada posicion esta ocupada.
    // si esta posicion ya estaba ocupada, entonces se modifica ligeramente para
    // devolver una posicion libre.
    // si posicionInicial estaba libre, la funcion devuelve posicionInicial
    // si posicionInicial estaba ocupada, la funcion devuelve una posicion ligeramente modificada, y que estaba libre.
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public int M_markBusyPosition( int posicionInicial )
    {
    	int result = -1;

    	int ii;
    	HexNum num = new HexNum( A_maxLevel );
    	int peso;
    	int nivel;
    	PosTreeNode nodo;
    	boolean continuar;
    	int jj;

    	//  printf( "M_marcaPosicionOcupada(%d)\n", posicionInicial );

    	peso = A_weightHighBits;
    	for( ii=1; ii<A_maxLevel+1; ii++ )
    	{
    		num.A_dato[ii] = ( new Integer( (posicionInicial/peso) & 0x3 ) ).byteValue();
    		peso = peso / 4;
    	}

    	nivel = 1;
    	nodo = A_tree;
    	continuar = true;
    	while(  continuar &&
    			(nivel < A_maxLevel) &&
    			( (nodo.A_descendent[num.A_dato[nivel]] != null) &&
    					( (nodo.A_flags & A_maskDescendentFull[num.A_dato[nivel]]) == 0 ) ||
    					( (nodo.A_flags & A_maskDescendentFull[num.A_dato[nivel]]) != 0 )
    			)
    		  )
    	{
    		if( (nodo.A_flags & A_maskDescendentFull[num.A_dato[nivel]]) == 0 )
    		{
    			nodo = nodo.A_descendent[num.A_dato[nivel]];
    			nivel++;
    		}
    		else
    		{
    			jj = ( num.A_dato[nivel] + 1 ) % 4;
    			while(  continuar && ( jj != num.A_dato[nivel] ) &&
    					( (nodo.A_descendent[jj] != null) &&
    							( (nodo.A_flags & A_maskDescendentFull[jj]) != 0 ) ||
    							( (nodo.A_flags & A_maskDescendentFull[jj]) != 0 )
    					)
    	              )
    			{
    				jj = (jj + 1) % 4;
    			}
    			if( jj == num.A_dato[nivel] )
    			{
    				System.out.println( "Error, nivel ocupado totalmente y no marcado en el nivel superior\n" );
    				return( result );
    			}
    			if( nodo.A_descendent[jj] == null )
    			{
    				num.A_dato[nivel] = ( new Integer( jj ) ).byteValue();
    				continuar = false;
    			}
    			else
    			{
    				num.A_dato[nivel] = ( new Integer( jj ) ).byteValue();
    				nodo = nodo.A_descendent[num.A_dato[nivel]];
    				nivel = nivel + 1;
    			}
    		}
    	}

    	if( nivel == A_maxLevel )
    	{
    		ii = 0;
    		jj = num.A_dato[nivel];
    		while(  (ii<4) &&
    				( (nodo.A_flags & A_maskDescendentFull[jj]) != 0 )
    			  )
    		{
    			jj = ( jj + 1 ) % 4;
    			ii++;
    		}
    		if( ii==4 )
    		{
    			System.out.println( "El ultimo nivel estaba totalmente ocupado, pero eso no se ha notificado al padre. " +
    								"(nivel=" + nivel + ")" );
    			return( result );
    		}
    		else
    		{
    			num.A_dato[nivel] = ( new Integer( jj ) ).byteValue();
    			nodo.A_flags = nodo.A_flags | A_maskDescendentFull[jj];
    		}
    	}
    	else if( !continuar || ( nodo.A_descendent[num.A_dato[nivel]] == null ) )
    	{
    		nodo = M_insertPositionInTree( nodo, num );
    	}
    	else
    	{
    		System.out.println( "No se ha detectado que falte el hijo, pero no estamos en el ultimo nivel" );
    		return( result );
    	}

    	continuar = true;
    	nivel = A_maxLevel;
    	while( continuar && ( nodo.A_parent != null ) )
    	{
    		ii=0;
    		while( continuar && (ii<4) )
    		{
    			continuar = continuar && ( ( nodo.A_flags & A_maskDescendentFull[ii] ) != 0 );
    			ii++;
    		}
    		if( continuar )
    		{
    			nodo = nodo.A_parent;
    			nivel--;
    			nodo.A_flags = nodo.A_flags | A_maskDescendentFull[ num.A_dato[nivel] ];
    		}
    	}

    	peso = 1;
    	result = 0;
    	for( ii = A_maxLevel; ii>0; ii-- )
    	{
    		result = result + peso * num.A_dato[ii];
    		peso = peso * 4;
    	}

    	//  printf( "salida M_marcaPosicionOcupada=%d\n", result );

    	return( result );
    }

    public boolean M_isFull()
    {
    	boolean result = true;

    	for( int ii=0; (ii<4) && result; ii++ ) result = ( (A_tree.A_flags & A_maskDescendentFull[ii] ) > 0 );

    	return( result );
    }

    protected void M_initializeTree( int numElem )
    {
        A_numMaxElem = numElem;
        int[] var = new int[1];
        var[0]=0;
        A_maxLevel = M_calculateMaxLevel( numElem, var );
        A_weightHighBits= var[0];
        A_maskDescendentFull = new int[4];
        int maskDescendantFull = 0x100;
        for( int ii=0; ii<4; ii++ )
        {
        	A_maskDescendentFull[ii] = maskDescendantFull;
        	maskDescendantFull = maskDescendantFull << 1;
        }
        
        A_tree = new PosTreeNode();
        A_tree.A_flags = 1;

        for( int ii=0; ii<4; ii++ )
        {
          if( ii*A_weightHighBits > numElem - 1 )
          {
        	  A_tree.A_flags = A_tree.A_flags | A_maskDescendentFull[ii];
          }
        }
    }

    protected int M_calculateMaxLevel( int numElem, int[] weightHighBits )
    {
    	int result = 0;
    	weightHighBits[0] = 1;

    	if( numElem > 0 )
    	{
    		numElem--;
    		do
    		{
    			result++;
    			weightHighBits[0] = weightHighBits[0] * 4;
    			numElem = numElem / 4;
    		} while( numElem > 0 );
    	}
    	weightHighBits[0] = weightHighBits[0] / 4;

    	return( result );
    }

    protected PosTreeNode M_initializeNewNode( PosTreeNode parent, int level )
    {
    	PosTreeNode result = new PosTreeNode();

    	result.A_flags = level;
    	result.A_parent = parent;

    	return( result );
    }

    protected PosTreeNode M_insertPositionInTree( PosTreeNode node, HexNum num )
    {
    	int ii;
    	int numHastaEsteNivel;
    	int peso;
    	boolean llenarMayores;
    	int numTmp;
    	int pesoAnterior;
    	int jj;

    	numHastaEsteNivel = 0;
    	pesoAnterior = A_weightHighBits * 4;
    	peso = A_weightHighBits;
    	for( ii=1; ii < (node.A_flags & 0xff) + 1; ii++ )
    	{
    		numHastaEsteNivel = numHastaEsteNivel + num.A_dato[ii] * peso;
    		pesoAnterior = peso;
    		peso = peso / 4;
    	}

    	llenarMayores = true;
    	for( ii = (node.A_flags & 0xff) + 1; ii<=A_maxLevel; ii++ )
    	{
    		while( ( (numHastaEsteNivel + peso * num.A_dato[ii] ) > ( A_numMaxElem - 1 ) ) && ( num.A_dato[ii] >= 0 ) )
    		{
    			num.A_dato[ii] = ( new Integer( num.A_dato[ii] - 1 ) ).byteValue();
    		}

    		if( num.A_dato[ii] < 0 )
    		{
    			System.out.println( "Error. No se encontro hijo menor que numMaxElem\n" );
    			return( node );
    		}

    		PosTreeNode nuevoNodo = M_initializeNewNode( node, ii );
    		node.A_descendent[ num.A_dato[ii-1] ] = nuevoNodo;

    		numTmp = numHastaEsteNivel + pesoAnterior - peso;

    		jj = 3;
    		while ( llenarMayores && ( jj>-1 ) && (numTmp > A_numMaxElem -1) )
    		{
    			nuevoNodo.A_flags = nuevoNodo.A_flags | A_maskDescendentFull[jj];
    			jj--;
    			numTmp = numTmp - peso;
    		}

    		if( jj==-1 )
    		{
    			System.out.println( "Error, todos los hijos del nodo son mayores que el numMaxElem\n" );
    			return( node );
    		}

    		pesoAnterior = peso;
    		peso = peso / 4;
    		numHastaEsteNivel = numHastaEsteNivel + pesoAnterior * num.A_dato[ii];

    		llenarMayores = llenarMayores && ( numTmp == numHastaEsteNivel );
    		node = nuevoNodo;
    	}

    	node.A_flags = node.A_flags | A_maskDescendentFull[ num.A_dato[ A_maxLevel ] ];
    	return( node );
    }

    protected void M_freeTree()
    {
    	M_freeTree( A_tree );
    }

    protected void M_freeTree( PosTreeNode node )
    {
    	for( int ii=0; ii<4; ii++ )
    	{
    		if( node.A_descendent[ii] != null )
    		{
    			M_freeTree( node.A_descendent[ii] );
    			node.A_descendent[ii] = null;
    		}
    	}
    }

    protected PosTreeNode A_tree;

    protected int A_maxLevel;
    protected int A_weightHighBits;
    protected int A_maskDescendentFull[];	// es un array de cuatro, que indica si los hijos estan llenos
    protected int A_numMaxElem;

}
