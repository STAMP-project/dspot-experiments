package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import com.annimon.stream.function.LongUnaryOperator;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapTest {
    @Test
    public void testMap() {
        LongUnaryOperator negator = new LongUnaryOperator() {
            @Override
            public long applyAsLong(long operand) {
                return -operand;
            }
        };
        LongStream.of(10L, 20L, 30L).map(negator).custom(assertElements(Matchers.arrayContaining((-10L), (-20L), (-30L))));
        LongStream.empty().map(negator).custom(assertIsEmpty());
    }
}

