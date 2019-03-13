package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public final class FindSingleTest {
    @Test
    public void testFindSingleOnEmptyStream() {
        Assert.assertThat(Stream.empty().findSingle(), isEmpty());
    }

    @Test
    public void testFindSingleOnOneElementStream() {
        Optional<Integer> result = Stream.of(42).findSingle();
        Assert.assertThat(result, hasValue(42));
    }

    @Test(expected = IllegalStateException.class)
    public void testFindSingleOnMoreElementsStream() {
        Stream.rangeClosed(1, 2).findSingle();
    }

    @Test
    public void testFindSingleAfterFilteringToEmptyStream() {
        Optional<Integer> result = Stream.range(1, 5).filter(Functions.remainder(6)).findSingle();
        Assert.assertThat(result, isEmpty());
    }

    @Test
    public void testFindSingleAfterFilteringToOneElementStream() {
        Optional<Integer> result = Stream.range(1, 10).filter(Functions.remainder(6)).findSingle();
        Assert.assertThat(result, hasValue(6));
    }

    @Test(expected = IllegalStateException.class)
    public void testFindSingleAfterFilteringToMoreElementStream() {
        Stream.range(1, 100).filter(Functions.remainder(6)).findSingle();
    }
}

