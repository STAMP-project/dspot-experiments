package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import com.annimon.stream.function.IntToLongFunction;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapToLongTest {
    @Test
    public void testMapToLong() {
        IntStream.rangeClosed(2, 4).mapToLong(new IntToLongFunction() {
            @Override
            public long applyAsLong(int value) {
                return value * 10000000000L;
            }
        }).custom(assertElements(Matchers.arrayContaining(20000000000L, 30000000000L, 40000000000L)));
    }
}

