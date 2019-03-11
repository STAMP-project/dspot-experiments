package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import com.annimon.stream.function.IntBinaryOperator;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapIndexedTest {
    @Test
    public void testMapIndexed() {
        IntStream.rangeClosed(4, 8).mapIndexed(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int index, int value) {
                return index * value;
            }
        }).custom(assertElements(// (0 * 4)
        // (1 * 5)
        // (2 * 6)
        // (3 * 7)
        // (4 * 8)
        Matchers.arrayContaining(0, 5, 12, 21, 32)));
    }

    @Test
    public void testMapIndexedWithStartAndStep() {
        IntStream.rangeClosed(4, 8).mapIndexed(20, (-5), new IntBinaryOperator() {
            @Override
            public int applyAsInt(int index, int value) {
                return index * value;
            }
        }).custom(assertElements(// (20 * 4)
        // (15 * 5)
        // (10 * 6)
        // (5  * 7)
        // (0  * 8)
        Matchers.arrayContaining(80, 75, 60, 35, 0)));
    }
}

