package com.annimon.stream.streamtests;


import com.annimon.stream.Stream;
import com.annimon.stream.test.hamcrest.StreamMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class DistinctTest {
    @Test
    public void testDistinct() {
        Stream.of(1, 1, 2, 3, 5, 3, 2, 1, 1, (-1)).distinct().sorted().custom(assertElements(Matchers.contains((-1), 1, 2, 3, 5)));
    }

    @Test
    public void testDistinctEmpty() {
        Stream.<Integer>empty().distinct().custom(StreamMatcher.<Integer>assertIsEmpty());
    }

    @Test
    public void testDistinctPreservesOrder() {
        Stream.of(1, 1, 2, 3, 5, 3, 2, 1, 1, (-1)).distinct().custom(assertElements(Matchers.contains(1, 2, 3, 5, (-1))));
    }

    @Test
    public void testDistinctLazy() {
        List<Integer> input = new ArrayList<Integer>(10);
        input.addAll(Arrays.asList(1, 1, 2, 3, 5));
        Stream<Integer> stream = Stream.of(input).distinct();
        input.addAll(Arrays.asList(3, 2, 1, 1, (-1)));
        Assert.assertThat(stream, elements(Matchers.contains(1, 2, 3, 5, (-1))));
    }
}

