package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public final class MinTest {
    @Test
    public void testMin() {
        Assert.assertFalse(IntStream.empty().min().isPresent());
        Assert.assertTrue(IntStream.of(42).min().isPresent());
        Assert.assertEquals(IntStream.of(42).min().getAsInt(), 42);
        Assert.assertEquals(IntStream.of((-1), (-2), (-3), (-2), (-3), (-5), (-2), Integer.MIN_VALUE, Integer.MAX_VALUE).min().getAsInt(), Integer.MIN_VALUE);
    }
}

