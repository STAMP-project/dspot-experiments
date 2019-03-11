package com.annimon.stream.longstreamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.LongStream;
import com.annimon.stream.OptionalLong;
import org.junit.Assert;
import org.junit.Test;


public final class FindSingleTest {
    @Test
    public void testFindSingleOnEmptyStream() {
        Assert.assertThat(LongStream.empty().findSingle(), isEmpty());
    }

    @Test
    public void testFindSingleOnOneElementStream() {
        Assert.assertThat(LongStream.of(42L).findSingle(), hasValue(42L));
    }

    @Test(expected = IllegalStateException.class)
    public void testFindSingleOnMoreElementsStream() {
        LongStream.of(1, 2).findSingle();
    }

    @Test
    public void testFindSingleAfterFilteringToEmptyStream() {
        OptionalLong result = LongStream.of(5, 7, 9).filter(Functions.remainderLong(2)).findSingle();
        Assert.assertThat(result, isEmpty());
    }

    @Test
    public void testFindSingleAfterFilteringToOneElementStream() {
        OptionalLong result = LongStream.of(5, 10, (-15)).filter(Functions.remainderLong(2)).findSingle();
        Assert.assertThat(result, hasValue(10L));
    }

    @Test(expected = IllegalStateException.class)
    public void testFindSingleAfterFilteringToMoreElementStream() {
        LongStream.of(5, 10, (-15), (-20)).filter(Functions.remainderLong(2)).findSingle();
    }
}

