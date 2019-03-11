package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public final class MaxTest {
    @Test
    public void testMax() {
        Assert.assertFalse(IntStream.empty().max().isPresent());
        Assert.assertTrue(IntStream.of(42).max().isPresent());
        Assert.assertEquals(IntStream.of(42).max().getAsInt(), 42);
        Assert.assertEquals(IntStream.of((-1), (-2), (-3), (-2), (-3), (-5), (-2), Integer.MIN_VALUE, Integer.MAX_VALUE).max().getAsInt(), Integer.MAX_VALUE);
    }
}

