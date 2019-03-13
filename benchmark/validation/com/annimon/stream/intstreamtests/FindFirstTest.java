package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public final class FindFirstTest {
    @Test
    public void testFindFirst() {
        Assert.assertFalse(IntStream.empty().findFirst().isPresent());
        Assert.assertEquals(IntStream.of(42).findFirst().getAsInt(), 42);
        Assert.assertTrue(IntStream.rangeClosed(2, 5).findFirst().isPresent());
    }
}

