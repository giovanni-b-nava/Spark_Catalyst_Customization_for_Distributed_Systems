package TreeStructure;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class BinaryTree<E> {

	private BinaryNode<E> root;

	public BinaryTree() {
		root = null;
	}

	public BinaryTree(E element) {
		root = new BinaryNode<E>(element);
	}

	public BinaryTree(BinaryNode<E> root) {
		this.root = root;
	}

	public BinaryNode<E> getRoot() {
		return root;
	}

	public void setRoot(BinaryNode<E> node) {
		root = node;
	}

	public int getNodeNumber() {
		return getNodeNumber(root);
	}

	// Calculate recursively the numeber of nodes in the tree
	private int getNodeNumber(BinaryNode<E> root) {
		return (root == null ? 0 : (getNodeNumber(root.getLeft()) + getNodeNumber(root.getRight()) + 1));
	}

	public int getLevel(BinaryNode<E> node)	{
		int level = 0;
		if (node == null) return -1;
		if (node.getLeft() != null) level++;
		if (node.getRight() != null) level++;
		return level;
	}

	public E getElement(BinaryNode<E> node) {
		return node.getElement();
	}

	public BinaryNode<E> getFather(BinaryNode<E> node) {
		return node.getFather();
	}

	public BinaryNode<E> getLeft(BinaryNode<E> node) {
		return node.getLeft();
	}

	public BinaryNode<E> getRight(BinaryNode<E> node) {
		return node.getRight();
	}

	public boolean isLeftChild(BinaryNode<E> node) {
		if (node.equals(node.getFather().getLeft()))
			return true;
		return false;
	}

	public boolean isRightChild(BinaryNode<E> node) {
		if (node.equals(node.getFather().getRight()))
			return true;
		return false;
	}

	public void setElement(BinaryNode<E> node, E element) {
		node.setElement(element);
	}

	public void swapElement(BinaryNode<E> node1, BinaryNode<E> node2) {
		E temp = node1.getElement();
		node1.setElement(node2.getElement());
		node2.setElement(temp);
	}

	public void setLeft(BinaryNode<E> node, BinaryTree<E> tree)	{
		BinaryNode<E> child = tree.getRoot();
		if (child != null)
			child.setFather(node);
		node.setLeft(child);
	}

	public void setRight(BinaryNode<E> node, BinaryTree<E> tree) {
		BinaryNode<E> child =  tree.getRoot();
		if (child != null)
			child.setFather(node);
		node.setRight(child);
	}

	public BinaryTree<E> trimLeft(BinaryNode<E> father) {
		BinaryNode<E> child = father.getLeft();
		child.setFather(null);
		BinaryTree<E> tree = new BinaryTree<E>(child);
		father.setLeft(null);
		return tree;
	}

	public BinaryTree<E> trimRight(BinaryNode<E> father) {
		BinaryNode<E> child = father.getRight();
		child.setFather(null);
		BinaryTree<E> tree = new BinaryTree<E>(child);
		father.setRight(null);
		return tree;
	}

	public BinaryTree<E> trim(BinaryNode<E> node) {
		// Empty tree
		if (node == null)
			return new BinaryTree<E>();
		// Root
		if (node.getFather() == null)
		{
			root = null;
			return new BinaryTree<E>(node);
		}
		BinaryNode<E> father = node.getFather();
		if (isLeftChild(node))
			return trimLeft(father);
		else
			return trimRight(father);
	}

	public List<E> DFSVisit()
	{
		List<E> output = new LinkedList<E>();
		Stack<BinaryNode<E>> stack = new Stack<BinaryNode<E>>();
		if (root != null)
			stack.push(root);
		while (!(stack.isEmpty()))
		{
			BinaryNode<E> node = stack.pop();
			output.add(node.getElement());
			if (node.getRight() != null)
				stack.push(node.getRight());
			if (node.getLeft() != null)
				stack.push(node.getLeft());
		}
		return output;
	}

	public List<E> orderedVisit()
	{
		List<E> output = new LinkedList<E>();
		return orderedVisit(root, output);
	}

	private List<E> orderedVisit(BinaryNode<E> node, List<E> output) {
		if (node.getLeft() != null)
			orderedVisit(node.getLeft(), output);

		output.add(node.getElement());

		if(node.getRight() != null )
			orderedVisit(node.getRight(), output);

		return output;
	}

}
