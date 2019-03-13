package com.annimon.stream.streamtests;


import org.junit.Assert;
import org.junit.Test;


public final class CountTest {
    @Test
    public void testCount() {
        long count = com.annimon.stream.Stream.range(10000000000L, 10000002000L).count();
        Assert.assertEquals(2000, count);
    }

    @Test
    public void testCountMinValue() {
        long count = com.annimon.stream.Stream.range(Integer.MIN_VALUE, ((Integer.MIN_VALUE) + 100)).count();
        Assert.assertEquals(100, count);
    }

    @Test
    public void testCountMaxValue() {
        long count = com.annimon.stream.Stream.range(((Long.MAX_VALUE) - 100), Long.MAX_VALUE).count();
        Assert.assertEquals(100, count);
    }
}

