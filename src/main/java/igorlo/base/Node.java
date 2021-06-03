package igorlo.base;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

public class Node implements Serializable {

    public final Collection<Node> children = new LinkedList<>();
    public Node parent = null;
    public final int value;
    public int sum = 0;

    public Node(int value) {
        this.value = value;
    }

    public void addChild(Node node) {
        this.children.add(node);
        node.parent = this;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

}
