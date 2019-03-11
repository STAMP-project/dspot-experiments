package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapToObjTest {
    @Test
    public void testMapToObj() {
        IntStream.rangeClosed(2, 4).mapToObj(new com.annimon.stream.function.IntFunction<String>() {
            @Override
            public String apply(int value) {
                return Integer.toString(value);
            }
        }).custom(assertElements(Matchers.contains("2", "3", "4")));
    }
}

