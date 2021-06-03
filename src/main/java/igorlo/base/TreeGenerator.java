package igorlo.base;


import java.util.Random;

public class TreeGenerator {

    private final Random random;
    private static final int DEFAULT_DEPTH = 5;
    private static final int DEFAULT_MAX_NODES = 10;
    private static final int DEFAULT_MAX_VALUE = 1000;

    public TreeGenerator(Long seed) {
        this.random = new Random(seed);
    }

    public TreeGenerator() {
        this.random = new Random();
    }

    public Node generateTree(int maxNodes, int depth, boolean alwaysMaxNodes) {
        Node root = new Node(value());
        populateNode(root, maxNodes, 1, depth, alwaysMaxNodes);
        return root;
    }

    private void populateNode(Node node, int maxNodes, int currentDepth, int maxDepth, boolean alwaysMaxNodes) {
        int nodeCount = alwaysMaxNodes ? maxNodes : 1 + randomInt(maxNodes);
        for (int i = 0; i < nodeCount; i++) {
            Node current = new Node(value());
            node.addChild(current);
            if (currentDepth < maxDepth) {
                populateNode(current, maxNodes, currentDepth + 1, maxDepth, alwaysMaxNodes);
            }
        }
    }

    private int randomInt(int bound) {
        return random.nextInt(bound);
    }

    private int value() {
        return random.nextInt(DEFAULT_MAX_VALUE);
    }

}
