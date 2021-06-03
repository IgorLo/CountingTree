package igorlo.coolParallel;

import igorlo.base.ANodeCounter;
import igorlo.base.Node;
import mpi.MPI;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static igorlo.MPIBenchmark.ROOT_ID;

public class MPICounter extends ANodeCounter {

    public MPICounter(Node root) {
        super(root);
    }

    @Override
    protected void processTree() {
        int id = MPI.COMM_WORLD.Rank();
        int numProcs = MPI.COMM_WORLD.Size();

        System.out.println("MPI процесс номер " + id + " начал работу");

        int localSize = (queue.size()/numProcs) + 1;

        Object[] nodesArray = null;
        Object[] localNodes = new Object[localSize];
        List<Object> toShuffle = Arrays.asList(queue.toArray());
        Collections.shuffle(toShuffle);
        nodesArray = toShuffle.toArray();

        Object[] expandedArray = new Object[localSize * numProcs];
        for (int i = 0; i < nodesArray.length; i++) {
            expandedArray[i] = nodesArray[i];
        }

        if (id == ROOT_ID) {
            System.out.println("Всего узлов = " + nodesArray.length);
        }

        MPI.COMM_WORLD.Scatter(expandedArray, 0, localSize, MPI.OBJECT,
                                localNodes, 0, localSize, MPI.OBJECT,
                                ROOT_ID);

        Object[] localArray = localNodes.clone();

        /* отдельный поток работает */
        int count = 0;
        for (int i = 0; i < localSize; i++) {
            Node currentNode = (Node) localArray[i];
            if (currentNode != null){
                count++;
                calculateForNode((Node) localArray[i]);
            }
        }

        System.out.println("Потоком " + id + " проведено работы = " + count);

        MPI.COMM_WORLD.Gather(localNodes, 0, localSize, MPI.OBJECT,
            expandedArray, 0, localSize, MPI.OBJECT,
                                ROOT_ID);

//        System.out.println(Arrays.toString(nodesArray));

        System.out.println("MPI процесс номер " + id + " закончил работу");
    }

}
