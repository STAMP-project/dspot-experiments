package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.IntPair;
import com.annimon.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class IndexedTest {
    @Test
    public void testIndexed() {
        int[] expectedIndices = new int[]{ 0, 1, 2, 3 };
        int[] actualIndices = Stream.of("a", "b", "c", "d").indexed().mapToInt(Functions.<String>intPairIndex()).toArray();
        Assert.assertThat(actualIndices, Matchers.is(expectedIndices));
    }

    @Test
    public void testIndexedCustomStep() {
        int[] expectedIndices = new int[]{ -10, -15, -20, -25 };
        int[] actualIndices = Stream.of("a", "b", "c", "d").indexed((-10), (-5)).mapToInt(Functions.<String>intPairIndex()).toArray();
        Assert.assertThat(actualIndices, Matchers.is(expectedIndices));
    }

    @Test
    public void testIndexedReverse() {
        Stream.of("first", "second", "third", "fourth", "fifth").indexed(0, (-1)).sortBy(new com.annimon.stream.function.Function<IntPair<String>, Integer>() {
            @Override
            public Integer apply(IntPair<String> t) {
                return t.getFirst();
            }
        }).map(new com.annimon.stream.function.Function<IntPair<String>, String>() {
            @Override
            public String apply(IntPair<String> t) {
                return t.getSecond();
            }
        }).custom(assertElements(Matchers.contains("fifth", "fourth", "third", "second", "first")));
    }
}

