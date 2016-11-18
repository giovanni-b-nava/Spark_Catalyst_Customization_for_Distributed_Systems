package TreeStructure;

public class BinaryNode<E>
{

	private E element;
	private BinaryNode<E> padre;
	private BinaryNode<E> sinistro;
	private BinaryNode<E> destro;

	public BinaryNode(E element)
	{
		this.element = element;
		padre = sinistro = destro = null;	
	}

	public void setElement(E element)
	{
		this.element = element;
	}

	public E getElement()
	{
		return element;
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
