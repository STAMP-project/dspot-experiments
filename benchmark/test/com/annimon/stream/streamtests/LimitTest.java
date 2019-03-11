package com.annimon.stream.streamtests;


import com.annimon.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class LimitTest {
    @Test
    public void testLimit() {
        Stream.range(0, 10).limit(2).custom(assertElements(Matchers.contains(0, 1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLimitNegative() {
        Stream.range(0, 10).limit((-2)).count();
    }

    @Test
    public void testLimitZero() {
        final Stream<Integer> stream = Stream.range(0, 10).limit(0);
        Assert.assertThat(stream, isEmpty());
    }

    @Test
    public void testLimitMoreThanCount() {
        Stream.range(0, 5).limit(15).custom(assertElements(Matchers.contains(0, 1, 2, 3, 4)));
    }
}

