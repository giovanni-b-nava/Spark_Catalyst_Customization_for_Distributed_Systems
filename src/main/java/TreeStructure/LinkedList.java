package TreeStructure;

/**
 * Created by Spark on 18/11/2016.
 */
public class  LinkedList {

    TNodeList head, tail;
    int size;

    public LinkedList() {
        head = tail = null;
        size = 0;
    }

    public void addFirst(TNodeList node) {
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head = node;
        }
        size++;
    }
    public void addLast(TNodeList node) {
        if(tail==null)
        {
            head=tail=node;
        }
        else
        {
            tail.next=node;
            tail=node;
        }
        size++;
    }
}
