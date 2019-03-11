package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class RangeTest {
    @Test
    public void testStreamRange() {
        Assert.assertEquals(10, IntStream.range(1, 5).sum());
        Assert.assertEquals(0, IntStream.range(2, 2).count());
    }

    @Test
    public void testStreamRangeOnMinValue() {
        Assert.assertThat(IntStream.range(Integer.MIN_VALUE, ((Integer.MIN_VALUE) + 5)).count(), CoreMatchers.is(5L));
    }

    @Test
    public void testStreamRangeOnEqualValues() {
        Assert.assertThat(IntStream.range(Integer.MIN_VALUE, Integer.MIN_VALUE).count(), CoreMatchers.is(0L));
        Assert.assertThat(IntStream.range(0, 0).count(), CoreMatchers.is(0L));
        Assert.assertThat(IntStream.range(Integer.MAX_VALUE, Integer.MAX_VALUE).count(), CoreMatchers.is(0L));
    }

    @Test
    public void testStreamRangeOnMaxValue() {
        Assert.assertThat(IntStream.range(((Integer.MAX_VALUE) - 5), Integer.MAX_VALUE).count(), CoreMatchers.is(5L));
    }

    @Test
    public void testStreamRangeClosed() {
        Assert.assertEquals(15, IntStream.rangeClosed(1, 5).sum());
        Assert.assertEquals(5, IntStream.rangeClosed(1, 5).count());
    }

    @Test
    public void testStreamRangeClosedStartGreaterThanEnd() {
        Assert.assertThat(IntStream.rangeClosed(5, 1).count(), CoreMatchers.is(0L));
    }

    @Test
    public void testStreamRangeClosedOnMinValue() {
        Assert.assertThat(IntStream.rangeClosed(Integer.MIN_VALUE, ((Integer.MIN_VALUE) + 5)).count(), CoreMatchers.is(6L));
    }

    @Test
    public void testStreamRangeClosedOnEqualValues() {
        Assert.assertThat(IntStream.rangeClosed(Integer.MIN_VALUE, Integer.MIN_VALUE).single(), CoreMatchers.is(Integer.MIN_VALUE));
        Assert.assertThat(IntStream.rangeClosed(0, 0).single(), CoreMatchers.is(0));
        Assert.assertThat(IntStream.rangeClosed(Integer.MAX_VALUE, Integer.MAX_VALUE).single(), CoreMatchers.is(Integer.MAX_VALUE));
    }

    @Test
    public void testStreamRangeClosedOnMaxValue() {
        Assert.assertThat(IntStream.rangeClosed(((Integer.MAX_VALUE) - 5), Integer.MAX_VALUE).count(), CoreMatchers.is(6L));
    }
}

