package TreeStructure;

/**
 * Created by Spark on 18/11/2016.
 */
public class NTL {

    public TNodeList root;
    public int size;

    public NTL(TNodeList root) {
        this.root = root;
        size = 0;
    }

    public boolean insert(TNodeList[] sons, TNodeList father) {
        if (this.search(father)) {
            TNodeList tmp = this.searchNode(father);
            if (sons.length != 0) {
                for (int i = 0; i < sons.length; i++) {
                    father.sons.addLast(sons[i]);
                }
                return true;
            } else {
                System.out.println("Nessun figlio da inseririe");
                return false;
            }
        }
        System.out.println("Nodo padre inesistente");
        return false;
    }

    public boolean search(TNodeList node) {
        if (size != 0) {
            TNodeList p = this.root;
            if (p != null) {
                if (p == node) return true;
                else {
                    TNodeList t = p.sons.head;
                    while (t != null) {
                        search(t);
                        t = t.next;
                    }
                }
            }
        }
        return false;
    }

    public TNodeList searchNode(TNodeList node) {
        TNodeList p = this.root;
        if (p != null) {
            if (p == node) return p;
            else {
                TNodeList t = p.sons.head;
                while (t != null) {
                    search(t);
                    t = t.next;
                }
            }
        }
        return null;
    }

    public void preorder() {
        preorder(root);
    }

    public void preorder(TNodeList p) {
        if (p != null) {
            System.out.println(p.toString());
            TNodeList t = p.sons.head;
            while (t != null) {
                preorder(t);
                t = t.next;
            }
        }
    }

    public void postorder() {
        postorder(root);
    }

    public void postorder(TNodeList p) {
        if (p != null) {
            TNodeList t = p.sons.head;
            while (t != null) {
                postorder(t);
                t = t.next;
            }
            System.out.println(p.toString());
        }
    }
}