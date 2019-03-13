package com.annimon.stream.streamtests;


import IntUnaryOperator.Util;
import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FlatMapToIntTest {
    @Test
    public void testFlatMapToInt() {
        Stream.rangeClosed(2, 4).flatMapToInt(new com.annimon.stream.function.Function<Integer, IntStream>() {
            @Override
            public IntStream apply(Integer t) {
                return IntStream.iterate(t, Util.identity()).limit(t);
            }
        }).custom(assertElements(Matchers.arrayContaining(2, 2, 3, 3, 3, 4, 4, 4, 4)));
    }
}

