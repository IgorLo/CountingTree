package igorlo.thread;

import igorlo.base.ANodeCounter;
import igorlo.base.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BatchedParallelCounter extends ANodeCounter {

    private final ExecutorService executor;
    private final List<Callable<Boolean>> tasks;

    public BatchedParallelCounter(Node root, int threads, int batchSize) {
        super(root);
        executor = Executors.newWorkStealingPool(threads);

        Callable<Boolean> task = () -> {
            for (int j = 0; j < batchSize; j++) {
                Node current = queue.poll();
                if (current == null)
                    return true;
                int counted = calculateForNode(current);
                results.put(current, counted);
            }
            return true;
        };

        tasks = new ArrayList<>();
        int counter = (queue.size() / batchSize) + 2;
        for (int i = 0; i < counter; i++) {
            tasks.add(task);
        }
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
