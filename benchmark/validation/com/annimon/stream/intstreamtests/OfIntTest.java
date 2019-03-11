package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public final class OfIntTest {
    @Test
    public void testStreamOfInt() {
        Assert.assertEquals(1, IntStream.of(42).count());
        Assert.assertTrue(IntStream.of(42).findFirst().isPresent());
    }
}

