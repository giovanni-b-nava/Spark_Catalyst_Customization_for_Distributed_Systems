package TreeStructure;

/**
 * Created by Spark on 18/11/2016.
 */
public class TNodeList<E> {

    public E element;
    public TNodeList next;
    public LinkedList sons;

    public TNodeList(E element) {
        sons=new LinkedList();
        this.element=element;
    }

    public String toString() {
        return (String)this.element;
    }
}

