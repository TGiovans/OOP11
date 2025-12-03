package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to sum all elements of a matrix using multithreading.
 */
public final class MultiThreadedSumMatrix implements SumMatrix {

    private final int workCount;

    /**
     *
     * @param n amount of threads.
     */
    public MultiThreadedSumMatrix(final int n) {
        this.workCount = n;
    }

    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length > 0 ? matrix.length * matrix[0].length : 0;
        final int split = size / this.workCount + size % workCount;
        final List<Worker> workers = new ArrayList<>(workCount);
        System.out.println(split + " split per thread check that");
        for (int start = 0; start < size; start += split) {
            workers.add(new Worker(matrix, start, split));
        }
        for (final Worker w: workers) {
            w.start();
        }
        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        return sum;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private long res;

        /**
         * Build a new worker.
         *
         * @param list
         *            the list to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        Worker(final double[][] list, final int startpos, final int nelem) {
            super();
            this.matrix = list;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public synchronized void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < matrix.length * matrix[0].length && i < startpos + nelem; i++) {
                this.res += this.matrix[i / matrix[0].length][i % matrix[0].length];
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         *
         * @return the sum of every element in the array
         */
        public synchronized double getResult() {
            return this.res;
        }
    }
}
