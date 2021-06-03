package igorlo.linear;

import igorlo.base.ANodeCounter;
import igorlo.base.Node;


public class LinearCounter extends ANodeCounter {

    public LinearCounter(Node root) {
        super(root);
    }

    @Override
    protected void processTree() {
        queue.forEach(node -> {
            results.put(node, calculateForNode(node));
        });
    }

}
