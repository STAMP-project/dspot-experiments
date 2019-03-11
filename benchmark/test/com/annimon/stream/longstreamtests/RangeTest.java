package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class RangeTest {
    @Test
    public void testStreamRange() {
        Assert.assertEquals(10, LongStream.range(1, 5).sum());
        Assert.assertEquals(0, LongStream.range(2, 2).count());
    }

    @Test(timeout = 1000)
    public void testStreamRangeOnMinValue() {
        Assert.assertThat(LongStream.range(Long.MIN_VALUE, ((Long.MIN_VALUE) + 5)).count(), Matchers.is(5L));
    }

    @Test(timeout = 1000)
    public void testStreamRangeOnEqualValues() {
        Assert.assertThat(LongStream.range(Long.MIN_VALUE, Long.MIN_VALUE), isEmpty());
        Assert.assertThat(LongStream.range(0, 0), isEmpty());
        Assert.assertThat(LongStream.range(Long.MAX_VALUE, Long.MAX_VALUE), isEmpty());
    }

    @Test(timeout = 1000)
    public void testStreamRangeOnMaxValue() {
        Assert.assertThat(LongStream.range(((Long.MAX_VALUE) - 5), Long.MAX_VALUE).count(), Matchers.is(5L));
    }

    @Test
    public void testStreamRangeClosed() {
        Assert.assertThat(LongStream.rangeClosed(1, 5).sum(), Matchers.is(15L));
        Assert.assertThat(LongStream.rangeClosed(1, 5).count(), Matchers.is(5L));
    }

    @Test
    public void testStreamRangeClosedStartGreaterThanEnd() {
        Assert.assertThat(LongStream.rangeClosed(5, 1), isEmpty());
    }

    @Test(timeout = 1000)
    public void testStreamRangeClosedOnMinValue() {
        Assert.assertThat(LongStream.rangeClosed(Long.MIN_VALUE, ((Long.MIN_VALUE) + 5)).count(), Matchers.is(6L));
    }

    @Test(timeout = 1000)
    public void testStreamRangeClosedOnEqualValues() {
        Assert.assertThat(LongStream.rangeClosed(Long.MIN_VALUE, Long.MIN_VALUE), elements(Matchers.arrayContaining(Long.MIN_VALUE)));
        Assert.assertThat(LongStream.rangeClosed(0, 0), elements(Matchers.arrayContaining(0L)));
        Assert.assertThat(LongStream.rangeClosed(Long.MAX_VALUE, Long.MAX_VALUE), elements(Matchers.arrayContaining(Long.MAX_VALUE)));
    }

    @Test(timeout = 1000)
    public void testStreamRangeClosedOnMaxValue() {
        Assert.assertThat(LongStream.rangeClosed(((Long.MAX_VALUE) - 5), Long.MAX_VALUE).count(), Matchers.is(6L));
    }
}

