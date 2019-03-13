package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public final class SumTest {
    @Test
    public void testSum() {
        Assert.assertEquals(IntStream.empty().sum(), 0);
        Assert.assertEquals(IntStream.of(42).sum(), 42);
        Assert.assertEquals(IntStream.rangeClosed(4, 8).sum(), 30);
    }
}

