package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import com.annimon.stream.function.IntUnaryOperator;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapTest {
    @Test
    public void testMap() {
        IntStream.of(5).map(new IntUnaryOperator() {
            @Override
            public int applyAsInt(int operand) {
                return -operand;
            }
        }).custom(assertElements(Matchers.arrayContaining((-5))));
        IntStream.of(1, 2, 3, 4, 5).map(new IntUnaryOperator() {
            @Override
            public int applyAsInt(int operand) {
                return -operand;
            }
        }).custom(assertElements(Matchers.arrayContaining((-1), (-2), (-3), (-4), (-5))));
    }
}

