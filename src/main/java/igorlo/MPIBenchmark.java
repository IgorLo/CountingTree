package igorlo;

import igorlo.base.ANodeCounter;
import igorlo.base.Node;
import igorlo.base.TreeGenerator;
import igorlo.coolParallel.MPICounter;
import mpi.MPI;


public class MPIBenchmark {

    public final static int ROOT_ID = 0;

    public static void main(String[] args) {
        MPI.Init(args);
        int id = MPI.COMM_WORLD.Rank();

        long startTime = System.currentTimeMillis();

        long seed = 1999L;
        TreeGenerator generator = new TreeGenerator(seed);
        Node root = generator.generateTree(30, 5, false);
        ANodeCounter mpiParallel = new MPICounter(root);
        System.out.println("Поток " + id + " прошел инициализацию");

        mpiParallel.calculate();

        if (id == ROOT_ID) {
            System.out.println("Время выполнения = " + (System.currentTimeMillis() - startTime));
        }

        MPI.Finalize();
    }

}
