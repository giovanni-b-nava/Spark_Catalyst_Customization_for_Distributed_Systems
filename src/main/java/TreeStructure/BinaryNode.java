package TreeStructure;

public class BinaryNode<E>
{

	private E elemento;
	private BinaryNode<E> padre;
	private BinaryNode<E> sinistro;
	private BinaryNode<E> destro;

	public BinaryNode(E elemento)
	{
		this.elemento = elemento;
		padre = sinistro = destro = null;	
	}

	public void setElemento(E elemento)
	{
		this.elemento = elemento;
	}

	public E getElemento()
	{
		return elemento;
	}

	public void setPadre(BinaryNode<E> nodo)
	{
		padre = nodo;
	}

	public BinaryNode<E> getPadre()
	{
		return padre;
	}

	public void setSinistro(BinaryNode<E> nodo)
	{
		sinistro = nodo;
	}

	public BinaryNode<E> getSinistro()
	{
		return sinistro;
	}

	public void setDestro(BinaryNode<E> nodo)
	{
		destro = nodo;
	}

	public BinaryNode<E> getDestro()
	{
		return destro;
	}

}
