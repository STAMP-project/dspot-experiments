package com.annimon.stream.streamtests;


import LongUnaryOperator.Util;
import com.annimon.stream.LongStream;
import com.annimon.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FlatMapToLongTest {
    @Test
    public void testFlatMapToLong() {
        Stream.rangeClosed(2L, 4L).flatMapToLong(new com.annimon.stream.function.Function<Long, LongStream>() {
            @Override
            public LongStream apply(Long t) {
                return LongStream.iterate(t, Util.identity()).limit(t);
            }
        }).custom(assertElements(Matchers.arrayContaining(2L, 2L, 3L, 3L, 3L, 4L, 4L, 4L, 4L)));
    }
}

