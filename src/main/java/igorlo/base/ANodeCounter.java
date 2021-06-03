package igorlo.base;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ANodeCounter implements Serializable {

    protected Map<Node, Integer> results = new ConcurrentHashMap<>();
    protected Queue<Node> queue = new LinkedList<>();
    protected final Node root;

    protected ANodeCounter(Node root) {
        this.root = root;
        addAllToQueue(root);
    }

    protected abstract void processTree();

    public Map<Node, Integer> calculate() {
        processTree();
        return results;
    }

    protected void addAllToQueue(Node node) {
        queue.add(node);
        if (!node.isLeaf()) {
            for (Node child : node.children) {
                addAllToQueue(child);
            }
        }
    }

    protected int calculateForNode(Node node) {
        int result = 0;
        for (Node child : node.children) {
            result += calculateForNode(child);
        }
        node.sum = result;
        return result;
    }

    public int getQueueSize() {
        return queue.size();
    }
}
