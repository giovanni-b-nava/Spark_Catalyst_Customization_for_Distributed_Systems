package TreeStructure;

public class BinaryNode<E> {

	private E element;
	private BinaryNode<E> father;
	private BinaryNode<E> left;
	private BinaryNode<E> right;

	public BinaryNode() {
		this.element = null;
		father = left = right = null;
	}

	public BinaryNode(E element) {
		this.element = element;
		father = left = right = null;
	}

	public void setElement(E element) {
		this.element = element;
	}

	public E getElement() {
		return element;
	}

	public void setFather(BinaryNode<E> node) {
		father = node;
	}

	public BinaryNode<E> getFather() {
		return father;
	}

	public void setLeft(BinaryNode<E> node)	{
		left = node;
	}

	public BinaryNode<E> getLeft() {
		return left;
	}

	public void setRight(BinaryNode<E> node) {
		right = node;
	}

	public BinaryNode<E> getRight() {
		return right;
	}
}
