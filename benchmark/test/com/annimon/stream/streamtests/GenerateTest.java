package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class GenerateTest {
    @Test
    public void testGenerate() {
        Stream.generate(Functions.fibonacci()).limit(10).custom(assertElements(Matchers.contains(0L, 1L, 1L, 2L, 3L, 5L, 8L, 13L, 21L, 34L)));
    }

    @Test(expected = NullPointerException.class)
    public void testGenerateNull() {
        Stream.generate(null);
    }
}

