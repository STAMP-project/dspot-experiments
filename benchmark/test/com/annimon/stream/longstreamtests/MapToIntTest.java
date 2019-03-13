package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import com.annimon.stream.function.LongToIntFunction;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapToIntTest {
    @Test
    public void testMapToInt() {
        LongToIntFunction mapper = new LongToIntFunction() {
            @Override
            public int applyAsInt(long value) {
                return ((int) (value / 10));
            }
        };
        LongStream.of(10L, 20L, 30L, 40L).mapToInt(mapper).custom(assertElements(Matchers.arrayContaining(1, 2, 3, 4)));
    }
}

