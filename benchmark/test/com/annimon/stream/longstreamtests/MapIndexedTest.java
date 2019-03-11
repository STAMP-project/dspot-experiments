package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import com.annimon.stream.function.IndexedLongUnaryOperator;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapIndexedTest {
    @Test
    public void testMapIndexed() {
        LongStream.rangeClosed(4, 8).mapIndexed(new IndexedLongUnaryOperator() {
            @Override
            public long applyAsLong(int index, long value) {
                return index * value;
            }
        }).custom(assertElements(// (0 * 4)
        // (1 * 5)
        // (2 * 6)
        // (3 * 7)
        // (4 * 8)
        Matchers.arrayContaining(0L, 5L, 12L, 21L, 32L)));
    }

    @Test
    public void testMapIndexedWithStartAndStep() {
        LongStream.rangeClosed(4, 8).mapIndexed(20, (-5), new IndexedLongUnaryOperator() {
            @Override
            public long applyAsLong(int index, long value) {
                return index * value;
            }
        }).custom(assertElements(// (20 * 4)
        // (15 * 5)
        // (10 * 6)
        // (5  * 7)
        // (0  * 8)
        Matchers.arrayContaining(80L, 75L, 60L, 35L, 0L)));
    }
}

