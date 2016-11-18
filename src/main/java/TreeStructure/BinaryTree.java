package TreeStructure;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class BinaryTree<E>
{

	private BinaryNode<E> root;
	
	public BinaryTree()
	{
		root = null;
	}
	
	public BinaryTree(E elemento)
	{
		root = new BinaryNode<E>(elemento);
	}
	
	public BinaryTree(BinaryNode<E> root)
	{
		this.root = root;
	}
	
	public BinaryNode<E> getRoot()
	{
		return root;
	}
	
	public void setRoot(BinaryNode<E> nodo)
	{
		root = nodo;
	}
	
	public int getNumeroNodi()
	{
		return getNumeroNodi(root);
	}
	
	/*
	 * Restituisce il numero di nodi dell'albero radicato root.
	 * Conteggio ricorsivo del numero di nodi dei sottoalberi
	 * radicati nei figli di root
	 */
	private int getNumeroNodi(BinaryNode<E> radice)
	{
		return (radice == null ? 0 : (getNumeroNodi(radice.getSinistro()) + getNumeroNodi(radice.getDestro()) + 1));
	}
	
	public int getGrado(BinaryNode<E> nodo)
	{
		int grado = 0;
		if (nodo == null) return -1;
		if (nodo.getSinistro() != null) grado++;
		if (nodo.getDestro() != null) grado++;
		return grado;
	}
	
	public E getElemento(BinaryNode<E> nodo)
	{
		return nodo.getElement();
	}
	
	public BinaryNode<E> getPadre(BinaryNode<E> nodo)
    {
	   return nodo.getPadre();
    }
	
	public BinaryNode<E> getSinistro(BinaryNode<E> nodo)
	{
		return nodo.getSinistro();
	}
		
	public BinaryNode<E> getDestro(BinaryNode<E> nodo)
	{
		return nodo.getDestro();
	}
	
	public boolean isFiglioSinistro(BinaryNode<E> nodo)
	{
		if (nodo.equals(nodo.getPadre().getSinistro()))
			return true;
		return false;		
	}
	
	public boolean isFiglioDestro(BinaryNode<E> nodo)
	{
		if (nodo.equals(nodo.getPadre().getDestro()))
			return true;
		return false;		
	}
	
	public void setElemento(BinaryNode<E> nodo, E elemento)
	{
		nodo.setElement(elemento);
	}
	
	public void scambiaElemento(BinaryNode<E> nodo1, BinaryNode<E> nodo2)
	{
		E temp = nodo1.getElement();
		nodo1.setElement(nodo2.getElement());
		nodo2.setElement(temp);
	}
	
	public void innestaSinistro(BinaryNode<E> nodo, BinaryTree<E> albero)
	{
		BinaryNode<E> figlio = albero.getRoot();
		if (figlio != null)
			figlio.setPadre(nodo);
		nodo.setSinistro(figlio);
	}
	
	public void innestaDestro(BinaryNode<E> nodo, BinaryTree<E> albero)
	{
		BinaryNode<E> figlio =  albero.getRoot();
		if (figlio != null)
			figlio.setPadre(nodo);
		nodo.setDestro(figlio);
	}
	
	public BinaryTree<E> potaSinistro(BinaryNode<E> padre)
	{
		BinaryNode<E> figlio = padre.getSinistro();
		figlio.setPadre(null);
		BinaryTree<E> albero = new BinaryTree<E>(figlio);
		padre.setSinistro(null);
		return albero;
	}
	
	public BinaryTree<E> potaDestro(BinaryNode<E> padre)
	{
		BinaryNode<E> figlio = padre.getDestro();
		figlio.setPadre(null);
		BinaryTree<E> albero = new BinaryTree<E>(figlio);
		padre.setDestro(null);
		return albero;
	}
	
	public BinaryTree<E> pota(BinaryNode<E> nodo)
	{
		// Albero vuoto
		if (nodo == null)
			return new BinaryTree<E>();
		// Nodo � root
		if (nodo.getPadre() == null) 
		{ 
			root = null;
			return new BinaryTree<E>(nodo);
		}
		
		BinaryNode<E> padre = nodo.getPadre();
		if (isFiglioSinistro(nodo))
			return potaSinistro(padre);
		else 
			return potaDestro(padre);
	}
	
	public List<E> visitaDFS()
	{
		List<E> output = new LinkedList<E>();
		Stack<BinaryNode<E>> stack = new Stack<BinaryNode<E>>();
		if (root != null)
			stack.push(root);
		while (!(stack.isEmpty()))
		{
			BinaryNode<E> node = stack.pop();
			output.add(node.getElement());
			if (node.getDestro() != null)
				stack.push(node.getDestro());
			if (node.getSinistro() != null)
				stack.push(node.getSinistro());
		}
		return output;
	}

//	public List<E> visitaBFS()
//	{
//		List<E> output = new LinkedList<E>();
//		Coda<BinaryNode<E>> coda = new CodaCollegata<BinaryNode<E>>();
//		if (root != null)
//			coda.enqueue(root);
//		while (!(coda.isEmpty()))
//		{
//			BinaryNode<E> nodo = coda.dequeue();
//			output.add(nodo.getElement());
//			if( nodo.getSinistro() != null)
//				coda.enqueue(nodo.getSinistro());
//			if (nodo.getDestro() != null)
//				coda.enqueue(nodo.getDestro());
//		}
//		return output;
//	}
	
	public List<E> visitaInOrder()
	{
		List<E> output = new LinkedList<E>();
		return visitaInOrder(root, output);
	}

	private List<E> visitaInOrder(BinaryNode<E> nodo, List<E> output)
	{
		if (nodo.getSinistro() != null)
	        visitaInOrder(nodo.getSinistro(), output);

		output.add(nodo.getElement());

		if(nodo.getDestro() != null )
	        visitaInOrder(nodo.getDestro(), output);

        return output;
	}

}
