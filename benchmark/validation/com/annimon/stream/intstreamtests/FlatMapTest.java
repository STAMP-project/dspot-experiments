package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FlatMapTest {
    @Test
    public void testFlatMap() {
        IntStream.range((-1), 5).flatMap(new com.annimon.stream.function.IntFunction<IntStream>() {
            @Override
            public IntStream apply(int value) {
                if (value < 0) {
                    return null;
                }
                if (value == 0) {
                    return IntStream.empty();
                }
                return IntStream.range(0, value);
            }
        }).custom(assertElements(// -1
        // 0
        // 1
        // 2
        // 3
        // 4
        Matchers.arrayContaining(0, 0, 1, 0, 1, 2, 0, 1, 2, 3)));
    }

    @Test(expected = NoSuchElementException.class)
    public void testFlatMapIterator() {
        IntStream.empty().flatMap(new com.annimon.stream.function.IntFunction<IntStream>() {
            @Override
            public IntStream apply(int value) {
                return IntStream.of(value);
            }
        }).iterator().nextInt();
    }
}

