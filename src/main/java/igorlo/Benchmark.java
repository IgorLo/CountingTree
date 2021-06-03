package igorlo;

import igorlo.base.ANodeCounter;
import igorlo.base.Node;
import igorlo.base.TreeGenerator;
import igorlo.coolParallel.MPICounter;
import igorlo.linear.LinearCounter;
import igorlo.thread.BatchedParallelCounter;
import igorlo.thread.ParallelCounter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Benchmark {

    public static Map<Long, String> avgResults = new TreeMap<>();
    public static Map<String, List<Long>> allResults = new HashMap<>();

    public static void main(String[] args) {
        long seed = 1999L;
        int testTreesSize = 25;
        int depth = 5;
        int maxNodes = 30;
        boolean alwaysMax = false;

        TreeGenerator generator = new TreeGenerator(seed);

        List<Node> testTrees = new ArrayList<>();
        System.out.println("Генерация, может занять немного времени");
        for (int i = 0; i < testTreesSize; i++) {
            System.out.println("Генерация, может занять немного времени : " + i + "/" + testTreesSize);
            testTrees.add(generator.generateTree(maxNodes, depth, alwaysMax));
        }

        for (Node root: testTrees) {
            ANodeCounter linear = new LinearCounter(root);
            runningTime(linear, "linear");

//        Arrays.asList(1, 2, 4, 8, 16, 32, 64, 128).forEach(size -> {
        Arrays.asList(1, 2, 4, 8, 16, 32, 64).forEach(size -> {
//            Arrays.asList(1, 2, 4, 8, 16).forEach(size -> {
                ANodeCounter parallel = new ParallelCounter(root, size);
                runningTime(parallel, "parallel: threads " + size);
            });

            Arrays.asList(1, 2, 4, 8, 16, 32, 64).forEach(size -> {
                Arrays.asList(1, 4, 16, 128, 512, 1024, 10000).forEach(batch -> {
                    ANodeCounter parallel = new BatchedParallelCounter(root, size, batch);
                    runningTime(parallel, "batched parallel: threads " + size + ", batch " + batch);
                });
            });
        }

        System.out.println("--------------------------");
        System.out.println("Глубина дерева: " + depth);
        System.out.println("Кол-во детей  : " + maxNodes);
        System.out.println("Всегда макс д.:  : " + alwaysMax);
        System.out.println("Кол-во тестов :  : " + testTreesSize);
        System.out.println("--------------------------");

        allResults.keySet().forEach(name -> {
            List<Long> current = allResults.get(name);
            long avg = current.stream().reduce(0L, Long::sum) / current.size();
            avgResults.put(avg, name);
        });

        avgResults.forEach((time, name) -> System.out.println(time + " : " + name));
    }

    public static void runningTime(ANodeCounter counter, String name) {
        System.out.println("Начал для реализации " + name);
        int queueSize = counter.getQueueSize();
        long startTime = System.currentTimeMillis();
        Map<Node, Integer> results = counter.calculate();
        long time = System.currentTimeMillis() - startTime;
//        System.out.println("Закончил корректно: " + (results.size() == queueSize));
//        System.out.println("Закончил за " + time + " (" + time/1000 + " сек)");
        List<Long> allForCurrent = allResults.getOrDefault(name, new ArrayList<>());
        allForCurrent.add(time);
        allResults.put(name, allForCurrent);
    }

}
