package com.annimon.stream.intstreamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.IntStream;
import java.util.NoSuchElementException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class SingleTest {
    @Test(expected = NoSuchElementException.class)
    public void testSingleOnEmptyStream() {
        IntStream.empty().single();
    }

    @Test
    public void testSingleOnOneElementStream() {
        int result = IntStream.of(42).single();
        Assert.assertThat(result, CoreMatchers.is(42));
    }

    @Test(expected = IllegalStateException.class)
    public void testSingleOnMoreElementsStream() {
        IntStream.rangeClosed(1, 2).single();
    }

    @Test(expected = NoSuchElementException.class)
    public void testSingleAfterFilteringToEmptyStream() {
        IntStream.range(1, 5).filter(Functions.remainderInt(6)).single();
    }

    @Test
    public void testSingleAfterFilteringToOneElementStream() {
        int result = IntStream.range(1, 10).filter(Functions.remainderInt(6)).single();
        Assert.assertThat(result, CoreMatchers.is(6));
    }

    @Test(expected = IllegalStateException.class)
    public void testSingleAfterFilteringToMoreElementStream() {
        IntStream.range(1, 100).filter(Functions.remainderInt(6)).single();
    }
}

