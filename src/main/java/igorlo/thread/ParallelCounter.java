package igorlo.thread;

import igorlo.base.ANodeCounter;
import igorlo.base.Node;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ParallelCounter extends ANodeCounter {

    private final ExecutorService executor;
    private final List<Callable<Boolean>> tasks;

    public ParallelCounter(Node root, int threads) {
        super(root);
        executor = Executors.newWorkStealingPool(threads);
        tasks = queue.stream().map(node -> (Callable<Boolean>) () -> {
            int counted = calculateForNode(node);
            results.put(node, counted);
            return true;
        }).collect(Collectors.toList());
    }

    @Override
    protected void processTree() {
        try {
            executor.invokeAll(tasks).stream().map(future -> {
                try {
                    return future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    return null;
                }
            });
            executor.shutdown();
            executor.awaitTermination(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
