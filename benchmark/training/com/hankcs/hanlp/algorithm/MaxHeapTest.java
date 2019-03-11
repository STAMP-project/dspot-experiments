package com.hankcs.hanlp.algorithm;


import java.util.Comparator;
import junit.framework.TestCase;


public class MaxHeapTest extends TestCase {
    final MaxHeap<Integer> heap = new MaxHeap<Integer>(5, new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }
    });

    public void testToList() throws Exception {
        TestCase.assertEquals("[9, 8, 7, 6, 5]", heap.toList().toString());
    }
}

