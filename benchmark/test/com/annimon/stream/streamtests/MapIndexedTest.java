package com.annimon.stream.streamtests;


import com.annimon.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapIndexedTest {
    @Test
    public void testMapIndexed() {
        Stream.rangeClosed(4, 8).mapIndexed(new com.annimon.stream.function.IndexedFunction<Integer, Integer>() {
            @Override
            public Integer apply(int index, Integer t) {
                return index * t;
            }
        }).custom(assertElements(// (0 * 4)
        // (1 * 5)
        // (2 * 6)
        // (3 * 7)
        // (4 * 8)
        Matchers.contains(0, 5, 12, 21, 32)));
    }

    @Test
    public void testMapIndexedWithStartAndStep() {
        Stream.rangeClosed(4, 8).mapIndexed(20, (-5), new com.annimon.stream.function.IndexedFunction<Integer, Integer>() {
            @Override
            public Integer apply(int index, Integer t) {
                return index * t;
            }
        }).custom(assertElements(// (20 * 4)
        // (15 * 5)
        // (10 * 6)
        // (5  * 7)
        // (0  * 8)
        Matchers.contains(80, 75, 60, 35, 0)));
    }
}

