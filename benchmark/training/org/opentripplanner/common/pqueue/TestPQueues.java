package org.opentripplanner.common.pqueue;


import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import junit.framework.TestCase;


/* Test correctness and relative speed of various
priority queue implementations.
 */
public class TestPQueues extends TestCase {
    private static final int N = 50000;

    public void testCompareHeaps() throws InterruptedException {
        List<Integer> input;
        List<Integer> expected;
        input = new ArrayList<Integer>(TestPQueues.N);
        for (int i = 0; i < (TestPQueues.N); i++)
            input.add(((int) ((Math.random()) * 10000)));

        // First determine the expected results using a plain old PriorityQueue
        expected = new ArrayList<Integer>(TestPQueues.N);
        PriorityQueue<Integer> q = new PriorityQueue<Integer>(TestPQueues.N);
        for (Integer j : input) {
            q.add(j);
        }
        while (!(q.isEmpty())) {
            expected.add(q.remove());
        } 
        doQueue(new BinHeap<Integer>(), input, expected);
        fillQueue(new BinHeap<Integer>(), input);
    }

    /* You must be careful to produce unique objects for rekeying,
    otherwise the same object might be rekeyed twice or more.
     */
    public void testRekey() throws InterruptedException {
        final int N = 5000;
        final int ITER = 2;
        List<Double> keys;
        List<Integer> vals;
        keys = new ArrayList<Double>(N);
        vals = new ArrayList<Integer>(N);
        BinHeap<Integer> bh = new BinHeap<Integer>(20);
        for (int iter = 0; iter < ITER; iter++) {
            // reuse internal array in binheap
            bh.reset();
            // fill both keys and values with random numbers
            for (int i = 0; i < N; i++) {
                keys.add(i, ((Math.random()) * 10000));
                vals.add(i, ((N - i) * 3));
            }
            // insert them into the queue
            for (int i = 0; i < N; i++) {
                bh.insert(vals.get(i), keys.get(i));
            }
            // requeue every item with a new key that is an
            // order-preserving function of its place in the original list
            for (int i = 0; i < N; i++) {
                bh.rekey(vals.get(i), ((i * 2.0) + 10));
                // bh.dump();
            }
            // pull everything out of the queue in order
            // and check that the order matches the original list
            for (int i = 0; i < N; i++) {
                Double qp = bh.peek_min_key();
                Integer qi = bh.extract_min();
                TestCase.assertEquals(qi, vals.get(i));
            }
            // the queue should be empty at the end of each iteration
            TestCase.assertTrue(bh.empty());
        }
    }
}

